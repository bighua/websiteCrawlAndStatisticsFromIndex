package com.jcm.statistics.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataItem implements Cloneable {

    private String name = null;
    
    private Long total = 0L;
    
    private Long increment = 0L;
    
    private Map<String, DataItem> subItems = null;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Long getIncrement() {
        return increment;
    }

    public void setIncrement(Long increment) {
        this.increment = increment;
    }

    public Map<String, DataItem> getSubItems() {
        return subItems;
    }

    public void setSubItems(Map<String, DataItem> subItems) {
        this.subItems = subItems;
    }

    public void wrapItems(List<String> dataList) {
        String[] items = dataList.remove(0).split(",");
        name = items[0];
        total = Long.valueOf(items[1]);
        increment = Long.valueOf(items[2]);
        // 非最后一层
        if (items.length == 4) {
            int subCount = Integer.valueOf(items[3]);
            subItems = new HashMap<String, DataItem>();
            while (subCount-- > 0) {
                DataItem sub = new DataItem();
                sub.wrapItems(dataList);
                subItems.put(sub.getName(), sub);
            }
        }
    }
    
    public void resetInc(DataItem origin) {
        increment = total - (origin == null ? 0 : origin.getTotal());
        if (subItems != null) {
            for (String key : subItems.keySet()) {
                subItems.get(key).resetInc(origin.getSubItems().get(key));
            }
        }
    }
    
    public DataItem clone() {
        DataItem di = new DataItem();
        di.setName(name);
        di.setTotal(total);
        if (subItems != null) {
            Map<String, DataItem> sub = new HashMap<String, DataItem>();
            for (String key : subItems.keySet()) {
                sub.put(key, subItems.get(key).clone());
            }
            di.setSubItems(sub);
        }
        return di;
    }
    
    public void setPeriodData(DataItem sData, DataItem fData) {

        Long sTotal = sData == null ? 0 : sData.getTotal();
        Long fTotal = fData.getTotal();
        total = fTotal;
        increment = fTotal - sTotal + (sData == null ? 0 : sData.getIncrement());
        if (fData.getSubItems() != null) {
            subItems = new HashMap<String, DataItem>();
            for (String key : fData.getSubItems().keySet()) {
                DataItem item = new DataItem();
                item.setPeriodData(sData.getSubItems().get(key), fData.getSubItems().get(key));
                subItems.put(key, item);
            }
        }
    }
}
