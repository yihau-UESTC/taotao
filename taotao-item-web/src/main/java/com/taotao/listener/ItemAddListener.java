package com.taotao.listener;

import com.taotao.pojo.Item;
import com.taotao.pojo.TbItem;
import com.taotao.pojo.TbItemDesc;
import com.taotao.service.ItemService;
import freemarker.core.ParseException;
import freemarker.template.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

public class ItemAddListener implements MessageListener {

    @Autowired
    private ItemService itemService;
    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;
    @Value("${HTML_OUT_DIR}")
    private String HTML_OUT_DIR;

    @Override
    public void onMessage(Message message) {
        if (message instanceof TextMessage){
            TextMessage textMessage = (TextMessage) message;
            try {
                String strId = textMessage.getText();
                long itemId = Long.parseLong(strId);
                Thread.sleep(1000);
                TbItem tbItem = itemService.getItemById(itemId);
                Item item = new Item(tbItem);
                TbItemDesc itemDesc = itemService.getItemDescById(itemId);
                Configuration configuration = freeMarkerConfigurer.getConfiguration();
                Template template = configuration.getTemplate("item.ftl");
                Writer out = new FileWriter(new File(HTML_OUT_DIR + itemId + ".html"));
                Map map = new HashMap();
                map.put("item", item);
                map.put("itemDesc", itemDesc);
                template.process(map, out);
            } catch (JMSException e) {
                e.printStackTrace();
            } catch (MalformedTemplateNameException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            } catch (TemplateNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (TemplateException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
