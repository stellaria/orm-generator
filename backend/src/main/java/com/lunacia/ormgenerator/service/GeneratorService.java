package com.lunacia.ormgenerator.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface GeneratorService {


	/**
	 * 生成基础的pojo类
	 * @param entityName
	 * @param tableName
	 * @param field
	 * @return pojoUrl
	 */
	String basePOJOGenerator(String entityName, String tableName, String packageName, List<String> field) throws IOException;

	/**
	 * 生成基础Mapper文件
	 * @param entityName
	 * @param tableName
	 * @param field
	 * @return mapperUrl
	 */
	String baseMapperGenerator(String entityName, String tableName, String packageName, List<String> field) throws IOException;


	/**
	 * 生成带级联的pojo文件
	 * @param entityName
	 * @param tableName
	 * @param packageName
	 * @param field key为实体类属性名，value为属性类型
	 * @param refer 关联属性 key,value均为为实体类.实体属性
	 * @return
	 * @throws IOException
	 */
	String cascadePOJOGenerator(String entityName, String tableName, String packageName, Map<String, String> field, Map<String, String> refer) throws IOException;

	/**
	 *
	 * @param entityName
	 * @param tableName
	 * @param packageName
	 * @param field key为数据库列名，value为实体类属性名和属性类型，用;分割
	 * @param refer 关联column key,value均为为实体类.实体属性
	 * @param type 对每个关联实体指明1：n还是n：1，没有多对多
	 * @return
	 * @throws IOException
	 */
	String cascadeMapperGenerator(String entityName, String tableName, String packageName,
	                              Map<String, String> field, Map<String, String> refer, Map<String, String> type) throws IOException;
}
