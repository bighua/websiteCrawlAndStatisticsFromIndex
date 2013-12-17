package com.jcm.statistics.cache;

import java.util.HashMap;
import java.util.Map;

import com.jcm.statistics.Util;
import com.jcm.statistics.bean.BaseData;

public class Cache {

    /**
     * news_model,img_site_model,video_site,praise_site
     */
    private Map<String, BaseData> baseData = new HashMap<String, BaseData>();
    
    private Map<String, Integer> version = new HashMap<String, Integer>();
    
    /**
     * 车型：车型ID
     */
    private Map<String, Long> modelId = new HashMap<String, Long>();
    
    public BaseData getBaseData(String key) {
        BaseData data = baseData.get(key);
        if (data == null) {
            data = new BaseData();
            baseData.put(key, data);
        }
        return data;
    }
    
    public Long getId(String model) {
        return modelId.get(model);
    }
    
    public int getVersion(String key) {
        Integer ver = version.get(key);
        if (ver == null) {
            ver = Util.getVersion(key);
            version.put(key, ver);
        }
        return ver;
    }
    
    public void setVersion(String key, int ver) {
        version.put(key, ver);
    }
    
    public void resetPollutedFlg(String key) {
        BaseData data = baseData.get(key);
        data.resetPolluted();
    }
}
