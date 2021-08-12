package org.example.util;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class TestXmlUtils {

    private static final String xmlStr = "<?xml version='1.0' encoding='UTF-8'?>\n" +
            "<operation table='GG.TCUSTORD' type='I' ts='2013-06-02 22:14:36.000000' current_ts='2015-10-06T12:21:50.100001' pos='00000000000000001444' numCols='7'>\n" +
            " <col name='CUST_CODE' index='0'>\n" +
            "   <before missing='true'/>\n" +
            "   <after><![CDATA[WILL]]></after>\n" +
            " </col>\n" +
            " <col name='ORDER_DATE' index='1'>\n" +
            "   <before missing='true'/>\n" +
            "   <after><![CDATA[1994-09-30:15:33:00]]></after>\n" +
            " </col>\n" +
            " <col name='PRODUCT_CODE' index='2'>\n" +
            "   <before missing='true'/>\n" +
            "   <after><![CDATA[CAR]]></after>\n" +
            " </col>\n" +
            " <col name='ORDER_ID' index='3'>\n" +
            "   <before missing='true'/>\n" +
            "   <after><![CDATA[144]]></after>\n" +
            " </col>\n" +
            " <col name='PRODUCT_PRICE' index='4'>\n" +
            "   <before missing='true'/>\n" +
            "   <after><![CDATA[17520.00]]></after>\n" +
            " </col>\n" +
            " <col name='PRODUCT_AMOUNT' index='5'>\n" +
            "   <before missing='true'/>\n" +
            "   <after><![CDATA[3]]></after>\n" +
            " </col>\n" +
            " <col name='TRANSACTION_ID' index='6'>\n" +
            "   <before missing='true'/>\n" +
            "   <after><![CDATA[100]]></after>\n" +
            " </col>\n" +
            " <tokens>\n" +
            "   <token>\n" +
            "     <Name><![CDATA[R]]></Name>\n" +
            "     <Value><![CDATA[AADPkvAAEAAEqL2AAA]]></Value>\n" +
            "   </token>\n" +
            " </tokens>\n" +
            "</operation>\n";

    private static Document document;

    @BeforeClass
    public static void init() throws DocumentException {
        document = XmlUtils.parse(xmlStr);
    }

    @Test
    public void testGetValueOfNodeField() throws DocumentException {
        Node col = XmlUtils.getNodeByXpath(document, "//operation/col");
        String index = col.valueOf("@index");
        System.out.println(index);
    }

    @Test
    public void testGetNodeContent() {
        Node after = XmlUtils.getNodeByXpath(document, "//operation/col/after");
        String nodeContent = XmlUtils.getNodeContent(after);
        System.out.println(nodeContent);
    }

    @Test
    public void testGetName() throws DocumentException {
        Document document = XmlUtils.parse(xmlStr);
        Node operationNode = XmlUtils.getNodeByXpath(document, "//operation");
        String name = operationNode.getName();
        System.out.println(name);
    }

    @Test
    public void testTreeWalk() throws DocumentException {
        Document document = XmlUtils.parse(xmlStr);
        Node operationNode = XmlUtils.getNodeByXpath(document, "//operation");
        Map<String, String> requiredData = getRequiredData((Element) operationNode);
        System.out.println(requiredData);
    }

    public Map<String, String> getRequiredData(Element element) {
        Map<String, String> map = new HashMap<>();
        XmlUtils.treeWalk(element, node -> {
            System.out.println(node.getName());
            if ("after".equalsIgnoreCase(node.getName())) {
                Element parent = node.getParent();
                String field = XmlUtils.getValueOfNodeField(parent, "@name");
                String afterContent = XmlUtils.getNodeContent(node);
                if (!StringUtils.isBlank(field))
                    map.put(field, afterContent);
            }
        });
        return map;
    }
}
