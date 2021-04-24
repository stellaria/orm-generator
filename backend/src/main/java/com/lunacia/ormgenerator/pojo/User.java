package com.lunacia.ormgenerator.pojo;

import java.util.List;

public class User {
	private int id;
	private String t_username;
	private String t_password;
	private List<Record> records;

	public int getId () {
		return id;
	}

	public void setId (int id) {
		this.id = id;
	}

	public String getT_username () {
		return t_username;
	}

	public void setT_username (String t_username) {
		this.t_username = t_username;
	}

	public String getT_password () {
		return t_password;
	}

	public void setT_password (String t_password) {
		this.t_password = t_password;
	}

	public List<Record> getRecords () {
		return records;
	}

	public void setRecords (List<Record> records) {
		this.records = records;
	}
}
