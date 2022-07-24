package com.eka.connectscheduler.utils;

import static com.eka.connectscheduler.constants.MetaConstants.LISTVIEW;
import static com.eka.connectscheduler.constants.MetaConstants.PREPACKAGED_COLLECTION;
import static com.eka.connectscheduler.constants.WorkflowConstants.OBJECT_ACTION;
import static com.eka.connectscheduler.constants.WorkflowConstants.DISABLE_CHECK_SCHEDULED_IN_PROGRESS;
import static com.eka.connectscheduler.constants.GlobalConstants._ID;
import static com.eka.connectscheduler.constants.GlobalConstants.PLATFORM_ID;
import static com.eka.connectscheduler.constants.GlobalConstants.IS_WORKFLOW_APP;
import static com.eka.connectscheduler.constants.GlobalConstants.SYS_UUID;
import static com.eka.connectscheduler.constants.MetaConstants.META;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import org.json.JSONObject;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.Logger;
import org.quartz.CronExpression;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.util.UriComponentsBuilder;

import com.eka.connectscheduler.constants.GlobalConstants;
import com.eka.connectscheduler.constants.MetaConstants;
import com.eka.connectscheduler.constants.WorkflowConstants;
import com.eka.connectscheduler.exception.ConnectException;
import com.eka.connectscheduler.intercepter.ContextSetter;
import com.eka.connectscheduler.jobs.WorkflowJob;
import com.eka.connectscheduler.pojo.FilterData;
import com.eka.connectscheduler.scheduler.QrtzScheduler;
import com.eka.connectscheduler.service.ConnectCommonService;
import com.google.gson.Gson;

@Service
public class SchedulerUtils {

	final static Logger logger = ESAPI.getLogger(ContextSetter.class);

	@Value("${eka_connect_host}")
	private String ekaConnectHost;

	@Value("${scheduler.enabled.tenantIds}")
	private List<String> tenantIds;
	
	@Autowired
	MongoTemplate mongoTemplate;
	
	@Autowired
	private CommonUtils commonUtils;

	@Autowired
	ConnectCommonService connectCommonService;

	@Autowired
	RestTemplateGetRequestBodyFactory restTemplateGetWityBody;

	@Autowired
	ContextProvider contextProvider;
	@Autowired
	QrtzScheduler qrtzScheduler;
	@Autowired
	Scheduler scheduler;
	
	public List<JobDetail> buildJobs() {

		List<JobDetail> jobs = new ArrayList<>();

		logger.info(Logger.EVENT_SUCCESS, "@@@ tenantIds " + tenantIds);
		for (String tenantId : tenantIds) {
			try {
				Map<String, List<String>> systToken = connectCommonService.generateSystemToken(tenantId);

				HttpHeaders headers = new HttpHeaders();
				headers.add(GlobalConstants.X_TENANT_ID, tenantId);
				headers.add(MetaConstants.AUTHORIZATION, systToken.get(tenantId).get(0));
				headers.add(HttpHeaders.CONTENT_TYPE, GlobalConstants.APPLICATION_JSON);

				List<Map<String, Object>> scheduledWorkFlows = getAllScheduledWorkflows(null, headers,
						systToken.get(tenantId).get(1) , tenantId);

				logger.info(Logger.EVENT_SUCCESS, "scheduledWorkFlows " + scheduledWorkFlows.size());

				headers.add(MetaConstants.X_SCHEDULER, "Y");

				for (Map<String, Object> scheduledWorkFlow : scheduledWorkFlows) {
					JobDataMap jobDataMap = new JobDataMap();
					jobDataMap.put(GlobalConstants.HEADERS, headers);
					jobDataMap.put(GlobalConstants.SCHEDULED_WORKFLOW, scheduledWorkFlow);

					JobDetail job = JobBuilder.newJob(WorkflowJob.class)
							.withIdentity("Qrtz_Job_Detail" + UUID.randomUUID().toString(), "workflow-jobs")
							.withDescription("Invoke Workflow Job service...").usingJobData(jobDataMap).storeDurably()
							.build();

					jobs.add(job);

				}
			} catch (Exception e) {
				logger.error(Logger.EVENT_FAILURE, "The " + tenantId + " has not subscribed for any connect apps.");
				continue;
			}
		}
		return jobs;
	}

	public List<Trigger> buildTriggers(List<JobDetail> jobs, String type) {
		List<Trigger> triggers = new ArrayList<>();
		if (type.equalsIgnoreCase("cron")) {
			triggers = buildCronTriggers(jobs);
		} else {
			triggers = buildSimpleTriggers(jobs);
		}
		return triggers;
	}

	public List<Trigger> buildSimpleTriggers(List<JobDetail> jobs) {
		List<Trigger> triggers = new ArrayList<>();
		SimpleTrigger trigger = null;
		for (JobDetail jobDetail : jobs) {
			trigger = TriggerBuilder.newTrigger().forJob(jobDetail)
					.withIdentity(jobDetail.getKey().getName(), "workflow-triggers")
					.withDescription("Send Simple Workflow Trigger")
					// .startAt(Date.from(startAt.toInstant()))
					.withSchedule(SimpleScheduleBuilder.repeatSecondlyForever(10)).build();
			triggers.add(trigger);
		}
		return triggers;
	}

	private List<Trigger> buildCronTriggers(List<JobDetail> jobs) {
		List<Trigger> triggers = new ArrayList<>();
		// int count = 0;
		CronTrigger trigger = null;
		CronExpression exp = null;
		Object cronExpression = null;
		for (JobDetail jobDetail : jobs) {
			try {
				Object schedulerConfig = ((Map<String, Object>) jobDetail.getJobDataMap()
						.get(GlobalConstants.SCHEDULED_WORKFLOW)).get(GlobalConstants.SCHEDULER_CONFIG);
				if (Objects.nonNull(schedulerConfig) && schedulerConfig != null) {
					cronExpression = ((Map<String, Object>) schedulerConfig).get(GlobalConstants.CRON_EXPRESSION);

					if (Objects.nonNull(cronExpression))
						exp = new CronExpression(cronExpression.toString());
					else
						exp = new CronExpression(GlobalConstants.CRON_EXPRESSION_EVERY_HOUR);
				} else {
					exp = new CronExpression(GlobalConstants.CRON_EXPRESSION_EVERY_HOUR);
				}

			} catch (Exception e) {
				logger.error(Logger.EVENT_FAILURE,
						(cronExpression + "is not a valid CronExpression " + e.getLocalizedMessage()), e);
				continue;
			}
			trigger = TriggerBuilder.newTrigger().forJob(jobDetail)
					.withIdentity(jobDetail.getKey().getName(), "workflow-triggers")
					.withDescription("Send Cron Workflow Trigger")
					// .startAt(Date.from(startAt.toInstant()))
					.withSchedule(CronScheduleBuilder.cronSchedule(exp)).startNow().build();
			triggers.add(trigger);
		}
		return triggers;
	}

	public List<Map<String, Object>> getAllScheduledWorkflows(String appId, HttpHeaders headers, String platformUrl, String tenantId) {

		try {
			List<String> connectAppIds = new ArrayList<String>();
			// create filter to fetch scheduledworkflows --

			if (appId != null) {
				connectAppIds = new ArrayList<>();
				connectAppIds.add(appId);
			} else {

				// Hardcoding screference worfklows which are scheduler enabled
				connectAppIds.addAll(getAllAppIds(appId, headers, platformUrl,tenantId));
			}
			if (CollectionUtils.isEmpty(connectAppIds)) {
				logger.warning(Logger.EVENT_SUCCESS, "No workflow Apps configured for this client");
				return null;
			}

			Map<String, Object> filter = new HashMap<>();
			Map<String, Object> filterColumns = new HashMap<>();
			filterColumns.put(MetaConstants.FIELD_NAME, "isScheduled");
			filterColumns.put(MetaConstants.OPERATOR, "eq");
			filterColumns.put(MetaConstants.VALUE, true);
			List<Map<String, Object>> list = new ArrayList<>();
			List<Map<String, Object>> list1 = new ArrayList<>();
			list.add(filterColumns);

			Map<String, Object> filterColumns1 = new HashMap<>();
			filterColumns1.put(MetaConstants.FIELD_NAME, "refTypeId");
			filterColumns1.put(MetaConstants.OPERATOR, "in");
			filterColumns1.put(MetaConstants.VALUE, connectAppIds);
			list.add(filterColumns1);

			filter.put(WorkflowConstants.FILTER, list);

			List<Map<String, Object>> dataList = new ArrayList<>();
			Query query = null;
			Criteria criteria = null;
			List<Object> queryResult = new ArrayList<Object>();
			criteria = Criteria.where(GlobalConstants.TYPE).is(WorkflowConstants.TYPE_WORKFLOW);
				String filters = new Gson().toJson(filter);
				CommonUtils.createCriteriaFromAdditionalFilters(criteria,
						new Gson().fromJson(filters, FilterData.class));
			
			query = new Query(criteria);
			queryResult = commonUtils.executeQuery(query, tenantId+META);
			List<String> ids  = new ArrayList<>();
			for(Object obj : queryResult) {
				Map<String, Object> doc = (Map) obj;
				dataList.add(doc);
				String mongoId = (String) doc.get(_ID);
				ids.add(mongoId);
			}
			Map<String, Object> filterColumns2 = new HashMap<>();
			filterColumns2.put(MetaConstants.FIELD_NAME, _ID);
			filterColumns2.put(MetaConstants.OPERATOR, "nin");
			filterColumns2.put(MetaConstants.VALUE, ids);
			list1.add(filterColumns2);
			filter.put(WorkflowConstants.FILTER, list1);	
			String filters1 = new Gson().toJson(filter);
			CommonUtils.createCriteriaFromAdditionalFilters(criteria,
					new Gson().fromJson(filters1, FilterData.class));
			query = new Query(criteria);
			queryResult = commonUtils.executeQuery(query, PREPACKAGED_COLLECTION);
			for(Object obj : queryResult) {
				dataList.add((Map<String,Object>)obj);
			}
			return dataList;

		} catch (Exception e) {
			logger.error(Logger.EVENT_FAILURE,
					("Exception inside method getAllScheduledWorkflows" + e.getLocalizedMessage()), e);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public List<String> getAllAppIds(String appId, HttpHeaders headers, String platformUrl,String tenantId) {

		ResponseEntity<Object> getResult = null;

		List<Map<String, Object>> appListFromConnect =  new ArrayList<>();

		try {
			URI uri = UriComponentsBuilder.fromHttpUrl(platformUrl).path("/spring/apps")
					.queryParam("_dc", "1589541458058").queryParam("apptype", "Pre-packaged%20Apps")
					.queryParam("includeSeededApps", true).build().toUri();
			HttpEntity<String> httpEntity = new HttpEntity<String>(null, headers);
			getResult = restTemplateGetWityBody.getRestTemplate().exchange(uri, HttpMethod.GET, httpEntity,
					Object.class);

			List<Map<String, Object>> appListFromPlatform = null;
			if (getResult != null) {
				appListFromPlatform = (List<Map<String, Object>>) getResult.getBody();

				logger.debug(Logger.EVENT_SUCCESS, "no of valid apps from Platform for tenant "
						+ headers.get(GlobalConstants.X_TENANT_ID).get(0) + ": " + appListFromPlatform.size());
				// if there is no draft estimate in db of any type, return empty as
				// transformation cannot be performed--
				if (appListFromPlatform.isEmpty())
					return null;
			}

			Query query = null;
			Criteria criteria = null;
			List<Object> queryResult = new ArrayList<Object>();
				criteria = Criteria.where(GlobalConstants.TYPE).is(WorkflowConstants.APP);
				query = new Query(criteria);
				queryResult = commonUtils.executeQuery(query, tenantId+META);
				List<String> ids  = new ArrayList<>();
				for(Object obj : queryResult) {
					Map<String, Object> doc = (Map) obj;
					appListFromConnect.add(doc);
					String mongoId = (String) doc.get(_ID);
					ids.add(mongoId);
				}
				List<Map<String, Object>> list = new ArrayList<>();
				Map<String, Object> filter = new HashMap<>();
				Map<String, Object> filterColumns2 = new HashMap<>();
				filterColumns2.put(MetaConstants.FIELD_NAME, _ID);
				filterColumns2.put(MetaConstants.OPERATOR, "nin");
				filterColumns2.put(MetaConstants.VALUE, ids);
				list.add(filterColumns2);
				filter.put(WorkflowConstants.FILTER, list);	
				String filters1 = new Gson().toJson(filter);
				CommonUtils.createCriteriaFromAdditionalFilters(criteria,
						new Gson().fromJson(filters1, FilterData.class));
				query = new Query(criteria);
				queryResult = commonUtils.executeQuery(query, PREPACKAGED_COLLECTION);
				for(Object obj : queryResult) {
					appListFromConnect.add((Map<String,Object>)obj);
				}
	
				logger.debug(Logger.EVENT_SUCCESS, "no of apps in Connect for tenant " + ": " 
				+ appListFromConnect.size());

				// if there is no draft estimate in db of any type, return empty as
				// transformation cannot be performed--
				if (appListFromConnect.isEmpty())
					return null;

			List<String> connectAppIds = new ArrayList<String>();

			List<Map<String, Object>> appList = appListFromPlatform;

			connectAppIds = appListFromConnect.stream()
					.filter(connectApp -> appList.stream()
							.anyMatch(platformApp -> (Objects.nonNull(connectApp.get(PLATFORM_ID))
									&& Objects.nonNull(platformApp.get(_ID)))
									&& connectApp.get(PLATFORM_ID).toString().equals(platformApp.get(_ID).toString())
									&& ((Objects.nonNull(platformApp.get(IS_WORKFLOW_APP))
											&& platformApp.get(IS_WORKFLOW_APP) instanceof Boolean
											&& (boolean) platformApp.get(IS_WORKFLOW_APP).equals(true)))
									|| (Objects.nonNull(platformApp.get(_ID))
											&& platformApp.get(_ID).toString().equalsIgnoreCase("39"))))
					.map(connectApp -> (String) connectApp.get(SYS_UUID)).collect(Collectors.toList());
			//added "admin" to the list of connectAppIds
			connectAppIds.add("admin");
			return connectAppIds;

		} catch (Exception e) {
			logger.error(Logger.EVENT_FAILURE,
					("Exception while calling connect /meta/workflow " + e.getLocalizedMessage()), e);
			return null;
		}
	}

	// TODO:make this method static and call it with className
	public static JSONObject getReqBody(Map<String, Object> scheduledWorkflow) {
		JSONObject jsonObject = new JSONObject();
		String taskId = (String) (scheduledWorkflow.get(WorkflowConstants.TASK_ID)==null?scheduledWorkflow.get(WorkflowConstants.WRK_FLOW_TASK_NAME):scheduledWorkflow.get(WorkflowConstants.TASK_ID));
		String refTypeId = (String) (scheduledWorkflow.get(MetaConstants.REF_TYPE_ID)==null?scheduledWorkflow.get(WorkflowConstants.APP_ID):scheduledWorkflow.get(MetaConstants.REF_TYPE_ID));
		jsonObject.put(WorkflowConstants.APP_NAME, refTypeId);
		jsonObject.put(WorkflowConstants.OBJECT, scheduledWorkflow.get(WorkflowConstants.OBJECT));
		jsonObject.put(WorkflowConstants.WRK_FLOW_TASK_NAME, taskId);
		if (scheduledWorkflow.containsKey(OBJECT_ACTION)
				&& (scheduledWorkflow.get(OBJECT_ACTION).toString().equalsIgnoreCase(LISTVIEW)
						|| scheduledWorkflow.get(OBJECT_ACTION).toString().equalsIgnoreCase("READ"))) {
			jsonObject.put(GlobalConstants.WORKFLOW_TASK, taskId);
			JSONObject qParams = new JSONObject();
			qParams.put(WorkflowConstants.FROM, 0);
			qParams.put(WorkflowConstants.SIZE, 400);
			jsonObject.put(WorkflowConstants.Q_PARAMS, qParams);
			jsonObject.put(WorkflowConstants.DATA_CALL, true);
			jsonObject.put(WorkflowConstants.OPERATION, new ArrayList<>());
		} else {
			jsonObject.put(WorkflowConstants.TASK, taskId);
			JSONObject output = new JSONObject();
			output.put(taskId, new JSONObject());
			jsonObject.put(WorkflowConstants.OUTPUT, scheduledWorkflow.getOrDefault(WorkflowConstants.OUTPUT, output));
		}
		if(scheduledWorkflow.containsKey(DISABLE_CHECK_SCHEDULED_IN_PROGRESS) && scheduledWorkflow.get(DISABLE_CHECK_SCHEDULED_IN_PROGRESS).equals(true))
			jsonObject.put(DISABLE_CHECK_SCHEDULED_IN_PROGRESS, true);
		return jsonObject;
	}
	
	public Object scheduleWorkflowForData(Map<String,Object> workflowData,List<String> uniqueFields) {

		List<JobDetail> jobs = new ArrayList<>();
		try {

			String tenantId = contextProvider.getCurrentContext().getRequest().getHeader(GlobalConstants.X_TENANT_ID);
			Map<String, List<String>> systToken = connectCommonService.generateSystemToken(tenantId);

			HttpHeaders headers = new HttpHeaders();
			headers.add(GlobalConstants.X_TENANT_ID, tenantId);
			headers.add(MetaConstants.AUTHORIZATION, systToken.get(tenantId).get(0));
			headers.add(HttpHeaders.CONTENT_TYPE, GlobalConstants.APPLICATION_JSON);

			headers.add(MetaConstants.X_SCHEDULER, "Y");
			if(workflowData.containsKey(WorkflowConstants.ADD_UNIQUE_FIELDS_TO_JOB_NAME) && workflowData.get(WorkflowConstants.ADD_UNIQUE_FIELDS_TO_JOB_NAME).equals(true)) {
				List<Object> data =(List<Object>) ((Map<String,Object>) workflowData.get(WorkflowConstants.OUTPUT)).get(workflowData.get(WorkflowConstants.WRK_FLOW_TASK_NAME).toString());
				for (Object dataEntry : data) {
					JobDataMap jobDataMap = new JobDataMap();
					jobDataMap.put(GlobalConstants.HEADERS, headers);
					jobDataMap.put(GlobalConstants.SCHEDULED_WORKFLOW, workflowData);

					JobDetail job = JobBuilder.newJob(WorkflowJob.class)
							.withIdentity("Qrtz_Job_Detail" + uniqueFields.stream().map(field->((Map<String,Object>)dataEntry).get(field).toString()).reduce("", String::concat),
									"workflow-jobs")
							.withDescription("Invoke Workflow Job service...").usingJobData(jobDataMap).storeDurably()
							.build();

					jobs.add(job);
				}
			}
			else {
				JobDataMap jobDataMap = new JobDataMap();
				jobDataMap.put(GlobalConstants.HEADERS, headers);
				jobDataMap.put(GlobalConstants.SCHEDULED_WORKFLOW, workflowData);

				JobDetail job = JobBuilder.newJob(WorkflowJob.class)
						.withIdentity("Qrtz_Job_Detail" + workflowData.get(WorkflowConstants.WRK_FLOW_TASK_NAME), "workflow-jobs")
						.withDescription("Invoke Workflow Job service...").usingJobData(jobDataMap).storeDurably()
						.build();

				jobs.add(job);
			}

			for (JobDetail jobEntry : jobs) {
				scheduler.addJob(jobEntry, true);
			}
			List<Trigger> triggers = buildCronTriggers(jobs);
			for (Trigger trigger : triggers) {
				if(scheduler.checkExists(trigger.getKey()))
					scheduler.rescheduleJob(trigger.getKey(), trigger);
				else
					scheduler.scheduleJob(trigger);
			}
			Object response =triggers.stream().map(t->t.getJobKey()+" will be triggered at "+t.getNextFireTime()).collect(Collectors.toList());
			logger.debug(logger.EVENT_SUCCESS, ESAPI.encoder().encodeForHTML("Job is scheduled :"+response.toString()));
			return response;

		} catch (Exception e) {
			logger.error(Logger.EVENT_FAILURE, "error inside method scheduleWorkflow", e);
			throw new ConnectException(
					"error in scheduling workfolow task " + e.getLocalizedMessage(),
					e.getCause());
		}
	}

}
