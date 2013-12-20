package com.jcm.statistics.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.jcm.statistics.Util;

public class DataAccessor implements IDataAccessor {

    public List<Data> getData(String date, String type, String dimension) 
            throws IOException, CloneNotSupportedException {
        
        List<Data> dataList = new ArrayList<Data>();
        BufferedReader br = null;
        String ouputDir = Util.getDirPath(Util.p.getProperty("dir_output"), date.substring(0, 4));
        
        int version = 0;
        Data origin = null;
        Data newData = null;
        Data copyData = null;
        while (true) {
            File f = new File(ouputDir, type + "_" + dimension + "_" + date + "_" + version);
            if (!f.exists()) break;
            try {
                br = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF-8"));
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
     * 取得一天的总量和增量
     * @param date 日期
     * @param type 类型
     * @param dimension 统计维度
     * @param dir 统计数据目录
     * @return
     * @throws CloneNotSupportedException 
     * @throws IOException 
     */
    public Data getDailyData(String date, String type, String dimension) throws IOException, CloneNotSupportedException {
        Data dailyData = null;
        String prefix = type + "_" + dimension + "_" + date;
        String ouputDir = Util.getDirPath(Util.p.getProperty("dir_output"), date.substring(0, 4));
        File sf = new File(ouputDir, prefix + "_" + 0);
        Data sData = getSData(sf);
        if (sData != null) {
            dailyData = new Data();
            int version = Util.getVersion(prefix);
            File ff = new File(ouputDir, prefix + "_" + version);
            Data fData = getFdata(ff);
            dailyData.setPeriodData(date, sData, fData);
        }
        return dailyData;
    }
    
    public static Data getSData(File f) throws IOException {

        Data data = null;
        BufferedReader br = null;
        if (!f.exists()) return data;
        data = new Data();
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF-8"));
            String line = null;
            List<String> items = new LinkedList<String>();
            while (!(line = br.readLine()).startsWith(Util.TAIL_FLG)) {
                if (!Util.START_FLG.equals(line)) items.add(line);
            }
            data.wrapData(items, null, false);
            return data;
        } finally {
            if (br != null) {
                br.close();
            }
        }
    }

    public static Data getFdata(File f) throws IOException {  

        Data data = new Data();
        FileChannel fcin = null;
        RandomAccessFile rf = null;
        try {
            rf = new RandomAccessFile(f, "r");
            fcin = rf.getChannel();  
            int tailSize = Integer.valueOf(Util.p.getProperty("tailSize"));
            ByteBuffer rBuffer = ByteBuffer.allocate(tailSize);
            Charset charset = Charset.forName("UTF-8");
            String tail = null;
            long offset = 0;
            int size = 3000;
            // 查找偏移量和大小
            if (fcin.read(rBuffer, fcin.size() - tailSize) != -1) {  
                rBuffer.rewind();
                tail = charset.decode(rBuffer).toString();
                int start = tail.indexOf(Util.NO_UPDATE);
                if (start != -1) {
                    String[] ts = Util.trim(tail.substring(start)).split(",");
                    offset = Long.valueOf(ts[2]);
                } else if ((start = tail.indexOf(Util.TAIL_FLG)) != -1) {
                    String[] ts = Util.trim(tail.substring(start)).split(",");
                    offset = Long.valueOf(ts[1]);
                }
                rBuffer.clear();
            }
            // 查找数据
            rBuffer = ByteBuffer.allocate(size);
            String allLines = "";
            while (fcin.read(rBuffer, offset) != -1) {  
                rBuffer.rewind();
                String dataStr = charset.decode(rBuffer).toString();
                int index = dataStr.indexOf(Util.TAIL_FLG);
                if (index > 0) {
                    allLines = dataStr.substring(0, index - 2);
                    break;
                } else {
                    size += size;
                    rBuffer = ByteBuffer.allocate(size);
                }
            }
            String[] lines = allLines.split(Util.LINE_SEPARATOR);
            List<String> items = new LinkedList<String>();
            for (String l : lines) {
                if (!Util.START_FLG.equals(l)) items.add(l);
            }
            data.wrapData(items, null, false);
            return data;
        } finally {
            rf.close();
            fcin.close();
        }
    }  
}
