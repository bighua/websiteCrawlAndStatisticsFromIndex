package com.jcm.statistics.bean;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.solr.client.solrj.response.QueryResponse;

import com.jcm.statistics.Util;
import com.jcm.statistics.cache.Cache;

public abstract class BaseData {

    protected long total = 0;
    
    protected String dateTime = null;
//    private String type = null;

//    public void setType(String type) {
//        this.type = type;
//    }
    
    // 用于分割文件(200M上限)
    protected int version = 0;
    
    protected long dataSize = Long.valueOf(Util.p.getProperty("maxsize"));
    
    protected boolean isPolluted = false;

    protected Map<String, Long> dataCount = new HashMap<String, Long>();

    public void setCount(String name, long count) {
        Long originalCount = dataCount.put(name, count);
        if (originalCount == null || count != originalCount) {
            isPolluted = true;
        }
    }
    
    public long getCount(String name) {
        Long count = dataCount.get(name);
        if (count == null) {
            count = 0L;
        }
        return count;
    }
    
    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        if (this.total != total) {
            isPolluted = true;
        }
        this.total = total;
    }

    public boolean isPolluted() {
        return isPolluted;
    }
    
    public void setPolluted(boolean polluted) {
        this.isPolluted = polluted;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
    
    public String getDateTime() {
        return dateTime;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public void setDataSize(long dataSize) {
        this.dataSize = dataSize;
    }

    public long getDataSize() {
        return dataSize;
    }

    public abstract void createData(QueryResponse qr, String tableCol, Cache cache, String dimension, StringBuffer sb) throws IOException;
    
    public abstract void readIntoCache(BufferedReader br) throws NumberFormatException, IOException;
    
    public abstract void writeFromCache(StringBuffer sb);
}
