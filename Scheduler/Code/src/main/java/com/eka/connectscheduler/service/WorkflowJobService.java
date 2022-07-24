package com.eka.connectscheduler.service;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.tomcat.jni.Global;
import javax.validation.Valid;
import org.json.JSONObject;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.Logger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.eka.connectscheduler.constants.GlobalConstants;
import com.eka.connectscheduler.constants.MetaConstants;
import com.eka.connectscheduler.constants.WorkflowConstants;
import com.eka.connectscheduler.exception.ConnectException;
import com.eka.connectscheduler.utils.CommonUtils;
import com.eka.connectscheduler.utils.SchedulerUtils;
import com.google.gson.Gson;

@Service
public class WorkflowJobService {

	final static Logger logger = ESAPI.getLogger(WorkflowJobService.class);

	public static final long EXECUTION_TIME = 5000L;

	@Value("${eka_connect_host}")
	private String ekaConnectHost;

	@Autowired
	ConnectCommonService connectCommonService;
	@Autowired
	SchedulerUtils schedulerUtils;

	private AtomicInteger count = new AtomicInteger();

	public void executeSampleJob(JobExecutionContext context) {

		String[] jobKeys = context.getJobDetail().getJobDataMap().getKeys();
		logger.info(Logger.EVENT_SUCCESS,
				"The workflow job has begun..." + jobKeys + "of trigger type " + context.getTrigger().getDescription());
		try {

			JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
			Map<String, Object> scheduledWorkFlow = (Map<String, Object>) jobDataMap
					.get(GlobalConstants.SCHEDULED_WORKFLOW);
			logger.info(Logger.EVENT_SUCCESS, "workflow name is " + scheduledWorkFlow.get(GlobalConstants._ID));
			String apiPath = "/scheduler/executeworkflow";

			URI connectUri = UriComponentsBuilder.fromHttpUrl(ekaConnectHost).path(apiPath).build().toUri();

			JSONObject req = (JSONObject) SchedulerUtils.getReqBody(scheduledWorkFlow);

			HttpHeaders header = (HttpHeaders) jobDataMap.get(GlobalConstants.HEADERS);
			String tenantId = header.get(GlobalConstants.X_TENANT_ID).get(0);
			header.set(MetaConstants.REQUEST_ID, CommonUtils.GenerateUUID());
			Map<String, List<String>> systToken = connectCommonService.generateSystemToken(tenantId);
			header.set(MetaConstants.AUTHORIZATION, systToken.get(tenantId).get(0));
			HttpEntity<Map> httpEntity = new HttpEntity<Map>(req.toMap(), header);
			// TODO: need to autowired it
			RestTemplate schedulerTemplate = new RestTemplate();
			logger.debug(Logger.EVENT_SUCCESS,
					"calling POST " + apiPath.toString() + " with headers :" + header + " with Body :" + req );
			ResponseEntity<Object> exchange = schedulerTemplate.exchange(connectUri, HttpMethod.POST, httpEntity,
					Object.class);
			logger.info(Logger.EVENT_SUCCESS,
					"calling POST " + apiPath.toString() + " Completed. with responseBody: " + (exchange != null
							? exchange.getBody().toString()
							: ""));

		} catch (Exception e) {
			logger.error(Logger.EVENT_FAILURE, "Error while executing workflow job", e);
		} finally {
			count.incrementAndGet();
			logger.info(Logger.EVENT_SUCCESS, "Workflow job has finished..." + jobKeys);
		}
	}


	/**
	 * Executes scheduled jobs immediately if below key is set true.
	 * "isImmediateExecutionRequired" : true
	 * 
	 * @param jobs List of scheduled jobs
	 */
	public void executeSampleJobImmediately(List<JobDetail> jobs) {

			for (JobDetail jobDetail : jobs) {
				try {
					JobDataMap jobDataMap = jobDetail.getJobDataMap();
					Map<String, Object> scheduledWorkFlow = (Map<String, Object>) jobDataMap
							.get(GlobalConstants.SCHEDULED_WORKFLOW);
					Object isImmediateExecutionRequired = scheduledWorkFlow.get(GlobalConstants.IS_IMMEDIATE_EXECUTION_REQUIRED);
					if (Objects.nonNull(isImmediateExecutionRequired) && isImmediateExecutionRequired != null && isImmediateExecutionRequired.equals(true)) {
						
						logger.info(Logger.EVENT_SUCCESS, "workflow name is " + scheduledWorkFlow.get(GlobalConstants._ID));
						String apiPath = "/scheduler/executeworkflow";

						URI connectUri = UriComponentsBuilder.fromHttpUrl(ekaConnectHost).path(apiPath).build().toUri();

						JSONObject req = (JSONObject) SchedulerUtils.getReqBody(scheduledWorkFlow);

						HttpHeaders header = (HttpHeaders) jobDataMap.get(GlobalConstants.HEADERS);
						String tenantId = header.get(GlobalConstants.X_TENANT_ID).get(0);
						header.set(MetaConstants.REQUEST_ID, CommonUtils.GenerateUUID());
						Map<String, List<String>> systToken = connectCommonService.generateSystemToken(tenantId);
						header.set(MetaConstants.AUTHORIZATION, systToken.get(tenantId).get(0));
						HttpEntity<Map> httpEntity = new HttpEntity<Map>(req.toMap(), header);
						// TODO: need to autowired it
						RestTemplate schedulerTemplate = new RestTemplate();
						logger.debug(Logger.EVENT_SUCCESS,
								"calling POST " + apiPath.toString() + " with headers :" + header + " with Body :" + req );
						ResponseEntity<Object> exchange = schedulerTemplate.exchange(connectUri, HttpMethod.POST, httpEntity,
								Object.class);
						logger.info(Logger.EVENT_SUCCESS,
								"calling POST " + apiPath.toString() + " Completed. with responseBody: " + (exchange != null
										? exchange.getBody().toString()
										: ""));
					}

				} catch (Exception e) {
					logger.error(Logger.EVENT_FAILURE,
							("Exception during processing " + jobDetail + " " + e.getLocalizedMessage()), e);
					continue;
				}
			}
	}

	public int getNumberOfInvocations() {
		return count.get();
	}

	public Object scheduleWorkflowForData(@Valid String workflowData,HttpHeaders header) {

		try {
			if (logger.isDebugEnabled())
				logger.debug(logger.EVENT_SUCCESS,ESAPI.encoder().encodeForHTML(
						"Execution of scheduleWorkflow method started with body: " + workflowData));

			if (isValidWorkFlowBody(new JSONObject(workflowData))) {
				Gson gson = new Gson();
				List<String> uniqueFields;
				Map<String,Object> workFlowMap = gson.fromJson(workflowData,Map.class);
				if(workFlowMap.containsKey(WorkflowConstants.ADD_UNIQUE_FIELDS_TO_JOB_NAME) && workFlowMap.get(WorkflowConstants.ADD_UNIQUE_FIELDS_TO_JOB_NAME).equals(true)) {
					HttpEntity<Map> httpEntity = new HttpEntity<Map>(null, header);
					String apiPath = "/meta/app/"+ workFlowMap.get(WorkflowConstants.APP_ID) + "/object/" +  workFlowMap.get(WorkflowConstants.OBJECT);

					URI connectUri = UriComponentsBuilder.fromHttpUrl(ekaConnectHost).path(apiPath).build().toUri();
					// TODO: need to autowired it
					RestTemplate schedulerTemplate = new RestTemplate();
					logger.debug(Logger.EVENT_SUCCESS,
							"calling GET " + connectUri.toString() + " with headers :" + header);
					ResponseEntity<Object> response = schedulerTemplate.exchange(connectUri, HttpMethod.GET, httpEntity,
							Object.class);
					logger.info(Logger.EVENT_SUCCESS,
							"calling GET " + connectUri.toString() + " Completed. with responseBody: " + (response != null
							? response.getBody().toString()
									: ""));
					uniqueFields = (List<String>)((Map<String, Object>) response.getBody()).get(WorkflowConstants.UNIQUE_FIELDS);
				}
				else {
					uniqueFields = null;
				}

				return schedulerUtils.scheduleWorkflowForData(workFlowMap,uniqueFields);
			} else {
				throw new ConnectException(
						"Workflow Body is not configured properly.Workflow Body should be in this format :-  {\"workflowTaskName\":\"createderivative\",\"task\":\"createderivative\",\"appId\":\"e621d081-85cb-4951-adea-49b88d7f4ab0\",\r\n"
								+ "\"output\":{\"createderivative\":{\"sourceId\":\"\"}},\"schedulerConfig\" : {"
								+ "        \"cronExpression\" : \"1 40 4 7 5 ? 2022\""
								+ "    },\"object\":\"ab3fc628-ecbc-43e0-81cb-aa2d12345678\"}");
			}
		} catch (Exception e) {
			logger.error(Logger.EVENT_FAILURE, "error inside method scheduleWorkflowForData", e);
			throw new ConnectException(
					"error in scheduling workfolow task " + e.getLocalizedMessage(),
					e.getCause());
		}

	}

	private boolean isValidWorkFlowBody(JSONObject workFlowJSON) {
		if ((workFlowJSON.has(WorkflowConstants.APP_ID) && workFlowJSON.has(WorkflowConstants.TASK) && workFlowJSON.has(WorkflowConstants.WRK_FLOW_TASK_NAME)
				&& workFlowJSON.has(WorkflowConstants.OUTPUT) && workFlowJSON.has(WorkflowConstants.OBJECT))) {
			JSONObject workFlowTaskJSON = (JSONObject) workFlowJSON.get(WorkflowConstants.OUTPUT);
			if (workFlowTaskJSON.has(workFlowJSON.getString(WorkflowConstants.WRK_FLOW_TASK_NAME)))
				return true;
		}
		return false;
	}
}