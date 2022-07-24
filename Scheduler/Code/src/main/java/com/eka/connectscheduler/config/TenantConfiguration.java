package com.eka.connectscheduler.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.eka.connectscheduler.pojo.TenantConfigDetails;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties
public class TenantConfiguration {

	private Map<String,TenantConfigDetails> tenant = new HashMap<String, TenantConfigDetails>();

	public void setTenant(Map<String, TenantConfigDetails> tenant) {
		this.tenant = tenant;
	}

	public Map<String, TenantConfigDetails> getTenant() {
		return tenant;
	}

	public TenantConfigDetails getValue(String tenantShortName) {
		return tenant.computeIfAbsent(tenantShortName,
				k -> new TenantConfigDetails());
	}

}

