package com.zqh.fun.pp;

/**
 * Created by hadoop on 15-2-12.
 */
import java.util.List;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class DealUrl implements Runnable{
    // 已解析url队列
    private List<String> visited = null;
    // 未解析url队列
    private List<String> hrefs = null;
    // 图片链接队列
    private List<String> images = null;

    //已解析链接数
    private int analyze = 0;
    private int count   = 0;
    public DealUrl(List<String> hrefs, List<String> visited, List<String> images) {
        this.hrefs = hrefs;
        this.visited = visited;
        this.images = images;
    }

    public void run() {
        while (!hrefs.isEmpty()) {
            // 把当前要解析的url字符串从hrefs移到visited
            String urlTmp = hrefs.remove(0);
            if (visited.indexOf(urlTmp) != -1)
                continue;
            visited.add(urlTmp);
            Document doc = getUrlDoc((String) visited.get(visited.size() - 1));
            if (doc == null)
                continue;
            System.out.println("已解析第 " + ++analyze + " 个连接。。。"+urlTmp);
            Elements hrefLinks = doc.select("a[href]");
            Elements imgLinks = doc.select("img[src]");
            if (hrefLinks != null)
                for (Element link : hrefLinks) {
                    String newUrl = link.attr("abs:href");
                    if (newUrl.indexOf("ququ") != -1)
                        hrefs.add(newUrl);
                    // System.out.println(++count + "  >>> " +
                    // link.attr("abs:href"));
                }
            if (imgLinks == null)
                continue;
            for (Element link : imgLinks) {
                String temImgUrl = link.attr("abs:src");
                if (temImgUrl.indexOf(".jpg") != -1 && images.indexOf(temImgUrl) == -1) {
                    images.add(link.attr("abs:src"));
                    System.out.println("img:"+link.attr("abs:src"));
                }
            }
            new Thread(new DownloadImage(images)).start();
        }
        System.gc();
    }
    public Document getUrlDoc(String url){
        Document doc = null;
        try {
            new UseProxy();//不是代理上网的可以注释掉
            Connection conneciton = Jsoup.connect(url);
            conneciton.userAgent(Constant.AGENT);
            doc = conneciton.get();
        } catch (Exception e) {
            System.out.println("connect fail!");
            return null;
        }
        return doc;
    }
}
