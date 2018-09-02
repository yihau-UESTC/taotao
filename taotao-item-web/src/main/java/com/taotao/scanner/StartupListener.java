package com.taotao.scanner;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.web.context.support.XmlWebApplicationContext;

import javax.servlet.ServletContext;
import java.io.File;

public class StartupListener implements ApplicationListener<ContextRefreshedEvent> {
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        XmlWebApplicationContext o = (XmlWebApplicationContext) event.getSource();
        ServletContext servletContext = o.getServletContext();
        String path1 = servletContext.getRealPath("/WEB-INF/html");
        File file = new File(path1);
        System.out.println(file.isDirectory());
        String[] list = file.list();
        if (list != null) {
            for (String s : list) {
                String s1 = s.substring(0, s.lastIndexOf(".html"));
                long id = Long.valueOf(s1);
                HtmlsHolder.addHtml(id, s1);
                System.out.println(s1 + "has add to htmlHolders");
            }
        }
    }
}
