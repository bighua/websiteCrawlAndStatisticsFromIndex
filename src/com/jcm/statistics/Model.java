package com.jcm.statistics;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Model {

	private Map<String, Long> modelCount = new HashMap<String, Long>();
	
	private String dateTime = null;
//	private String type = null;
	
	private boolean isPolluted = false;
	
	
	public void setModelCount(String model, long count) throws IOException {
		if (count != modelCount.put(model, count)) {
			isPolluted = true;
		}
	}
	
	public long getModelCount(String model) throws IOException {
		return modelCount.get(model);
	}
	
	public boolean isPolluted() {
		return isPolluted;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

//	public void setType(String type) {
//		this.type = type;
//	}
	
	public void updateModel(String dir, String type) throws IOException {
		BufferedWriter writer = null;
		try {
			if (!isPolluted) return;
			File d = new File(dir);
			if (!d.exists()) {
				d.mkdirs();
			}
			writer = new BufferedWriter(new FileWriter(new File(dir, type)));
			StringBuffer sb = new StringBuffer();
			sb.append(dateTime).append(",");
			for (String key : modelCount.keySet()) {
				sb.append(key).append(",").append(modelCount.get(key)).append(",");
			}
			writer.write(sb.substring(0, sb.length() - 1).toString());
		    writer.flush();
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}
}
