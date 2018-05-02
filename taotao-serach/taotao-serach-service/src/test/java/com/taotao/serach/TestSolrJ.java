package com.taotao.serach;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Test;

import java.io.IOException;

public class TestSolrJ {

    @Test
    public void TestSolrJ() throws IOException, SolrServerException {
        SolrServer solrServer = new HttpSolrServer("http://192.168.0.203:8080/solr/new_core");
        SolrInputDocument document = new SolrInputDocument();
        document.addField("id", "test001");
        document.addField("item_title", "测试商品1");
        document.addField("item_price", 1000);
        solrServer.add(document);
        solrServer.commit();
    }

    @Test
    public void TestDeleteById()throws Exception{
        SolrServer solrServer = new HttpSolrServer("http://192.168.0.203:8080/solr/new_core");
        solrServer.deleteById("test001");
        solrServer.commit();
    }

    @Test
    public void TestCount()throws Exception{
        SolrServer server = new HttpSolrServer("http://192.168.0.203:8080/solr/new_core");

        SolrQuery params = new SolrQuery();
        params.set("q", "*:*");
        QueryResponse rsp = server.query(params);
        SolrDocumentList docs = rsp.getResults();
        System.out.println(docs.getNumFound());
    }
}
