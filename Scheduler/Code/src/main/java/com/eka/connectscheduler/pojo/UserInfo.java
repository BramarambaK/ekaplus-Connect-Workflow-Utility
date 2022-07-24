package com.eka.connectscheduler.pojo;

import java.util.List;


public class UserInfo{
	
	private String userId;
	private String userName;
	private Integer userType;
	private String externalUserId;
	private String firstName;
	private String lastName;
	private String email;
	private Boolean isClientAdmin;
	private Boolean isFirstLogin;
	private List<String> roleIds;
    private List<String> permCodes;
    private List<String> businessPartys;
	
    public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public Integer getUserType() {
		return userType;
	}
	public void setUserType(Integer userType) {
		this.userType = userType;
	}
	public String getExternalUserId() {
		return externalUserId;
	}
	public void setExternalUserId(String externalUserId) {
		this.externalUserId = externalUserId;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public Boolean getIsClientAdmin() {
		return isClientAdmin;
	}
	public void setIsClientAdmin(Boolean isClientAdmin) {
		this.isClientAdmin = isClientAdmin;
	}
	public Boolean getIsFirstLogin() {
		return isFirstLogin;
	}
	public void setIsFirstLogin(Boolean isFirstLogin) {
		this.isFirstLogin = isFirstLogin;
	}
	public List<String> getRoleIds() {
		return roleIds;
	}
	public void setRoleIds(List<String> roleIds) {
		this.roleIds = roleIds;
	}
	public List<String> getPermCodes() {
		return permCodes;
	}
	public void setPermCodes(List<String> permCodes) {
		this.permCodes = permCodes;
	}
	public List<String> getBusinessPartys() {
		return businessPartys;
	}
	public void setBusinessPartys(List<String> businessPartys) {
		this.businessPartys = businessPartys;
	}
	@Override
	public String toString() {
		return "UserInfo [userId=" + userId + ", userName=" + userName + ", userType=" + userType + ", externalUserId="
				+ externalUserId + ", email=" + email + ", isClientAdmin=" + isClientAdmin + ", roleIds=" + roleIds
				+ ", businessPartys=" + businessPartys + "]";
	}
}