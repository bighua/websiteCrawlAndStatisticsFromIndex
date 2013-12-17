package com.jcm.statistics.data;

import java.io.Serializable;
import java.util.List;


public class Data implements Cloneable,Serializable {

    /**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -3907405035422763905L;

	private String dateTime = null;

    private DataItem item = null;

    public DataItem getItem() {
        return item;
    }

    public void setItem(DataItem item) {
        this.item = item;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public void wrapData(List<String> dataList, Data originalData, boolean isStart) {
        dateTime = dataList.remove(0);
        item = new DataItem();
        item.wrapItems(dataList);
        if (isStart) item.resetInc(originalData.getItem());
    }

    public Data clone() throws CloneNotSupportedException {
        // deep clone
        Data d = new Data();
        d.setItem(item.clone());
        return d;
    }
    
    public Data ShadowClone(String dateTime) throws CloneNotSupportedException {
        Data d = (Data)super.clone();
        d.setDateTime(dateTime);
        return d;
    }
    
    public void setPeriodData(String dateTime, Data sData, Data fData) {

        this.dateTime = dateTime;
        if (fData.getItem() != null) {
            item = new DataItem();
            item.setPeriodData(sData.getItem(), fData.getItem());
        }
    }
}
