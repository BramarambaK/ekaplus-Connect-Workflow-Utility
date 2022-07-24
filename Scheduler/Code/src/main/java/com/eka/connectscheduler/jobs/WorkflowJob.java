package com.eka.connectscheduler.jobs;

import java.util.UUID;

import org.owasp.esapi.ESAPI;
import org.owasp.esapi.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.http.HttpHeaders;

import com.eka.connectscheduler.constants.GlobalConstants;
import com.eka.connectscheduler.service.WorkflowJobService;

@Component
public class WorkflowJob implements Job {

	final static Logger logger = ESAPI.getLogger(WorkflowJob.class);

	@Autowired
	private WorkflowJobService jobService;

	public void execute(JobExecutionContext context) throws JobExecutionException {

		try {
			setTenantNameAndRequestIdToLog(context);
			jobService.executeSampleJob(context);

		} catch (Exception e) {
			logger.error(Logger.EVENT_FAILURE, "WorkflowJob: Error while executing workflow job", e);
		} finally {
			MDC.clear();
		}
	}

	private void setTenantNameAndRequestIdToLog(JobExecutionContext context) {
		String requestId = null;
		String tenantName = null;

		HttpHeaders headers = (HttpHeaders) context.getJobDetail().getJobDataMap().get(GlobalConstants.HEADERS);
		requestId = UUID.randomUUID().toString().replace("-", "") + "-GEN";
		if (null != headers.get(GlobalConstants.X_TENANT_ID)) {
			headers.get(GlobalConstants.X_TENANT_ID).get(0);
			tenantName = headers.get(GlobalConstants.X_TENANT_ID).get(0);
		}
		MDC.put(GlobalConstants.REQUEST_ID, requestId);
		MDC.put(GlobalConstants.TENANT_NAME, tenantName);
	}

}
