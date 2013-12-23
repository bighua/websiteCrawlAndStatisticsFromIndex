package com.jcm.statistics.bean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.PivotField;
import org.apache.solr.client.solrj.response.QueryResponse;

import com.jcm.statistics.Util;

public class BaseData {

	private long total = 0;
    
    private String dateTime = null;
    
    private boolean isPolluted = false;
    
    private String offset = "0";

    public String getOffset() {
		return offset;
	}

	public void setOffset(String offset) {
		this.offset = offset;
	}

	private Map<String, Long> dataCount = new HashMap<String, Long>();
    
    private Map<String, BaseData> subData = new HashMap<String, BaseData>();

    public BaseData getSubData(String subName) {
        BaseData data = subData.get(subName);
        if (data == null) {
            data = new BaseData();
            subData.put(subName, data);
        }
        return data;
    }

    public void resetPolluted() {
        isPolluted = false;
        for (BaseData m : subData.values()) {
            m.resetPolluted();
        }
    }

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

    public int createData(QueryResponse qr, String tableCol, StringBuffer sb) {

        int subCount = 0;
        if (tableCol.indexOf(",") > 0) {
            // facet.pivot
            List<PivotField> lp = qr.getFacetPivot().get(tableCol);
            subCount = lp.size();
            createSubData(lp, sb);
        } else {
            // facet.field
            List<Count> lc = qr.getFacetField(tableCol).getValues();
            subCount = lc.size();
            for (Count c : lc) {
                String site = c.getName();
                long count = c.getCount();
                // site
                sb.append(site).append(",");
                // 总量
                sb.append(count).append(",");
                // 增量
                sb.append(count - getCount(site));
                sb.append(Util.LINE_SEPARATOR);
                setCount(site, count);
            }
        }
        return subCount;
    }

    private void createSubData(List<PivotField> lp, StringBuffer sb) {
        
        for (PivotField pf : lp) {
            String name = pf.getValue().toString().trim();
            int count = pf.getCount();
            sb.append(name).append(",");
            // 总量
            sb.append(count).append(",");
            // 增量
            sb.append(count - getCount(name));
            // 缓存数据更新
            setCount(name, count);
            List<PivotField> subData = pf.getPivot();
            if (subData != null) {
                sb.append(",").append(subData.size()).append(Util.LINE_SEPARATOR);
                BaseData m = getSubData(name);
                m.createSubData(subData, sb);
                // 子数据更新污染父数据
                isPolluted |= m.isPolluted();
            } else {
                sb.append(Util.LINE_SEPARATOR);
            }
        }
    }
}
