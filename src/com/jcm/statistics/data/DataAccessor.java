package com.jcm.statistics.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.jcm.statistics.Util;
import com.jcm.statistics.bean.Model;

public class DataAccessor {

    /**
     * 读取1个自然日的统计数据
     */
    public List<Model> readDailyData(String date, String type, String dimension) throws IOException {

        BufferedReader br = null;
        String ouputDir = Util.p.getProperty("dir_output");
        
        int version = 0;
        while (true) {
	        File f = new File(ouputDir, type + "_" + dimension + date + "_" + version);
	        if (f.exists()) {
		        br = new BufferedReader(new FileReader(f));
	        }
        }
    }
    
    public List<Data> getData(String date, String type, String dimension) throws IOException {
    	List<Data> data = new ArrayList<Data>();

        BufferedReader br = null;
        String ouputDir = Util.p.getProperty("dir_output") + date.substring(0, 4);
        
        int version = 0;
        String[] ds = dimension.split("_");
        while (true) {
	        File f = new File(ouputDir, type + "_" + dimension + "_" + date + "_" + version);
	        if (f.exists()) {
	        	try {
			        br = new BufferedReader(new FileReader(f));
			        version++;
	        	} catch (IOException e) {
	        		throw e;
				} finally {
	        		if (br != null) {
	        			br.close();
	        		}
	        	}
	        }
	        break;
        }
    	return data;
    }
}
