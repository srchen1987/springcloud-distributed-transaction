package com.pttl.distributed.transaction.util;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * 
 * @ClassName: JsonUtils
 * @Description: 一个json 工具类 直接copy github上的
 * @author: srchen
 * @date: 2019年11月02日 上午1:03:22
 */
public class JsonUtils {
	private static Logger log = LoggerFactory.getLogger(JsonUtils.class);

	/**
	 * 初始化变量
	 */
	private static ObjectMapper mapper = new ObjectMapper();

	static {
		// 解决实体未包含字段反序列化时抛出异常
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		// 对于空的对象转json的时候不抛出错误
		mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

		// 允许属性名称没有引号
		mapper.configure(Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);

		// 允许单引号
		mapper.configure(Feature.ALLOW_SINGLE_QUOTES, true);
	}

	public static String objectToJson(Object obj) {
		String json = null;
		try {
			json = mapper.writeValueAsString(obj);
		} catch (Exception e) {
			log.error("", e);
		}
		return json;
	}

	public static byte[] objectToJsonByte(Object obj) {
		byte[] json = null;
		try {
			json = mapper.writeValueAsBytes(obj);
		} catch (Exception e) {
			log.error("", e);
		}
		return json;
	}

	public static <T> T jsonToClass(String json, Class<T> beanType) {
		T t = null;
		try {
			t = mapper.readValue(json, beanType);
		} catch (Exception e) {
			log.error("", e);
		}
		return t;
	}

	public static <T> T jsonToClass(byte[] jsonByte, Class<T> beanType) {
		T t = null;
		try {
			t = mapper.readValue(jsonByte, beanType);
		} catch (Exception e) {
			log.error("", e);
		}
		return t;
	}

	public static Map<String, Object> jsonToMap(String json) {
		Map<String, Object> map = null;
		try {
			map = mapper.readValue(json, new TypeReference<Map<String, Object>>() {
			});
		} catch (Exception e) {
			log.error("", e);
		}
		return map;
	}

	public static <T> List<T> jsonToList(String json, Class<T> beanType) {
		List<T> list = null;
		try {
			JavaType javaType = mapper.getTypeFactory().constructParametricType(List.class, beanType);
			list = mapper.readValue(json, javaType);
		} catch (Exception e) {
			log.error("", e);
		}
		return list;
	}

}
