package com.jcm.statistics;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.solr.client.solrj.response.QueryResponse;

import com.jcm.monitor.bean.DataType;
import com.jcm.monitor.bean.Pdu;
import com.jcm.monitor.bean.ServiceType;
import com.jcm.monitor.client.MonitorClient;
import com.jcm.solrj.ext.SolrDb;
import com.jcm.solrj.ext.TableAccessor;
import com.jcm.solrj.ext.query.QueryParams;
import com.jcm.solrj.ext.query.params.FacetingParamsComp;
import com.jcm.statistics.bean.BaseData;
import com.jcm.statistics.cache.Cache;

public class Statistics {

    private Cache cache = null;
    
    private Timer timer = null;
    
    private SolrDb sd = null;
    
    public Statistics() throws InstantiationException, IllegalAccessException, ClassNotFoundException, IOException {
        // 缓存初期化
        Util.initResource();
        Long interval = Long.valueOf(Util.p.getProperty("interval"));
        cache = new Cache();
        timer = new Timer();
        sd = new SolrDb(Util.p.getProperty("solr_server"));
        timer.schedule(new TimerStatistics(), 0, interval);
    }
    
    class TimerStatistics extends TimerTask {
        private String startFlg = Util.START_FLG + Util.LINE_SEPARATOR;

        public void run() {
            try {
                String[] types = Util.p.getProperty("table").toLowerCase().split("/");
                String[] dimension = Util.p.getProperty("dimension").toLowerCase().split("/");
                String now = Util.getRemoteTime();
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
                sendMonitorInfo(now);
            } catch (Exception e) {
                System.out.println("exception happens in execution, process exit!");
                e.printStackTrace();
                timer.cancel();
            }
        }

        private void sendMonitorInfo(String start) throws Exception {
        	SimpleDateFormat format = new SimpleDateFormat ("yyyyMMddHHmmss");
            long duration = format.parse(Util.getRemoteTime()).getTime() - format.parse(start).getTime();
            MonitorClient client = new MonitorClient();
            
            Pdu pdu = new Pdu();

            pdu.setTime(format.parse(start).getTime());
            pdu.setServiceType(ServiceType.THREAD);

            pdu.setPort(Integer.valueOf(Util.p.getProperty("registryPort")));
            // 获取进程号
            pdu.setTid(Thread.currentThread().getId());
            // 连接的host
//            pdu.setExtendInfo(csocket.getInetAddress().getHostAddress().getBytes());
            pdu.setDataType(DataType.NUMBER);
            pdu.setData(com.jcm.monitor.Util.longToBytes(duration));
            String key = ServiceType.THREAD.name() + "://" + pdu.getHost() + ":" + pdu.getPid();
            client.sendPdu(key, pdu, false);
        }
        
        public void outputData(String dt, String type, QueryResponse qr, String tableCol, String dimension) 
                throws IOException {
            BufferedWriter writer = null;
            // xx/xx/2013
            String ouputDir = Util.getDirPath(Util.p.getProperty("dir_output"), dt.substring(0, 4));
            long maxSize = Long.valueOf(Util.p.getProperty("maxsize"));
            try {
                String cacheKey = type + "_" + dimension;
                BaseData data = cache.getBaseData(cacheKey);
                data.setDateTime(dt);
                long totalCount = qr.getResults().getNumFound();
                long totalInc = totalCount - data.getTotal();
                data.setTotal(totalCount);
                
                File d = new File(ouputDir);
                if (!d.exists()) {
                    d.mkdirs();
                }
                long vol = 0L;
                String head = "";
                String tail = "";
                StringBuffer body = new StringBuffer();
                int subCount = 0;
                if (!"".equals(tableCol)) {
                    subCount = data.createData(qr, tableCol, body);
                }
                // 文件名:news_site_model_20131210_0
                String verKey = cacheKey + "_" + dt.substring(0, 8);
                int version = cache.getVersion(verKey);
                File f = new File(ouputDir, verKey + "_" + version);
                String offset = String.valueOf(f.length());
                // 新的一天开始的第一条数据或者有更新的数据完整记录
                if ((version == 0 && f.length() == 0) || data.isPolluted()) {
                    // 头数据：时间，
                    //      总量，增量，子分类个数
                    head = dt + Util.LINE_SEPARATOR;
                    head += dimension + "," + totalCount + "," + totalInc + "," + subCount + Util.LINE_SEPARATOR;
                    vol += head.getBytes().length;
                    vol += body.length() * 2;
                    // 结尾标志，偏移量
                    tail = Util.TAIL_FLG + "," + offset;
                    vol += tail.getBytes().length;
                    // 新文件
                    if ((maxSize - f.length()) < vol) {
                        offset = "0";
                        // 更换偏移量
                        tail = tail.replace(tail.substring(tail.lastIndexOf(',') + 1), offset);
                        version++;
                        f = new File(ouputDir, verKey + "_" + version);
                        cache.setVersion(verKey, version);
                    }
                } else {
                    offset = data.getOffset();
                    // 不需要更新：无更新标志，时间，偏移量
                    tail = Util.NO_UPDATE + "," + dt + "," + offset;
                    vol += tail.getBytes().length;
                    // 需要分割文件时
                    if ((maxSize - f.length()) < vol) {
                        offset = "0";
                        version++;
                        head = dt + Util.LINE_SEPARATOR;
                        head += dimension + "," + totalCount + "," + totalInc + "," + subCount + Util.LINE_SEPARATOR;
                        // 结尾标志，偏移量，大小
                        tail = Util.TAIL_FLG + "," + offset;
                        f = new File(ouputDir, verKey + "_" + version);
                        cache.setVersion(verKey, version);
                    } else {
                        body.delete(0, body.length());
                    }
                }
                data.setOffset(offset);
                writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f, true), "UTF-8"));
                writer.write(startFlg);
                writer.write(head);
                writer.write(body.toString());
                writer.write(tail + Util.LINE_SEPARATOR);
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
        } catch (Exception e) {
            System.out.println("error occurs when prepare the initialize the process.");
        }
    }
}
