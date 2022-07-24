package com.eka.connectscheduler.utils;

import java.net.URI;
import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.json.JSONObject;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import com.eka.connectscheduler.error.ConnectError;
import com.eka.connectscheduler.exception.ConnectException;
import com.eka.connectscheduler.exception.ConnectV2Exception;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class RestTemplateGetRequestBodyFactory {

	final static Logger logger = ESAPI.getLogger(RestTemplateGetRequestBodyFactory.class);

	public static final String[] MANDATORY_HEADERS_TRM = { "X-Forwarded-Host", "Referrer", "Origin" };

	private RestTemplate restTemplate = new RestTemplate();

	private RestTemplate schedulerRestTemplate = new RestTemplate();

	@Autowired
	ContextProvider contextProvider;

	@PostConstruct
	public void init() {
		this.restTemplate.setRequestFactory(getClientHttpRequestFactory(true));
		this.schedulerRestTemplate.setRequestFactory(getClientHttpRequestFactory(false));
	}

	private static final class HttpComponentsClientHttpRequestWithBodyFactory
			extends HttpComponentsClientHttpRequestFactory {
		@Override
		protected HttpUriRequest createHttpUriRequest(HttpMethod httpMethod, URI uri) {
			if (httpMethod == HttpMethod.GET) {
				return new HttpGetRequestWithEntity(uri);
			}
			return super.createHttpUriRequest(httpMethod, uri);
		}

		public void setConnectTimeout(int value) {
			super.setConnectionRequestTimeout(value);
		}

		public void setReadTimeout(int value) {
			super.setReadTimeout(value);
		}
	}

	private ClientHttpRequestFactory getClientHttpRequestFactory(boolean considerTimeOutParameters) {
		HttpComponentsClientHttpRequestWithBodyFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestWithBodyFactory();

		if (considerTimeOutParameters) {
			// Connect timeout
			clientHttpRequestFactory.setConnectTimeout(10000);

			// Read timeout
			clientHttpRequestFactory.setReadTimeout(10000);
		}
		return clientHttpRequestFactory;
	}

	private static final class HttpGetRequestWithEntity extends HttpEntityEnclosingRequestBase {
		public HttpGetRequestWithEntity(final URI uri) {
			super.setURI(uri);
		}

		@Override
		public String getMethod() {
			return HttpMethod.GET.name();
		}
	}

	public RestTemplate getSuitableRestTemplate() {
		if (contextProvider != null && contextProvider.getCurrentContext() != null
				&& contextProvider.getCurrentContext().isSchedulerRequest()) {

			logger.info(Logger.EVENT_SUCCESS, "Using Scheduler based restTemplate");

			return getSchedulerRestTemplate();
		}
		return getRestTemplate();
	}

	public RestTemplate getRestTemplate() {
		return restTemplate;
	}

	public RestTemplate getSchedulerRestTemplate() {
		return schedulerRestTemplate;
	}

	public <T, E> ResponseEntity<E> fireHttpRequest(URI uri, HttpMethod method, T requestBody, HttpHeaders headers,
			Class<E> responseEntityClass) {
		ResponseEntity<E> response = null;
		try {
			addMandatoryHeadersForTrm(headers);
			HttpEntity<T> request = new HttpEntity<T>(requestBody, headers);
			logger.debug(Logger.EVENT_SUCCESS, ("inside baseHttpClient fireHttpRequest method calling uri:" + uri
					+ ",method:" + method + ",with requestBody:" + requestBody));
			response = restTemplate.exchange(uri, method, request, responseEntityClass);
		} catch (RestClientResponseException httpException) {
			// RestClientResponseException catches all of HttpStatusCodeException,
			// HttpServerErrorException,
			// HttpClientErrorException(all rest exceptions with response body)
			ConnectError connectError = null;
			try {
				ObjectMapper oMapper = new ObjectMapper();
				connectError = oMapper.readValue(httpException.getResponseBodyAsString(), ConnectError.class);
			} catch (Exception e) {
				// do nothing as just wanted to check it is ConnectError or not
			}
			if (Objects.nonNull(connectError)) {
				logger.error(Logger.EVENT_FAILURE,
						("caught connect error inside baseHttpClient fireHttpRequest method :"
								+ new JSONObject(connectError)),
						httpException);
				throw new ConnectV2Exception(connectError, HttpStatus.valueOf(httpException.getRawStatusCode()),
						httpException);
			} else {
				logger.error(Logger.EVENT_FAILURE,
						("Http Exception while calling endpoint: " + uri.getPath() + " , API response :"
								+ httpException.getResponseBodyAsString().replace("\\", "").replace("\"", "")),
						httpException);
				throw new ConnectException(
						"Http Exception while calling endpoint: " + uri.getPath() + " , API response :"
								+ httpException.getResponseBodyAsString().replace("\\", "").replace("\"", ""));
			}
		} catch (Exception ex) {
			// this block catches ResourceAccessException,RestClientException(basically all
			// rest exceptions without response body)
			if (checkIsHttpStatusCode(ex.getLocalizedMessage())) {
				String reasonPhrase = HttpStatus.valueOf(Integer.parseInt(ex.getLocalizedMessage())).getReasonPhrase();
				logger.error(Logger.EVENT_FAILURE, ("Http Exception while calling endpoint: " + uri.getPath()
						+ " ,with reason phrase :" + reasonPhrase + ", please contact system admin"), ex);
				throw new ConnectException("Http Exception while calling endpoint: " + uri.getPath()
						+ " ,with reason phrase :" + reasonPhrase + ", please contact system admin");
			} else {
				logger.error(
						Logger.EVENT_FAILURE, ("Http Exception while calling endpoint: " + uri.getPath()
								+ ", localizedMessage:" + ex.getLocalizedMessage() + ", please contact system admin"),
						ex);
				throw new ConnectException(
						"Http Exception while calling endpoint: " + uri.getPath() + ", please contact system admin");
			}
		}
		return response;
	}

	private boolean checkIsHttpStatusCode(String input) {
		if (Objects.nonNull(input) && input.matches("\\d\\d\\d"))
			return true;
		return false;
	}

	private void addMandatoryHeadersForTrm(HttpHeaders headers) {
		HttpServletRequest currentRequest = contextProvider.getCurrentContext().getRequest();
		for (String headerName : MANDATORY_HEADERS_TRM)
			if (!ObjectUtils.isEmpty(currentRequest.getHeader(headerName))
					&& ObjectUtils.isEmpty(headers.get(headerName)))
				headers.set(headerName, currentRequest.getHeader(headerName));
	}

}
