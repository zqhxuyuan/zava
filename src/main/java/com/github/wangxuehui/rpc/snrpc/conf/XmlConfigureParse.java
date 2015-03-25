package com.github.wangxuehui.rpc.snrpc.conf;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.github.wangxuehui.rpc.snrpc.util.StringUtil;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author skyim E-mail:wxh64788665@gmail.com
 */
public class XmlConfigureParse implements ConfigureParse {
    private static final Logger LOGGER = LoggerFactory.getLogger(SnRpcConfig.class);

    private String configFile = null;
    private Document document = null;
    private Element root = null;

    public XmlConfigureParse(String configFile) {
        super();
        this.configFile = configFile;
        this.root = getRoot();
    }

    @SuppressWarnings("unchecked")
    private Element getRoot() {
        Document doc = getDocument();
        List<Element> list = doc.selectNodes("//application");
        if (list.size() > 0) {
            Element aroot = list.get(0);
            return aroot;
        }
        return null;
    }

    private Document getDocument() {
        InputStream is = getFileStream();
        try {
            if (document == null) {
                SAXReader sr = new SAXReader();
                sr.setValidation(false);
                if (is == null) {
                    throw new RuntimeException("can not find config File..." + configFile);
                }
                document = sr.read(is);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException("get xml file failed");
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return document;
    }

    private InputStream getFileStream() {
        return getFileStream(configFile);
    }

    private InputStream getFileStream(String fileName) {
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
        return is;
    }

    @SuppressWarnings({"unchecked", "unused"})
    public List<RpcService> parseService() {
        List<RpcService> slist = new ArrayList<RpcService>();
        Node serviceRoot = root.selectSingleNode("//rpcServices");
        /**
         <rpcService name="SnRpcInterface" interface="com.github.wangxuehui.rpc.test.SnRpcInterface" overload="true">
            <rpcImplementor class="com.github.wangxuehui.rpc.test.SnRpcImpl"/>
         </rpcService>
         */
        List<Element> serviceList = serviceRoot.selectNodes("//rpcService");

        int i = 0;
        for (Element serviceNode : serviceList) {
            String name = serviceNode.attributeValue("name"); //service name;
            String interfaceStr = serviceNode.attributeValue("interface");
            String overloadStr = serviceNode.attributeValue("overload");
            if (StringUtil.isEmpty(name)) {
                LOGGER.warn(configFile + ":a rpcservice's name is empty");
                continue;
            }
            if (StringUtil.isEmpty(interfaceStr)) {
                LOGGER.warn(configFile + ":rpcservice[" + name + "] has an empty interface configure");
                continue;
            }
            Class<?> type = null;
            try {
                type = Class.forName(interfaceStr);
            } catch (ClassNotFoundException e) {
                LOGGER.error(e.getMessage());
                throw new RuntimeException("can't find rpc Interface:" + interfaceStr);
            }
            //解析出XML信息,将其元数据封装成RpcService对象
            RpcService service = new RpcService("" + i, name);
            if (StringUtil.isNotEmpty(overloadStr) && "true".equals(overloadStr.trim())) {
                service.setOverload(true);
            }
            Element rpcImplementor = serviceNode.element("rpcImplementor");
            String processor = rpcImplementor.attributeValue("class");
            Class<?> providerClass = null;
            try {
                //实现类的类型
                providerClass = Class.forName(processor);
            } catch (ClassNotFoundException e) {
                LOGGER.error(e.getMessage());
                throw new RuntimeException(" can't find rpcImplementor class:" + processor);
            }
            //实现类
            RpcImplementor sv = new RpcImplementor(providerClass);
            service.setRpcImplementor(sv);
            slist.add(service);
            i++;
        }
        return slist;
    }
}
