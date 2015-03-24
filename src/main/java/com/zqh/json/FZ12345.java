package com.zqh.json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zqh.base.FileIOEncode;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import org.junit.Test;

/**
 * Created by zqhxuyuan on 15-3-24.
 */
public class FZ12345 {

    String URL_getSearchCall = "http://218.5.2.252:8333/callcenter/api/fzCallServlet?act=getSearchCall&sq={}&format=json";

    public static void main(String[] args) throws Exception{

    }

    @Test
    public void testParseList()throws Exception{
        String data = getJSON(URL_getSearchCall);
        JSONObject jo = JSON.parseObject(data);
        int totalNum = jo.getInteger("totalNum"); //84989
        int start = 0;
        int limit = 200;
        String url = URL_getSearchCall;
        for (int i = 0; i < totalNum/limit; i++) { //totalNum/interval
            url = URL_getSearchCall + "&from=" + start + "&limit=" + limit;
            start += limit;
            parseList(url);
        }

        url = URL_getSearchCall + "&from=" + (totalNum/limit)*limit + "&limit=" + (totalNum-(totalNum/limit)*limit);
        parseList(url);
    }

    private static void parseList(String urlPrefix)throws Exception{
        String data = getJSON(urlPrefix);
        JSONObject jo = JSON.parseObject(data);
        JSONArray array = jo.getJSONArray("data");

        List<Call> list = JSON.parseArray(array.toString(), Call.class);
        for(Call c : list){
            System.out.println(c);
        }
    }

    @Test
    public void testWriteJSONRecords2File()throws Exception{
        String jsonFile = "/home/hadoop/data/internet/fz12345.json";
        FileIOEncode.append(jsonFile,"[");
        long startT = System.currentTimeMillis();

        String data = getJSON(URL_getSearchCall);
        JSONObject jo = JSON.parseObject(data);
        int totalNum = jo.getInteger("totalNum"); //84989
        int start = 0;
        int limit = 200;
        String url = URL_getSearchCall;
        for (int i = 0; i < totalNum/limit; i++) {
            url = URL_getSearchCall + "&from=" + start + "&limit=" + limit;
            start += limit;
            data = batchCallStr(url).replaceAll("},", "},\n") + ",\n";
            FileIOEncode.append(jsonFile,data);
        }

        url = URL_getSearchCall + "&from=" + (totalNum/limit)*limit + "&limit=" + (totalNum-(totalNum/limit)*limit);
        data = batchCallStr(url).replaceAll("},","},\n") + "]";
        FileIOEncode.append(jsonFile,data);

        System.out.println("over:::"+(System.currentTimeMillis() - start)/1000);
    }

    private static String batchCallStr(String urlPrefix) throws Exception{
        String data = getJSON(urlPrefix);
        JSONObject jo = JSON.parseObject(data);
        JSONArray array = jo.getJSONArray("data");
        String calls = array.toString().substring(1,array.toString().length()-1); //{},{}
        return calls;
    }

    @Test
    public void testStartAndLimit() throws Exception{
        String data = getJSON(URL_getSearchCall);
        JSONObject jo = JSON.parseObject(data);
        int totalNum = jo.getInteger("totalNum"); //84989
        int start = 0;
        int limit = 200;
        String url = URL_getSearchCall;
        for (int i = 0; i < totalNum/limit; i++) { //totalNum/interval
            url = URL_getSearchCall + "&from=" + start + "&limit=" + limit;
            System.out.println(url);
            start += limit;
        }

        url = URL_getSearchCall + "&from=" + (totalNum/limit)*limit + "&limit=" + (totalNum-(totalNum/limit)*limit);
        System.out.println(url);
    }

    //URL Connect Result
    private static String getJSON(String url) throws Exception{
        return getJSON(url, 5000);
    }

    public static String getJSON(String url, int timeout) throws Exception{
        try {
            URL u = new URL(url);
            HttpURLConnection c = (HttpURLConnection) u.openConnection();
            c.setRequestMethod("GET");
            c.setRequestProperty("Content-length", "0");
            c.setUseCaches(false);
            c.setAllowUserInteraction(false);
            //c.setConnectTimeout(timeout);
            //c.setReadTimeout(timeout);
            c.connect();
            int status = c.getResponseCode();

            switch (status) {
                case 200:
                case 201:
                    BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line+"\n");
                    }
                    br.close();
                    return sb.toString();
            }

        } catch (MalformedURLException ex) {
            //Logger.getLogger(DebugServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            //Logger.getLogger(DebugServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
