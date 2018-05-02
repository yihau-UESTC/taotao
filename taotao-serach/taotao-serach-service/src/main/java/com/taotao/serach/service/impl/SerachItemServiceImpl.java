package com.taotao.serach.service.impl;

import com.taotao.pojo.SerachItem;
import com.taotao.pojo.SerachResult;
import com.taotao.pojo.TaotaoResult;
import com.taotao.serach.dao.SerachDao;
import com.taotao.serach.service.SerachItemService;
import com.taotao.serach.service.mapper.SerachItemMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SerachItemServiceImpl implements SerachItemService {

    @Autowired
    private SerachItemMapper serachItemMapper;
    @Autowired
    private HttpSolrServer httpSolrServer;
    @Autowired
    private SerachDao serachDao;

    @Override
    public TaotaoResult importAllItems() throws Exception{
        List<SerachItem> list = serachItemMapper.getItemList();
        for (SerachItem item : list){
            SolrInputDocument document = new SolrInputDocument();
            document.addField("id", item.getId());
            document.addField("item_title", item.getTitle());
            document.addField("item_sell_point",item.getSell_point());
            document.addField("item_price", item.getPrice());
            document.addField("item_image",item.getImage());
            document.addField("item_category_name", item.getCategory_name());
            document.addField("item_desc", item.getItem_desc());
            httpSolrServer.add(document);
        }
        httpSolrServer.commit();
        return TaotaoResult.ok();
    }

    @Override
    public SerachResult serach(String queryString, int page, int rows) throws Exception {
        SolrQuery query = new SolrQuery();
        query.setQuery(queryString);
        query.setStart((page - 1) * rows);
        query.setRows(rows);
        //设置搜索域
        query.set("df", "item_title");
        query.setHighlight(true);
        query.set("hl.fl", "item_title");
        query.setHighlightSimplePre("<font color=\"red\">");
        query.setHighlightSimplePost("</font>");
        SerachResult serachResult = serachDao.serach(query);
        int totalPages = (int) Math.ceil(serachResult.getTotalItems() / rows);
        serachResult.setTotalPages(totalPages);
        return serachResult;
    }

    @Override
    public SerachItem importAddItem(long id) throws Exception {
        SerachItem serachItem = serachItemMapper.getItemById(id);
        return serachItem;
    }

}
