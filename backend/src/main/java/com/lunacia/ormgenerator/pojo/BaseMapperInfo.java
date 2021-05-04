package com.lunacia.ormgenerator.pojo;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class BaseMapperInfo implements Serializable {
	private String entity;
	private String table;
	private String packageName;
	private List<String> field;

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

	public List<String> getField() {
		return field;
	}

	public void setField(List<String> field) {
		this.field = field;
	}
}
