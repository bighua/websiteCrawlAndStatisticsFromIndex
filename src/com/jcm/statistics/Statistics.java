package com.jcm.statistics;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.QueryResponse;

import com.jcm.solrj.ext.SolrDb;
import com.jcm.solrj.ext.TableAccessor;
import com.jcm.solrj.ext.query.QueryParams;
import com.jcm.solrj.ext.query.params.FacetingParamsComp;

public class Statistics {

	public static void main(String[] args) {
		
		try {
			String[] carTypes = new String(Util.p.getProperty("car_type").getBytes("ISO8859-1"), "UTF-8").split(",");
//			String fileName = Util.getRemoteTime();
			SolrDb sd = new SolrDb(Util.p.getProperty("solr_server"));
			String statistics_col = Util.p.getProperty("statistics_table");
			QueryParams params = new QueryParams();
			params.setRows(0);
			FacetingParamsComp facet = new FacetingParamsComp();
			facet.addFacetField("urlmodelcom");
			params.addParamsComponent(facet);
//			for (String t : TABLES) {
			String t = "news";
			TableAccessor table = sd.getTableAccessor(t);
				QueryResponse qr = table.query(params);
				System.out.println(t + ":" + qr.getResults().getNumFound());
				List<Count> lc = qr.getFacetField("urlmodelcom").getValues();
				for (Count c : lc) {
					System.out.println(c.getName() + ":" + c.getCount());
				}
//			}
//			File f = new File(fileName);
//			FileInputStream fis = new FileInputStream(f);
		} catch (IOException | SolrServerException e) {
			e.printStackTrace();
		}
		
	}

}
