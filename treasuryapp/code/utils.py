from constants import LOG, PAGINATION_LIMIT
import requests
from requests.exceptions import ConnectionError, HTTPError, RequestException, ReadTimeout
from datetime import datetime, timedelta
from constants import BALANCES_DISTINCT_KEY
from constants import NON_WORKING_DAYS
# Headers
auth = 'X-AccessToken'
tenant = 'X-TenantID'
appid = 'appId'
workflow = 'workFlowTask'
workflow_for_save = 'workflowTaskForSave'  # no need
get_data_method = 'method'
obj_header = 'X-Object'
obj_body = 'object'
platform_url_header = 'X-PlatformUrl'
device_id = 'Device-Id'
user_id_ = 'user_id'


def post_data_to_api(url, data=None, headers=None):
    try:
        response_data = requests.post(url=url, json=data, headers=headers)
    except ConnectionError as conerr:
        err = f"Connection Error occurred {conerr}"
        LOG.error(err)
    except HTTPError as httperr:
        err = f"HTTP Error occurred {httperr}"
        LOG.error(err)
    except ReadTimeout as rterr:
        err = f"Timeout error occurred  {rterr}"
        LOG.error(err)
    except RequestException as reqerr:
        err = f"Request Error occurred {reqerr}"
        LOG.error(err)
    except Exception as err:
        err = f"Error occurred {err}"
        LOG.error(err)
    else:
        return response_data
    LOG.info(err)
    return {"ERROR": err}


def update_data_to_api(url, data=None, headers=None):
    try:
        response_data = requests.put(url=url, json=data, headers=headers)
    except ConnectionError as conerr:
        err = f"Connection Error occurred {conerr}"
        LOG.error(err)
    except HTTPError as httperr:
        err = f"HTTP Error occurred {httperr}"
        LOG.error(err)
    except ReadTimeout as rterr:
        err = f"Timeout error occurred  {rterr}"
        LOG.error(err)
    except RequestException as reqerr:
        err = f"Request Error occurred {reqerr}"
        LOG.error(err)
    except Exception as err:
        err = f"Error occurred {err}"
        LOG.error(err)
    else:
        return response_data
    LOG.info(err)
    return {"ERROR": err}


def fetch_data_from_api(url, params=None, data=None, headers=None):
    try:
        response_data = requests.get(
            url=url, params=params, json=data, headers=headers)
        LOG.info(response_data.text)
        response_data.raise_for_status()
    except ConnectionError as conerr:
        err = f"Connection Error occurred {conerr}"
        LOG.error(err)
    except HTTPError as httperr:
        err = f"HTTP Error occurred {httperr}"
        LOG.error(err)
    except ReadTimeout as rterr:
        err = f"Timeout error occurred  {rterr}"
        LOG.error(err)
    except RequestException as reqerr:
        err = f"Request Error occurred {reqerr}"
        LOG.error(err)
    except Exception as err:
        err = f"Error occurred {err}"
        LOG.error(err)
    else:
        return response_data
    print(err)
    return {"ERROR": err}


def get_data_using_pagination(url, params={}, data={}, headers=None, **kwargs):
    start = kwargs.get("start") or 0
    limit = kwargs.get("limit") or PAGINATION_LIMIT
    paginated_data = []
    while True:
        data["pagination"] = {"start": start, "limit": limit}
        response_data = fetch_data_from_api(
            url, params=params, data=data, headers=headers)
        if not response_data or "ERROR" in response_data:
            break
        current_response_data = response_data.json()["data"]
        data_size = len(current_response_data)
        paginated_data += current_response_data
        if data_size != limit:
            return paginated_data
        start += data_size
        limit += data_size
        print(f"Start: {start} and Limit {limit}")
    return False


def fetch_data_using_pagination(url, params={}, data={}, headers=None, **kwargs):
    start = kwargs.get("start") or 0
    limit = kwargs.get("limit") or PAGINATION_LIMIT
    is_collection = kwargs.get("is_collection") or False
    paginated_data = []
    data = {"start": start, "limit": limit}
    if is_collection:
        params.update({"start": start, "limit": limit})
    while "ERROR" not in (response_data := fetch_data_from_api(
            url, params=params, data=data, headers=headers)):
        current_response_data = response_data.json()["data"]
        LOG.info(current_response_data)
        data_size = len(current_response_data)
        paginated_data += current_response_data
        if data_size != limit:
            return paginated_data
        start += data_size
        limit += data_size
        data["start"] = start
        data["limit"] = limit
        if is_collection:
            params["start"] = start
            params["limit"] = limit
    return False


def add_days_to_date(date_str, days, date_format, output_format):
    date_value = parse_datetime(date_str, date_format)
    while days:
        date_value = date_value+timedelta(days=1)
        weekday = date_value.strftime("%A").lower()
        if weekday not in NON_WORKING_DAYS:
            days -= 1
    date_value = datetime.strftime(date_value, output_format)
    return str(date_value)


def parse_datetime(date_str, date_format):
    if isinstance(date_str, (float, int)):
        date_str /= 1000  # converting timestamp from milliseconds to seconds
        date_value = datetime.utcfromtimestamp(date_str)
        return date_value
    date_value = datetime.strptime(date_str, date_format)
    return date_value


def format_datetime(date_value, date_format):
    date_value = datetime.strftime(date_value, date_format)
    return str(date_value)


def get_payload_value(keytype, payload, rules, key=None, lookup={}):
    return_value = ""
    try:
        if keytype == "constant":
            return_value = rules["value"]
        elif keytype == "attribute":
            value = payload[rules["key"]]
            return_value = value
        elif keytype == "truncate":
            value_ = payload[rules["key"]]
            start = rules.get("start", 0)
            end = rules.get("end", -1)
            value = value_[start:end]
            return_value = value
        elif keytype == "function":
            if rules["name"] == "lookup":
                payload_value = payload[rules["key"]]
                return_value = lookup.get(key, {})[payload_value]
            elif rules["name"] == "controlflow":
                payload_value = payload[rules["key"]]
                controldata = lookup[key]
                value_rules = controldata["data"]
                value_payload_rules = value_rules[payload_value]
                value = payload[value_payload_rules["key"]]
                LOG.info(
                    f"check 1 value{value},valuerules {value_rules} ,vpr{value_payload_rules}")
                if value_payload_rules.get("datatype", "").lower() == "date":
                    updated_date = add_days_to_date(
                        value, value_payload_rules["add"], value_payload_rules["inputformat"], value_payload_rules["outputformat"])
                    return updated_date
                elif value_payload_rules.get("datatype", "").lower() == "number":
                    LOG.info(f"check 2 value{value},vpr{value_payload_rules}")
                    multiply_number = value_payload_rules.get("product", 1)
                    LOG.info(
                        f"check 3 value{value},vpr{value_payload_rules} number {multiply_number}")
                    return value * multiply_number
                LOG.info(f"check 4 value return {value}")
                return value
        if rules.get("datatype") == "date":
            inputformat = rules.get("inputformat")
            outputformat = rules.get("outputformat")
            return_value = format_datetime(parse_datetime(
                return_value, inputformat), outputformat)
    except Exception as err:
        msg = f"Exception occured while parsing the key {key},Error: {err}"
        LOG.error(msg)
        if not rules.get("isrequired", False):
            return rules.get("default", "")
        raise
    return return_value


def convert_to_eka_format(data, policies):
    for key in policies:
        for item in data:
            try:
                item[key] = policies[key][item[key]]
            except KeyError as keyerr:
                LOG.error(
                    f"Error with key {key} in convert to eka format method. Error {keyerr}")
                continue
    return data


def auto_fill_values(data, keys):
    key_data = {}
    for item in data:
        for key in keys:
            if key in item:
                key_data[key] = item[key]
            else:
                item[key] = key_data.get(key, "")
    return data


def find_value_in_policy(policy_key, value, policies):
    try:
        print(policy_key, value, policies)
        policy_value = policies[policy_key][value]
    except KeyError as keyerr:
        LOG.error(f"Key Error occured for policy {policy_key}, Error {keyerr}")
        return {"Error": keyerr}
    except Exception as err:
        LOG.error(f"Exception occured for policy {policy_key}, Error {err}")
        return {"Error": err}
    else:
        return policy_value


def make_pairs(key, value, data):
    paired_data = {}
    for item in data:
        try:
            paired_data[item[key]] = item[value]
        except Exception as err:
            LOG.error(
                f"empty key assigned to key {key} and value {value}. {err}")
            paired_data[item[key]] = ""
    return paired_data


def distinct_elements_in_list_of_dictionaries_of_key(item_list, key):
    try:
        final_list = list({item[key]: item for item in item_list}.values())
    except Exception as err:
        LOG.info(
            f"Error while getting distinct elements from list of dictionaries.{err}")
    else:
        return final_list
    return False


def return_sum_of_keys(item_key, items_list):
    try:
        total_sum = sum(item[item_key] for item in items_list)
    except KeyError as keyerr:
        msg = f"Key Error occured while calculating sum for key {item_key}. {keyerr}"
        LOG.error(msg)
    except Exception as err:
        msg = f"Error occured while calculating sum for key {item_key}. {err}"
        LOG.error(msg)
    else:
        return total_sum
    return None


def get_data_in_seq_of_list(data, seq_list):
    list_of_final_data = []
    for item in data:
        list_of_final_data.append([item.get(seq_item, "")
                                   for seq_item in seq_list])
    return list_of_final_data


def fill_total_credits_and_debits(data_list):
    accountnumdict = {}
    for item in data_list:
        distinct_key = item[BALANCES_DISTINCT_KEY]
        if distinct_key in accountnumdict:
            if item["Direction"] == "Credit":
                account_dict["Total Credits"] += float(
                    item["Transaction Amount"])
            elif item["Direction"] == "Debit":
                account_dict["Total Debits"] += float(
                    item["Transaction Amount"])
        else:
            account_dict = accountnumdict[distinct_key] = {}
            if item["Direction"] == "Credit":
                account_dict["Total Credits"] = float(
                    item["Transaction Amount"])
                account_dict["Total Debits"] = 0
            elif item["Direction"] == "Debit":
                account_dict["Total Debits"] = float(
                    item["Transaction Amount"])
                account_dict["Total Credits"] = 0
    LOG.info(f"account number dict {accountnumdict}")
    for item in data_list:
        item.update(accountnumdict[item[BALANCES_DISTINCT_KEY]])