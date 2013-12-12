package com.jcm.statistics.bean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.PivotField;
import org.apache.solr.client.solrj.response.QueryResponse;

import com.jcm.statistics.Util;
import com.jcm.statistics.cache.Cache;

public class Site extends BaseData {

    private Map<String, BaseData> siteModel = new HashMap<String, BaseData>();
    
    public BaseData getSiteModel(String sm) {
        BaseData m = siteModel.get(sm);
        if (m == null) {
            m = new Model();
            siteModel.put(sm, m);
        }
        return m;
    }
    
    public void resetPolluted() {
        super.resetPolluted();
        for (BaseData m : siteModel.values()) {
            m.setPolluted(false);
        }
    }

    public void createData(QueryResponse qr, String tableCol, Cache cache, String dimension, StringBuffer sb) {

        if (tableCol.indexOf(",") > 0) {
            // "sitename,urlmodelcom"
            List<PivotField> lp = qr.getFacetPivot().get(tableCol);
            for (PivotField pf : lp) {
                String sitename = pf.getValue().toString();
                int siteCount = pf.getCount();
                sb.append(sitename).append(",");
                // 总量
                sb.append(siteCount).append(",");
                // 增量
                sb.append(siteCount - getCount(sitename)).append(",");
                // 缓存数据更新
                setCount(sitename, siteCount);
                List<PivotField> models = pf.getPivot();
                if (models == null) {
                    sb.append(0);
                } else {
                    // 子分类个数
                    sb.append(models.size());
                }
                sb.append(Util.LINE_SEPARATOR);
                if (models != null) {
                    for (PivotField subData : models) {
                        // model name
                        String model = subData.getValue().toString().trim();
//                        // 不存在的车型不统计
//                        if (cache.getId(model) != null) {
                            int count = subData.getCount();
                            BaseData m = getSiteModel(sitename);
                            // 车型名
                            sb.append(model).append(",");
                            // 总量
                            sb.append(count).append(",");
                            // 增量
                            sb.append(count - m.getCount(model));
                            sb.append(System.getProperty("line.separator"));
                            m.setCount(model, count);
                            // 子数据更新污染父数据
                            isPolluted |= m.isPolluted();
//                        }
                    }
                }
            }
        } else {
            List<Count> lc = qr.getFacetField(tableCol).getValues();
            for (Count c : lc) {
                String site = c.getName();
                long count = c.getCount();
                // site
                sb.append(site).append(",");
                // 总量
                sb.append(count).append(",");
                // 增量
                sb.append(count - getCount(site));
                sb.append(System.getProperty("line.separator"));
                setCount(site, count);
            }
        }
    }
}
