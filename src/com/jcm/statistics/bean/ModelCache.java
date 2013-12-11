package com.jcm.statistics.bean;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.jcm.statistics.Util;

public class ModelCache {

//	/**
//	 * news,img,video,praise
//	 */
//	private static Map<String, Model> cache = new HashMap<String, Model>();
	
//	public static Model getModel(String type) {
//		Model m = cache.get(type);
//		if (m == null) {
//			m = new Model();
//			cache.put(type, m);
//		}
//		return m;
//	}
	
//	public static void init(String dir, String[] types, String d) throws IOException {
//		BufferedReader br = null;
//		try {
//			// 从本地缓存文件中读取数据到内存中
//			for (String t : types) {
//				String cacheName = t + "_" + d;
//				File f = new File(dir, cacheName);
//				String[] items = null;
//				Model m = new Model();
//				if (f.exists()) {
//					br = new BufferedReader(new FileReader(f));
//					items = br.readLine().split(",");
//					int i = 0;
//					m.setDateTime(items[i++]);
//					m.setTotal(Integer.valueOf(items[i++]));
//					m.setVersion(Integer.valueOf(items[i++]));
//					m.setDataSize(Integer.valueOf(items[i++]));
//					for (;i < items.length; i++) {
//						m.setCount(items[i], Long.valueOf(items[i++]));
//					}
//					br.close();
//				}
//				cache.put(cacheName, m);
//			}
//		} finally {
//			if (br != null) {
//				br.close();
//			}
//		}
//	}
}
