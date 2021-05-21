package com.lunacia.ormgenerator.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.lunacia.ormgenerator.exception.UnknownDatabaseException;
import com.lunacia.ormgenerator.exception.UsernameOrPasswordErrorException;
import com.lunacia.ormgenerator.pojo.BaseMapperInfo;
import com.lunacia.ormgenerator.pojo.BasePOJOInfo;
import com.lunacia.ormgenerator.pojo.CascadeMapperInfo;
import com.lunacia.ormgenerator.pojo.CascadePOJOInfo;
import com.lunacia.ormgenerator.service.DatabaseService;
import com.lunacia.ormgenerator.service.GeneratorService;
import com.lunacia.ormgenerator.utils.JsonUtil;
import com.lunacia.ormgenerator.utils.ResCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/mybatis/gen")
@CrossOrigin
public class MybatisGeneratorController {

	@Autowired
	private DatabaseService databaseService;

	@Autowired
	private GeneratorService generatorService;


	@PostMapping("/conn")
	public ResponseEntity databaseConn(@RequestBody Map body) throws Exception {
		String connUrl = (String) body.get("conn_url");
		String username = (String) body.get("conn_username");
		String password = (String) body.get("conn_passwd");
		String type = (String) body.get("conn_type");

		List<List<Map>> list = null;
		list = databaseService.getSchemaInfo(connUrl,username,password,type);



		Map<String, Object> data = new HashMap<>();
		data.put("list", list);
		data.put("tableName", list.get(0).get(0).get("TABLE_SCHEMA"));
		return ResponseEntity.ok(JsonUtil.success(data));
	}

//	@PostMapping("/base/pojo")
//	public ResponseEntity generatePOJO(@RequestBody List<BasePOJOInfo> infos) throws IOException {
//
//		for (BasePOJOInfo info : infos) {
//			generatorService.basePOJOGenerator(info.getEntity(), info.getTable(), info.getPackageName(), info.getField());
//		}
//		return ResponseEntity.ok().build();
//	}
//
//	@PostMapping("/base/mapper")
//	public ResponseEntity generateMapper(
//			@RequestBody Map<String, Object> infos
//	) throws IOException {
//		List<Map<String, Object>> list = (List<Map<String, Object>>) infos.get("infos");
//		String path = null;
//		for (Map<String, Object> map : list) {
//			String entity = (String) map.get("entity");
//			String table = (String) map.get("table");
//			String packageName = (String) map.get("packageName");
//			List<String> field = (List<String>) map.get("field");
//			path = generatorService.baseMapperGenerator(entity,table,packageName,field);
//		}
//		return ResponseEntity.ok().build();
//	}
//
//	@PostMapping("/cascade/pojo")
//	public ResponseEntity generateCascadePOJO(@RequestBody List<CascadePOJOInfo> infos) throws IOException {
//
//		for (CascadePOJOInfo info : infos) {
//			generatorService.cascadePOJOGenerator(info.getEntity(), info.getTable(), info.getPackageName(), info.getField(), info.getRefer());
//		}
//		return ResponseEntity.ok().build();
//	}
//
//	@PostMapping("/cascade/mapper")
//	public ResponseEntity generateCascadeMapper(@RequestBody List<CascadeMapperInfo> infos) throws IOException {
//		for (CascadeMapperInfo info : infos) {
//			generatorService.cascadeMapperGenerator(info.getEntity(), info.getTable(), info.getPackageName(),
//					info.getField(), info.getRefer(), info.getType());
//		}
//
//		return ResponseEntity.ok().build();
//	}

	@PostMapping("/gen")
	public ResponseEntity generate(@RequestBody Map map) throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		List<Map<String, Object>> basePojoList = (List<Map<String, Object>>) map.get("basePojoList");
		List<Map<String, Object>> baseMapperList = objectMapper.readValue((String)map.get("baseMapperList"), List.class);
		List<Map<String, Object>> cascadePojoList = objectMapper.readValue((String)map.get("cascadePojoList"), List.class);
		List<Map<String, Object>> cascadeMapperList = objectMapper.readValue((String)map.get("cascadeMapperList"), List.class);

		for (Map<String, Object> basePojo: basePojoList) {
			String entity = (String) basePojo.get("entity");
			String table = (String) basePojo.get("table");
			String packageName = (String) basePojo.get("packageName");
			List<String> field = (List<String>) basePojo.get("field");
			generatorService.basePOJOGenerator(entity,table,packageName,field);
		}

		for (Map<String, Object> baseMapper: baseMapperList) {
			String entity = (String) baseMapper.get("entity");
			String table = (String) baseMapper.get("table");
			String packageName = (String) baseMapper.get("packageName");
			Map<String, String> field = objectMapper.readValue((String)baseMapper.get("field"), Map.class);
			generatorService.baseMapperGenerator(entity,table,packageName,field);
		}

		for (Map<String, Object> cascadePojo: cascadePojoList) {
			String entity = (String) cascadePojo.get("entity");
			String table = (String) cascadePojo.get("table");
			String packageName = (String) cascadePojo.get("packageName");
			List<String> field = (List<String>) cascadePojo.get("field");
			Map<String, String> refer = objectMapper.readValue((String)cascadePojo.get("refer"), Map.class);
			generatorService.cascadePOJOGenerator(entity,table,packageName,field,refer);
		}
		for (Map<String, Object> cascadeMapper: cascadeMapperList) {
			String entity = (String) cascadeMapper.get("entity");
			String table = (String) cascadeMapper.get("table");
			String packageName = (String) cascadeMapper.get("packageName");
			Map<String, String> field = objectMapper.readValue((String)cascadeMapper.get("field"), Map.class);
			Map<String, String> refer = objectMapper.readValue((String)cascadeMapper.get("refer"), Map.class);
			Map<String, String> type = objectMapper.readValue((String)cascadeMapper.get("type"), Map.class);
			generatorService.cascadeMapperGenerator(entity,table,packageName,field,refer,type);
		}

		return ResponseEntity.ok().build();
	}
}
