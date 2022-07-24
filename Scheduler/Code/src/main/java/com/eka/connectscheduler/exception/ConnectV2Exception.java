package com.eka.connectscheduler.exception;

import java.util.Objects;

import org.springframework.http.HttpStatus;

import com.eka.connectscheduler.error.ConnectError;


/**
 * {@code ConnectV2Exception} is the wrapper to carry connect exception that are
 * caught and identified in connect API calls and propagate throughout this
 * repository
 **/
public class ConnectV2Exception extends RuntimeException {

	private static final long serialVersionUID = -6580084539119446609L;

	private ConnectError connectError;
	private HttpStatus httpStatusCode;

	public ConnectError getConnectError() {
		return connectError;
	}

	public HttpStatus getHttpStatusCode() {
		return httpStatusCode;
	}

	public ConnectV2Exception(ConnectError connectError, HttpStatus httpStatusCode, Throwable cause) {
		super(cause);
		connectError.setErrorMessage(
				Objects.nonNull(connectError.getErrorLocalizedMessage()) ? connectError.getErrorLocalizedMessage()
						: connectError.getErrorMessage());
		this.connectError = connectError;
		this.httpStatusCode = httpStatusCode;
	}

}
