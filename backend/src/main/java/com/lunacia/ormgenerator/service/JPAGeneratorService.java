package com.lunacia.ormgenerator.service;


import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface JPAGeneratorService {

	/**
	 *
	 * @param entityName
	 * @param tableName
	 * @param packageName
	 * @param field key为数据库列名;数据库列类型 value为实体类属性名;属性类型
	 * @return
	 * @throws IOException
	 */
	String entityGenerator(String entityName, String tableName, String packageName, Map<String, String> field) throws IOException;

	/**
	 *
	 * @param entityName
	 * @param tableName
	 * @param packageName
	 * @param field key为数据库列名;数据库列类型 value为实体类属性名;属性类型
	 * @param refer key为当前实体名.属性名 value为指向的实体名.属性名
	 * @param type 1:n n:1 1:1
	 * @return
	 */
	String entityGenerator(String entityName, String tableName, String packageName, Map<String, String> field, Map<String, String> refer, Map<String, String> type) throws IOException;


	/**
	 *
	 * @param entityName
	 * @param tableName
	 * @param packageName
	 * @param field key为数据库列名;数据库列类型 value为实体类属性名;属性类型
	 * @return
	 */
	String repositoryGenerator(String entityName, String tableName, String packageName, Map<String, String> field) throws IOException;

	/**
	 *
	 * @param entityName
	 * @param tableName
	 * @param packageName
	 * @param field key为数据库列名;数据库列类型 value为实体类属性名;属性类型
	 * @param refer key为当前实体名.属性名 value为指向的实体名.属性名
	 * @param type 1:n n:1 1:1
	 * @return
	 */
	String repositoryGenerator(String entityName, String tableName, String packageName, Map<String, String> field, Map<String, String> refer, Map<String, String> type) throws IOException;

}
