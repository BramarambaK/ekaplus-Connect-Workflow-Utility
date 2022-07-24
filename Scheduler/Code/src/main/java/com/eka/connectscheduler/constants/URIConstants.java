package com.eka.connectscheduler.constants;

public interface URIConstants {
	
	public static String USER_INFO_URI = "/cac-security/api/userinfo";
	public static String ACCESS_TOKEN_URI = "/cac-security/api/oauth/token";
	public static String PERM_CODES_URI = "/spring/permcodes/";
	public static String CAC_WEB_SERVER_LOGIN_URI = "/spring/smartapp/login";
	public static String COPY_API_URI = "/common/copyConnectDocuments";
	public static String BUSINESS_PARTY_URI = "/spring/customers/getBusinessPartyDetails";
	// Query Param Constants
	public static String GRANT_TYPE = "grant_type";
	public static String GRANT_TYPE_VALUE = "cloud_credentials";
	public static String CLIENT_ID = "client_id";
	public static String CLIENT_ID_VALUE = "2";
	public static String PLATFORM_LOGIN_TYPE = "type";
	public static String PLATFORM_LOGIN_TYPE_VALUE = "1";

}
