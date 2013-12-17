package com.jcm.statistics.data;

import java.io.IOException;
import java.util.List;

public interface IDataAccessor {
    
    public List<Data> getData(String date, String type, String dimension) throws IOException, CloneNotSupportedException;

    public Data getDailyData(String date, String type, String dimension) throws IOException, CloneNotSupportedException;
}
