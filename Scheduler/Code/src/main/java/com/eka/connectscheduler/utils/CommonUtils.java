package com.eka.connectscheduler.utils;

import static com.eka.connectscheduler.constants.MetaConstants.COMMA;
import static com.eka.connectscheduler.constants.ParametersConstants.OBJECT;
import static com.eka.connectscheduler.constants.ParametersConstants.STATE_DELETE;
import static com.eka.connectscheduler.constants.ParametersConstants.SYS_DATA_STATE;
import static com.eka.connectscheduler.constants.ParametersConstants.SYS_IS_DELETED;
import static com.eka.connectscheduler.constants.ParametersConstants._ID;
import static com.eka.connectscheduler.constants.WorkflowConstants.FILTER_BY;
import static com.eka.connectscheduler.constants.WorkflowConstants.GET_INITIAL_DATA;
import static com.eka.connectscheduler.constants.WorkflowConstants.LAYOUT;
import static com.eka.connectscheduler.constants.WorkflowConstants.PARAMS;
import static com.eka.connectscheduler.constants.WorkflowConstants.PAY_LOAD_DATA;
import static com.eka.connectscheduler.constants.WorkflowConstants.SESSION_DATA;
import static com.eka.connectscheduler.constants.WorkflowConstants.STATIC_PARAMS;
import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.bson.Document;
import org.bson.json.JsonMode;
import org.bson.json.JsonWriterSettings;
import org.json.JSONArray;
import org.json.JSONObject;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.mongodb.core.DocumentCallbackHandler;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import com.eka.connectscheduler.constants.MetaConstants;
import com.eka.connectscheduler.constants.NiFiConstants;
import com.eka.connectscheduler.constants.ParametersConstants;
import com.eka.connectscheduler.error.ConnectError;
import com.eka.connectscheduler.exception.ConnectException;
import com.eka.connectscheduler.pojo.FilterData;
import com.eka.connectscheduler.pojo.MongoOperations;
//import com.eka.connectscheduler.pojo.FilterData;
//import com.eka.ekaconnect.model.FileModel;
//import com.eka.ekaconnect.model.SupplierDocument;
//import com.eka.ekaconnect.service.ApplicationObjectMetaService;
//import com.eka.ekaconnect.service.GenericService;
//import com.eka.ekaconnect.service.PropertyService;
//import com.eka.ekaconnect.service.WorkflowExecutorHelper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.jayway.jsonpath.JsonPath;
import com.mongodb.MongoException;

@Component("commonUtils")
public class CommonUtils {

	@Autowired
	ContextProvider contextProvider;

	@Autowired
	TranslatorProvider translatorProvider;

	@Autowired
	MongoTemplate mongoTemplate;

	final static Logger logger = ESAPI.getLogger(CommonUtils.class);

	@Value("${eka_connect_host}")
	String ekaConnectHost;

	public void checkIsDeletedData(String name, String refTypeId, String id, String propertyLevel) {
		try {
			Query query = null;
			if (id != null) {
				query = new Query(Criteria.where(_ID).is(id));
			} else if (Objects.nonNull(propertyLevel) && Objects.isNull(refTypeId)) {
				query = new Query(Criteria.where("propertyName").is(name).and("propertyLevel").is(propertyLevel));
			} else if (Objects.nonNull(propertyLevel) && Objects.nonNull(refTypeId)) {
				query = new Query(Criteria.where("propertyName").is(name).and("refTypeId").is(refTypeId)
						.and("propertyLevel").is(propertyLevel));
			} else if (Objects.isNull(propertyLevel) && Objects.nonNull(refTypeId)) {
				query = new Query(Criteria.where("propertyName").is(name).and("refTypeId").is(refTypeId)
						.and("propertyLevel").is("app"));
			} else {
				query = new Query(Criteria.where("refTypeId").is(refTypeId).and(OBJECT).is(name));
			}

			List<Object> result = executeQuery(query, resovleCollectionDataName());
			if (result.size() == 0) {
				throw new ConnectException(MetaConstants.ERROR_NO_OBJECT, MetaConstants.CONNECT);
			}
		} catch (Exception e) {
			logger.error(Logger.EVENT_FAILURE,
					("error inside method checkIsDeletedData due to:" + e.getLocalizedMessage()));
			throw new ConnectException(MetaConstants.ERROR_NO_OBJECT, MetaConstants.CONNECT);
		}
	}

	public ConnectError generateConnectError(String errorCode, String errorContext, Object... replacementFields) {
		String appName = contextProvider.getCurrentContext().getAppName();
		// String
		// message=applicationObjectMetaService.getConnectErrorMessage(errorCode,null==appName?MetaConstants.CONNECT:appName,replacementFields);
		String message = errorContext;
		ConnectError myConnectError = new ConnectError(errorCode, message, errorContext);
		return myConnectError;
	}

	public String getTranslatedMessage(String error) {
		String translatedError = translatorProvider.getTranslatedMessage(error);
		translatedError = translatedError == null ? error : translatedError;
		return translatedError;
	}

	public List<Object> executeQuery(Query query, String collectionDataName) {
		List<Object> obj = new ArrayList<Object>();
		// Settings for Object Id and ISODate fields in Bson document.Any other
		// converters need to be added here
		JsonWriterSettings writerSettings = createJSONWriterObject();
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		mongoTemplate.executeQuery(query, collectionDataName, new DocumentCallbackHandler() {
			@Override
			public void processDocument(Document document) throws MongoException, DataAccessException {
				try {
					if (!((document.containsKey(SYS_IS_DELETED) && document.get(SYS_IS_DELETED).equals(true))
							|| (document.containsKey(SYS_DATA_STATE)
									&& document.get(SYS_DATA_STATE).equals(STATE_DELETE)))) {
						obj.add(mapper.readValue(document.toJson(writerSettings), Object.class));
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});

		return obj;
	}

	/**
	 * This method will convert saved data object into JSON.
	 * 
	 * @param dataObject
	 * @return
	 */
	public static Object parseDataObjectToJSON(Document dataObject) {
		// TODO Auto-generated method stub
		JsonWriterSettings writerSettings = createJSONWriterObject();
		try {
			Map<String, Object> mapObj = new JSONObject(dataObject.toJson(writerSettings)).toMap();
			return mapObj;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ConnectException(MetaConstants.ERROR_JSON_CONVERSION, MetaConstants.CONNECT);
		}
	}

	public static JsonWriterSettings createJSONWriterObject() {
		// TODO Auto-generated method stub
		return JsonWriterSettings.builder().outputMode(JsonMode.SHELL)
				.objectIdConverter((value, writer) -> writer.writeString(value.toString()))
				.dateTimeConverter((value, writer) -> writer.writeNumber(value.toString()))
				.int64Converter((value, writer) -> writer.writeNumber(value.toString())).build();
	}

	public static String GenerateUUID() {
		return UUID.randomUUID().toString();
	}

	private String resovleCollectionDataName() {
		return contextProvider.getCurrentContext().getTenantID() + "_Data";
	}

	public String resovleCollectionMetaName() {
		return contextProvider.getCurrentContext().getTenantID() + "_Meta";
	}

	public void parseAPIInfo(JSONObject apiInfo, Map<String, Object> payload) {

		if (null == apiInfo)
			return;

		parseJsonArrayValues(apiInfo.optJSONArray(NiFiConstants.PATH_PARAMS), payload);
		parseJsonObjectValues(apiInfo.optJSONObject(NiFiConstants.QUERY_PARAMS), payload);
		parseJsonObjectValues(apiInfo.optJSONObject(NiFiConstants.BODY), payload);
	}

	private void parseJsonObjectValues(JSONObject json, Map<String, Object> payload) {

		if (null != json) {
			for (Iterator<String> iterator = json.keys(); iterator.hasNext();) {
				String key = iterator.next();
				Object value = json.get(key);
				if (value instanceof JSONObject) {
					parseJsonObjectValues((JSONObject) value, payload);
				} else if (value instanceof JSONArray) {
					parseJsonArrayValues((JSONArray) value, payload);
				} else if (value instanceof String) {
					String valueStr = String.valueOf(value);
					if (StringUtils.startsWithIgnoreCase(valueStr, "##user.")) {
						json.put(key, parseCurrentUserInfoExpr(valueStr));
					} else if (StringUtils.startsWithIgnoreCase(valueStr, "##output.") && null != payload) {
						json.put(key, parseInfoFromResponseExpr(valueStr, payload));
					}
				}
			}
		}

	}

	private void parseJsonArrayValues(JSONArray array, Map<String, Object> payload) {

		if (null == array)
			return;

		for (int i = 0; i < array.length(); i++) {
			if (Objects.nonNull(array.get(i))) {
				Object value = array.get(i);
				if (value instanceof JSONObject) {
					parseJsonObjectValues((JSONObject) value, payload);
				} else if (value instanceof JSONArray) {
					parseJsonArrayValues((JSONArray) value, payload);
				} else if (value instanceof String) {
					String valueStr = String.valueOf(value);
					if (StringUtils.startsWithIgnoreCase(valueStr, "##user.")) {
						array.put(i, parseCurrentUserInfoExpr(valueStr));
					} else if (StringUtils.startsWithIgnoreCase(valueStr, "##output.") && null != payload) {
						array.put(i, parseInfoFromResponseExpr(valueStr, payload));
					}
				}
			}
		}
	}

	private static Object parseInfoFromResponseExpr(String expression, Map<String, Object> payload) {

		String keyPath = expression.split("##output.")[1];
		if ("*".equals(keyPath)) {
			return payload;
		}
		try {
			Object parsedValue = JsonPath.parse(payload).read(MetaConstants.DOLLAR + MetaConstants.DOT + keyPath,
					Object.class);

			if (parsedValue instanceof List) {
				return ((List<Object>) parsedValue).stream().map(String::valueOf).collect(Collectors.joining(COMMA));
			}
			return parsedValue;
		} catch (Exception ex) {
			// TODO:: Logging of error
			return expression;
		}
	}

	private String parseCurrentUserInfoExpr(String pathParam) {

		switch (pathParam.toLowerCase()) {
		case "##user.cp":
			return contextProvider.getCurrentContext().getUserInfo().getBusinessPartys().stream()
					.collect(Collectors.joining(COMMA));
		case "##user.roleids":
			return contextProvider.getCurrentContext().getUserInfo().getRoleIds().stream()
					.collect(Collectors.joining(COMMA));
		case "##user.id":
			return contextProvider.getCurrentContext().getUserInfo().getUserId();
		case "##user.name":
			return contextProvider.getCurrentContext().getUserInfo().getUserName();
		default:
			return pathParam;
		}
	}

	// public String replaceWithPropValues(String propertyWIthDollar, String
	// refTypeId) {
	// refTypeId = Objects.nonNull(refTypeId) ? refTypeId :
	// contextProvider.getCurrentContext().getAppUUID();
	// while (propertyWIthDollar.contains("${")) {
	// String propertyToBeSubstituted =
	// StringUtils.substringBetween(propertyWIthDollar, "${", "}");
	// propertyWIthDollar = propertyWIthDollar.replace("${" +
	// propertyToBeSubstituted + "}",
	// getPropValueForApp(propertyToBeSubstituted, refTypeId));
	// }
	// return propertyWIthDollar;
	// }

	public static void checkPropertyInObject(Map<String, Object> jsonObject, String... propertiesToCheck) {
		for (String propertyToCheck : propertiesToCheck) {
			if (!jsonObject.containsKey(propertyToCheck)) {
				throw new ConnectException(propertyToCheck + " property is not avaliable in request.");
			}
		}
	}

	/**
	 * This method is used to get URI for connect UI host
	 * 
	 * @param path
	 * @param connectUiHost
	 * @return
	 */
	public static URI constructConnectHostUIURI(String path, String connectUiHost) {

		return UriComponentsBuilder.fromHttpUrl(connectUiHost).path(path).build().toUri();
	}

	public static HttpHeaders getDefaultHeaders() {

		HttpHeaders defaultHeaders = new HttpHeaders();
		defaultHeaders.add(CONTENT_TYPE, APPLICATION_JSON_UTF8_VALUE);
		defaultHeaders.add(ACCEPT, APPLICATION_JSON_UTF8_VALUE);
		return defaultHeaders;
	}

	public static HttpHeaders appendDynamicHeaders(HttpServletRequest request, HttpHeaders headers,
			String... headerNames) {

		if (headerNames == null)
			return headers;

		if (null == headers)
			headers = new HttpHeaders();

		for (String headerName : headerNames) {
			headers.add(headerName, request.getHeader(headerName));
		}
		return headers;
	}

	public static JSONObject appendDynamicHeaders(HttpServletRequest request, JSONObject existingHeaders,
			String... headerNames) {

		if (headerNames == null)
			return existingHeaders;

		if (null == existingHeaders)
			existingHeaders = new JSONObject();

		for (String headerName : headerNames) {
			existingHeaders.put(headerName, request.getHeader(headerName));
		}

		return existingHeaders;
	}

	public static List<ConnectError> createConnectError(ConnectException connectException) {
		List<ConnectError> errors = null;
		ConnectError error = new ConnectError(connectException);
		if (null == errors)
			errors = new ArrayList<>();
		errors.add(error);
		return errors;
	}

	public static HttpHeaders getHeadersFromRequest(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeaders();
		Enumeration<String> headerNames = request.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String headerName = headerNames.nextElement();
			headers.add(headerName, request.getHeader(headerName));
		}
		return headers;
	}

	public static String shortUUID(String uuid, String... tennantId) {
		String uuidTennatId = null;
		StringBuilder uuidTennatIdBuilder = new StringBuilder(uuid);
		for (String string : tennantId) {
			if (string != null)
				uuidTennatIdBuilder.append(string);
		}
		uuidTennatId = uuidTennatIdBuilder.toString();
		String appIdTennantIdUUID = UUID.nameUUIDFromBytes(uuidTennatId.getBytes()).toString();
		return Long.toString(ByteBuffer.wrap(appIdTennantIdUUID.getBytes()).getLong(), Character.MAX_RADIX);
	}

	public static boolean isIntegerString(String strNum) {
		if (strNum == null) {
			return false;
		}
		try {
			Integer.parseInt(strNum);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}

	/**
	 * This method will remove the sensitive keys from the Map.
	 * 
	 * @param toBeRemovedKeys
	 * @param taskInfo
	 */
	public static void removeSensitiveKeys(Map<String, Object> taskInfo, String... toBeRemovedKeys) {

		if (toBeRemovedKeys != null && toBeRemovedKeys.length > 0 && taskInfo != null) {
			for (String key : toBeRemovedKeys) {
				taskInfo.remove(key);

			}
		}

	}

	public Map<String, Object> constructQueryParamsAsPerLayoutConf(JSONObject payload, Map<String, Object> taskInfo,
			Map<String, Object> queryParams) {
		if (taskInfo.containsKey(LAYOUT) && ((HashMap) taskInfo.get(LAYOUT)).containsKey(GET_INITIAL_DATA)
				&& ((HashMap) taskInfo.get(LAYOUT)).get(GET_INITIAL_DATA) instanceof Map
				&& ((HashMap) ((HashMap) taskInfo.get(LAYOUT)).get(GET_INITIAL_DATA)).containsKey(FILTER_BY)) {
			HashMap filterByOption = (HashMap) ((HashMap) ((HashMap) taskInfo.get(LAYOUT)).get(GET_INITIAL_DATA))
					.get(FILTER_BY);
			String[] filterByTypes = { PAY_LOAD_DATA, SESSION_DATA, PARAMS };
			queryParams = constructQueryParamsAsPerFilterByTypes(payload, queryParams, filterByOption, filterByTypes);
			if (filterByOption.containsKey(STATIC_PARAMS)) {
				Map<String, Object> staticParamObj = (HashMap<String, Object>) filterByOption.get(STATIC_PARAMS);
				for (Iterator<String> iterator = staticParamObj.keySet().iterator(); iterator.hasNext();) {
					String key = iterator.next();
					queryParams.put(key, staticParamObj.get(key).toString());
				}
			}
		}
		return queryParams;
	}

	private Map<String, Object> constructQueryParamsAsPerFilterByTypes(JSONObject requestBody,
			Map<String, Object> queryParams, HashMap filterByOption, String[] filterByTypes) {
		queryParams = new HashMap<>();
		for (int i = 0; i < filterByTypes.length; i++) {
			String filterByType = filterByTypes[i];
			if (filterByOption.containsKey(filterByType) && requestBody.has(filterByType)) {
				ArrayList fieldsToFilter = (ArrayList) filterByOption.get(filterByType);
				JSONArray fieldsOfReqBody = null;
				Object o = requestBody.get(filterByType);
				if (o instanceof JSONArray)
					fieldsOfReqBody = (JSONArray) o;
				else if (o instanceof JSONObject) {
					fieldsOfReqBody = new JSONArray().put((JSONObject) o);
				}

				Iterator<String> iter = fieldsToFilter.iterator();
				while (iter.hasNext()) {
					String key = iter.next();
					List<Object> array = new ArrayList<>();
					for (int j = 0; j < fieldsOfReqBody.length(); j++) {
						JSONObject record = fieldsOfReqBody.getJSONObject(j);
						if (record.has(key) && Objects.nonNull(record.get(key))) {
							array.add(record.get(key));
						}
					}
					queryParams.put(key, array);
				}
			}
		}
		return queryParams;
	}

	public static String convertExceptionStackTraceToString(Exception e) {

		if (e != null) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			String sStackTrace = sw.toString(); // stack trace as a string
			return sStackTrace;
		}
		return null;
	}

	public static void main(String[] args) {
		try {
			Class.forName("s.d.d.d.d");
		} catch (Exception e) {
			logger.error(Logger.EVENT_FAILURE, convertExceptionStackTraceToString(e));
		}
	}

	public static Map<String, Object> extractQueryParams(String queryString) {
		Map<String, Object> query_pairs = new HashMap<String, Object>();
		try {
			if (queryString != null) {
				String[] pairs = queryString.split("&");
				for (String pair : pairs) {
					int idx = pair.indexOf("=");
					query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"),
							URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
				}
			}
		} catch (UnsupportedEncodingException e) {
			// logger.error("could not decode query string "+queryString,e);
			throw new ConnectException(MetaConstants.ERROR_DECODE_QUERY_STRING, MetaConstants.CONNECT);
		} catch (Exception e) {
			// logger.error("could not decode query string "+queryString,e);
			throw new ConnectException(MetaConstants.ERROR_DECODE_QUERY_STRING, MetaConstants.CONNECT);
		}
		return query_pairs;
	}

	public List<String> getMissingFieldsInObject(Map<String, Object> data, List<String> fields) {
		List<String> missingFields = new ArrayList<>();
		Iterator<String> fieldsIterator = fields.iterator();
		while (fieldsIterator.hasNext()) {
			String field = fieldsIterator.next();
			if (!data.containsKey(field) || org.springframework.util.StringUtils.isEmpty(data.get(field)))
				missingFields.add(field);
		}
		return missingFields;
	}

	/**
	 * Usage: This method returns the error message against the error code from
	 * mongo
	 * 
	 * @param errorCode
	 * @param category
	 * @return errorMessage
	 */
	public String getErrorMessage(String errorCode, String category) {
		// String errorMessage =
		// applicationObjectMetaService.getError(errorCode,category);
		String errorMessage = "Connect Error : " + category;
		return errorMessage;
	}

	/**
	 * Usage: This method returns the list of version documents of a data item
	 * 
	 * @param appUUID   the app uuid
	 * @param refTypeId the sys__UUID of data item whose versions are fetched
	 * @return List<JSONObject>
	 * @throws MongoDBQueryException
	 */
	// public List<JSONObject> getDataObjectVersions(String appUUID, String
	// refTypeId) throws MongoDBQueryException {
	// MongoDBQuery findVersionDocsQuery = new MongoDBQuery();
	// findVersionDocsQuery.where(ConditionEnum.eq, MetaConstants.TYPE,
	// MetaConstants.OBJECT_DATA_VERSIONS);
	// findVersionDocsQuery.and(ConditionEnum.eq, REF_TYPE,
	// ParametersConstants.OBJECT_DATA);
	// findVersionDocsQuery.and(ConditionEnum.eq, MetaConstants.APP_UUID, appUUID);
	// findVersionDocsQuery.and(ConditionEnum.eq, REF_TYPE_ID, refTypeId);
	// findVersionDocsQuery.setProjection(ProjectionEnum.EXCLUSION, "_id");
	// return MongoQueryFactory.getInstance().find(resovleCollectionDataName(),
	// findVersionDocsQuery);
	// }

	public String injectedFieldValueToScriptToEvaluateIsMandatoryScript(Map<String, Object> inputData,
			String defaultValExpression, List<ConnectError> myListConnectError, String metaField) {
		do {
			int indexOfStartOfField = defaultValExpression.indexOf("$");
			int indexOfEndOfField = 0;
			for (int i = indexOfStartOfField; i < defaultValExpression.length(); i++) {
				if (defaultValExpression.charAt(i + 1) == '}') {
					indexOfEndOfField = i;
					break;
				}
			}
			Object fieldValue = null;
			Object fieldName = defaultValExpression.substring(indexOfStartOfField + 2, indexOfEndOfField + 1);
			if (inputData.containsKey(fieldName)) {
				fieldValue = inputData.get(fieldName);
			}
			defaultValExpression = defaultValExpression.substring(0, indexOfStartOfField) + fieldValue
					+ defaultValExpression.substring(indexOfEndOfField + 2, defaultValExpression.length());
		} while (defaultValExpression.contains("$"));
		return defaultValExpression;
	}

	public String injectedFieldValueToScript(Map<String, Object> inputData, String defaultValExpression,
			List<ConnectError> myListConnectError, String metaField) {
		String message = null;
		do {
			int indexOfStartOfField = defaultValExpression.indexOf("$");
			int indexOfEndOfField = 0;
			for (int i = indexOfStartOfField; i < defaultValExpression.length(); i++) {
				if (defaultValExpression.charAt(i + 1) == '}') {
					indexOfEndOfField = i;
					break;
				}
			}
			Object fieldName = defaultValExpression.substring(indexOfStartOfField + 2, indexOfEndOfField + 1);
			if (!inputData.containsKey(fieldName)) {
				myListConnectError.add(
						generateConnectError(MetaConstants.ERROR_SCRIPT_002, "{field:" + metaField + "}", fieldName));
			}
			Object fieldValue = inputData.get(fieldName);
			defaultValExpression = defaultValExpression.substring(0, indexOfStartOfField) + fieldValue
					+ defaultValExpression.substring(indexOfEndOfField + 2, defaultValExpression.length());
		} while (defaultValExpression.contains("$"));
		return defaultValExpression;
	}

	public Object scriptExecutor(String fun) {
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine engine = manager.getEngineByName("JavaScript");
		Invocable inv = (Invocable) engine;
		try {
			engine.eval(fun);
			return inv.invokeFunction("evalFunc");
		} catch (NoSuchMethodException | ScriptException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @param objectMeta
	 * @param inputData
	 * @return
	 */
	public Map<String, Object> getUniqueFieldsMap(Map<String, Object> objectMeta, Map<String, Object> inputData) {
		ArrayList uniqueFieldsMeta = (ArrayList) objectMeta.get(ParametersConstants.UNIQUE_FIELDS);
		Map<String, Object> uniqueFieldsQueryMap = new HashMap<String, Object>();
		if (!(Objects.isNull(uniqueFieldsMeta) || uniqueFieldsMeta.isEmpty())) {
			Iterator<Object> uniqFieldIter = uniqueFieldsMeta.iterator();
			while (uniqFieldIter.hasNext()) {
				String uniqueFieldName = uniqFieldIter.next().toString();
				if ((Objects.isNull(inputData.get(uniqueFieldName))
						|| org.springframework.util.StringUtils.isEmpty(inputData.get(uniqueFieldName)))) {
					List<ConnectError> myListConnectError = new ArrayList<ConnectError>();
					myListConnectError.add(generateConnectError(MetaConstants.ERROR_UNIQUE_001, null, uniqueFieldName));
					throw new ConnectException(myListConnectError);
				} else {
					uniqueFieldsQueryMap.put(uniqueFieldName, inputData.get(uniqueFieldName).toString());
				}
			}
		}
		return uniqueFieldsQueryMap;
	}

	public static void mergeSourceObjectToTarget(JSONObject source, JSONObject target) {
		if (source != null && target != null) {
			String[] names = JSONObject.getNames(source);
			for (String key : names) {
				target.put(key, source.get(key));
			}
		}
	}

	// Adding below method as upload api requires content-type as multipart/form
	// data from postman
	// once the File is passed we need to make content-type as application/json for
	// trm upload and for property api
	public HttpHeaders getHttpHeaderWithContentType() {

		HttpHeaders headers = new HttpHeaders();

		HttpServletRequest httpRequest = contextProvider.getCurrentContext().getRequest();

		Enumeration<?> names = httpRequest.getHeaderNames();

		while (names.hasMoreElements()) {

			String name = (String) names.nextElement();
			headers.add(name, httpRequest.getHeader(name));
		}
		addContentTypeToHeaders(headers);
		return headers;

	}

	// Adding below method as upload api requires content-type as multipart/form
	// data from postman
	// once the File is passed we need to make content-type as application/json for
	// trm upload and for property api
	private void addContentTypeToHeaders(HttpHeaders headers) {
		// TODO Auto-generated method stub
		headers.remove("Content-Type");
		headers.setContentType(MediaType.APPLICATION_JSON);

	}

	public HttpHeaders getHttpHeader() {

		HttpHeaders headers = new HttpHeaders();

		HttpServletRequest httpRequest = contextProvider.getCurrentContext().getRequest();

		Enumeration<?> names = httpRequest.getHeaderNames();

		while (names.hasMoreElements()) {

			String name = (String) names.nextElement();
			// headers.add(name, Validate.cleanData(httpRequest.getHeader(name)));
			headers.add(name, (httpRequest.getHeader(name)));
		}

		return headers;

	}

	/**
	 * Usage: This method will determine if the excel file to be uploaded has
	 * content or not.
	 * 
	 * @param file
	 * @return
	 */
	public boolean doesExcelHasContent(InputStream file, String extension) {
		boolean hasContent = false;
		// XSSFWorkbook workbook = null;
		Workbook workbook = null;
		try {
			if (extension.equalsIgnoreCase("xls"))
				workbook = new HSSFWorkbook(file);
			else
				workbook = new XSSFWorkbook(file);
			for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
				hasContent = isSheetEmpty(workbook.getSheetAt(i), extension);
			}
		} catch (IOException e) {
			logger.error(Logger.EVENT_FAILURE, e.getMessage());
			throw new ConnectException("IOException in doesExcelHasContent method:" + e.getLocalizedMessage());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(Logger.EVENT_FAILURE, e.getMessage());
			throw new ConnectException("Exception in doesExcelHasContent method:" + e.getLocalizedMessage());
		} finally {
			if (workbook != null)
				try {
					workbook.close();
				} catch (IOException e) {
					logger.error(Logger.EVENT_FAILURE, "Exception while closing workbook:" + e.getMessage());
					e.printStackTrace();
				}
		}

		return hasContent;
	}

	boolean isSheetEmpty(Sheet sheet, String extension) {
		Iterator<Row> rows = sheet.rowIterator();
		Row row = null;
		while (rows.hasNext()) {
			if (extension.equalsIgnoreCase("xls"))
				row = (HSSFRow) rows.next();
			else
				row = (XSSFRow) rows.next();
			Iterator<Cell> cells = row.cellIterator();
			Cell cell = null;
			while (cells.hasNext()) {
				if (extension.equalsIgnoreCase("xls"))
					cell = (HSSFCell) cells.next();
				else
					cell = (XSSFCell) cells.next();
				if (!cell.getStringCellValue().isEmpty()) {
					return true;
				}
			}
		}
		return false;
	}

	/*
	 * Added the above methods from utility service for file upload download
	 * 
	 */
	/**
	 * Usage: This method will set additional headers
	 * 
	 * @param headers
	 */
	public void setCSRFSpecificHeaders(HttpHeaders headers) {

		if (Objects.nonNull(contextProvider.getCurrentContext())
				&& Objects.nonNull(contextProvider.getCurrentContext().getRequest())) {
			logger.info(Logger.EVENT_SUCCESS, "Setting CSRF headers");
			HttpServletRequest request = contextProvider.getCurrentContext().getRequest();

			if (!StringUtils.isEmpty(request.getHeader(MetaConstants.HEADER_X_FORWARDED_HOST))) {
				logger.info(Logger.EVENT_SUCCESS,
						("X_FORWARDED_HOST_HEADER:" + request.getHeader(MetaConstants.HEADER_X_FORWARDED_HOST)));
				headers.add(MetaConstants.HEADER_X_FORWARDED_HOST,
						request.getHeader(MetaConstants.HEADER_X_FORWARDED_HOST));
			}
			if (!StringUtils.isEmpty(request.getHeader(MetaConstants.REFERER))) {
				logger.info(Logger.EVENT_SUCCESS, ("REFERRER_HEADER:" + request.getHeader(MetaConstants.REFERER)));
				headers.add(MetaConstants.REFERER, request.getHeader(MetaConstants.REFERER));
			}
			if (!StringUtils.isEmpty(request.getHeader(MetaConstants.ORIGIN))) {
				logger.info(Logger.EVENT_SUCCESS, "ORIGIN_HEADER:" + request.getHeader(MetaConstants.ORIGIN));
				headers.add(MetaConstants.ORIGIN, request.getHeader(MetaConstants.ORIGIN));
			}

		}
	}

	/**
	 * Usage: The <i>pushDataToElastic</i> method is created as an extra precaution
	 * in case data is not pushed to elastic as part of notifyDataChange api. This
	 * method will pull latest data from TRM and push it to elastic. As of now this
	 * is is implemented for Supplier Connect app.
	 * 
	 * @param payLoadObj
	 * @param headers
	 * @param trmUrl
	 */

	/**
	 * 
	 * @param optJSONObject
	 * @param criteria      this should not be null as it should have where
	 *                      condition . Criteria.where("type").is("object");
	 */
	public static Criteria addFilterConditionToCriteria(JSONObject optJSONObject, Criteria criteria) {

		if (criteria == null) {
			throw new IllegalArgumentException("criteria object cannot be null");
		}

		JSONObject filterData = optJSONObject.optJSONObject("filterData");
		if (filterData != null) {
			JSONArray filterArray = filterData.optJSONArray("filter");
			if (filterArray != null && filterArray.length() > 0) {
				for (int i = 0; i < filterArray.length(); i++) {
					JSONObject filterObj = filterArray.getJSONObject(i);
					criteria = criteria.and(filterObj.getString("fieldName"));
					switch (filterObj.getString("operator").toLowerCase()) {
					case "eq":
						criteria.is(filterObj.getString("value"));
						break;
					case "like":
						criteria.regex(filterObj.getString("value"));
						break;
					case "ne":
						criteria.ne(filterObj.getString("value"));
						break;
					case "exists":
						criteria.exists(filterObj.getBoolean("value"));
						break;
					default:
						criteria.equals(filterObj.getString("value"));
					}

				}
			}
		}

		return criteria;

	}
	
	public static void createCriteriaFromAdditionalFilters(Criteria criteria, FilterData filterData) {
		List<MongoOperations> filters = filterData.getFilter();
		Iterator<MongoOperations> iterator = filters.iterator();
		while (iterator.hasNext()) {
			MongoOperations individualFilter = iterator.next();
			String operator = individualFilter.getOperator();
			switch (operator) {
			case "eq": {
				criteria = criteria.and(individualFilter.getFieldName()).is(individualFilter.getValue());
				break;
			}
			case "ne": {
				criteria = criteria.and(individualFilter.getFieldName()).ne(individualFilter.getValue());
				break;
			}
			case "like": {
				criteria = criteria.and(individualFilter.getFieldName()).regex(individualFilter.getValue().toString());
				break;
			}
			case "in": {
				criteria = criteria.and(individualFilter.getFieldName()).in((List) individualFilter.getValue());
				break;
			}
			case "nin": {
				criteria = criteria.and(individualFilter.getFieldName()).nin((List) individualFilter.getValue());
				break;
			}
			case "exists":{
				criteria = criteria.and(individualFilter.getFieldName()).exists((boolean)individualFilter.getValue());
				break;
			}
			case "all":{
				criteria = criteria.and(individualFilter.getFieldName()).all((List) individualFilter.getValue());
				break;
			}
			case "lte":{
				criteria = criteria.and(individualFilter.getFieldName()).lte(individualFilter.getValue().toString().contains("$UTCDateTime:") ? Date.from(Instant.parse(individualFilter.getValue().toString().replace("$UTCDateTime:", ""))): individualFilter.getValue());
				break;
			}
			case "gte":{
				criteria = criteria.and(individualFilter.getFieldName()).gte(individualFilter.getValue().toString().contains("$UTCDateTime:") ? Date.from(Instant.parse(individualFilter.getValue().toString().replace("$UTCDateTime:", ""))): individualFilter.getValue());
				break;
			}
			case "lt":{
				criteria = criteria.and(individualFilter.getFieldName()).lt(individualFilter.getValue().toString().contains("$UTCDateTime:") ? Date.from(Instant.parse(individualFilter.getValue().toString().replace("$UTCDateTime:", ""))): individualFilter.getValue());
				break;
			}
			case "gt":{
				criteria = criteria.and(individualFilter.getFieldName()).gt(individualFilter.getValue().toString().contains("$UTCDateTime:") ? Date.from(Instant.parse(individualFilter.getValue().toString().replace("$UTCDateTime:", ""))): individualFilter.getValue());
				break;
			}
			}
		}
	}

}
