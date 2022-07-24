from datetime import datetime
import json
from utils import get_payload_value, make_pairs, fetch_data_from_api, fetch_data_using_pagination
from constants import LOG, property_api, collection_api
from constants import DEFAULT_SWIFT_TO_EKA_NAME
from constants import ERROR_MESSAGES
from utils import fill_total_credits_and_debits,update_data_to_api
from constants import ERROR_COLUMNS,COLLECTION_DATA_APPEND_URL
import os
# URLs and paths
security_info_endpoint = "/cac-security/api/userinfo"

# Headers
auth = 'X-Accesstoken'
tenant = 'X-Tenantid'
appid_key = 'appId'
get_data_method = 'method'
obj_body = 'object'
platform_url_header = 'X-Platformurl'
stack_name = os.environ.get('STACK_NAME')


def fetch_collection_data(url, collection_params, headers):
    try:
        LOG.info(f"Start colelction fetch {url} {collection_params} {headers}")
        collection_data = fetch_data_using_pagination(
            url, params=collection_params, headers=headers, is_collection=True)
        LOG.info(f"collection data {collection_data}")
    except Exception as err:
        err_msg = f"Error while fetching collection data {err}"
        LOG.error(err_msg)
        return False
    return collection_data

def save_data_to_all_collections(url, data, headers, list_of_collection, input_body):
    for collection_name_key, collection_sequence in list_of_collection.items():
        collection_name = input_body.get(
            collection_name_key, collection_name_key)
        try:
            response = save_data_to_collections(
                url, data, headers, collection_name, collection_sequence)
        except Exception as err:
            msg = f"Error Occured while saving data to collection {collection_name}. {err} "
            LOG.error(msg)
        else:
            LOG.info(
                f"Data Response for collection {collection_name}:{response}")


def get_data_in_seq_of_list(data, seq_list):
    list_of_final_data = []
    for item in data:
        list_of_final_data.append([item.get(seq_item, "")
                                   for seq_item in seq_list])
    return list_of_final_data


def save_data_to_collections(url, data, headers, collection_name, collection_sequence):
    collection_url = f"{url}{COLLECTION_DATA_APPEND_URL}"
    data_list = get_data_in_seq_of_list(data, collection_sequence)
    payload = {"collectionName": collection_name,
               "collectionData": data_list,
               "format": "JSON"}
    LOG.info(data_list)
    return update_data_to_api(collection_url, data=payload, headers=headers)

def get_and_parse_lookup_collection_data(url, lookupformat, headers, payload={}):
    lookupdata = {}
    for item in lookupformat:
        pairing_data = None
        try:
            current_item_value = lookupformat[item]
            if current_item_value["type"] == "collection":
                collection_name = current_item_value["name"]
                params = {"collectionName": collection_name}
                pairing_data = fetch_collection_data(url,
                                                     params, headers)
            elif current_item_value["type"] == "payload":
                LOG.info("payload type 1")
                payload_key = current_item_value["name"]
                LOG.info(f"payload type 1 {payload_key}")
                pairing_data = payload[payload_key]
                LOG.info("payload type")

            else:
                lookupdata[item] = current_item_value
                continue
        except Exception as err:
            LOG.info(
                f"Error for item {item} for lookup collection data, {err}")
        else:
            LOG.info(f"Pairing Data {pairing_data}")
            if not pairing_data:
                return False
            LOG.info(f"Pairing Data {pairing_data}")
            pairing_data = json.loads(pairing_data) if isinstance(
                pairing_data, str) else pairing_data
            lookupdata[item] = make_pairs(
                current_item_value["pairs"]["key"], current_item_value["pairs"]["value"], pairing_data)
    LOG.info(f"lookupdata {lookupdata}")
    return lookupdata


def create_error_log_item(error_item, error_key, collection_mapping):
    error_item["Error Message"] = ERROR_MESSAGES.get(
        error_key, "No Error Message Mapping Data")
    error_item["Error Column"] = error_key
    error_item["Collection"] = collection_mapping


def data_parse(payload, dataformat, lookup_data={}, collection_mapping=""):
    parsed_data = []
    error_data = []
    for item in payload:
        is_valid = True
        is_error = False
        current_item = {}
        for key in dataformat:
            try:
                keytype = dataformat[key]["type"]
                current_item_value = get_payload_value(
                    keytype, item, dataformat[key], key, lookup=lookup_data)
                if current_item_value is None:
                    current_item_value = ""
                current_item[key] = current_item_value
            except (KeyError, IndexError, TypeError) as kiterr:
                msg = f"Error occured in data parse {kiterr}"
                LOG.error(msg)
                current_item[key] = item.get(
                    dataformat[key]["key"], "")
                if not is_error:
                    is_error = True
                    create_error_log_item(
                        current_item, key, collection_mapping)
                is_valid = False
        if is_valid:
            parsed_data.append(current_item)
        else:
            error_data.append(current_item)

    return parsed_data, error_data


def unpack_request(headers_post, input_body):
    LOG.info(f"unpack request {headers_post}")
    headers = {}
    try:
        headers["Authorization"] = headers_post[auth]
        # headers["X-TenantId"] = headers_post[tenant]
        platform_url = headers_post[platform_url_header]
    except Exception as err:
        LOG.error(
            f"Error raised while unpacking the request headers and body. {err}")
    else:
        return headers, platform_url
    return None, None, None


def get_event_headers_and_payload(event):
    headers_post = event.get('headers', {})
    input_body = event['body']
    if not isinstance(input_body, dict):
        input_body = input_body.rstrip('\r\n')
        input_body = json.loads(input_body)
    LOG.info(f"Input Body {input_body}")
    return headers_post, input_body


def pre_processing_adapter(event, context):
    try:
        LOG.info(f"Event Object {event}")
        headers_post, input_body = get_event_headers_and_payload(event)
        headers, platform_url = unpack_request(headers_post, input_body)
        if not (headers or platform_url):
            err_msg = {"statusCode": 200, "body": json.dumps(
                {"msg": "Failed to extract headers and Platform url"})}
            return err_msg
        collection_name = input_body["mapping"]
        payload = input_body["data"]
        mappingdata = input_body["property"]
        if isinstance(mappingdata, str):
            LOG.info("Received Mapping Data as String")
            mappingdata = json.loads(mappingdata)
        LOG.info(f"Mapping data {type(mappingdata)},{mappingdata}")
        if not mappingdata:
            err_msg = f"Failed to fetch property file"
            LOG.error(err_msg)
            return {"statusCode": 400, "body": json.dumps({"message": err_msg})}
        collection_url = f"{platform_url}{collection_api}"
        LOG.info(f"Collection URL QA: {collection_url}")
        lookup_data = get_and_parse_lookup_collection_data(
            collection_url, mappingdata["lookup"], headers,payload=input_body)
        if not lookup_data:
            err_msg = "Failed to parse lookup data"
            LOG.error(err_msg)
            return {"statusCode": 400, "body": json.dumps({"message": err_msg})}
        parsed_data, error_data = data_parse(
            payload, mappingdata[collection_name], lookup_data=lookup_data)
        LOG.info(f"Parsed Data:{parsed_data}, Error Data:{error_data}")
        if collection_name == DEFAULT_SWIFT_TO_EKA_NAME:
            try:
                fill_total_credits_and_debits(parsed_data)
            except Exception as err:
                msg = f"Exception Occured for total credits and debits for mapping {DEFAULT_SWIFT_TO_EKA_NAME}. {err}"
                LOG.error(msg)
                return {"statusCode": 400, "body": json.dumps({"message": msg})}
        if error_data and input_body.get("EKA_ERROR_COLLECTION", False):
            save_data_to_all_collections(
                platform_url, error_data, headers, ERROR_COLUMNS, input_body)
        
        return_payload = {"eka_format_collection": parsed_data,
                          "eka_error_collection": error_data}
    except Exception as err:
        LOG.error(f"Error {err}")
        return {"statusCode": 400, "body": json.dumps({"message": f"Exception occured in preprocessing {err}"})}
    return {"statusCode": 200, "body": json.dumps({"response": return_payload})}
