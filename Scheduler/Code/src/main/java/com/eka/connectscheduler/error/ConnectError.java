package com.eka.connectscheduler.error;

import java.util.List;

import com.eka.connectscheduler.exception.ConnectException;

public class ConnectError {

	private String errorCode;
	private String errorMessage;
	private String errorContext;
	private String errorLocalizedMessage;
	
	private List<ConnectError> errors;

	public ConnectError() {
	}

	public ConnectError(String errorCode, String errorMessage) {
		super();
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}
	public ConnectError(String errorCode, String errorMessage, String errorContext) {
		super();
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
		this.errorLocalizedMessage = errorMessage;
		this.errorContext = errorContext;
	}
	public ConnectError(String errorCode, String errorMessage, List<ConnectError> errors, Throwable ex) {
		this();
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
		this.setErrors(errors);
	}
	
	
	public ConnectError(String errorCode, String errorMessage, String errorLocalizedMessage,List<ConnectError> errors, Throwable ex) {
		this();
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
		this.errorLocalizedMessage=errorLocalizedMessage;
		this.setErrors(errors);
	}
	

	public ConnectError(String errorCode, String errorMessage, String errorLocalizedMessage,List<ConnectError> errors) {
		this.setErrorCode(errorCode);
		this.setErrorMessage(errorMessage);
		this.setErrorLocalizedMessage(errorLocalizedMessage);
		this.setErrors(errors);
	}
	public ConnectError(ConnectException ce) {
		this();
		this.errorMessage = ce.getMessage();
		this.setErrors(ce.getErrors());
	}
	
	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getErrorLocalizedMessage() {
		return errorLocalizedMessage;
	}

	public void setErrorLocalizedMessage(String errorLocalizedMessage) {
		this.errorLocalizedMessage = errorLocalizedMessage;
	}

	public List<ConnectError> getErrors() {
		return errors;
	}

	public void setErrors(List<ConnectError> errors) {
		this.errors = errors;
	}

	public String getErrorContext() {
		return errorContext;
	}

	public void setErrorContext(String errorContext) {
		this.errorContext = errorContext;
	}
}