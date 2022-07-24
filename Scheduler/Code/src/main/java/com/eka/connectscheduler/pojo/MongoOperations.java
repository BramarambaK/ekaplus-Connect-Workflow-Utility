package com.eka.connectscheduler.pojo;

public class MongoOperations {
	private String fieldName;
	private Object value;
	private String operator;
	public MongoOperations(String fieldName, Object value, String operator) {
		this.fieldName = fieldName;
		this.value = value;
		this.operator = operator;
	}
	public MongoOperations() {		
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}
	@Override
	public String toString() {
		return "MongoOperations [fieldName=" + fieldName + ", value=" + value + ", operator=" + operator+ "]";
	}
	

}
