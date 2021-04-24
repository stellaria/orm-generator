package com.lunacia.ormgenerator.pojo;

import com.lunacia.ormgenerator.pojo.ProductInfo;

import java.util.List;

public class Type {

	private Integer id;
	private String name;
	private List<ProductInfo> pis;

	public Integer getId() {return this.id;}
	public void setId(Integer id) {this.id=id;}
	public String getName() {return this.name;}
	public void setName(String name) {this.name=name;}
	public List<ProductInfo> getPis() {return this.pis;}
	public void setPis(List<ProductInfo> pis) {this.pis=pis;}
}