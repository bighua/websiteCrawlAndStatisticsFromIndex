package com.jcm.statistics;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;

import com.jcm.solrj.ext.SolrDb;
import com.jcm.solrj.ext.TableAccessor;
import com.jcm.solrj.ext.query.QueryParams;
import com.jcm.solrj.ext.query.params.FacetingParamsComp;
import com.jcm.statistics.bean.BaseData;
import com.jcm.statistics.cache.Cache;
import com.jcraft.jsch.JSchException;

public class Statistics {

    private Cache cache = null;
    
    private Timer timer = null;
    
    private SolrDb sd = null;
    
    private String startFlg = Util.START_FLG;
    
    public Statistics() throws InstantiationException, IllegalAccessException, ClassNotFoundException, IOException {
        // 缓存初期化
        String[] types = Util.p.getProperty("table").split("/");
        String[] dimension = Util.p.getProperty("dimension").split("/");
        Long interval = Long.valueOf(Util.p.getProperty("interval"));
        cache = new Cache(types, dimension);
        timer = new Timer();
        sd = new SolrDb(Util.p.getProperty("solr_server"));
        timer.schedule(new TimerStatistics(), 0, interval);
    }
    
    class TimerStatistics extends TimerTask {

        public void run() {
            try {
                String[] types = Util.p.getProperty("table").split("/");
                String[] dimension = Util.p.getProperty("dimension").split("/");
                String now = Util.getRemoteTime().replaceAll("\n", "");
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
                        if (!"".equals(col)) {
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
                        outputData(now, t, qr, col, d);
                    }
                }
                startFlg = "";
            } catch (IOException |  JSchException | InterruptedException | SolrServerException | InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                e.printStackTrace();
                System.out.println("exception happens in execution, process exit!");
                timer.cancel();
            }
        }
        
        public void outputData(String dt, String type, QueryResponse qr, String tableCol, String dimension) 
                throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
            BufferedWriter writer = null;
            // xx/xx/2013
            String ouputDir = Util.p.getProperty("dir_output") + dt.substring(0, 4);
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
                long vol = 0L;
                // 头数据：时间，总量，增量
                String head = dt + "," + totalCount + "," + totalInc + Util.LINE_SEPARATOR;
                vol += head.getBytes().length;
                StringBuffer body = new StringBuffer();
                if (!"".equals(tableCol)) {
                    data.createData(qr, tableCol, cache, dimension, body);
                }
                if (!data.isPolluted()) {
                    // 不需要更新：标志串，文件版本号，偏移量
                    body = new StringBuffer(Util.NO_UPDATE).append(Util.LINE_SEPARATOR);
                }
                vol += body.length() * 2;
                // 文件名:news_site_model_20131210_0
                String verKey = cacheKey + "_" + dt.substring(0, 8);
                int version = cache.getVersion(verKey);
                File f = new File(ouputDir, verKey + "_" + version);
                long maxSize = Long.valueOf(Util.p.getProperty("maxsize"));
                if ((maxSize - f.length()) < vol) {
                    version++;
                    f = new File(ouputDir, verKey + "_" + version);
                    cache.setVersion(verKey, version);
                }
                writer = new BufferedWriter(new FileWriter(f, true));
                writer.write(startFlg);
                writer.write(head);
                writer.write(body.toString());
                writer.flush();
                cache.resetPollutedFlg(cacheKey);
            } finally {
                if (writer != null) {
                    writer.close();
                }
            }
        }
    }
    
    public static void main(String[] args) {
        try {
            new Statistics();
        } catch (IOException |  InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            System.out.println("error occurs when prepare the cache.");
            e.printStackTrace();
        }
    }
}
