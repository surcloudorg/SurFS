package com.surfs.storage.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertiesFactory {

	private static final Logger LOG = LoggerFactory
			.getLogger(PropertiesFactory.class);

	private static final Map<String, Properties> map = new ConcurrentHashMap<String, Properties>();

	private static final String[] propertiesFileNames = { "/rest.properties" , "/shell.properties", "/surfs.properties"};

	static {
		LOG.info("PropertiesFactory initializing!");
		for (String propertiesFileName : propertiesFileNames) {
			PropertiesFactory.init(propertiesFileName);
			LOG.info("loading '{}'!", propertiesFileName);
		}
		LOG.info("PropertiesFactory initializing completed!");
	}

	public static Properties getProperties(String propertiesFileName) {
		if(!map.containsKey(propertiesFileName))
			throw new RuntimeException(propertiesFileName + " not found!");
		return map.get(propertiesFileName);
	}

	private static void init(String propertiesFileName) {
		InputStream fis = PropertiesFactory.class
				.getResourceAsStream(propertiesFileName);
		Properties properties = new Properties();
		try {
			properties.load(fis);
			map.put(propertiesFileName, properties);
		} catch (IOException e) {
			LOG.error("PropertiesFactory initializing, IOException:{}",
					e.getMessage());
		} finally {
			try {
				fis.close();
			} catch (IOException e) {
				LOG.error(
						"PropertiesFactory initializing inputstream closing, IOException:{}",
						e.getMessage());
			}
		}
	}

}
