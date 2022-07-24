package com.eka.connectscheduler.utils;

import org.owasp.esapi.ESAPI;
import org.owasp.esapi.Logger;
import org.springframework.stereotype.Component;

import com.eka.connectscheduler.pojo.ContextInfo;

/**
 * The Class ContextProvider.
 */
@Component("contextProvider")
public class ContextProvider {

	/** The logger. */
	final static Logger logger = ESAPI.getLogger(ContextProvider.class);

	/** The current tenant. */
	private ThreadLocal<ContextInfo> currentContext = new ThreadLocal<>();

	/**
	 * Sets the current tenant.
	 *
	 * @param tenant the new current tenant
	 */
	public void setCurrentContext(ContextInfo tenant) {
		logger.debug(Logger.EVENT_SUCCESS, ("Setting currentContext to " + currentContext));
		currentContext.set(tenant);
	}

	/**
	 * Gets the current tenant.
	 *
	 * @return the current tenant
	 */
	public ContextInfo getCurrentContext() {
		return currentContext.get();
	}

	/**
	 * Clear.
	 */
	public void clear() {
		currentContext.set(null);
	}

	/**
	 * remove ThreadLocal.
	 */
	public void remove() {
		currentContext.remove();
	}

}
