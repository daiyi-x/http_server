package com.server;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.*;

public class Request {

    //请求方式
    private String method;
    //请求资源
    private String url;
    //请求参数
    private Map<String,List<String>> parameterMapValues;

    //内部
    public static final String CRLF="\r\n";
    private InputStream is;
    private String requestInfo;

    public Request(){
        method ="";
        url ="";
        parameterMapValues=new HashMap<String,List<String>>();
        requestInfo="";
    }
    public Request(InputStream is){
        this();
        this.is=is;
        try {
            byte[] data = new byte[20480];
            int len = is.read(data);
            requestInfo = new String(data, 0, len);
            System.out.println(requestInfo);
        } catch (Exception e) {
            return ;
        }
        parseRequestInfo();
    }

    private void parseRequestInfo(){
        if(null==requestInfo ||(requestInfo=requestInfo.trim()).equals("")){
            return ;
        }

        String paramString ="";

        String firstLine =requestInfo.substring(0,requestInfo.indexOf(CRLF));
        int idx =requestInfo.indexOf("/");
        this.method=firstLine.substring(0, idx).trim();
        String urlStr =firstLine.substring(idx,firstLine.indexOf("HTTP/")).
                trim();
        if(this.method.equalsIgnoreCase("post")){
            this.url=urlStr;
            paramString=requestInfo.substring(requestInfo.
                    lastIndexOf(CRLF)).trim();

        }else if(this.method.equalsIgnoreCase("get")){
            if(urlStr.contains("?")){
                String[] urlArray=urlStr.split("\\?");
                this.url=urlArray[0];
                paramString=urlArray[1];
            }else{
                this.url=urlStr;
            }
        }

        if(paramString.equals("")){
            return ;
        }
        parseParams(paramString);
    }
    private void parseParams(String paramString){
        StringTokenizer token=new StringTokenizer(paramString,"&");
        while(token.hasMoreTokens()){
            String keyValue =token.nextToken();
            String[] keyValues=keyValue.split("=");
            if(keyValues.length==1){
                keyValues = Arrays.copyOf(keyValues, 2);
                keyValues[1] =null;
            }

            String key = keyValues[0].trim();
            String value = null==keyValues[1]?null:decode(keyValues[1].trim(),"gbk");
            //转换成Map 分拣
            if(!parameterMapValues.containsKey(key)){
                parameterMapValues.put(key,new ArrayList<String>());
            }
            List<String> values =parameterMapValues.get(key);
            values.add(value);
        }

    }

    private String decode(String value,String code){
        try {
            return java.net.URLDecoder.decode(value, code);
        } catch (UnsupportedEncodingException e) {
            //e.printStackTrace();
        }
        return null;
    }

    public String[] getParameterValues(String name){
        List<String> values=null;
        if((values=parameterMapValues.get(name))==null){
            return null;
        }else{
            return values.toArray(new String[0]);
        }
    }

    public String getParameter(String name){
        String[] values =getParameterValues(name);
        if(null==values){
            return null;
        }
        return values[0];
    }
    public String getUrl() {
        return url;
    }

}
