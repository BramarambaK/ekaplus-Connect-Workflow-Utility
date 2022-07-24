package com.eka.connectscheduler.exception;

import java.util.List;

import com.eka.connectscheduler.error.ConnectError;

public class ConnectException extends RuntimeException {

	private static final long serialVersionUID = -6580084539119446608L;
	
	private List<ConnectError> errors;

	private String errorCode;
	
	private String category;
	
	private String message;
	
	private Object[] replacementFields;

	
	

	public ConnectException() {
		super();
	}

	public ConnectException(String message, Throwable cause) {
		super(message, cause);
		this.message= message;
	}

	public ConnectException(String message) {
		this.message=message;

	}
	
	public ConnectException(String errorCode,String category,String message) {
		this.message=message;
		this.errorCode=errorCode;
		this.category=category;
	}

	public ConnectException(String errorCode,String category,String message,List<ConnectError> connectError) {
		this.message=message;
		this.errorCode=errorCode;
		this.category=category;
		this.setErrors(connectError);
	}
	public ConnectException(String errorCode,String category) {
		this.errorCode=errorCode;
		this.category=category;
	}
	
	public Object[] getReplacementFields() {
		return replacementFields;
	}

	public ConnectException(String errorCode,String category,Object[] replacementFields) {
		this.replacementFields=replacementFields;
		this.errorCode=errorCode;
		this.category=category;
	}
		

	public ConnectException(Throwable cause) {
		super(cause);
	}

	public ConnectException(List<ConnectError> errorResponse,String errorLocalizedMessage) {
		this.message=errorLocalizedMessage;
		//super(errorLocalizedMessage);
		this.setErrors(errorResponse);
	}
	
	public ConnectException(List<ConnectError> errorResponse,String errorLocalizedMessage, String errorCode) {
		this.message=errorLocalizedMessage;
		this.setErrors(errorResponse);
		this.errorCode=errorCode;
	}


	public ConnectException(List<ConnectError> errors2) {
		// TODO Auto-generated constructor stub
		this.setErrors(errors2);
	}
	public ConnectException(String errorCode, Throwable cause,Object[] replacementFields) {
        super(cause.getMessage(),cause);
        this.message=cause.getMessage();
        this.errorCode= errorCode;
        this.replacementFields=replacementFields;
    }
	public ConnectException(String errorCode, Object[] replacementFields) {       
        this.errorCode= errorCode;
        this.replacementFields=replacementFields;
    }
	public List<ConnectError> getErrors() {
		return errors;
	}

	public void setErrors(List<ConnectError> errors) {
		this.errors = errors;
	}
	
	public void setCategory(String category) {
		this.category = category;
	}

	
	public String getMessage() {
		return message;
	}
	
	public String getErrorCode() {
		return errorCode;
	}

	public String getCategory() {
		return category;
	}
}
