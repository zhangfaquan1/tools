package org.example.util;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.function.Consumer;

/**
 * @descriptions: 解析xml工具类
 * @author: zhangfaquan
 * @date: 2021/7/29 9:30
 * @version: 1.0
 */
public class XmlUtils {

    private XmlUtils() {
    }

    /**
     * @descriptions 解析xml
     * @param xmlStr 字符串格式的xml
     * @return org.dom4j.Document
     */
    public static Document parse(String xmlStr) throws DocumentException {
        SAXReader reader = new SAXReader();
        InputStream in = new ByteArrayInputStream(xmlStr.getBytes());
        return reader.read(in);
    }

    /**
     * @descriptions 获取单个节点
     * @param document xml文档对象
     * @param xPathExpressions XPATH表达式，例子：//operation/col
     * @return org.dom4j.Node
     */
    public static Node getNodeByXpath(Document document, String xPathExpressions) {
        return document.selectSingleNode(xPathExpressions);
    }

    /**
     * @descriptions 获取节点下的所有子节点
     * @param document xml文档对象
     * @param xPathExpressions XPATH表达式，例子：//operation
     * @return
     */
    public static List<Node> getNodesByXpath(Document document, String xPathExpressions) {
        return document.selectNodes(xPathExpressions);
    }

    /**
     * @descriptions 获取标签的属性的值
     * @param node xml节点
     * @param field xml节点中的属性, 例子：@name
     * @return
     */
    public static String getValueOfNodeField(Node node, String field) {
        return node.valueOf(field);
    }

    /**
     * @descriptions 获取标签的属性的值
     * @param xmlStr 字符串格式的xml
     * @param xPathExpressions XPATH表达式，例子：//operation
     * @return
     */
    public static String getValueOfNodeField(String xmlStr, String xPathExpressions, String field) throws Exception {
        Document document = parse(xmlStr);
        Node node = getNodeByXpath(document, xPathExpressions);
        return getValueOfNodeField(node, field);
    }

    /**
     * @descriptions 获取节点内容
     * @param node xml节点
     * @return
     */
    public static String getNodeContent(Node node) {
        return node.getText();
    }

    /**
     * @descriptions 遍历指定元素下的所有元素，并执行用户实现的方法。
     * @param element 标签的元素对象
     * @param consumer 用于自定义的处理方法
     * @return
     */
    public static void treeWalk(Element element, Consumer<Node> consumer) {
        for ( int i = 0, size = element.nodeCount(); i < size; i++) {
            Node node = element.node(i);
            if ( node instanceof Element ) {
                treeWalk((Element) node, consumer);
            }
            if (consumer != null)
                consumer.accept(node);
        }
    }
}
