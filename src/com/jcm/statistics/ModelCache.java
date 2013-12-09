package com.jcm.statistics;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ModelCache {

	/**
	 * news,img,video,praise
	 */
	private static Map<String, Model> cache = new HashMap<String, Model>();
	
	/**
	 * model:id pair
	 */
	private static Map<String, Long> modelId = new HashMap<String, Long>();
	
	public static Model getModel(String type) {
		Model m = cache.get(type);
		if (m == null) {
			m = new Model();
			cache.put(type, m);
		}
		return m;
	}
	
	public static void init(String dir, String[] types) throws IOException {
		BufferedReader br = null;
		try {
			for (String t : types) {
				File f = new File(dir, t);
				String[] items = null;
				Model m = new Model();
				if (f.exists()) {
					br = new BufferedReader(new FileReader(f));
				    String data = br.readLine();
					items = data.split(",");
					for (int i = 1; i < items.length; i++) {
						m.setModelCount(items[i], Long.valueOf(items[i++]));
					}
					m.setDateTime(items[0]);
					br.close();
				} else {
					items = new String(Util.p.getProperty("models").getBytes("ISO8859-1"), "UTF-8").split(",");
					for (int i = 0; i < items.length; i++) {
						m.setModelCount(items[i+1], 0L);
						modelId.put(items[i], Long.valueOf(items[i++]));
					}
				}
				cache.put(t, m);
			}
		} finally {
			if (br != null) {
				br.close();
			}
		}
	}
	
	public static Long getId(String model) {
		return modelId.get(model);
	}
}
