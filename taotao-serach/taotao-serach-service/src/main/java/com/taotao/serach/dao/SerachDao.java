package com.taotao.serach.dao;

import com.taotao.pojo.SerachItem;
import com.taotao.pojo.SerachResult;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class SerachDao {

    @Autowired
    private HttpSolrServer httpSolrServer;

    public SerachResult serach(SolrQuery query) throws Exception {
        QueryResponse response = httpSolrServer.query(query);
        SolrDocumentList documents = response.getResults();
        List<SerachItem> list = new ArrayList<>();
        for (SolrDocument document : documents){
            SerachItem item = new SerachItem();
            item.setId((String) document.get("id"));
            item.setCategory_name((String) document.get("item_category_name"));
//            item.setImage((String) document.get("item_image"));
            String imurl = (String) document.get("item_image");
            if (!StringUtils.isBlank(imurl)){
                item.setImage(imurl.split(",")[0]);
            }
            item.setPrice((Long) document.get("item_price"));
            item.setSell_point((String) document.get("item_sell_point"));
            //对title进行取高亮显示
            Map<String, Map<String, List<String>>> highlighting = response.getHighlighting();
            List<String> hlist = highlighting.get(document.get("id")).get(document.get("item_title"));
            String itemTitle = "";
            if (hlist != null && hlist.size() > 0){
                itemTitle = hlist.get(0);
            }else {
                itemTitle = (String) document.get("item_title");
            }
            item.setTitle(itemTitle);
            list.add(item);
        }
        SerachResult result = new SerachResult();
        result.setItemList(list);
        result.setTotalItems(documents.getNumFound());
        return result;
    }
}
