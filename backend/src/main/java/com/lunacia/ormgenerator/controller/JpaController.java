package com.lunacia.ormgenerator.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lunacia.ormgenerator.service.JPAGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/jpa/gen")
@CrossOrigin
public class JpaController {

	@Autowired
	private JPAGeneratorService jpaGeneratorService;

	@PostMapping("/gen")
	public ResponseEntity jpaGenerator(@RequestBody Map map) throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		List<Map<String, Object>> basePojoList = objectMapper.readValue((String)map.get("basePojoList"), List.class);
		List<Map<String, Object>> baseMapperList = objectMapper.readValue((String)map.get("baseMapperList"), List.class);
		List<Map<String, Object>> cascadePojoList = objectMapper.readValue((String)map.get("cascadePojoList"), List.class);
		List<Map<String, Object>> cascadeMapperList = objectMapper.readValue((String)map.get("cascadeMapperList"), List.class);

		for (Map<String, Object> basePojo: basePojoList) {
			String entity = (String) basePojo.get("entity");
			String table = (String) basePojo.get("table");
			String packageName = (String) basePojo.get("packageName");
			Map<String, String> field = objectMapper.readValue((String)basePojo.get("field"), Map.class);
			jpaGeneratorService.entityGenerator(entity,table,packageName,field);
		}

		for (Map<String, Object> baseMapper: baseMapperList) {
			String entity = (String) baseMapper.get("entity");
			String table = (String) baseMapper.get("table");
			String packageName = (String) baseMapper.get("packageName");
			Map<String, String> field = objectMapper.readValue((String)baseMapper.get("field"), Map.class);
			jpaGeneratorService.repositoryGenerator(entity,table,packageName,field);
		}

		for (Map<String, Object> cascadePojo: cascadePojoList) {
			String entity = (String) cascadePojo.get("entity");
			String table = (String) cascadePojo.get("table");
			String packageName = (String) cascadePojo.get("packageName");
			Map<String, String> field = objectMapper.readValue((String)cascadePojo.get("field"), Map.class);
			Map<String, String> refer = objectMapper.readValue((String)cascadePojo.get("refer"), Map.class);
			Map<String, String> type = objectMapper.readValue((String)cascadePojo.get("type"), Map.class);
			jpaGeneratorService.entityGenerator(entity,table,packageName,field,refer,type);
		}
		for (Map<String, Object> cascadeMapper: cascadeMapperList) {
			String entity = (String) cascadeMapper.get("entity");
			String table = (String) cascadeMapper.get("table");
			String packageName = (String) cascadeMapper.get("packageName");
			Map<String, String> field = objectMapper.readValue((String)cascadeMapper.get("field"), Map.class);
			Map<String, String> refer = objectMapper.readValue((String)cascadeMapper.get("refer"), Map.class);
			Map<String, String> type = objectMapper.readValue((String)cascadeMapper.get("type"), Map.class);
			jpaGeneratorService.repositoryGenerator(entity,table,packageName,field,refer,type);
		}

		return ResponseEntity.ok().build();
	}
}
