package com.lunacia.ormgenerator.pojo;

import java.util.Map;

public class CascadeMapperInfo {
	private String entity;
	private String table;
	private String packageName;

	Map<String, String> field;
	Map<String, String> refer;
	Map<String, String> type;

	public String getEntity () {
		return entity;
	}

	public void setEntity (String entity) {
		this.entity = entity;
	}

	public String getTable () {
		return table;
	}

	public void setTable (String table) {
		this.table = table;
	}

	public String getPackageName () {
		return packageName;
	}

	public void setPackageName (String packageName) {
		this.packageName = packageName;
	}

	public Map<String, String> getField () {
		return field;
	}

	public void setField (Map<String, String> field) {
		this.field = field;
	}

	public Map<String, String> getRefer () {
		return refer;
	}

	public void setRefer (Map<String, String> refer) {
		this.refer = refer;
	}

	public Map<String, String> getType () {
		return type;
	}

	public void setType (Map<String, String> type) {
		this.type = type;
	}
}
