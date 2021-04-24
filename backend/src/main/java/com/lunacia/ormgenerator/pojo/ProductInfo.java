package com.lunacia.ormgenerator.pojo;

import com.lunacia.ormgenerator.pojo.Type;

public class ProductInfo {

	private Integer id;
	private String code;
	private String name;
	private Type type;

	public Integer getId() {return this.id;}
	public void setId(Integer id) {this.id=id;}
	public String getCode() {return this.code;}
	public void setCode(String code) {this.code=code;}
	public String getName() {return this.name;}
	public void setName(String name) {this.name=name;}
	public Type getType() {return this.type;}
	public void setType(Type type) {this.type=type;}
}