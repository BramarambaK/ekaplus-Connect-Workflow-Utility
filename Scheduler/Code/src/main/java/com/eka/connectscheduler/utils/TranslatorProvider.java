package com.eka.connectscheduler.utils;

import java.util.LinkedHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("translatorProvider")
public class TranslatorProvider {

	@Autowired
	ContextProvider contextProvider;
	
	@SuppressWarnings("rawtypes")
	public String getTranslatedMessage(String unTranslatedValue) {

		try {
			LinkedHashMap translation = null;
			String translatedValue = null;
			LinkedHashMap<String, LinkedHashMap> translatedMap = contextProvider.getCurrentContext().getTranslation();
			String locale = contextProvider.getCurrentContext().getLocale();
			if (translatedMap != null && translatedMap.containsKey(locale)) {
				translation = translatedMap.get(locale);
				translatedValue = (String) translation.get(unTranslatedValue);
				return translatedValue;
			}
		} catch (Exception e) {
			return unTranslatedValue;
		}
		return unTranslatedValue;
	}
}
