package com.eka.connectscheduler.common.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.jar.Attributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.owasp.esapi.ESAPI;
import org.owasp.esapi.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.eka.connectscheduler.service.ManifestService;
import com.eka.connectscheduler.service.WorkflowJobService;
import static com.eka.connectscheduler.utils.CommonUtils.getHeadersFromRequest;

@RestController
@RequestMapping("/common")
public class CommonController {
	final static Logger logger = ESAPI.getLogger(CommonController.class);	
	@Autowired
	ManifestService manifestService;
	@Autowired
	private WorkflowJobService workflowJobService;
	/**
	 * Gets the manifest attributes.
	 *
	 * @return the manifest attributes
	 * @throws FileNotFoundException the file not found exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@GetMapping(value = "/getManifestInfo")
	public ResponseEntity<Attributes> getManifestAttributes() {
		ResponseEntity<Attributes> respEntity=new ResponseEntity<Attributes>(manifestService.getManifestAttributes(), HttpStatus.OK);
		return respEntity;
	}
	/**
	 * To schedule a workflow/Task for passed data
	 * 
	 * @param workflowData workflow/Task with data
	 * {"workflowTaskName": "elastic_updatePriceData",
	 *	"appId": "12325a98-a959-4939-9005-4158d136afcd",	
     *		"output": {
	 *			"elastic_updatePriceData": [
	 *             {
	 *                "priceId": "CASH0000248",
	 *                "status" : "Expired"
	 *	            }
	 *	        ]
	 *	    },
	 *	    "schedulerConfig" : {
	 *	        "cronExpression" : "1 40 4 7 5 ? 2022"
	 *	    },
	 *	    "object":"ab3fc628-ecbc-43e0-81cb-aa2d5b6e5c40"
	 *}
	 *or
	 *{
	 *   "output": {
	 *       "check_limit_breach_on_change_schedule_to_active_prices_internal": {}
	 *   },
	 *   "task": "check_limit_breach_on_change_schedule_to_active_prices_internal",
	 *   "appId": "12325a98-a959-4939-9005-4158d136afcd",
	 *   "workflowTaskName": "check_limit_breach_on_change_schedule_to_active_prices_internal",
	 *   "object": "ab3fc628-ecbc-43e0-81cb-aa2d5b6e5c40",
	 *   "schedulerConfig": {
	 *       "cronExpression": "1 40 4 7 5 ? 2022"
	 *   }
	 *}
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@PostMapping(value = "/scheduleWorkflowForData")
	public @ResponseBody Object scheduleWorkflowForData(@Valid @RequestBody String workflowData, HttpServletRequest request) throws Exception{
		logger.debug(logger.EVENT_SUCCESS, ESAPI.encoder().encodeForHTML("Execution of scheduleWorkflowForData Controller method started"));
		return workflowJobService.scheduleWorkflowForData(workflowData,getHeadersFromRequest(request));
	}
}
