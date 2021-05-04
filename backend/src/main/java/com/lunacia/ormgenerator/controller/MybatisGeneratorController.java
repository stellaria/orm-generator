package com.lunacia.ormgenerator.controller;


import com.lunacia.ormgenerator.pojo.BaseMapperInfo;
import com.lunacia.ormgenerator.pojo.BasePOJOInfo;
import com.lunacia.ormgenerator.pojo.CascadeMapperInfo;
import com.lunacia.ormgenerator.pojo.CascadePOJOInfo;
import com.lunacia.ormgenerator.service.DatabaseService;
import com.lunacia.ormgenerator.service.GeneratorService;
import com.lunacia.ormgenerator.utils.JsonUtil;
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
	public ResponseEntity databaseConn(@RequestBody Map body) {
		String connUrl = (String) body.get("conn_url");
		String username = (String) body.get("conn_username");
		String password = (String) body.get("conn_passwd");
		String type = (String) body.get("conn_type");

		List list = databaseService.getSchemaInfo(connUrl,username,password,type);

		Map<String, Object> data = new HashMap<>();
		data.put("list", list);
		return ResponseEntity.ok(JsonUtil.success(data));
	}

	@PostMapping("/base/pojo")
	public ResponseEntity generatePOJO(@RequestBody List<BasePOJOInfo> infos) throws IOException {

		for (BasePOJOInfo info : infos) {
			generatorService.basePOJOGenerator(info.getEntity(), info.getTable(), info.getPackageName(), info.getField());
		}
		return ResponseEntity.ok().build();
	}

	@PostMapping("/base/mapper")
	public ResponseEntity generateMapper(
			@RequestBody Map<String, Object> infos
	) throws IOException {
		List<Map<String, Object>> list = (List<Map<String, Object>>) infos.get("infos");
		String path = null;
		for (Map<String, Object> map : list) {
			String entity = (String) map.get("entity");
			String table = (String) map.get("table");
			String packageName = (String) map.get("packageName");
			List<String> field = (List<String>) map.get("field");
			path = generatorService.baseMapperGenerator(entity,table,packageName,field);
		}
		System.out.println(path);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/cascade/pojo")
	public ResponseEntity generateCascadePOJO(@RequestBody List<CascadePOJOInfo> infos) throws IOException {

		for (CascadePOJOInfo info : infos) {
			generatorService.cascadePOJOGenerator(info.getEntity(), info.getTable(), info.getPackageName(), info.getField(), info.getRefer());
		}
		return ResponseEntity.ok().build();
	}

	@PostMapping("/cascade/mapper")
	public ResponseEntity generateCascadeMapper(@RequestBody List<CascadeMapperInfo> infos) throws IOException {
		for (CascadeMapperInfo info : infos) {
			generatorService.cascadeMapperGenerator(info.getEntity(), info.getTable(), info.getPackageName(),
					info.getField(), info.getRefer(), info.getType());
		}

		return ResponseEntity.ok().build();
	}
}
