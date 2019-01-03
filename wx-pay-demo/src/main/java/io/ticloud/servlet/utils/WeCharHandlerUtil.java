package io.ticloud.servlet.utils;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WeCharHandlerUtil {

    public static Map <String, String> parseXml(HttpServletRequest request) throws IOException, DocumentException {
        Map <String, String> map = new HashMap <>(16);
        ServletInputStream inputStream = request.getInputStream();
        SAXReader reader = new SAXReader();
        Document document = reader.read(inputStream);
        Element root = document.getRootElement();
        List<Element> elements = root.elements();
        for (Element element : elements) {
            System.out.println(element.getName() + "|" + element.getText());
            map.put(element.getName(), element.getText());
        }
        return map;
    }
}
