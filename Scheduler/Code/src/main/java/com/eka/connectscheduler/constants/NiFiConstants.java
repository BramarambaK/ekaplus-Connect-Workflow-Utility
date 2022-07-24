package com.eka.connectscheduler.constants;

public class NiFiConstants {
	
	public static final String SYSTEM_TASKS_API_CONTEXT = "/system-tasks";
    public static final String NIFI_API_CONTEXT = "/nifi";
	
	// URI Handlers Setup on NiFi
    public static final String REST_API_INVOCATION_URI = "/api-invocations/rest-api";
    public static final String EMAIL_URI = "/notifications/emails";

	// General Constants
	public static final String HOST = "host";
	public static final String QUERY_PARAMS = "queryParams";
	public static final String PATH_PARAMS = "pathParams";
	public static final String PATH = "path";
	public static final String HEADERS = "headers";
	public static final String OUTPUT_MAPPING = "outputMapping";
	public static final String INPUT_MAPPING = "inputMapping";
	public static final String USE_OUTPUT_MAPPING = "useOutputMapping";
	public static final String USE_INPUT_MAPPING = "useInputMapping";
	public static final String BODY = "body";
	public static final String URL = "url";
	public static final String NIFI_HOST = "nifi.host";
	public static final String NIFI_PAYLOAD_HOST = "nifi.payload.host";
	
	public static final String NIFI_ENDPOINT_VS_NIFIPORT = "nifi.endPointVsNifiPort";
	public static final String NIFI_THREAD_POOL = "nifi.thread.pool";
	public static final String EKA_CONNECT_ELASTIC_SEARCH_INDEX = "eka-connect-elasticsearch-index";
	public static final String EKA_CONNECT_ELASTIC_SEARCH_TYPE = "eka-connect-elasticsearch-type";
	
	public static final String NIFI_REST_API_ERROR_HEADER = "eka-connect-rest-api-error-response";
	

}
