package com.eka.connectscheduler.constants;

import java.util.Arrays;
import java.util.List;

public interface MetaConstants {

	public static final String TYPE = "type";
	public static final String REF_TYPE = "refType";
	public static final String REF_TYPE_ID = "refTypeId";
	public static final String LOCALE= "locale";
	public static final String NAME = "name";
	public static final String TYPE_APP = "app";
	public static final String TYPE_ERROR = "error";
	public static final String TYPE_OBJECT = "object";
	public static final String TYPE_OBJECT_DATA = "objectdata";
	public static final String TYPE_POLICY = "policy";
	public static final String TYPE_MESSAGE = "message";
	public static final String TYPE_UISTATE = "uiState";
	public static final String TYPE_LISTENER = "listener";
	public static final String TYPE_LAYOUT = "layout";
	public static final String CONNECT= "connect";
	public static final String DEVICE_ID = "Device-Id";
	public static final String LAYOUT = "layout";
	public static final String TEMPLATE = "template";
	public static final String ASYNCHRONOUS_OBJECT_ID = "3d3977c0-8af1-44e2-b118-2f0ec353e4d7";

	//prepackaged collection Name
	public static final String PREPACKAGED_COLLECTION = "prepackaged_Meta";

	//Error Code constants
	public static final String DEFAULT_ERROR = "defaulterror";
	public static final String ERROR_MONGO_001 = "001";
	public static final String ERROR_MONGO_002 = "002";
	public static final String ERROR_EXECPTION_001 = "003";
	public static final String ERROR_MANDATORY = "004";
	public static final String ERROR_DATATYPE = "005";
	public static final String ERROR_LENGTH = "006";
	public static final String ERROR_EMAIL = "007";
	public static final String ERROR_PHONE = "008";
	public static final String ERROR_COMPARISON_001 = "009";
	public static final String ERROR_COMPARISON_002 = "010";
	public static final String ERROR_COMPARISON_003 = "011";
	public static final String ERROR_COMPARISON_004 = "012";
	public static final String ERROR_COMPARISON_005 = "013";
	public static final String ERROR_COMPARISON_006 = "014";
	public static final String ERROR_COMPARISON_007 = "015";
	public static final String ERROR_UNIQUE_001 = "016";
	public static final String ERROR_UNIQUE_002 = "017";
	public static final String ERROR_SCRIPT_001 = "018";
	public static final String ERROR_SCRIPT_002 = "019";
	public static final String ERROR_NOTVALID_001 = "020";
	public static final String ERROR_DATANOTVALID = "021";
	public static final String ERROR_VALIDATION = "022";
	public static final String ERROR_SERIAL_NUM = "023";
	public static final String ERROR_FORMAT_META = "024";
	public static final String ERROR_DATE_FORMAT = "025";
	public static final String ERROR_NO_OBJECT = "026";
	public static final String ERROR_JSON_CONVERSION = "027";
	public static final String ERROR_PROPERTY_TENANT= "028";
	public static final String ERROR_METHOD_TENANT= "029";
	public static final String ERROR_PROPERTY_REQUEST= "030";
	public static final String ERROR_ELSATIC_URL= "031";
	public static final String ERROR_DECODE_QUERY_STRING = "043";
	public static final String ERROR_USER_UNAUTHORIZED = "044";
	public static final String ERROR_APP_META_CALL = "034";
	public static final String ERROR_REQURL_MATCH_WITH_REFERAR_AND_HOST = "035";
	public static final String ERROR_INAPPROPRIATE_API_INVACATION = "036";
	public static final String ERROR_USER_UNAUTHORIZED_FOR_OBJECT = "037";
	public static final String ERROR_POLICY_NOT_EXISTES_FOR_APP = "038";
	public static final String ERROR_HTTP_ACTION_NOT_CONFIGURED_IN_META = "039";
	public static final String ERROR_CALL_ELASTIC_SEARCH_RESULT_DATA = "040";
	public static final String ERROR_INPUT_DATA_NODE_NOT_DEFINED = "041";
	public static final String ERROR_IN_GET_TASK_DATA_CALL = "042";
	public static final String ERROR_FILE= "032";
	public static final String ERROR_IO= "033";
	public static final String DUPLICATE_ERROR="045";
	public static final String ERROR_RESOURCE_ACCESS = "046";
	public static final String ERROR_REST_CLIENT = "047";
	public static final String ERROR_HTTPCLIENT_EXCEPTION = "048";
	public static final String ERROR_FILE_MSG1 = "063";
	public static final String ERROR_STORAGETYPE_MSG1 = "064";
	public static final String ERROR_FILE_MISSING_FILEID = "065";
	public static final String ERROR_HTTPSTATUSCODE_EXCEPTION = "066";
	public static final String ERROR_EXCEPTION_GENERAL = "067";
	public static final String ERROR_AWS_UPLOAD = "068";
	public static final String ERROR_AWSSDK_UPLOAD = "069";
	public static final String ERROR_EXCEPTION_UPLOAD = "070";
	public static final String ERROR_MULTIPART_UPLOADMSG1 = "071";
	public static final String ERROR_MULTIPART_UPLOADMSG2 = "072";
	public static final String ERROR_MULTIPART_UPLOADMSG3 = "073";
	public static final String ERROR_S3_CREATEMSG = "074";
	public static final String ERROR_AWS_DOWNLOAD = "075";
	public static final String ERROR_EXCEPTION_DOWNLOAD = "076";
	public static final String ERROR_AWS_S3_DEL = "077";
	public static final String ERROR_AWS_GENERAL_DEL = "078";
	public static final String ERROR_S3_AWSPROPERTY = "079";
	public static final String ERROR_AWS_UPDATE = "080";
	public static final String ERROR_EXCEPTION_UPDATE = "081";
	public static final String ERROR_S3_ACCESS = "082";
	public static final String ERROR_AWS_UPLOADTAGS = "083";
	public static final String ERROR_AWSSDK_UPLOADTAGS = "084";
	public static final String ERROR_EXCEPTION_UPLOADTAGS = "085";
	public static final String ERROR_AWSEXCEPTION_GENERAL = "086";
	public static final String ERROR_SDKEXCEPTION_GENERAL = "087";
	public static final String ERROR_UPLOAD_CLIENTMSG = "088";
	public static final String ERROR_DOWNLOAD_CLIENTMSG = "089";
	public static final String ERROR_FILELIST_GENERAL = "090";
	public static final String ERROR_FILEDELETE_GENERAL = "091";
	public static final String ERROR_FILESYS_UPLOAD = "092";
	public static final String ERROR_FILESYS_DOWNLOAD = "093";
	public static final String ERROR_FILESYS_CREATE = "094";
	public static final String ERROR_FILESYS_DELETE = "095";
	public static final String ERROR_UPLOAD_GENERAL = "096";
	public static final String ERROR_DOWNLOAD_GENERAL = "097";
	public static final String ERROR_DOWNLOAD_EMAIL = "098";
	public static final String ERROR_EMAIL_FROMADDR = "099";
	public static final String ERROR_EMAIL_TOADDR = "100";
	public static final String ERROR_FILEOBJ_SAVETODB = "101";
	public static final String ERROR_FILEOBJ_UPDATETODB = "102";
	public static final String ERROR_FILEBULKUPLOAD_WRITE = "103";
	public static final String ERROR_FILEBULKUPLOAD_NUMREC = "104";
	public static final String ERROR_FILEBULKUPLOAD_EXT = "105";
	public static final String ERROR_FILEBULKUPLOAD_PROCESS = "106";
	public static final String ERROR_FILEBULKUPLOAD_EMPTY = "107";
	public static final String ERROR_FILEBULKUPLOAD_NODATA = "108";
	public static final String ERROR_FILEBULKUPLOAD_PROCESS_CONTENT = "109";
	public static final String ERROR_FILEBULKUPLOAD_SAVE_CONTENT = "110";
	public static final String ERROR_FILEBULKUPLOAD_PROCESS_CONTENT2 = "111";
	public static final String ERROR_IGNITE_DROP_TABLE = "112";
	public static final String ERROR_IGNITE_INIT = "113";
	public static final String ERROR_SYSTEMBULKSAVE_PAYLOAD = "115";
	public static final String ERROR_MENUID_MISSING_IN_CONFIG = "116";
	public static final String ERROR_PAYLOAD_PARSE = "117";
	public static final String ERROR_ELASTIC_DATA_FILTER = "118";
	public static final String ERROR_USERINFO_VALUES_NULL_IN_CONTEXT = "119";
	public static final String ERROR_SERVER_NOT_ACCESSIBLE = "121";
	// Policy Specific Constants
	public static final String GLOBAL_GRANTS = "globalGrants";
	public static final String USER_GRANTS = "userGrants";
	public static final String ALLOWED_GLOBAL_ACTIONS = "allowedGlobalActions";
	public static final String ALLOWED_ITEM_LEVEL_ACTIONS = "allowedItemLevelActions";
	public static final String ITEM_LEVEL_FILTERS = "itemLevelFilters";
	public static final String OBJECTGRANTS = "objectGrants";
	public static final String KEY = "key";
	public static final String VALUE = "value";
	public static final String OPERATOR = "operator";
	public static final String EQUALS = "equals";
	public static final String NOT_EQUALS = "notequals";
	public static final String NOT_IN = "notin";

	public static final String LOGICAL_OPERATOR = "logicalOperator";
	public static final String AND = "and";
	public static final String OR = "or";
	public static final String DOT = ".";
	public static final String DOLLAR = "$";

	public static final String FIRST_FILTER = "firstFilter";
	public static final String SECOND_FILTER = "secondFilter";
	public static final String TENANT_ID = "tenantId";

	public static final String INPUT = "input";
	public static final String INITIAL_VAL = "initialValue";
	public static final String IN = "in";
	public static final String COND = "cond";
	public static final String NEW_ROOT = "newRoot";

	public static final String VALUE_AS_V = "v";
	public static final String KEY_AS_K = "k";
	public static final String ROOT = "ROOT";
	public static final String PATH = "path";
	public static final String PRESERVE_NULL_AND_EMPTY_ARRAYS = "preserveNullAndEmptyArrays";
	public static final String GRANTS_WITH_FULL_ACCESS = "grantsWithFullAccess";
	public static final String GRANTS_WITH_FILTERED_ACCESS = "grantsWithFilteredAccess";
	public static final String ITEM_ACTIONS_WITH_FULL_ACCESS = "itemActionsWithFullAccess";
	public static final String ITEM_ACTIONS_WITH_FILTERED_ACCESS = "itemActionsWithFilteredAccess";
	public static final String GLOBAL_ACTIONS = "globalActions";
	public static final String _ID = "_id";
	public static final String APP_UUID = "app_UUID";
	public static final String RESPONSE_DATA_KEY = "responseDataKey";
	public static final String FILTERS = "filters";
	public static final String ACTION = "action";
	public static final String VARS = "vars";
	public static final String DATA_ARRAY = "dataArray";
	public static final String PIPELINE = "pipeline";

	// Mongo Constants
	public static final String OP_OBJECT_TO_ARRAY = "$objectToArray";
	public static final String OP_ARRAY_TO_OBJECT = "$arrayToObject";
	public static final String OP_MERGE_OBJECTS = "$mergeObjects";
	public static final String OP_ARRAY_ELEM_AT = "$arrayElemAt";
	public static final String OP_REPLACE_ROOT = "$replaceRoot";
	public static final String OP_EQUALS = "$eq";
	public static final String OP_IF = "if";
	public static final String OP_THEN = "then";
	public static final String OP_ELSE = "else";
	public static final String OP_FROM = "from";
	public static final String OP_FIRST = "$first";
	public static final String OP_LAST = "$last";
	public static final String OP_AS = "as";
	public static final String OP_MATCH = "$match";
	public static final String OP_PROJECT = "$project";
	public static final String OP_GROUP = "$group";
	public static final String OP_NOT_EQUALS = "$ne";
	public static final String OP_EXISTS = "$exists";
	public static final String OP_IN = "$in";
	public static final String OP_PUSH = "$push";
	public static final String OP_REDACT = "$redact";
	public static final String OP_ADD_TO_SET = "$addToSet";
	public static final String OP_SET_UNION = "$setUnion";
	public static final String OP_CONCAT_ARRAYS = "$concatArrays";
	public static final String OP_CONCAT_STRINGS = "$concat";
	public static final String OP_IF_NULL = "$ifNull";
	public static final String OP_COND = "$cond";
	public static final String OP_AND = "$and";
	public static final String OP_OR = "$or";
	public static final String OP_LET = "$let";
	public static final String OP_MAP = "$map";
	public static final String OP_REDUCE = "$reduce";
	public static final String OP_FILTER = "$filter";
	public static final String OP_TO_STRING = "$toString";

	// Special Operators
	public static final String OP_THIS = "$$this";
	public static final String OP_VALUE = "$$value";
	public static final String OP_KEEP = "$$KEEP";
	public static final String OP_PRUNE = "$$PRUNE";
	public static final String TYPE_WORKFLOW = "workflow";
	public static final String TYPE_PROPERTY = "property";
	public static final String TYPE_PROCESSOR = "processor";
	public static final String TYPE_MENU_OBJECT = "menuobject";
	public static final String TYPE_CUSTOMIZED_DOCUMENT = "customizeddocument";

	// System Fields
	public static final String SYS_UUID = "sys__UUID";
	public static final String VALIDATE_ALL_GRANTS = "validateAllGrants";
	public static final String USER_UNAUTHORIZED = "User Unauthorized!";
	public static final String PLATFORM_ID = "platform_id";
	public static final String BASE_PLATFORM_ID ="base_platform_id";
	public static final String UNSUPPORTED_GRANTS = "Couldn't validation the grant(s)! Possible issues: Incorrect app setup/Incorrect grant/Validation of more than one grant not supported for the specified type.";
	public static final String PARENT_TYPE_ID = "parentTypeId";
	public static final String AUTHORIZATION = "authorization";
	public static final String IS_AUTHORIZED = "isAuthorized";

	public static final String LISTVIEW = "LISTVIEW";
	public static final List<String> POLICY_GRANTS = Arrays
			.asList(new String[] { "STD_APP_WORKFLOW_SECURITY_SETTINGS", "APPS_SECURITY" });
	public static final List<String> APP_VIEW_GRANTS = Arrays
			.asList(new String[] { "STD_APP_TILE", "DEFAULT_APP_VIEW", "SEEDED_APP_VIEW", "APPS_VIEW" });
	public static final List<String> PROCESSOR_ADMIN_GRANTS = Arrays
			.asList(new String[] { "STD_APP_WORKFLOW_PROCESSOR_SETTINGS", "APPS_EDIT" });
	public static final List<String> PROPERTY_ADMIN_GRANTS = Arrays
			.asList(new String[] { "STD_APP_WORKFLOW_PROPERTY_SETTINGS", "APPS_EDIT" });
	public static final List<String> APP_GENERAL_GRANTS = Arrays
			.asList(new String[] { "STD_APP_WORKFLOW_GENERAL_SETTINGS", "APPS_EDIT" });
	public static final List<String> MENU_ADMIN_GRANTS = Arrays
			.asList(new String[] { "STD_APP_WORKFLOW_MENU_SETTINGS", "APPS_EDIT" });
	public static final List<String> SECURITY_ADMIN_GRANTS = Arrays
			.asList(new String[] { "STD_APP_WORKFLOW_SECURITY_SETTINGS", "APPS_SECURITY" });
	public static final List<String> WORKFLOW_ADMIN_GRANTS = Arrays
			.asList(new String[] { "STD_APP_WORKFLOW_WORKFLOW_SETTINGS", "APPS_EDIT" });
	public static final List<String> OBJECT_ADMIN_GRANTS = Arrays.asList(new String[] { "ADMIN_WORKFLOW_OBJECTS_VIEW",
			"ADMIN_WORKFLOW_OBJECTS_CREATE", "ADMIN_WORKFLOW_OBJECTS_EDIT", "ADMIN_WORKFLOW_OBJECTS_DELETE" });
	public static final List<String> OBJECT_GRANTS = Arrays.asList(new String[] { "READ", "ADMIN_WORKFLOW_OBJECTS_VIEW",
			"ADMIN_WORKFLOW_OBJECTS_CREATE", "ADMIN_WORKFLOW_OBJECTS_EDIT", "ADMIN_WORKFLOW_OBJECTS_DELETE" });
	public static final List<String> OBJECT_VIEW_GRANTS = Arrays.asList(new String[] { "ADMIN_WORKFLOW_OBJECTS_VIEW",
			"STD_APP_TILE", "DEFAULT_APP_VIEW", "SEEDED_APP_VIEW", "APPS_VIEW" });
	public static final List<String> ALL_APP_GRANTS = Arrays
			.asList(new String[] { "ADMIN_WORKFLOW_LIST_OF_APPS_VIEW" });
	public static final List<String> APP_CREATE_GRANTS = Arrays.asList(new String[] { "APPS_CUSTOM_CREATE_WF_APP" });
	public static final List<String> ALL_OBJECT_GRANTS = Arrays.asList(new String[] { "ADMIN_WORKFLOW_OBJECTS_VIEW" });
	public static final List<String> APP_CUSTOM_CREATE_APP = Arrays
			.asList(new String[] { "APPS_CUSTOM_CREATE_WF_APP" });
	public static final List<String> OBJECT_CREATE_GRANT = Arrays
			.asList(new String[] { "ADMIN_WORKFLOW_OBJECTS_CREATE" });
	public static final List<String> OBJECT_DELETE_GRANTS = Arrays
			.asList(new String[] { "ADMIN_WORKFLOW_OBJECTS_DELETE" });
	public static final List<String> APP_UPDATE_GRANTS = Arrays
			.asList(new String[] { "SEEDED_WF_APP_SETT_GENERAL_VIEW", "APPS_VIEW" });
	public static final List<String> OBJECT_UPDATE_GRANTS = Arrays
			.asList(new String[] { "ADMIN_WORKFLOW_OBJECTS_EDIT" });
	public static final List<String> ALL_WORKFLOW_VIEW_GRANTS = Arrays
			.asList(new String[] { "ADMIN_WORKFLOW_LIST_OF_APPS_VIEW" });
	public static final List<String> WORKFLOW_VIEW_GRANTS = Arrays
			.asList(new String[] { "SEEDED_WF_APP_SETT_WORKFLOW_VIEW", "APPS_EDIT" });
	public static final List<String> OPEN_OBJECTS = Arrays.asList(new String[] { "USER_INFO" });

	public static final Object ACCESS_TOKEN_RESPONSE_KEY = "auth2AccessToken";
	public static final Object ACCESS_TOKEN = "access_token";
	public static final String PLATFORM_URL = "platform_url";
	public static final String PROPERTY_VALUE = "propertyValue";
	public static final String HEADER_X_PLATFORM_URL = "X-PlatformURL";
	public static final String PROPERTY_CACHE_TTL = "property_cache_ttl";
	public static final String PROPERTY_CSRF = "csrf_filter";

	public static final String REGEX_DOT = "\\.";

	public static final String AUTHENTICATED_USERS_ROLE = "AUTHENTICATED_USERS";
	public static final String PUBLIC_USERS_ROLE = "PUBLIC_USERS";

	public static final String REFERER = "Referer";
	public static final String ORIGIN = "Origin";
	public static final String HEADER_X_FORWARDED_HOST = "X-Forwarded-Host";

	
	public static final String COMMA = ",";
	public static final String HITS = "hits";
	public static final String ELASTIC_SEARCH = "elasticSearch";
	public static final String INPUT_DATA = "inputData";
	public static final String EXTERNAL_API = "externalApi";
	public static final String DEFAULT = "default";
	public static final String TOTAL = "total";
	public static final String SOURCE = "_source";
	public static final String SYSTEM_TASK = "SystemTask";

	public static final String META = "_Meta";
	public static final String DATA = "_Data";
	public static final String SERIAL_NUMBER = "serialNumber";
	public static final String OBJECT_DATA_ID = "objectDataId";
	public static final String USER_ID = "userId";
	public static final String YES = "yes";
	public static final String COPY = "copy";
	public static final String MDM = "mdm";
	public static final String OBJECT_DATA_VERSIONS = "objectDataVersions";
	public static final String ROLE_GRANTS = "roleGrants";
	public static final String ID2 = "id";
	public static final String TENANT = "tenant";
	public static final String PROPERTY_LEVEL = "propertyLevel";
	public static final String PROPERTY_NAME = "propertyName";
	public static final String TASK_ID = "taskId";
	public static final String MESSAGE = "message";
	public static final String SYS_CREATED_BY = "sys__createdBy";
	public static final String SYS_CREATED_ON = "sys__createdOn";
	public static final String SYS_UPDATED_BY = "sys__updatedBy";
	public static final String SYS_UPDATED_ON = "sys__updatedOn";
	public static final String OVERWRITE = "overwrite";
	public static final String PROCESSOR = "processor";
	public static final String OBJECT = "object";
	public static final String TRANSLATION = "translation";
	public static final String PROPERTY = "property";
	public static final String POLICY = "policy";
	public static final String WORKFLOW = "workflow";
	public static final String APP = "app";
	public static final String MENU_OBJECT = "menuObject";
	public static final String PROCESSORINFO = "processorInfo";
	public static final String LISTING_DATA_OPTIONS = "listingDataOptions";
	public static final String OPERATION = "operation";
	public static final Object FILTER_OPTIONS = "filterOptions";
	public static final String SORT = "SORT";
	public static final String FILTER = "FILTER";
	public static final String SEARCH = "SEARCH";
	public static final String DISTINCT_COLUMNS = "DISTINCTCOLUMNS";
	public static final String PAGINATION ="PAGINATION";
	public static final String REFRESH = "REFRESH";
	public static final String REQUEST_ID	="requestId";
	public static final String TRANSLATABLE_KEYS = "translatableKeys";
	public static final String NAVBAR = "navbar";
	public static final String API_MENU_DATA = "apiMenuData";
	public static final String MENU_ITEMS = "menuItems";
	public static final String ITEMS = "items";
	public static final String HANDLER = "handler";
	public static final String SOURCE_DEVICE_ID = "sourceDeviceId";
	public static final String TENANT_NAME = "tenantName";
	public static final String X_SCHEDULER  = "X-Scheduler";
	public static final String MENU_ID  = "menuId";

	public static final String FIELD_NAME= "fieldName";


}
