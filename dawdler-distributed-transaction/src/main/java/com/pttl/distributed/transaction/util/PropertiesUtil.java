package com.pttl.distributed.transaction.util;


import java.io.IOException;
import java.util.Properties;

import org.springframework.core.io.support.PropertiesLoaderUtils;

public class PropertiesUtil {
	public static int getIfNullReturnDefaultValueInt(String key, int defaultValue, Properties ps) {
		Object value = ps.get(key);
		if (value != null) {
			try {
				return Integer.parseInt(value.toString());
			} catch (Exception e) {
			}
		}
		return defaultValue;
	}

	public static long getIfNullReturnDefaultValueLong(String key, long defaultValue, Properties ps) {
		Object value = ps.get(key);
		if (value != null) {
			try {
				return Long.parseLong(value.toString());
			} catch (Exception e) {
			}
		}
		return defaultValue;
	}

	public static boolean getIfNullReturnDefaultValueBoolean(String key, boolean defaultValue, Properties ps) {
		Object value = ps.get(key);
		if (value != null) {
			try {
				return Boolean.parseBoolean(value.toString());
			} catch (Exception e) {
			}
		}
		return defaultValue;
	}
	
//	public static Properties loadActiveProfileProperties(String fileName) throws IOException {
//		String activeProfile = System.getProperty("spring.profiles.active");
//		return loadAllProperties(fileName +(activeProfile != null ? "-"+activeProfile : "")+".properties");
//	}
	
	public static Properties loadAllProperties(String fileName) throws IOException {
		return PropertiesLoaderUtils.loadAllProperties(fileName);
	}
	
}
