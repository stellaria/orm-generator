package com.lunacia.ormgenerator.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


public interface DatabaseService {


	List<List<Map>> getSchemaInfo(String url, String username, String passwd, String type) throws Exception;

//	List<Map> getTableInfo(String url, String username, String passwd, String type, String tableName);


//	void generatePOJO(String name, List<String> field);
}
