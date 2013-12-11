package com.jcm.statistics.bean;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.QueryResponse;

import com.jcm.statistics.Util;
import com.jcm.statistics.cache.Cache;

public class Model extends BaseData {

    public void writeFromCache(StringBuffer sb) {
        for (String key : dataCount.keySet()) {
            sb.append(key).append(",").append(dataCount.get(key)).append(Util.LINE_SEPARATOR);
        }
    }
    
    public void readIntoCache(BufferedReader br) throws NumberFormatException, IOException {
        String line = "";
        while ((line = br.readLine()) != null) {
            String[] siteInfo = line.split(",");
            setCount(siteInfo[0], Long.valueOf(siteInfo[1]));
        }
    }

    public void createData(QueryResponse qr, String tableCol, Cache cache, String dimension, StringBuffer sb) {

        List<Count> lc = qr.getFacetField(tableCol).getValues();
        for (Count c : lc) {
            String model = c.getName();
            long count = c.getCount();
//            if (cache.getId(model) != null) {
                // 车型名
                sb.append(model).append(",");
                // 总量
                sb.append(count).append(",");
                // 增量
                sb.append(count - getCount(model));
                sb.append(Util.LINE_SEPARATOR);
                setCount(model, count);
//            }
        }
    }
    
}
