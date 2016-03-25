package com.surfs.storage.web.utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtils {
	
	private static final ObjectMapper mapper = new ObjectMapper();
	
	public static String objectConvertJsonString(Object obj) throws JsonProcessingException {
		return mapper.writeValueAsString(obj);
	}
	
	public static Map<String, String> jsonStringConvertMap(String jsonString,
			String... args) throws JsonProcessingException, IOException {
		if (jsonString == null) 
			return new HashMap<String, String>();
		JsonNode jsonNode = mapper.readTree(jsonString);
		Map<String, String> map = new HashMap<String, String>();
		for (String key : args) {
			map.put(key, jsonNode.get(key).toString());
		}
		return map;
	}
	
	public static <T> T stringConvertObject(String jsonString,
			TypeReference<T> type) throws IOException {
		return mapper.readValue(jsonString, type);
	}
	
	public static String objectConvertString(Object object) throws IOException {
		return mapper.writeValueAsString(object);
	}

}
