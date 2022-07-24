package com.eka.connectscheduler.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.eka.connectscheduler.validator.util.Validate;


@Service
public class MongoOperationService {

	@Autowired
	private MongoTemplate mongoTemplate;

	public <T> List<T> list(Query query, Class<T> entityClass,
			String collectionName) {
		return mongoTemplate.find(query, entityClass,
				Validate.cleanData(collectionName));
	}

	public <T> T findOne(Query query, Class<T> entityClass,
			String collectionName) {
		return mongoTemplate.findOne(query, entityClass, collectionName);
	}

}
