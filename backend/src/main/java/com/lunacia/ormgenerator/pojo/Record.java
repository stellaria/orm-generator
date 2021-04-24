package com.lunacia.ormgenerator.pojo;

public class Record {
	private int id;
	private String t_record;
	private User user;

	public int getId () {
		return id;
	}

	public void setId (int id) {
		this.id = id;
	}

	public String getT_record () {
		return t_record;
	}

	public void setT_record (String t_record) {
		this.t_record = t_record;
	}

	public User getUser () {
		return user;
	}

	public void setUser (User user) {
		this.user = user;
	}
}
