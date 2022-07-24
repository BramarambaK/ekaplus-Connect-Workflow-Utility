package com.eka.connectscheduler.validator.util;

import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;



public class Validate {
	
	/**
	 * Validate.
	 *
	 * @param value
	 *            the value
	 * @return true, if successful
	 */
	public static boolean validate(Object... value) {

		for (Object val : value) {
			if (null == val)
				return false;
			if(val instanceof String){
			if (StringUtils.isEmpty((String)val))
				return false;
			}
			
			if(val instanceof List){
				if (((List<Object>) val).size()==0)
					return false;
				}
		}
		return true;

	}
	
	public static boolean lengthValid(String str, int minLength, int maxLength) {
		if (minLength <= str.length() && str.length() <= maxLength) {
			return true;
		} else
			return false;
	}

	public static boolean notNull(String str) {
		if (str == null) {
			return false;
		} else
			return true;
	}

	public static String cleanData(String str){
		//Use Untrusted Data validation routine as below
				String s = Normalizer.normalize(null!= str?str:"", Form.NFKC);
				// Validate
				Pattern pattern = Pattern.compile("[<>]");
				Matcher matcher = pattern.matcher(s);
				if (matcher.find()) {
				  // Found blacklisted tag
				  throw new IllegalStateException("Untrusted Data detected");
				} else {
					return StringUtils.normalizeSpace(s);
				}
	}


}
