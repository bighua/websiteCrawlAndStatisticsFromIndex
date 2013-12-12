package com.jcm.statistics.bean;

import java.util.List;

import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.QueryResponse;

import com.jcm.statistics.Util;
import com.jcm.statistics.cache.Cache;

public class Model extends BaseData {

    public int createData(QueryResponse qr, String tableCol, Cache cache, String dimension, StringBuffer sb) {

        List<Count> lc = qr.getFacetField(tableCol).getValues();
        for (Count c : lc) {
            String model = c.getName().trim();
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
        return lc.size();
    }
    
}
