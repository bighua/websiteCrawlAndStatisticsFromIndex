package com.jcm.statistics;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;

import com.jcm.solrj.ext.SolrDb;
import com.jcm.solrj.ext.TableAccessor;
import com.jcm.solrj.ext.query.QueryParams;
import com.jcm.solrj.ext.query.params.FacetingParamsComp;
import com.jcm.statistics.bean.BaseData;
import com.jcm.statistics.bean.Cache;
import com.jcm.statistics.bean.Model;
import com.jcraft.jsch.JSchException;

public class Statistics {

    private Cache cache = null;
    
    public Statistics(String[] types, String[] dimension) throws InstantiationException, IllegalAccessException, ClassNotFoundException, IOException {
        // 缓存初期化
        cache = new Cache(types, dimension);
    }
    
    public static void main(String[] args) {
        
        try {
            // TODO 定时执行
            long begin = System.currentTimeMillis();
            String now = Util.getRemoteTime().replaceAll("\n", "");
            
            SolrDb sd = new SolrDb(Util.p.getProperty("solr_server"));
            String[] types = Util.p.getProperty("table").split("/");
            String[] dimension = Util.p.getProperty("dimension").split("/");
            Statistics st = new Statistics(types, dimension);
            for (String d : dimension) {
                String[] cols = Util.p.getProperty("col_" + d).split("/");
                for (int i = 0; i < types.length; i++) {
                    String t = types[i];
                    String col = "";
                    if (i < cols.length) {
                        col = cols[i];
                    }
                    QueryParams params = new QueryParams();
                    params.setRows(0);
                    if (i < cols.length) {
                        if (col.indexOf(",") > 0) {
    
                            params.addParameter("facet", "true");
                            params.addParameter("facet.pivot", col);
                            params.addParameter("indent", "true");
                        } else {
                            FacetingParamsComp facet = new FacetingParamsComp();
                            facet.addFacetField(col);
                            params.addParamsComponent(facet);
                        }
                    }
                    TableAccessor table = sd.getTableAccessor(t);
                    QueryResponse qr = table.query(params);
                    // TODO time check?
                    st.outputData(now, t, qr, col, d);
                }
                System.out.println("this process cost " + (System.currentTimeMillis() - begin) + "ms");
            }
            System.out.println();
        } catch (IOException |  JSchException | InterruptedException | SolrServerException | InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            // TODO exception handle
            e.printStackTrace();
        }
        
    }
    
    public void outputData(String dt, String type, QueryResponse qr, String tableCol, String dimension) 
            throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        BufferedWriter writer = null;
        String ouputDir = Util.p.getProperty("dir_output");
        try {
            String cacheKey = type + "_" + dimension;
            BaseData data = (BaseData)cache.get(cacheKey, dimension);
            data.setDateTime(dt);
            long totalCount = qr.getResults().getNumFound();
            long totalInc = totalCount - data.getTotal();
            data.setTotal(totalCount);
            
            File d = new File(ouputDir);
            if (!d.exists()) {
                d.mkdirs();
            }
            StringBuffer sb = new StringBuffer();
            // 文件名:news_site_model_20131210_0
            File f = new File(ouputDir, cacheKey + "_" + dt.substring(0, 8) + "_" + data.getVersion());
            // 头数据：时间，总量，增量
            String head = dt + "," + totalCount + "," + totalInc;
            writer = new BufferedWriter(new FileWriter(f, true));
            writer.write(head);
            writer.newLine();
            if (!"".equals(tableCol)) {
                data.createData(qr, tableCol, cache, dimension, sb);
            }

            if (data.isPolluted()) {
                writer.write(sb.toString());
                // (16-bit chars) * 2 = byte
                data.setDataSize(sb.length() * 2 + head.getBytes().length);
            } else {
                // 不需要更新：标志串，文件版本号，偏移量
                writer.write(Util.NO_UPDATE + data.getVersion());
            }
            writer.flush();
            writer.close();
            long maxSize = Long.valueOf(Util.p.getProperty("maxsize"));
            cache.writeFromCache(cacheKey, maxSize - f.length());
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }
    
    /**
     * 读取1个自然日的统计数据
     */
    public List<Model> readDailyData(String date, String type, String dimension) throws IOException {

        BufferedReader br = null;
        String ouputDir = Util.p.getProperty("dir_output");
        
        int version = 0;
        while (true) {
	        File f = new File(ouputDir, type + "_" + dimension + date + "_" + version);
	        if (f.exists()) {
		        br = new BufferedReader(new FileReader(f));
	        }
        }
    }
}
