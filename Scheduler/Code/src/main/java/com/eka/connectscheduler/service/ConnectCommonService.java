package com.eka.connectscheduler.service;

import static com.eka.connectscheduler.constants.MetaConstants.PROPERTY_VALUE;
import static com.eka.connectscheduler.constants.MetaConstants.PROPERTY_NAME;
import static com.eka.connectscheduler.constants.MetaConstants.PROPERTY_LEVEL;
import static com.eka.connectscheduler.constants.MetaConstants.TENANT;
import static com.eka.connectscheduler.constants.MetaConstants.TYPE_PROPERTY;
import static com.eka.connectscheduler.constants.MetaConstants.DATA;
import static com.eka.connectscheduler.constants.MetaConstants.PLATFORM_URL;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.json.JSONObject;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import com.eka.connectscheduler.config.TenantConfiguration;
import com.eka.connectscheduler.constants.GlobalConstants;
import com.eka.connectscheduler.constants.MetaConstants;
import com.eka.connectscheduler.constants.URIConstants;
import com.eka.connectscheduler.exception.ConnectException;
import com.eka.connectscheduler.pojo.TenantConfigDetails;
import com.eka.connectscheduler.utils.CommonUtils;

@Service
public class ConnectCommonService {

	final static Logger logger = ESAPI.getLogger(ConnectCommonService.class);

	@Value("${eka_connect_host}")
	private String ekaConnectHost;

	@Autowired
	private MongoOperationService mongoOperationService;

	private RestTemplate platformRestTemplate;

	@Autowired
	private TenantConfiguration tenantConfiguration;

	@SuppressWarnings("rawtypes")
	public List getDocuments(Query query, String collectionName) {

		List list = mongoOperationService.list(query, HashMap.class, collectionName);
		return list;

	}

	public List getWorkflowTasksByTagName(String tagName, List<String> appUUIDs, String tenant,
			boolean referPrepackagedMeta) {
		Query query = new Query();
		query.addCriteria(Criteria.where("tags").regex(tagName));
		if (appUUIDs != null && appUUIDs.size() > 0)
			query.addCriteria(Criteria.where("refTypeId").in(appUUIDs));
		List<HashMap> list = mongoOperationService.list(query, HashMap.class,
				(referPrepackagedMeta ? "prepackaged" : tenant) + "_Meta");

		return list;
	}

	public String findNameBySysUUID(String appId) {
		if (appId == null) {
			return null;
		}
		Query query = new Query();
		query.addCriteria(Criteria.where("sys__UUID").is(appId));
		Document document = mongoOperationService.findOne(query, Document.class, "prepackaged_Meta");
		if (document == null) {
			return null;
		}
		return document.getString("name");
	}

	public String objectIdByTaskId(String taskId, String appId) {
		Query query = new Query();
		Criteria criteria = Criteria.where("taskId").is(taskId);
		criteria.and("refTypeId").is(appId);
		query.addCriteria(criteria);

		Document document = mongoOperationService.findOne(query, Document.class, "prepackaged_Meta");
		if (document == null) {
			return null;
		}
		String objectId = document.getString("object");
		return objectId;

	}

	public Document findOneMeta(Query query, String collectionName) {

		Document document = mongoOperationService.findOne(query, Document.class,
				StringUtils.isEmpty(collectionName) ? "prepackaged_Meta" : collectionName + "_Meta");
		return document;
	}

	/**
	 * 
	 * @param tenantId
	 * @return String, a valid token, else exception is thrown.
	 */
	public Map<String, List<String>> generateSystemToken(String tenantId) {
		if (StringUtils.isEmpty(tenantId)) {
			throw new IllegalArgumentException("tenant name cannot be empty");
		}

		TenantConfigDetails configDetails = tenantConfiguration.getValue(tenantId);
		if (configDetails == null || StringUtils.isEmpty(configDetails.getSystemuser())) {
			throw new ConnectException("Invalid Configuration. System user details not setup for tenant :" + tenantId);
		}
		return getAuthDetails("Basic " + configDetails.getSystemuser(), tenantId);

	}

	private Map<String, List<String>> getAuthDetails(String base64EncodedUserNamePassword, String tenantId)
			throws ConnectException {

		String basic_OAuth_string = base64EncodedUserNamePassword;
		URI uri = null;
		String token = null;
		String platformUrl = null;
		try {
			
			Query query = null;
			Criteria criteria = null;
				criteria = Criteria.where(PROPERTY_NAME).is(PLATFORM_URL).and(PROPERTY_LEVEL).is(TENANT).and(GlobalConstants.TYPE).is(TYPE_PROPERTY);
				query = new Query(criteria);
				Document document = mongoOperationService.findOne(query, Document.class, tenantId+DATA);
				Map<String, Object> mapObj = (Map<String, Object>) CommonUtils.parseDataObjectToJSON(document);
			
				if(mapObj.containsKey(PROPERTY_VALUE)) {
					platformUrl=mapObj.get(PROPERTY_VALUE).toString();
		
					HttpHeaders httpHeaders = new HttpHeaders();
					httpHeaders.set(MetaConstants.AUTHORIZATION, basic_OAuth_string);
					httpHeaders.set(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");
					String authUrl = platformUrl + URIConstants.ACCESS_TOKEN_URI
							+ "?client_id=2&grant_type=cloud_credentials";
					uri = new URI(authUrl);
					String body = "client_id=2&grant_type=cloud_credentials";
					HttpEntity<String> request = new HttpEntity<String>(body, httpHeaders);
					platformRestTemplate = new RestTemplate();
					ResponseEntity<HashMap> response = platformRestTemplate.exchange(uri, HttpMethod.POST, request,
							HashMap.class);

					JSONObject responseJSON = new JSONObject(response.getBody());

					// get the auth_token from responseJSON
					token = responseJSON.getJSONObject("auth2AccessToken").getString("access_token");
				
			} else {
				throw new ConnectException("Platform url not found for the tenantId " + tenantId);
			}
		} catch (URISyntaxException e) {
			logger.error(Logger.EVENT_FAILURE, "invalid Url " + uri, e);
		} catch (ConnectException e) {
			logger.error(Logger.EVENT_FAILURE, "Exception occured while generating token for system user", e);
			throw e;
		} catch (Exception e) {
			logger.error(Logger.EVENT_FAILURE, "Uhhandled Exception occured while generating token for system user", e);
			throw e;
		}
		List<String> tenantData = new ArrayList<>();
		tenantData.add(token);
		tenantData.add(platformUrl);
		Map<String, List<String>> tenantMap = new HashMap<String, List<String>>();
		tenantMap.put(tenantId, tenantData);

		return tenantMap;
	}

}
