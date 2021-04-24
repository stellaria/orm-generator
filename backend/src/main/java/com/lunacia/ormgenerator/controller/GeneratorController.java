package com.lunacia.ormgenerator.controller;


import com.lunacia.ormgenerator.service.DatabaseService;
import com.lunacia.ormgenerator.utils.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/gen")
public class GeneratorController {

	@Autowired
	private DatabaseService databaseService;


	public void hibernateGenerator(@RequestBody Map<String, String> body) {

	}


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

	@PostMapping("/pojo")
	public ResponseEntity generatePOJO(@RequestBody Map body) {
		String name = (String) body.get("name");
		List<String> field = (List<String>) body.get("field_list");



		return ResponseEntity.ok().build();
	}
}
