package com.eka.connectscheduler.intercepter;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.owasp.esapi.ESAPI;
import org.owasp.esapi.Logger;
import org.quartz.JobExecutionContext;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.eka.connectscheduler.utils.ContextProvider;
import com.eka.connectscheduler.constants.GlobalConstants;
import com.eka.connectscheduler.pojo.ContextInfo;

/**
 * <p>
 * <code>PropertyInterceptor</code> make Property API call and injects the same
 * into ApplicationProps.
 * <p>
 * <hr>
 * 
 * @author Ranjan.Jha
 * @version 1.0
 */

@Component
public class ContextSetter implements AsyncHandlerInterceptor {

	@Autowired
	public ContextProvider contextProvider;

	final static Logger logger = ESAPI.getLogger(ContextSetter.class);

	private static final String X_REQUEST_ID = "X-Request-Id";

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		// setTenantNameAndRequestIdToLog(request);
		setContextDefaultValues(request);

		String requestURI = request.getRequestURI();
		String requestMethod = request.getMethod();
		logger.info(Logger.EVENT_SUCCESS, "********* Connect-Scheduler-PreHandle Started......" + "Request Details: "
				+ requestMethod + " " + requestURI);

		// RequestResponseLogger.logRequest(request);

		logger.debug(Logger.EVENT_SUCCESS,
				ESAPI.encoder()
						.encodeForHTML("headers in current request: tenant:" + request.getHeader("X-TenantID")
								+ ",authToken:" + request.getHeader("Authorization"))
						+ ",content-type:" + request.getHeader("Content-Type"));
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			@Nullable ModelAndView modelAndView) throws Exception {
		// RequestResponseLogger.logResponseHeaders(response);
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		String requestURI = request.getRequestURI();
		String requestMethod = request.getMethod();
		response.addHeader("requestId", contextProvider.getCurrentContext().getRequestId());
		if (ex != null) {
			// RequestResponseLogger.logResponseHeaderDetails(response);
		}
		logger.info(Logger.EVENT_SUCCESS, "********* Connect-Scheduler User Request completed......"
				+ "Request Details: " + requestMethod + " " + requestURI);
		removeContext();
		MDC.clear();
	}

	public void setContextDefaultValues(HttpServletRequest request) {

		ContextInfo freshContext = new ContextInfo();
		freshContext.setRequest(request);
		contextProvider.setCurrentContext(freshContext);
		freshContext.setRequestId(MDC.get(GlobalConstants.REQUEST_ID));
		freshContext.setSourceDeviceId(MDC.get(GlobalConstants.SOURCE_DEVICE_ID));
	}

	public void removeContext() {

		contextProvider.remove();
	}

	private void setTenantNameAndRequestIdToLog(JobExecutionContext context) {
		String requestId = null;
		String tenantName = null;
		String sourceDeviceId = null;

		HttpHeaders headers = (HttpHeaders) context.getJobDetail().getJobDataMap().get("headers");
		if (null != headers.get(GlobalConstants.REQUEST_ID)) {
			requestId = headers.get(GlobalConstants.REQUEST_ID).get(0);
		} else {
			requestId = UUID.randomUUID().toString().replace("-", "") + "-GEN";
		}

		if (null == headers.get(GlobalConstants.X_TENANT_ID)) {
			// tenantName = request.getServerName();
			// tenantName = tenantName.split(GlobalConstants.REGEX_DOT)[0];
			// } else {
			tenantName = headers.get(GlobalConstants.X_TENANT_ID).get(0);
		}
		MDC.put(GlobalConstants.REQUEST_ID, requestId);
		MDC.put("tenantName", tenantName);
		MDC.put(GlobalConstants.SOURCE_DEVICE_ID, sourceDeviceId);
	}

}
