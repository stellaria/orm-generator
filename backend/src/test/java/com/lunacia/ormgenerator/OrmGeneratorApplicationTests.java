package com.lunacia.ormgenerator;

import com.lunacia.ormgenerator.service.GeneratorService;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@SpringBootTest
@MapperScan(value = "com.lunacia.ormgenerator.mapper")
class OrmGeneratorApplicationTests {

	@Autowired
	private GeneratorService generatorService;

	@Test
	void nto1Test() throws IOException {
		String entity="ProductInfo";
		String table="product_info";
		String packageName="com.lunacia.mapper";
		Map<String, String> field = new LinkedHashMap<>();
		field.put("id;Integer","id;Integer");
		field.put("code;String","code;String");
		field.put("name;String", "name;String");
		field.put("tid;Integer", "type;Type");
		Map<String, String> refer = new LinkedHashMap<>();
		refer.put("ProductInfo.type", "Type.id");
		Map<String, String> type = new LinkedHashMap<>();
		type.put("ProductInfo.type", "n:1");

		generatorService.cascadeMapperGenerator(entity,table,packageName,field,refer,type);

	}

	@Test
	void oneToNTest() throws IOException {
		String entity="Type";
		String table="type";
		String packageName="com.lunacia.mapper";
		Map<String, String> field = new LinkedHashMap<>();
		field.put("id;Integer","id;Integer");
		field.put("name;String", "name;String");
		field.put("NULL", "pis;List<ProductInfo>");
		Map<String, String> refer = new LinkedHashMap<>();
		refer.put("Type.pis", "ProductInfo.tid");
		Map<String, String> type = new LinkedHashMap<>();
		type.put("Type.pis", "1:n");


		generatorService.cascadeMapperGenerator(entity,table,packageName,field,refer,type);
	}
}
