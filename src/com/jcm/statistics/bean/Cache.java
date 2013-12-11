package com.jcm.statistics.bean;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.jcm.statistics.Util;

public class Cache {

    /**
     * news_model,img_site_model,video_site,praise_site
     */
    protected Map<String, BaseData> cache = new HashMap<String, BaseData>();
    
    /**
     * model:id pair
     */
    private Map<String, Long> modelId = new HashMap<String, Long>();
    
    public Cache(String[] types, String[] dimension) throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        for (String d : dimension) {
            init(types, d);
        }
    }
    
    public BaseData get(String key, String dimension) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        BaseData m = cache.get(key);
        if (m == null) {
            m = Util.getInstance(dimension);
            cache.put(key, m);
        }
        return m;
    }
    
    public Long getId(String model) {
        return modelId.get(model);
    }
    
    private void init(String[] types, String dimension) throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        String[] models = new String(Util.p.getProperty("models").getBytes("ISO8859-1"), "UTF-8").split(",");
        String dir = Util.p.getProperty("dir");
        for (int i = 0; i < models.length; i++) {
            modelId.put(models[i], Long.valueOf(models[++i]));
        }
        BufferedReader br = null;
        try {
            // 从本地缓存文件中读取数据到内存中
            for (String t : types) {
                String cacheName = t + "_" + dimension;
                File f = new File(dir, cacheName);
                String[] items = null;
                BaseData data = Util.getInstance(dimension);
                if (f.exists()) {
                    br = new BufferedReader(new FileReader(f));
                    items = br.readLine().split(",");
                    // 头数据
                    int i = 0;
                    data.setDateTime(items[i++]);
                    data.setTotal(Long.valueOf(items[i++]));
                    data.setVersion(Integer.valueOf(items[i++]));
                    data.setDataSize(Long.valueOf(items[i++]));
                    data.readIntoCache(br);
                    br.close();
                }
                cache.put(cacheName, data);
            }
        } finally {
            if (br != null) {
                br.close();
            }
        }
    }

    public void writeFromCache(String key, long availableSize) throws IOException {
        String dir = Util.p.getProperty("dir");
        BaseData data = cache.get(key);
        if (availableSize < data.getDataSize()) {
            data.setVersion(data.getVersion() + 1);
        }
        if (!data.isPolluted()) return;
        // 缓存文件更新
        BufferedWriter writer = null;
        try {
            File d = new File(dir);
            if (!d.exists()) {
                d.mkdirs();
            }
            writer = new BufferedWriter(new FileWriter(new File(dir, key)));
            StringBuffer sb = new StringBuffer();
            // 时间,总数，文件版本号,单条数据大小
            sb.append(data.getDateTime()).append(",").append(data.getTotal()).append(",").append(
                    data.getVersion()).append(",").append(data.getDataSize()).append(Util.LINE_SEPARATOR);
            // BaseData子类实现
            data.writeFromCache(sb);
            writer.write(sb.toString());
            writer.flush();
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }
}
