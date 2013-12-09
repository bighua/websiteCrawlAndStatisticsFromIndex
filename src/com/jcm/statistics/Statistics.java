package com.jcm.statistics;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.QueryResponse;

import com.jcm.solrj.ext.SolrDb;
import com.jcm.solrj.ext.TableAccessor;
import com.jcm.solrj.ext.query.QueryParams;
import com.jcm.solrj.ext.query.params.FacetingParamsComp;
import com.jcraft.jsch.JSchException;

public class Statistics {
	
	public static void main(String[] args) {
		
		try {
			// TODO 定时执行
			String now = Util.getRemoteTime().replaceAll("\n", "");
			SolrDb sd = new SolrDb(Util.p.getProperty("solr_server"));
			String statistics_tables = Util.p.getProperty("statistics_table");
			String[] types = statistics_tables.split(",");
			String statistics_col = Util.p.getProperty("statistics_col");
			String[] cols = statistics_col.split(",");
			String dir = Util.p.getProperty("dir");
			// 缓存初期化
			ModelCache.init(dir, types);
			
			QueryParams params = new QueryParams();
			params.setRows(0);
			for (int i = 0; i < types.length; i++) {

				String t = types[i];
//				String[] col = cols[i].split(",");
				if (!"".equals(cols[i])) {
					FacetingParamsComp facet = new FacetingParamsComp();
					facet.addFacetField(cols[i]);
					params.addParamsComponent(facet);
				}
				TableAccessor table = sd.getTableAccessor(t);
				QueryResponse qr = table.query(params);
				outputData(now, qr, cols[i], dir);
			}
		} catch (IOException |  JSchException | InterruptedException | SolrServerException e) {
			// TODO exception handle
			e.printStackTrace();
		}
		
	}
	
	public static void outputData(String dt, QueryResponse qr, String tableCol, String dir) throws IOException {

		long totalCount = qr.getResults().getNumFound();
		BufferedWriter writer = null;
		String ouputDir = Util.p.getProperty("dir_output");
		String statistics_tables = Util.p.getProperty("statistics_table");
		String[] types = statistics_tables.split(",");
		try {
			for (String t : types) {
				Model m = ModelCache.getModel(t);
				// TODO time check?
				m.setDateTime(dt);
				if (!m.isPolluted()) continue;
				File d = new File(ouputDir);
				if (!d.exists()) {
					d.mkdirs();
				}
				StringBuffer sb = new StringBuffer();
				sb.append("time:").append(dt).append("type:").append(t).append("total:").append(totalCount);
				// TODO file size check
				long bitLength = sb.length() * 16;
				writer = new BufferedWriter(new FileWriter(new File(ouputDir, t), true));
				// 时间
				writer.write(sb.toString());
				List<Count> lc = qr.getFacetField(tableCol).getValues();
				for (Count c : lc) {
					String model = c.getName();
					long count = c.getCount();
					// 车型ID
					sb.append(ModelCache.getId(model));
					// 增量
					sb.append(count - m.getModelCount(model));
					// 总量
					sb.append(count);
					sb.append(System.getProperty("line.separator"));
					// 缓存数据
					m.setModelCount(model, count);
				}
				m.updateModel(dir, t);
				if (m.isPolluted()) {
					writer.write(sb.toString());
				} else {
					// no update
					writer.write("_NU_");
				}
			    writer.flush();
				writer.close();
			}
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}
	
}
