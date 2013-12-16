package com.jcm.statistics.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.jcm.statistics.Util;

public class DataAccessor {

    public List<Data> getData(String date, String type, String dimension) 
            throws IOException, CloneNotSupportedException {
        
        List<Data> dataList = new ArrayList<Data>();
        Util.initResource();
        BufferedReader br = null;
        String ouputDir = Util.p.getProperty("dir_output") + date.substring(0, 4);
        
        int version = 0;
        Data origin = null;
        Data newData = null;
        Data copyData = null;
        while (true) {
            File f = new File(ouputDir, type + "_" + dimension + "_" + date + "_" + version);
            if (!f.exists()) break;
            try {
                br = new BufferedReader(new FileReader(f));
                version++;
                String line = null;
                List<String> items = new LinkedList<String>();
                boolean isStart = false;
                while ((line = br.readLine()) != null) {
                    if (line.startsWith(Util.NO_UPDATE)) {
                        dataList.add(copyData.ShadowClone(line.split(",")[1]));
                    } else if (Util.START_FLG.equals(line)) {
                        // 第一个start
                        isStart = (origin == null) ? false : true;
                    // 时间开始的记录
                    } else if (line.startsWith(date) && line.length() == 14) {
                        newData = new Data();
                        items.add(line);
                    } else if (Util.TAIL_FLG.equals(line)) {
                        newData.wrapData(items, origin, isStart);
                        dataList.add(newData);
                        origin = newData;
                        copyData = newData.clone();
                        // 清空接收下条数据
                        items.clear();
                        isStart = false;
                    } else {
                        items.add(line);
                    }
                }
            } finally {
                if (br != null) {
                    br.close();
                }
            }
        }
        return dataList;
    }
    
    /**
     * 取得一天的总量和增量(一级)
     * @param date
     * @param type
     * @param dimension
     * @return
     * @throws CloneNotSupportedException 
     * @throws IOException 
     */
    public Data getDailyData(String date, String type, String dimension) throws IOException, CloneNotSupportedException {
        List<Data> allData = getData(date, type, dimension);
        Data dailyData = null;
        if (allData.size() != 0) {
            Data sData = allData.get(0);
            Data fData = allData.get(allData.size() - 1);
            dailyData = new Data();
            dailyData.setPeriodData(date, sData, fData);
        }
        return dailyData;
    }
}
