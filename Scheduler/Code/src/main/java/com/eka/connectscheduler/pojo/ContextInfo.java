package com.eka.connectscheduler.pojo;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

@Component("contextInfo")
public class ContextInfo {
	private String tenantID;
	private UserInfo userInfo;
	private String locale;
	private String objectAction;
	private String appUUID;
	private String appName;
	private LinkedHashMap<String, LinkedHashMap> translation;
	private HttpServletRequest request;
	private String authToken;
	private String requestId;
	private String sourceDeviceId;
	private boolean isSchedulerRequest;
	private Map<String, Object> platformUrlProperty;
	private String deviceId;

	public String getTenantID() {
		return tenantID;
	}

	public UserInfo getUserInfo() {
		return userInfo;
	}

	public String getLocale() {
		return locale;
	}

	public String getAppUUID() {
		return appUUID;
	}

	public String getAppName() {
		return appName;
	}
	
	public Map<String, Object> getPlatformUrlProperty() {
		return platformUrlProperty;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public void setTenantID(String tenantID) {
		this.tenantID = tenantID;
	}

	public void setUserInfo(UserInfo userInfo) {
		this.userInfo = userInfo;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public LinkedHashMap<String, LinkedHashMap> getTranslation() {
		return translation;
	}

	public void setTranslation(LinkedHashMap<String, LinkedHashMap> translation) {
		this.translation = translation;
	}

	public String getObjectAction() {
		return objectAction;
	}

	public void setObjectAction(String objectAction) {
		this.objectAction = objectAction;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	public void setAppUUID(String appUUID) {
		this.appUUID = appUUID;
	}
	
	public String getAuthToken() {
		return authToken;
	}

	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getSourceDeviceId() {
		return sourceDeviceId;
	}

	public void setSourceDeviceId(String sourceDeviceId) {
		this.sourceDeviceId = sourceDeviceId;
	}

	public boolean isSchedulerRequest() {
		return isSchedulerRequest;
	}

	public void setSchedulerRequest(boolean isSchedulerRequest) {
		this.isSchedulerRequest = isSchedulerRequest;
	}
	
	public void setPlatformUrlProperty(Map<String, Object> platformUrlProperty) {
		this.platformUrlProperty = platformUrlProperty;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	
}
