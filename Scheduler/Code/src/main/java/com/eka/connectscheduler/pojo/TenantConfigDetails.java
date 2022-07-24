package com.eka.connectscheduler.pojo;

public class TenantConfigDetails {
	
        private String authUrl;
        private String secret;
		private String systemuser;
		private int tokentimeout=300;//300 seconds,  5minutes
		
		public int getTokentimeout() {
			return tokentimeout;
		}
		public String getSystemuser() {
			return systemuser;
		}
		public void setSystemuser(String systemuser) {
			this.systemuser = systemuser;
		}
		public void setTokentimeout(int tokentimeout) {
			this.tokentimeout = tokentimeout;
		}
		public String getAuthUrl() {
			return authUrl;
		}
		public void setAuthUrl(String authUrl) {
			this.authUrl = authUrl;
		}
		public String getSecret() {
			return secret;
		}
		public void setSecret(String secret) {
			this.secret = secret;
		} 
		
	}

