package com.eka.connectscheduler.constants;

import java.util.Arrays;
import java.util.List;

import org.json.JSONObject;

public final class GlobalConstants {

	private GlobalConstants() {
	}

	public static final String X_REQUEST_ID = "X-RequestId";
	public static final String COST_AMOUNT_IN_BASE_CURRENCY_UNIT_ID = "costAmountInBaseCurrencyUnitId";
	public static final String COST_AMOUNT_IN_BASE_CURRENCY = "costAmountInBaseCurrency";
	public static final String INSTRUMENT_NAME = "Instrument Name";
	public static final String MONTH_YEAR = "Month/Year";
	public static final String PRICE_UNIT = "Price Unit";
	public static final String COST_CURVE = "costCurve";
	public static final String COST_MONTH = "costMonth";
	public static final String APP_ID = "appId";
	public static final String DATA = "data";
	public static final String SERVICE_KEY = "serviceKey";
	public static final String SERVICE_KEY_PRODUCT_PRICE_UNIT = "productPriceUnit";
	public static final String DEPENDS_ON = "dependsOn";
	public static final String WORKFLOW_TASK = "workFlowTask";
	public static final String WORKFLOW_TASK_COST_ITEMS_FOR_EXISTING_ENTITY = "costitemsforexistingentity";
	public static final String PRODUCT_PRICE_UNIT = "productPriceUnit";
	public static final String KEY = "key";
	public static final String VALUE = "value";
	public static final String COST_PRICE_UNIT_ID = "costPriceUnitId";
	public static final String PERCENTAGE_OF_PRICE = "% of Price";
	public static final String RATE_TYPE_PRICE = "rateTypePrice";
	//TODO:
	public static final String Estimate_Id = "originalEstimateId";
	public static final String CONTRACT_PRICE = "contractPrice";
	public static final String COST_VALUE = "costValue";
	public static final String ITEM_QTY = "itemQty";
	public static final String PRODUCT_ID = "productId";
	public static final String CP_UNIT_ID_DISPLAY_NAME = "costPriceUnitIdDisplayName";
	public static final String ENTITY_REF_NO = "entityRefNo";
	public static final String ENTITY_ACTUAL_NO = "entityActualNo";
	public static final String DRAFT_ESTIMATE_NO = "draftEstimateNo";
	public static final String PAY_IN_CUR_ID = "payInCurId";
	public static final String COLLECTION_NAME = "collectionName";
	public static final String COLLECTION_DS_MARKET_PRICE = "DS-Market Price";
	public static final String TEMPLATE_NAME = "templateName";
	public static final String START = "start";
	public static final String LIMIT = "limit";
	public static final String COST_COMPONENT_NAME = "Cost Component Name";
	public static final String TEMPLATE_COST_COMPONENT_TO_INS_MAPPING = "CostComponentToInstrumentMapping";
	public static final String ITEM_QTY_UNIT_ID = "itemQtyUnitId";
	public static final String PHYSICAL_PRODUCT_QTY_LIST = "physicalproductquantitylist";
	public static final String QTY_CONVERSION_FACTOR = "quantityConversionFactor";
	public static final String SERVICEKEY_CURRENCYLIST = "currencylist";
	public static final String _ID = "_id";
	public static final String PLATFORM_ID = "platform_id";
	public static final String IS_WORKFLOW_APP = "isWorkFlowApp";
	public static final String WORKFLOW_TASK_NAME = "workflowTaskName";
	public static final String TASK_BULK_UPDATE_ESTIMATES = "bulkupdateestimates";
	public static final String TASK_BULK_SAVE_ESTIMATES = "bulksaveestimates";
	public static final String TASK = "task";
	public static final String APP_NAME = "appName";
	public static final String APP_NAME_COST_APP = "costapp";
	public static final String OUTPUT = "output";
	public static final String SYS_DATA_STATE = "sys__data__state";
	public static final String STATE_DELETE = "Delete";
	public static final String STATE_CREATE = "Create";
	public static final String TYPE = "type";
	public static final String REF_TYPE = "refType";
	public static final String REF_TYPE_ID = "refTypeId";
	public static final String APP_UUID = "app_UUID";
	public static final String OBJECT_DATA = "objectData";
	public static final String SYS_UUID = "sys__UUID";
	public static final String OBJECT = "object";
	public static final String OBJECT_DATA_VERSIONS = "objectDataVersions";
	public static final String GET_DELETED_DATA = "getDeletedData";
	public static final String PARAM_Y = "Y";
	public static final String PARAM_N = "N";
	public static final String ATTRIBUTES = "attributes";
	public static final String ATTRIBUTE_NAME = "attributeName";
	public static final String ATTRIBUTE_VALUE = "attributeValue";
	public static final String COST_COMPONENT_TEMPLATES = "costcomponentTemplates";
	public static final String ESTIMATE_FOR_DISPLAY_NAME = "estimateForDisplayName";
	public static final String ESTIMATE_FOR_VALUATION = "Valuation";
	public static final String ENTITY_TYPE = "entityType";
	public static final String ENTITY_TYPE_PLANNED_SHIPMENT = "Planned Shipment";
	public static final String RATE_TYPE_ABSOLUTE = "absolute";
	public static final String RATE_TYPE_RATE = "rate";
	public static final String RATE_TYPE_CURVE = "curve";
	public static final String RATE_TYPE_PERCENT_OF_PRICE = "% of Price";
	public static final String STATUS = "status";
	public static final String REMARKS = "remarks";
	public static final String MISSING_FIELDS = "missingFields";
	public static final String _500 = "500";
	public static final String COST_COMP_DISP_NAME = "costComponentDisplayName";
	public static final String FX_TO_BASE = "fxToBase";
	public static final String INC_EXPENSE = "incExpense";
	public static final String FX_TO_BASE_TYPE = "fxToBaseType";
	public static final String FX_TO_BASE_TYPE_ABSOLUTE = "Absolute";
	public static final String FX_TO_BASE_TYPE_CURVE = "Curve";
	public static final String INC_EXP_DISPLAY_NAME = "incExpenseDisplayName";
	public static final String CP_GRP_NAME_DISPLAY_NAME = "counterpartyGroupNameDisplayName";
	public static final String RATE_TYPE_PRICE_DISP_NAME = "rateTypePriceDisplayName";
	public static final String INC_EXPENSE_DISP_NAME = "incExpenseDisplayName";
	public static final String COST_COMPONENT = "costComponent";
	public static final String CP_GRP_NAME = "counterpartyGroupName";
	public static final String PARAMS = "params";
	public static final String PAYLOAD_DATA = "payLoadData";
	public static final String _TEMPORARY = "__temporary__";
	public static final String FX_RATE = "fxRate";
	public static final String CONVERSION_FACTOR = "conversionFactor";
	public static final String COMMENTS = "comments";
	public static final String QUALITY = "quality";
	public static final String PRODUCT = "product";
	public static final String SOURCE = "source";
	public static final String TARGET = "target";
	public static final String APPLICABLE_DATE = "applicableDate";
	public static final String ITEM_DETAILS = "itemDetails";
	public static final String CONTRACT_ITEM_REF_NO = "contractItemRefNo";
	public static final String PROFIT_CENTER = "profitCenter";
	public static final String QUANTITY = "quantity";
	public static final String RATE = "rate";
	public static final String COST_COMPONENT_DISPLAY_NAME = "costComponentDisplayName";
	public static final String ITEM_QTY_UNIT_DISPLAY_NAME = "itemQtyUnitDisplayName";
	public static final String FREIGHT = "freight";
	public static final String PLANNED_SHIPMENT = "Planned Shipment";
	public static final String ITEM_NO = "itemNo";
	public static final String COPY_VERSION_TRM_FLAG = "copyVersionHistory";
	public static final String ORIGINAL_MONGO_ID = "originalMongoId";
	public static final String UPLOAD_ESTIMATE_OBJECT_UUID = "6cdd3b23-84fd-42e6-91ea-3cc188127749";
	public static final String UPLOAD_STATUS = "UploadStatus";
	public static final String UPLOAD_REMARKS = "UploadRemarks";
	public static final String PROPERTY_BATCH_SIZE_IMPORT = "batch_size_import_estimates";
	public static final String STATUS_PROCESSING = "Processing";
	public static final String STATUS_PASS = "Pass";
	public static final String STATUS_ERROR = "Error";
	public static final String SYS_CREATED_ON = "sys__createdOn";
	public static final String CONTRACT_INCOTERM = "contractIncoTerm";
	public static final String VALUATION_INCOTERM = "valuationIncoTerm";
	public static final String PAYMENT_TERM = "paymentTerm";
	public static final String PROPERTY_PHYSICAL_TRADE_DETAILS_COLLECTION_NAME = "physical_trade_details_collection_name";
	public static final String COST_IMPORT_IGNORE_TRM = "cost_import_ignore_trm";
	public static final String SYS_VERSION = "sys__version";
	public static final String SYS_STARTING_VERSION = "sys__starting__version";
	public static final String BULK_UPDATE_PATH = "/data/updateBulk/{appUUID}/{objectUUID}";
	public static final int DOUBLE_PRECISION = 8;

	public static final List<String> MANDATORY_FIELDS_IN_COST_ESTIMATE = Arrays
			.asList(new String[] { "costComponentDisplayName", "estimateForDisplayName", "incExpenseDisplayName",
					"rateTypePriceDisplayName", "entityType", "fxToBaseType" });
	public static final List<String> MANDATORY_FIELDS_ABS = Arrays
			.asList(new String[] { "costValue", "costPriceUnitIdDisplayName" });
	public static final List<String> MANDATORY_FIELDS_RATE = Arrays
			.asList(new String[] { "costValue", "costPriceUnitIdDisplayName", "itemQtyUnitDisplayName", "product" });
	public static final List<String> MANDATORY_FIELDS_PERCENT_OF_PRICE = Arrays
			.asList(new String[] { "costValue", "contractPrice" });
	public static final List<String> MANDATORY_FIELDS_FOR_CURVE = Arrays.asList(new String[] { "costCurveDisplayName",
			"costMonthDisplayName", "product", "itemQtyUnitDisplayName", "applicableDate" });
	public static final List<String> MANDATORY_FIELDS_IN_GET_ALL_ESTIMATES = Arrays
			.asList(new String[] { COST_VALUE, COST_AMOUNT_IN_BASE_CURRENCY, COST_PRICE_UNIT_ID });
	public static final List<String> ALLOWED_ENTITY_TYPES = Arrays
			//TODO: .asList(new String[] { "Contract Item", "Planned Shipment", "PCS" });
			.asList(new String[] { "Contract Item"});
	public static final List<String> ALLOWED_INC_EXPENSE = Arrays.asList(new String[] { "Income", "Expense" });
	public static final List<String> ALLOWED_ESTIMATE_FOR_DISPLAY_NAME = Arrays
			.asList(new String[] { "Execution", "Valuation", "Execution & Valuation" });
	public static final List<String> ALLOWED_ESTIMATE_FOR_DISPLAY_NAME_PBS = Arrays
			.asList(new String[] { "Execution", "Execution & Valuation" });
	public static final List<String> ALLOWED_RATE_TYPE_PRICE_DISP_NAME = Arrays
			.asList(new String[] { "Absolute", "Rate", "Curve", "% of Price" });
	public static final List<String> ALLOWED_FX_TO_BASE_TYPE = Arrays.asList(new String[] { "Absolute", "Curve" });
	public static final List<String> MANDATORY_FIELDS_FX_ABSOLUTE = Arrays.asList(new String[] { "fxToBase" });
	public static final List<String> MANDATORY_FIELDS_FX_CURVE = Arrays
			.asList(new String[] { "fxValueDate", "fxRate" });
	public static final List<String> INDEPENDENT_MDM_SERVICE_KEYS = Arrays.asList(new String[] { "costcomponents",
			"CostIncExp", "corporatebusinesspartnerCombo", "productCurrencyList", "productComboDropDrown" });
	public static final List<String> DEPENDENT_MDM_SERVICE_KEYS = Arrays
			.asList(new String[] { "conversionFactor", "costCurve" });
	public static final List<String> DEPENDENT_MDM_SERVICE_KEYS_CURVE = Arrays
			.asList(new String[] { "costMonth", "curveCurrency", "costCurve", "physicalproductquantitylist" });
	public static final List<String> DEPENDENT_MDM_SERVICE_KEYS_FIRST_CALL = Arrays
			.asList(new String[] { "productPriceUnit", "physicalproductquantitylist" });
	public static final List<String> DEPENDENT_MDM_SERVICE_KEYS_PERCENT_OF_PRICE = Arrays
			.asList(new String[] { "productPriceUnit" });
	public static final List<String> PARAMS_FIELDS = Arrays.asList(new String[] { "entityType", "entityRefNo",
			"itemQty", "contractPrice", "productId", "payInCurId", "priceType", "paymentTerm", "contractIncoTerm",
			"valuationIncoTerm", "isPopUp", "showMenu", "shipmentDate", "itemQtyUnitId", "entityActualNo" });
	public static final List<String> ATTRIBUTE_NAMES_IN_ESTIMATE = Arrays
			.asList(new String[] { "contractType", "contractIncoTerm", "valuationIncoTerm", "paymentTerm" });

	public static final String[] DEPENDS_ON_FOR_COST_CURVE = { COST_COMPONENT, RATE_TYPE_PRICE };
	public static final String[] DEPENDS_ON_FOR_FX_RATE = { COST_PRICE_UNIT_ID, FX_TO_BASE_TYPE };
	public static final String[] DEPENDS_ON_FOR_CONVERSION_FACTOR = { COST_PRICE_UNIT_ID, RATE_TYPE_PRICE };
	public static final String[] DEPENDS_ON_FOR_PRODUCT_CURR_LIST = { RATE_TYPE_PRICE };
	public static final String[] DEPENDS_ON_FOR_PRODUCT_PRICE_UNIT = { PRODUCT_ID };
	public static final String[] DEPENDS_ON_FOR_ITEM_QTY_UNIT_ID = { PRODUCT_ID };
	public static final String[] DEPENDS_ON_FOR_COST_MONTH = { COST_CURVE };
	public static final String[] DEPENDS_ON_FOR_CURVE_CURRENCY = { COST_CURVE };
	public static final String[] MANDATORY_HEADERS_TRM = { "X-Forwarded-Host", "Referrer", "Origin" };

	public static final String LABEL_TO_FIELDNAME_MAPPING = "{\"Entity Item Ref No\":\"itemNo\",\"Contract Ref No\":\"entityActualNo\",\"Quantity Unit\":\"itemQtyUnitDisplayName\",\"Entity Name\":\"entityType\",\"Entity Ref No\":\"entityRefNo\",\"Cost Component Name\":\"costComponentDisplayName\",\"CP Name\":\"counterpartyGroupNameDisplayName\",\"Income/Expense\":\"incExpenseDisplayName\",\"Estimate For\":\"estimateForDisplayName\",\"Rate Type\":\"rateTypePriceDisplayName\",\"Cost Curve\":\"costCurveDisplayName\",\"Cost Month\":\"costMonthDisplayName\",\"Cost Value\":\"costValue\",\"Cost Value Unit\":\"costPriceUnitIdDisplayName\",\"Cost Currency\":\"costAmountInBaseCurrencyUnitId\",\"Contract Price\":\"contractPrice\",\"FX to Base Type\":\"fxToBaseType\",\"FX to Base Value\":\"fxToBase\",\"FX Rate\":\"fxRate\",\"FX to Position\":\"fxToPosition\",\"FX Date\":\"fxValueDate\",\"Product\":\"product\",\"Quantity\":\"itemQty\",\"Cost Estimate Original Id\":\"costEstimateOriginalId\"}";
	private static final String FIELDNAME_TO_LABEL = "{\"itemNo\":\"Entity Item Ref No\",\"entityActualNo\":\"Contract Ref No\",\"itemQty\":\"Quantity\",\"incExpenseDisplayName\":\"Income/Expense\",\"costAmountInBaseCurrencyUnitId\":\"Cost Currency\",\"product\":\"Product\",\"fxRate\":\"FX Rate\",\"costValue\":\"Cost Value\",\"entityType\":\"Entity Name\",\"fxToBaseType\":\"FX to Base Type\",\"fxToBase\":\"FX to Base Value\",\"costCurveDisplayName\":\"Cost Curve\",\"costComponentDisplayName\":\"Cost Component Name\",\"estimateForDisplayName\":\"Estimate For\",\"contractPrice\":\"Contract Price\",\"entityRefNo\":\"Entity Ref No\",\"fxToPosition\":\"FX to Position\",\"itemQtyUnitDisplayName\":\"Quantity Unit\",\"costMonthDisplayName\":\"Cost Month\",\"fxValueDate\":\"FX Date\",\"costPriceUnitIdDisplayName\":\"Cost Value Unit\",\"counterpartyGroupNameDisplayName\":\"CP Name\",\"rateTypePriceDisplayName\":\"Rate Type\",\"costEstimateOriginalId\":\"Cost Estimate Original Id\"}";
	public static final JSONObject FIELDNAME_TO_LABEL_MAPPING = new JSONObject(FIELDNAME_TO_LABEL);
	private static final String TEMP_LABEL_TO_FIELDNAME = "{\"EntityItemRefNo\":\"itemNo\",\"ContractRefNo\":\"entityActualNo\",\"QuantityUnit\":\"itemQtyUnitDisplayName\",\"EntityName\":\"entityType\",\"EntityRefNo\":\"entityRefNo\",\"CostComponentName\":\"costComponentDisplayName\",\"CPName\":\"counterpartyGroupNameDisplayName\",\"IncomeExpense\":\"incExpenseDisplayName\",\"EstimateFor\":\"estimateForDisplayName\",\"RateType\":\"rateTypePriceDisplayName\",\"CostCurve\":\"costCurveDisplayName\",\"CostMonth\":\"costMonthDisplayName\",\"CostValue\":\"costValue\",\"CostValueUnit\":\"costPriceUnitIdDisplayName\",\"CostCurrency\":\"costAmountInBaseCurrencyUnitId\",\"ContractPrice\":\"contractPrice\",\"FXtoBaseType\":\"fxToBaseType\",\"FXtoBaseValue\":\"fxToBase\",\"FXRate\":\"fxRate\",\"FXtoPosition\":\"fxToPosition\",\"FXDate\":\"fxValueDate\",\"Product\":\"product\",\"ApplicableDate\":\"applicableDate\",\"Quantity\":\"itemQty\",\"costEstimateOriginalId\":\"originalEstimateId\"}";
	public static final JSONObject TEMP_LABEL_TO_FIELD_MAPPING = new JSONObject(TEMP_LABEL_TO_FIELDNAME);
	private static final String TEMP_FIELDNAME_TO_LABEL = "{\"itemNo\":\"EntityItemRefNo\",\"entityActualNo\":\"ContractRefNo\",\"itemQty\":\"Quantity\",\"incExpenseDisplayName\":\"IncomeExpense\",\"costAmountInBaseCurrencyUnitId\":\"CostCurrency\",\"product\":\"Product\",\"fxRate\":\"FXRate\",\"costValue\":\"CostValue\",\"entityType\":\"EntityName\",\"fxToBaseType\":\"FXtoBaseType\",\"fxToBase\":\"FXtoBaseValue\",\"costCurveDisplayName\":\"CostCurve\",\"costComponentDisplayName\":\"CostComponentName\",\"estimateForDisplayName\":\"EstimateFor\",\"contractPrice\":\"ContractPrice\",\"entityRefNo\":\"EntityRefNo\",\"fxToPosition\":\"FXtoPosition\",\"itemQtyUnitDisplayName\":\"QuantityUnit\",\"costMonthDisplayName\":\"CostMonth\",\"fxValueDate\":\"FXDate\",\"costPriceUnitIdDisplayName\":\"CostValueUnit\",\"counterpartyGroupNameDisplayName\":\"CPName\",\"rateTypePriceDisplayName\":\"RateType\",\"status\":\"UploadStatus\",\"applicableDate\":\"ApplicableDate\",\"remarks\":\"UploadRemarks\",\"originalEstimateId\":\"costEstimateOriginalId\"}";
	public static final JSONObject TEMP_FIELDNAME_TO_LABEL_MAPPING = new JSONObject(TEMP_FIELDNAME_TO_LABEL);
	public static final String X_TENANT_ID = "X-TenantID";
	public static final String TENANT_NAME = "tenantName";
	public static final String APPLICATION_JSON = "application/json";
	public static final String HEADERS = "headers";

	//Scheduler Constants
	public static final String SCHEDULED_WORKFLOW = "scheduledWorkFlow";
	public static final String SCHEDULER_CONFIG = "schedulerConfig";
	public static final String CRON_EXPRESSION = "cronExpression";
	public static final String CRON_EXPRESSION_EVERY_HOUR = "0 0 * ? * * *";
	public static final String IS_IMMEDIATE_EXECUTION_REQUIRED = "isImmediateExecutionRequired";

	public static final String REGEX_DOT = "\\.";
	public static final String REQUEST_ID = "requestId";
	public static final String HEADER_X_LOCALE = "X-Locale";
	public static final String SOURCE_DEVICE_ID = "sourceDeviceId";
}
