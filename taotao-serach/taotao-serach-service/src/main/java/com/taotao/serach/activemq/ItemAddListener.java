package com.taotao.serach.activemq;

import com.taotao.pojo.SerachItem;
import com.taotao.serach.service.SerachItemService;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

public class ItemAddListener implements MessageListener {

    @Autowired
    private SerachItemService serachItemService;
    @Autowired
    private HttpSolrServer httpSolrServer;

    @Override
    public void onMessage(Message message){
        if (message instanceof TextMessage){
            try {
                TextMessage textMessage = (TextMessage) message;
                long id = Long.parseLong(textMessage.getText());
                SerachItem item = serachItemService.importAddItem(id);
                Thread.sleep(1000);
                SolrInputDocument document = new SolrInputDocument();
                document.addField("id", item.getId());
                document.addField("item_title", item.getTitle());
                document.addField("item_sell_point",item.getSell_point());
                document.addField("item_price", item.getPrice());
                document.addField("item_image",item.getImage());
                document.addField("item_category_name", item.getCategory_name());
                document.addField("item_desc", item.getItem_desc());
                httpSolrServer.add(document);
                httpSolrServer.commit();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
