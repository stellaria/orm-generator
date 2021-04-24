package com.lunacia.ormgenerator.utils;

import java.util.HashMap;
import java.util.Map;

public class JsonUtil {
	public static HashMap<String, Object> success(Map<String, Object> data) {
		HashMap<String, Object> res = new HashMap<>();
		res.put("code", ResCode.SUCCESS.getCode());
		res.put("msg", "");
		res.put("data", data);
		return res;
	}

	public static HashMap<String, Object> success() {
		HashMap<String, Object> res = new HashMap<>();
		res.put("code", ResCode.SUCCESS.getCode());
		res.put("msg", "");
		return res;
	}

	public static HashMap<String, Object> fail(String msg, int code) {
		HashMap<String, Object> res = new HashMap<>();
		res.put("code", code);
		res.put("msg", msg);
		return res;
	}
}
