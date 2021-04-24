package com.lunacia.ormgenerator;

import com.lunacia.ormgenerator.mapper.ProductInfoMapper;
import com.lunacia.ormgenerator.mapper.RecordMapper;
import com.lunacia.ormgenerator.mapper.TypeMapper;
import com.lunacia.ormgenerator.mapper.UserMapper;
import com.lunacia.ormgenerator.pojo.ProductInfo;
import com.lunacia.ormgenerator.pojo.Record;
import com.lunacia.ormgenerator.pojo.Type;
import com.lunacia.ormgenerator.pojo.User;
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
	private RecordMapper recordMapper;

	@Autowired
	private UserMapper userMapper;
	@Autowired
	private ProductInfoMapper productInfoMapper;
	@Autowired
	private TypeMapper typeMapper;


	@Autowired
	private GeneratorService generatorService;

	@Test
	void cascadeProductPOJOTest() throws IOException {
		String entityName = "productInfo";
		String tableName = "product_info";
		String packageName = "com.lunacia.ormgenerator.pojo";
		Map<String, String> field = new LinkedHashMap<>();
		field.put("id", "Integer");
		field.put("code", "String");
		field.put("name", "String");
		field.put("type", "Type");

		Map<String, String> refer = new LinkedHashMap<>();
		refer.put("productInfo.type", "type.id"); //存在于表中，但不存在于实体类中，因此使用列名


		generatorService.cascadePOJOGenerator(entityName,tableName,packageName,field, refer);
	}

	@Test
	void cascadeTypePOJOTest() throws IOException {
		String entityName = "type";
		String tableName = "type";
		String packageName = "com.lunacia.ormgenerator.pojo";
		Map<String, String> field = new LinkedHashMap<>();
		field.put("id", "Integer");
		field.put("name", "String");
		field.put("pis", "List<ProductInfo>");

		Map<String, String> refer = new LinkedHashMap<>();
		refer.put("type.pis", "productInfo.id"); //指向多部分的id


		String path = generatorService.cascadePOJOGenerator(entityName,tableName,packageName,field, refer);
		System.out.println(path);
	}

	@Test
	void cascadeProductInfoTest() throws IOException {
		String entityName = "productInfo";
		String tableName = "product_info";
		String packageName = "com.lunacia.ormgenerator.mapper";
		Map<String, String> field = new LinkedHashMap<>();
		field.put("id;Integer", "id;Integer");
		field.put("code;String", "code;String");
		field.put("name;String", "name;String");
		field.put("tid;Integer", "type;Type");

		Map<String, String> refer = new LinkedHashMap<>();
		refer.put("productInfo.type", "type.id"); //存在于表中，但不存在于实体类中，因此使用列名

		Map<String, String> type = new LinkedHashMap<>();
		type.put("productInfo.type", "n:1");

		generatorService.cascadeMapperGenerator(entityName,tableName,packageName,field,refer,type);
	}

	@Test
	void cascadeTypeTest() throws IOException {
		String entityName = "type";
		String tableName = "type";
		String packageName = "com.lunacia.ormgenerator.mapper";
		Map<String, String> field = new LinkedHashMap<>();
		field.put("id;Integer", "id;Integer");
		field.put("name;String", "name;String");
		field.put("NULL", "pis;List<ProductInfo>");

		Map<String, String> refer = new LinkedHashMap<>();
		refer.put("type.pis", "productInfo.tid"); //存在于表中，但不存在于实体类中，因此使用列名

		Map<String, String> type = new LinkedHashMap<>();
		type.put("type.pis", "1:n");

		generatorService.cascadeMapperGenerator(entityName,tableName,packageName,field,refer,type);
	}

	@Test
	void cascadeMapperTest() throws IOException {
		String entityName = "user";
		String tableName = "t_user";
		String packageName = "com.lunacia.ormgenerator.mapper";
		Map<String, String> field = new LinkedHashMap<>();
		field.put("t_id;Integer", "id;Integer");
		field.put("t_username;String", "t_username;String");
		field.put("t_password;String", "t_password;String");
		field.put("NULL", "records;List<Record>");

		Map<String, String> refer = new LinkedHashMap<>();
		refer.put("user.records", "record.t_uid"); //存在于表中，但不存在于实体类中，因此使用列名

		Map<String, String> type = new LinkedHashMap<>();
		type.put("user.records", "1:n");

		String path = generatorService.cascadeMapperGenerator(entityName, tableName, packageName, field, refer, type);
//		System.out.println(path);
	}

	@Test
	void cascadeMapper12nTest() throws IOException {
		String entityName = "record";
		String tableName = "t_record";
		String packageName = "com.lunacia.ormgenerator.mapper";
		Map<String, String> field = new LinkedHashMap<>();
		field.put("t_id;Integer", "id;Integer");
		field.put("t_record;String", "t_record;String");
		field.put("t_uid;Integer", "user;User");


		Map<String, String> refer = new LinkedHashMap<>();
		refer.put("record.user", "user.id");

		Map<String, String> type = new LinkedHashMap<>();
		type.put("record.user", "n:1");

		String path = generatorService.cascadeMapperGenerator(entityName, tableName, packageName, field, refer, type);
//		System.out.println(path);
	}


	@Test
	void mapperTest() {
		Type type = typeMapper.findTypeById(1);
		System.out.println(type.getPis().size());
		ProductInfo productInfo = productInfoMapper.findProductInfoById(1);
		System.out.println(productInfo.getType().getName());
	}

	@Test
	void getPath() throws FileNotFoundException {
		String path = ResourceUtils.getURL("classpath:").getPath();
		System.out.println(path);

	}
}
