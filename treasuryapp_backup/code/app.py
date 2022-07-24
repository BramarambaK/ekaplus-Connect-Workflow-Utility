from datetime import datetime
import json
from logging import error
import requests
from copy import deepcopy
from utils import get_payload_value, make_pairs, fetch_data_from_api, post_data_to_api,  fetch_data_using_pagination, auto_fill_values
from utils import distinct_elements_in_list_of_dictionaries_of_key
from constants import LOG, SUMMARY_TILES_SAVE_WORKFLOW, fetch_api, property_api, collection_api
from constants import treasuryappid, workflowtask, workflow_api, workflow_to_save_balances, workflow_to_save_transactions
from constants import BALANCES_DISTINCT_KEY
from constants import COLLECTION_SEQ
from constants import COLLECTION_DATA_APPEND_URL
from constants import DEFAULT_SWIFT_TO_EKA_NAME
from constants import BALANCE_COLLECTION_SEQ
from constants import ERROR_COLUMNS
from constants import ERROR_COLLECTION_NAME
from constants import ERROR_MESSAGES
from constants import COLLECTION_MAPPING
from constants import workflow_error_collection
from constants import LOGICAL_RESOURCE_ID_TREASURY
from constants import PROJECTION_ACTIVE_STATUS
from constants import PROJECTION_MATCHED_STATUS
from constants import PROJECTION_ROLLOVER_STATUS
from constants import FX_RATES_COLLECTION_KEY
from constants import ENTITY_COLLECTION_KEY
from constants import ENTITY_ACCOUNT_COLLECITON_KEY
from constants import PROJECTION_SAVE_WORKFLOW
from constants import SUMMARY_TILES_FETCH_WORKFLOW
from constants import PROJECTION_DATE_FORMAT
from constants import fetch_workflow_api
from constants import EKA_BALANCES_COLLECTION
from constants import EKA_FORMAT_COLLECTION
from constants import EKA_TRANSACTION_COLLECTION
from constants import COLLECTION_MAPPING
from utils import fetch_account_details
from utils import fetch_FX_rates
from utils import fill_total_credits_and_debits
from utils import update_data_to_api
from utils import get_data_in_seq_of_list
from utils import fetch_HO_curreny
from utils import fetch_entity_details
import boto3
import os
# URLs and paths
security_info_endpoint = "/cac-security/api/userinfo"

# Headers
auth = 'X-AccessToken'
tenant = 'X-TenantID'
appid_key = 'appId'
get_data_method = 'method'
obj_body = 'object'
platform_url_header = 'X-PlatformUrl'
stack_name = os.environ.get('STACK_NAME')


def fetch_property_data(url, property_name, appId, headers):
    try:
        property_url = f"{url}{property_api}/{appId}/{property_name}"
        LOG.info(f"Property url {property_url}")
        property_data = fetch_data_from_api(property_url, headers=headers)
        property_data = property_data.json()
    except Exception as err:
        LOG.error(f"Error occured while fetching property file {err}")
    else:
        LOG.info(f"Property data {property_data}")
        return property_data["propertyValue"]
    return False


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


def get_and_parse_lookup_collection_data(url, lookupformat, headers):
    lookupdata = {}
    for item in lookupformat:
        try:
            current_item_value = lookupformat[item]
            if current_item_value["type"] == "collection":
                collection_name = current_item_value["name"]
                params = {"collectionName": collection_name}
                collection_data = fetch_collection_data(url,
                                                        params, headers)
            else:
                lookupdata[item] = current_item_value
                continue
        except Exception as err:
            LOG.info(
                f"Error for item {item} for lookup collection data, {err}")
        else:
            if not collection_data:
                return False
            lookupdata[item] = make_pairs(
                current_item_value["pairs"]["key"], current_item_value["pairs"]["value"], collection_data)
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
                # collection doesnt support null so converting all None Types to Empty string
                if current_item_value is None:
                    current_item_value = ""
                current_item[key] = current_item_value
            except (KeyError, IndexError, TypeError) as kiterr:
                msg = f"Error occured in data parse {kiterr}"
                LOG.error(msg)
                current_item[key] = item.get(
                    dataformat[key]["key"], "No value in Payload")
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
    headers = {}
    try:
        headers["Authorization"] = headers_post[auth]
        headers["X-TenantId"] = headers_post[tenant]
        platform_url = headers_post[platform_url_header]
        appId = input_body[appid_key]
    except Exception as err:
        LOG.error(
            f"Error raised while unpacking the request headers and body. {err}")
    else:
        return headers, appId, platform_url
    return None, None, None


# def get_event_headers_and_payload(event):
#     headers_post = event['headers']
#     input_body = event['body']
#     input_body = input_body.rstrip('\r\n')
#     LOG.info(f"Input Body {input_body}")
#     input_body = json.loads(input_body)
#     return headers_post, input_body

def get_event_headers_and_payload(event):
    headers_post = event.get('headers', {})
    input_body = event['body']
    if not isinstance(input_body, dict):
        input_body = input_body.rstrip('\r\n')
        input_body = json.loads(input_body)
    LOG.info(f"Input Body {input_body}")
    return headers_post, input_body


def validate_token(auth_token, platform_url):
    """Validate token Authentication API."""
    if platform_url is not None:
        platform_url = platform_url + security_info_endpoint
        LOG.info(f"Validate token{platform_url}")
        headers = {'Authorization': auth_token}
        response = requests.get(platform_url, headers=headers)
    else:
        response = None
        return response
    return response


def authenticate_and_validate_user(headers):
    LOG.info(f"Headers {headers}")
    auth_token = headers[auth]
    authenticate_url = headers[platform_url_header]
    info_ = f"Platform URL is : {authenticate_url}, token {auth_token}"
    LOG.info(info_)
    if authenticate_url is not None:
        response = validate_token(
            auth_token=auth_token, platform_url=authenticate_url)
        return response
    return None


def save_data_to_workflow(url, payload, headers, workflow):
    workflow_url = f"{url}{workflow_api}"
    data = {"appId": treasuryappid,
            "workflowTaskName": workflow,
            "task": workflow,
            "output": {workflow: payload}}

    LOG.info(f"{headers},{workflow_url},{data}")
    workflow_response = post_data_to_api(
        workflow_url, data=data, headers=headers)
    LOG.info(f"workflow response {workflow_response.text}")
    return workflow_response


def save_data_to_collections(url, data, headers, collection_name, collection_sequence):
    collection_url = f"{url}{COLLECTION_DATA_APPEND_URL}"
    data_list = get_data_in_seq_of_list(data, collection_sequence)
    payload = {"collectionName": collection_name,
               "collectionData": data_list,
               "format": "JSON"}
    LOG.info(data_list)
    return update_data_to_api(collection_url, data=payload, headers=headers)


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


# def create_error_log_collection_data(error_data, error_columns_list):
#     error_log_data = []
#     for item in error_data:
#         current_error_item = {}
#         for key in error_columns_list:
#             current_error_item[key] = item.get(key,"")
#         error_log_data.append(current_error_item)
#     return error_log_data


# def save_error_data_to_collection(error_data, error_columns, collection_name, error_messages):
#     pass

def get_physical_resource(logical_resource_id, stack_name):
    cfn_client = boto3.client('cloudformation')
    LOG.info("Call to get the stack resources:")
    response = cfn_client.describe_stack_resources(
        StackName=stack_name, LogicalResourceId=logical_resource_id)
    LOG.info("The stack resource to be invoked is:")
    LOG.info(response)
    for i in response['StackResources']:
        if i['LogicalResourceId'] == logical_resource_id:
            physical_resource_id = i['PhysicalResourceId']
        else:
            LOG.info("The stack does not have the child lambda function.")
            physical_resource_id = logical_resource_id
    LOG.info(
        f"The physical resource id of the function to be invoked is : {physical_resource_id}")
    return physical_resource_id


# def process_summary_data(entities, entity_accounts, transactions_data, summing_key):
#     entities_details = {}
#     for entity in entities:
#         for account in account_entity_accounts:
#             if entity in entities_details:
#                 current_entity = entities_details[entity]
#                 current_entity["accounts"].append(account["Account Number"])
#             else:
#                 entities_details[entity] = {
#                     "accounts": [account["Account Number"]]}
#         else:
#             entities_details[entity]["Currency"] = entity["currency"]

def fill_projection_amount(projection_ds, projection_status, amount):
    """
    This function sums up the value based on the projection type
    """
    if projection_status == PROJECTION_ACTIVE_STATUS:
        projection_ds[PROJECTION_ACTIVE_STATUS] += amount
    elif projection_status == PROJECTION_MATCHED_STATUS:
        projection_ds[PROJECTION_MATCHED_STATUS] += amount
    elif projection_status == PROJECTION_ROLLOVER_STATUS:
        projection_ds[PROJECTION_ROLLOVER_STATUS] += amount


def proj_currency_summing_helper(entity_currency, fx_rates, entity_proj_ds, projection):
    """
    proj means projection
    This function helps to sum the values based on the data and projection type
    """
    proj_amount = 0
    proj_status = projection["Status"]
    proj_currency = projection["Currency Code"]
    if proj_status in [PROJECTION_ROLLOVER_STATUS]:
        proj_amount = float(projection["Rolled Over Amount"])
    else:
        proj_amount = float(projection["Amount"])
    if proj_currency == entity_currency:
        fill_projection_amount(
            entity_proj_ds, proj_status, proj_amount)
    else:
        fx_rate = fx_rates[proj_currency][entity_currency]
        converted_projection_amount = fx_rate * proj_amount
        fill_projection_amount(
            entity_proj_ds, proj_status, converted_projection_amount)


def process_projections_data_for_summary_date(entities, entity_accounts, fx_rates_data, projections_data):
    """
    params:
        entites: entities fetched from the collection
        entity_accounts: fetched from the collection
        fx_rates_data: fetched from the collection
        projection_data: data that has projection details
    """

    # creating data format for below data to optimise runtime
    fx_rates = fetch_FX_rates(fx_rates_data)
    head_office_currency = fetch_HO_curreny(entities)
    entity_details = fetch_entity_details(entities)
    projection_amount_ds = {PROJECTION_ACTIVE_STATUS: 0,
                            PROJECTION_MATCHED_STATUS: 0, PROJECTION_ROLLOVER_STATUS: 0}
    entity_projection_amount = {
        key: {"value": projection_amount_ds.copy(), "Currency": entity_details[key]["Currency"]} for key in entity_details}
    entity_projection_amount.update(
        {"all": {"value": projection_amount_ds.copy(), "Currency": head_office_currency}})
    entity_projection_amount_with_date = {}
    for projection in projections_data:
        try:
            current_entity = projection["Entity"]
            projections_date = projection["Projection Date"]
            if isinstance(projections_date, (int, float)):
                projections_date /= 1000
                date_value = datetime.utcfromtimestamp(projections_date)
                projections_date = str(
                    date_value.strftime(PROJECTION_DATE_FORMAT))
            entity_currency = entity_details[current_entity]["Currency"]
            if projections_date in entity_projection_amount_with_date:
                entity_projection_ds = entity_projection_amount_with_date[projections_date]
                if current_entity in entity_projection_ds:
                    proj_currency_summing_helper(
                        entity_currency, fx_rates, entity_projection_ds[current_entity]["value"], projection)
                proj_currency_summing_helper(
                    head_office_currency, fx_rates, entity_projection_ds["all"]["value"], projection)
            else:
                entity_projection_ds = entity_projection_amount_with_date[projections_date] = deepcopy(
                    entity_projection_amount)
                if current_entity in entity_projection_ds:
                    proj_currency_summing_helper(
                        entity_currency, fx_rates, entity_projection_ds[current_entity]["value"], projection)
                proj_currency_summing_helper(
                    head_office_currency, fx_rates, entity_projection_ds["all"]["value"], projection)
        except Exception as err:
            error_msg = f"Error occured for projection {projection} Error {err}"
            LOG.error(error_msg)
    return entity_projection_amount_with_date


def transaction_summing_helper(entity_transaction_ds, accounts, transaction, fx_rates, head_office_currency):
    """
    THis function helps to sum the value based on data and currency code for transactions
    """
    account_number = transaction["Account Number"]
    transaction_currency = transaction["Currency Code"]
    transaction_amount = float(
        transaction["Transaction Amount"])
    if account_number in accounts:
        entity = accounts[account_number]["Entity"]
        entity_currency = accounts[account_number]["Currency"]
        if transaction_currency == entity_currency:
            entity_transaction_ds[entity]["value"] += transaction_amount
        else:
            fx_rate = float(fx_rates[transaction_currency][entity_currency])
            converted_transaction_amount = fx_rate * transaction_amount
            entity_transaction_ds[entity]["value"] += converted_transaction_amount
    if transaction_currency == head_office_currency:
        entity_transaction_ds["all"]["value"] += transaction_amount
    else:
        fx_rate = float(fx_rates[transaction_currency][head_office_currency])
        converted_transaction_amount = fx_rate * transaction_amount
        entity_transaction_ds["all"]["value"] += converted_transaction_amount


def process_transaction_data_for_summary_with_date(entities, entity_accounts, fx_rates_data, transactions_data):
    """
    params:
        entites: entities fetched from the collection 
        entity_accounts: fetched from the collection
        fx_rates_data: fetched from the collection 
        transaction_data: data parsed
    """
    # creating data format for below data to optimise runtime
    accounts = fetch_account_details(entity_accounts)
    fx_rates = fetch_FX_rates(fx_rates_data)
    head_office_currency = fetch_HO_curreny(entities)
    entity_transaction_amount = {key["Entity Name"]: {
        "value": 0, "Currency": key["Currency"]} for key in entities}
    entity_transaction_amount.update(
        {"all": {"value": 0, "Currency": head_office_currency}})
    entity_transaction_amount_with_date = {}
    for transaction in transactions_data:
        try:
            transactions_date = transaction["Value Date"]
            if transactions_date in entity_transaction_amount_with_date:
                entity_transaction_ds = entity_transaction_amount_with_date[transactions_date]
                transaction_summing_helper(
                    entity_transaction_ds, accounts, transaction, fx_rates, head_office_currency)
            else:
                entity_transaction_ds = entity_transaction_amount_with_date[transactions_date] = deepcopy(
                    entity_transaction_amount)
                transaction_summing_helper(
                    entity_transaction_ds, accounts, transaction, fx_rates, head_office_currency)
        except Exception as err:
            error_msg = f"Error occured for transaction {transaction} Error {err}"
            LOG.error(error_msg)
    return entity_transaction_amount_with_date


def create_summary_tiles_payload_format_for_projections(projection_amount_data):
    """
    finally after calculating the values creating a payload to update the summary
    tiles with projection values
    """
    summary_data_list = []
    for date, entity_value in projection_amount_data.items():
        for key in entity_value:
            summary_data = {}
            summary_data["Entity"] = key
            summary_data["Currency Code"] = entity_value[key]["Currency"]
            summary_data["TotalProjectionValue"] = entity_value[key]["value"][PROJECTION_ACTIVE_STATUS]
            summary_data["RollOverTransactionValue"] = entity_value[key]["value"][PROJECTION_ROLLOVER_STATUS]
            summary_data["MacthedValue"] = entity_value[key]["value"][PROJECTION_MATCHED_STATUS]
            summary_data["Date"] = date
            summary_data_list.append(summary_data)
    return summary_data_list


def create_payload_for_summary_tiles_of_projections(summary_data, projection_amount_data):
    """
    This function helps to created the payload using existing summary data and entities data 
    created from projections
    """
    for item in summary_data:
        try:
            current_summary_date = item["Date"]
            if current_summary_date in projection_amount_data:
                current_summary_entity = item["Entity"]
                prev_rollovertransvalue = item["RollOverTransactionValue"]
                prev_matchedvalue = item["MacthedValue"]
                prev_projectionvalue = item["TotalProjectionValue"]
                if current_summary_entity in projection_amount_data[current_summary_date]:
                    current_entity_ds = projection_amount_data[
                        current_summary_date][current_summary_entity]["value"]
                    current_entity_ds["ACT"] += prev_projectionvalue
                    current_entity_ds["ROL"] += prev_rollovertransvalue
                    current_entity_ds["MAT"] += prev_matchedvalue
        except Exception as err:
            error_msg = f"Error Occured for Item {item} Error {err}"
            LOG.error(error_msg)
    summary_date = create_summary_tiles_payload_format_for_projections(
        projection_amount_data)
    return summary_date


def process_projections_data(input_body, headers, appId, platform_url):
    """
    params:
        input_body: payload body received from the request
        headers: headers received from request and changed as per requirement
        platform_url: url extracted from headers and used to fetch and update data
    This function helps to process the transaction data step wise like fetching the data from 
    required collections and updating the collection/connect DB 
    """
    try:
        payload = input_body["dynamicPayload"]
        collection_url = f"{platform_url}{collection_api}"
        projection_save_response = save_data_to_workflow(
            collection_url, payload, headers, PROJECTION_SAVE_WORKFLOW)
        LOG.info(f"Projection save status {projection_save_response}")
        fx_rates_collection = input_body[FX_RATES_COLLECTION_KEY]
        entity_collection = input_body[ENTITY_COLLECTION_KEY]
        entity_account_collections = input_body[ENTITY_ACCOUNT_COLLECITON_KEY]
        fx_rates_params = {"collectionName": fx_rates_collection}
        fx_rates_data = fetch_collection_data(
            collection_url, fx_rates_params, headers)
        entity_params = {"collectionName": entity_collection}
        entity_data = fetch_collection_data(
            collection_url, entity_params, headers)
        entity_account_params = {"collectionName": entity_account_collections}
        entity_account_data = fetch_collection_data(
            collection_url, entity_account_params, headers)
        entity_projection_amount = process_projections_data_for_summary_date(
            entity_data, entity_account_data, fx_rates_data, payload)
        existing_summary_data = fetch_existing_summary_data(
            platform_url, headers)
        projection_final_payload = create_payload_for_summary_tiles_of_projections(
            existing_summary_data, entity_projection_amount)
        summary_tiles_response = save_data_to_workflow(
            platform_url, projection_final_payload, headers, SUMMARY_TILES_SAVE_WORKFLOW)
    except Exception as err:
        error_msg = f"Error Occured while processing projections data {err}"
        LOG.error(error_msg)
    else:
        return summary_tiles_response
    return None


def fetch_existing_summary_data(url, headers):
    """
    This function is used to fetch the existing summary data
    """
    fetch_url = f"{url}{fetch_workflow_api}"
    payload = {"appId": treasuryappid,
               "workFlowTask": SUMMARY_TILES_FETCH_WORKFLOW, "getAllRecords": True}
    try:
        response = requests.post(fetch_url, json=payload, headers=headers)
        summary_data = response.json()
        LOG.info(f"Summary Data {summary_data}")
    except Exception as err:
        error_msg = f"Error Occured while fetching summary data Error {err}"
        LOG.error(error_msg)
    else:
        return summary_data["data"]
    return None


def create_summary_tiles_payload_format_for_transactions(transaction_amount_data):
    """
    finally after calculating the values creating a payload to update the summary tiles with transaction values

    """
    summary_tiles_list = []
    for date, entity_value in transaction_amount_data.items():
        for key in entity_value:
            summary_tile = {}
            summary_tile["Date"] = date
            summary_tile["Currency Code"] = entity_value[key]["Currency"]
            summary_tile["TotalTransactionValue"] = entity_value[key]["value"]
            summary_tile["Entity"] = key
            summary_tiles_list.append(summary_tile)
    return summary_tiles_list


def create_payload_for_summary_tiles_of_transactions(summary_data, transaction_amount_data):
    """
    This function helps to created the payload using existing summary data and entities data 
    created from transactions
    """
    for item in summary_data:
        try:
            current_summary_date = item["Date"]
            if current_summary_date in transaction_amount_data:
                current_summary_entity = item["Entity"]
                prev_transaction_value = float(
                    item["TotalTransactionValue"])
                if current_summary_entity in transaction_amount_data[current_summary_date]:
                    transaction_amount_data[current_summary_date][current_summary_entity]["value"] += prev_transaction_value
        except Exception as err:
            error_msg = f"Error Occured for Item {item} Error {err}"
            LOG.error(error_msg)
    summary_date = create_summary_tiles_payload_format_for_transactions(
        transaction_amount_data)
    return summary_date


def process_transactions_data(input_body, transactions_data, headers, platform_url):
    """
    params:
        input_body: payload body received from the request
        transaction_data: data which has been parsed
        headers: headers received from request and changed as per requirement
        platform_url: url extracted from headers and used to fetch and update data
    This function helps to process the transaction data step wise like fetching the data from 
    required collections and updating the collection/connect DB 
    """
    try:
        collection_url = f"{platform_url}{collection_api}"
        fx_rates_collection = input_body[FX_RATES_COLLECTION_KEY]
        entity_collection = input_body[ENTITY_COLLECTION_KEY]
        entity_account_collections = input_body[ENTITY_ACCOUNT_COLLECITON_KEY]
        fx_rates_params = {"collectionName": fx_rates_collection}
        fx_rates_data = fetch_collection_data(
            collection_url, fx_rates_params, headers)
        entity_params = {"collectionName": entity_collection}
        entity_data = fetch_collection_data(
            collection_url, entity_params, headers)
        entity_account_params = {"collectionName": entity_account_collections}
        entity_account_data = fetch_collection_data(
            collection_url, entity_account_params, headers)
        transactions_amount_data = process_transaction_data_for_summary_with_date(
            entity_data, entity_account_data, fx_rates_data, transactions_data)
        existing_summary_data = fetch_existing_summary_data(
            platform_url, headers)
        summary_data = create_payload_for_summary_tiles_of_transactions(
            existing_summary_data, transactions_amount_data)
        LOG.info(f"summary data {json.dumps(summary_data)}")
        summary_response = save_data_to_workflow(
            platform_url, summary_data, headers, SUMMARY_TILES_SAVE_WORKFLOW)
    except Exception as err:
        error_msg = f"Error occured in process transaction data {err}"
        LOG.error(error_msg)
    else:
        return summary_response
    return None


def pre_processing_adapter(event, context):
    try:
        LOG.info(f"Event Object {event}")
        headers_post, input_body = get_event_headers_and_payload(event)
        headers, appId, platform_url = unpack_request(headers_post, input_body)
        if not (headers or appId or platform_url):
            err_msg = {"statusCode": 200, "body": json.dumps(
                {"msg": "Failed to extract headers, appId and Platform url"})}
            return err_msg
        if "workflowname" in input_body and input_body["workflowname"].lower() == "projections":
            projections_res = process_projections_data(
                input_body, headers, appId, platform_url)
            LOG.info(f"Projections Response {projections_res}")
            return {"statusCode": 200, "body": json.dumps({"message": "Success"})}
        collection_name = input_body["collectionName"]
        # need to change below line based the payload we get from listner
        format_collection = input_body.get(
            EKA_FORMAT_COLLECTION, EKA_FORMAT_COLLECTION)
        transaction_collection = input_body.get(
            EKA_TRANSACTION_COLLECTION, EKA_TRANSACTION_COLLECTION)
        balance_collection = input_body.get(
            EKA_BALANCES_COLLECTION, EKA_BALANCES_COLLECTION)
        collection_mapping = f"{format_collection}/{transaction_collection}/{balance_collection}"
        payload = input_body["dynamicPayload"]
        mappingdatastr = input_body["property"]
        mappingdata = json.loads(mappingdatastr)
        LOG.info(f"Mapping data {type(mappingdata)},{mappingdata}")
        if not mappingdata:
            err_msg = f"Failed to fetch property file"
            LOG.error(err_msg)
            return {"statusCode": 200, "body": json.dumps({"message": err_msg})}
        collection_url = f"{platform_url}{collection_api}"
        LOG.info(f"Collection URL QA: {collection_url}")
        lookup_data = get_and_parse_lookup_collection_data(
            collection_url, mappingdata["lookup"], headers)
        if not lookup_data:
            err_msg = "Failed to parse lookup data"
            LOG.error(err_msg)
            return {"statusCode": 200, "body": json.dumps({"message": err_msg})}
        parsed_data, error_data = data_parse(
            payload, mappingdata[collection_name], lookup_data=lookup_data, collection_mapping=collection_mapping)
        LOG.info(f"Parsed Data:{parsed_data}, Error Data:{error_data}")
        if not parsed_data:
            err_msg = "Parsed Data and Error Data is empty, No SAVE Required."
            LOG.error(err_msg)
        if collection_name == DEFAULT_SWIFT_TO_EKA_NAME:
            try:
                fill_total_credits_and_debits(parsed_data)
            except Exception as err:
                msg = f"Exception Occured for total credits and debits in {DEFAULT_SWIFT_TO_EKA_NAME}. {err}"
                LOG.error(msg)
                return {"statusCode": 200, "body": json.dumps({"message": msg})}
        workflow_res = save_data_to_workflow(
            platform_url, parsed_data, headers, workflowtask)
        if not workflow_res:
            err_msg = "Failed to save parsed data to workflow"
            LOG.error(err_msg)
        balance_items = distinct_elements_in_list_of_dictionaries_of_key(
            parsed_data, BALANCES_DISTINCT_KEY)
        LOG.info(f"balance items {balance_items}")
        workflow_save_balances = save_data_to_workflow(
            platform_url, balance_items, headers, workflow_to_save_balances)
        if not workflow_save_balances:
            err_msg = "Failed to save balances data to workflow"
            LOG.error(err_msg)
        workflow_to_transactions = save_data_to_workflow(
            platform_url, parsed_data, headers, workflow_to_save_transactions)
        if not workflow_to_transactions:
            err_msg = "Failed to save transaction data to workflow"
            LOG.error(err_msg)
        save_data_to_all_collections(
            platform_url, parsed_data, headers, COLLECTION_SEQ, input_body)
        save_data_to_all_collections(
            platform_url, balance_items, headers, BALANCE_COLLECTION_SEQ, input_body)
        if error_data:
            error_data_workflow = save_data_to_workflow(
                platform_url, error_data, headers, workflow_error_collection)
            save_data_to_all_collections(
                platform_url, error_data, headers, ERROR_COLUMNS, input_body)
        summary_response = process_transactions_data(
            input_body, parsed_data, headers, platform_url)
        LOG.info(f"Summary response {summary_response}")
    except Exception as err:
        LOG.error(f"Error {err}")
        return {"statusCode": 200, "body": json.dumps({"message": f"Exception occured in lambda handler {err}"})}
    return {"statusCode": 200, "body": json.dumps({"message": "Success"})}


def lambda_handler(event, context):
    LOG.info('RECIEVED REQUEST TO TAG SENTENCE')
    LOG.info('EVENT OBJECT')
    LOG.info(event)
    try:
        headers_post, input_body = get_event_headers_and_payload(event)
    except Exception as err:
        LOG.info(f"Error while extracting headers adn payload.{err}")
        return {"statusCode": 200, "body": json.dumps({"message": "Please check the request headers and body."})}
    try:
        authentication_response = authenticate_and_validate_user(headers_post)
        authentication_response.raise_for_status()
    except Exception as err:
        LOG.info(f"Error while validating platform token.{err}")
        err_response = {"statusCode": 200, "body": json.dumps(
            {"msg": f"Error while validating platform token.{err}"})}
        return err_response
    LOG.info(f"Platform URL is Valid!")
    try:
        payload = {"headers": headers_post, "body": input_body}
        lambda_client = boto3.client('lambda')
        physical_resource_id = get_physical_resource(
            LOGICAL_RESOURCE_ID_TREASURY, stack_name)
        status_res = lambda_client.invoke(
            FunctionName=physical_resource_id, InvocationType='Event', Payload=json.dumps(payload))
    except Exception as err:
        LOG.error(f"Error {err}")
        return {"statusCode": 200, "body": json.dumps({"message": f"Exception occured in lambda handler {err}"})}
    else:
        LOG.info(f"Pre Processor has been invoked successfully {status_res}")
    return {"statusCode": 200, "body": json.dumps({"message": "Success"})}
