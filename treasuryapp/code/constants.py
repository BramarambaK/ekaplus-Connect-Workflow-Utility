import logging
LOG = logging.getLogger()
LOG_LEVEL = "DEBUG"
LOG.setLevel(LOG_LEVEL)
PAGINATION_LIMIT = 100
property_api = f"/property"
fetch_api = f"/data"
workflow_api = "/connect/api/workflow"
fetch_workflow_api = "/connect/api/workflow/data"
collection_api = "/collection/v1"
treasuryappid = "e4ba81fc-1304-4f01-b641-7425da52a666"
workflowtask = "treasury_update_eka_format_task"
workflow_to_save_balances = "treasury_balances_update_task"
workflow_to_save_transactions = "treasury_transactions_update_task_workflow"
workflow_error_collection = "treasury_error_collection_save_task"
BALANCES_DISTINCT_KEY = 'Account Number'
DEFAULT_EKA_FORMAT_COLLECTION = "EKA_Format_Collection"
DEFAULT_EKA_BALANCES_COLLECTION = "Eka_Balances"
DEFAULT_EKA_TRANSACTIONS_COLLECTION = "EKA_Transactions"
DEFAULT_SWIFT_TO_EKA_NAME = "swifttoeka"
LOGICAL_RESOURCE_ID_TREASURY = "TreasuryPreProcessing"
COLLECTION_DATA_APPEND_URL = "/collection/v1/append/data"
ERROR_COLLECTION_NAME = "Eka Error Collection"
ERROR_MESSAGES = {
    "Transaction Type": "Transaction type code not defined in the BAI/Swift - Eka Trn type mapping lookup",
    "Value Date": "Unable to derive the value date since the funds type isn't defined in BAI Funds Type to Value Date lookup",
    "Direction": "Transaction type code not defined in the BAI/Swift - Eka Trn type mapping lookup"}
NON_WORKING_DAYS = ["sunday", "saturday"]
PROJECTION_ACTIVE_STATUS = "ACT"
PROJECTION_MATCHED_STATUS = "MAT"
PROJECTION_ROLLOVER_STATUS = "ROL"
PROJECTION_SAVE_WORKFLOW = "treasury_projections_update_task"
SUMMARY_TILES_SAVE_WORKFLOW = "treasury_summary_details_update"
SUMMARY_TILES_FETCH_WORKFLOW = "treasury_fetch_summary_tiles_data"
FX_RATES_COLLECTION_KEY = "fx_rates_collection"
ENTITY_COLLECTION_KEY = "entity_collection"
ENTITY_ACCOUNT_COLLECITON_KEY = "entity_account_collection"
EKA_TRANSACTION_COLLECTION = "EKA_TRANSACTION_COLLECTION"
EKA_FORMAT_COLLECTION = "EKA_FORMAT_COLLECTION"
EKA_BALANCES_COLLECTION = "EKA_BALANCES_COLLECTION"
PROJECTION_DATE_FORMAT = "%d-%b-%Y"
ERROR_COLUMNS = {"EKA_ERROR_COLLECTION": ["Collection", "Error Column", "Error Message",
                                          "Transaction Type", "Transaction Ref No", "Currency Code",
                                          "Transaction Amount"]}
COLLECTION_DATA_APPEND_URL = "/collection/v1/append/data"
