package io.github.biezhi.session.util;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

/**
 * Created by biezhi on 2016/12/6.
 */
public class Utils {

    public static final Logger LOGGER = LoggerFactory.getLogger(Utils.class);

    private static final Gson gson = new Gson();

    public static <T> T parse(String json, Class<T> type){
        return gson.fromJson(json, type);
    }

    public static String toJSONString(Object object){
        return gson.toJson(object);
    }

    public static Config load(String location){
        if(location.startsWith("classpath:")){
            return loadClassPath(location);
        } else if (location.startsWith("file:")) {
            location = location.substring("file:".length());
            return load(new File(location));
        }
        return loadClassPath(location);
    }

    /**
     * 在classpath下查找配置文件
     * @param location
     * @return
     */
    private static Config loadClassPath(String location){
        location = location.substring("classpath:".length());
        if (location.startsWith("/")) {
            location = location.substring(1);
        }
        try {
            InputStream is = getDefault().getResourceAsStream(location);
            LOGGER.info("Load config [classpath:" + location + "]");
            return loadInputStream(is, location);
        } catch (Exception e){
            LOGGER.error("配置文件加载失败", e);
        }
        return null;
    }

    // 从 File 载入
    public static Config load(File file) {
        try {
            LOGGER.info("Load config [file:" + file.getPath() + "]");
            return loadInputStream(new FileInputStream(file), file.getName());
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private static Config loadInputStream(InputStream is, String location) {
        if (is == null) {
            throw new IllegalStateException("InputStream not found: " + location);
        }
        location = location.toLowerCase();
        try {
            Properties config = new Properties();
            config.load(is);
            return new Config(config);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        } finally {
            if(null != is){
                try {
                    is.close();
                } catch (Exception e){}
            }
        }
    }

    private static ClassLoader getDefault() {
        ClassLoader loader = null;
        try {
            loader = Thread.currentThread().getContextClassLoader();
        } catch (Exception e) {
        }
        if (loader == null) {
            loader = Config.class.getClassLoader();
            if (loader == null) {
                try {
                    // getClassLoader() returning null indicates the bootstrap ClassLoader
                    loader = ClassLoader.getSystemClassLoader();
                } catch (Exception e) {
                    // Cannot access system ClassLoader - oh well, maybe the caller can live with null...
                }
            }
        }
        return loader;
    }

    public static boolean isNumeric(String str) {
        if (null != str && 0 != str.trim().length() && str.matches("\\d*")) {
            return true;
        }
        return false;
    }

    public static boolean isEmpty(String str){
        return null==str||str.length()==0;
    }

    public static boolean isNotEmpty(String str){
        return !isEmpty(str);
    }

    public static String getCookieValue(Cookie[] cookies, String cookieName,
                                        String defaultValue) {
        if (null == cookies)
            return "";
        for (int i = 0, size = cookies.length; i < size; i++) {
            Cookie cookie = cookies[i];
            if (cookieName.equals(cookie.getName()))
                return cookie.getValue();
        }
        return defaultValue;
    }

    public static String getCookieValue(HttpServletRequest request,
                                        String cookieName, String defaultValue) {
        return getCookieValue(request.getCookies(), cookieName, defaultValue);
    }

    public static void setCookie(HttpCookie cookie,
                                 HttpServletResponse httpResponse) {
        if (httpResponse != null) {
            cookie.writeResponse(httpResponse);
        }
    }

    public static void clearCookie(Cookie[] cookies, String cookieName,
                                   HttpServletResponse httpResponse) {
        if (null != cookies) {
            for (int i = 0, size = cookies.length; i < size; i++) {
                Cookie cookie = cookies[i];
                if (cookieName.equals(cookie.getName()))
                    cookie.setMaxAge(0);
                if (httpResponse != null) {
                    httpResponse.addCookie(cookie);
                }
            }
        }
    }

    public static void clearCookie(HttpServletRequest request,
                                   String cookieName, HttpServletResponse httpResponse) {
        clearCookie(request.getCookies(), cookieName, httpResponse);
    }

    public static String getIPAddr(HttpServletRequest request){
        try {
            String ipAddress = null;
            //ipAddress = this.getRequest().getRemoteAddr();
            ipAddress = request.getHeader("x-forwarded-for");
            if(ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("Proxy-Client-IP");
            }
            if(ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("WL-Proxy-Client-IP");
            }
            if(ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("X-Real-IP");
            }
            if(ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getRemoteAddr();
                if(ipAddress.equals("127.0.0.1")){
                    //根据网卡取本机配置的IP
                    InetAddress inet=null;
                    try {
                        inet = InetAddress.getLocalHost();
                    } catch (UnknownHostException e) {

                    }
                    ipAddress= inet.getHostAddress();
                }

            }
            //对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
            if(ipAddress!=null && ipAddress.length()>15){ //"***.***.***.***".length() = 15
                if(ipAddress.indexOf(",")>0){
                    ipAddress = ipAddress.substring(0,ipAddress.indexOf(","));
                }
            }
            return ipAddress;
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 在字符串左侧填充一定数量的特殊字符
     *
     * @param o
     *            可被 toString 的对象
     * @param width
     *            字符数量
     * @param c
     *            字符
     * @return 新字符串
     */
    public static String alignRight(Object o, int width, char c) {
        if (null == o)
            return null;
        String s = o.toString();
        int len = s.length();
        if (len >= width)
            return s;
        return new StringBuilder().append(dup(c, width - len)).append(s).toString();
    }

    /**
     * 在字符串右侧填充一定数量的特殊字符
     *
     * @param o
     *            可被 toString 的对象
     * @param width
     *            字符数量
     * @param c
     *            字符
     * @return 新字符串
     */
    public static String alignLeft(Object o, int width, char c) {
        if (null == o)
            return null;
        String s = o.toString();
        int length = s.length();
        if (length >= width)
            return s;
        return new StringBuilder().append(s).append(dup(c, width - length)).toString();
    }

    /**
     * 复制字符
     *
     * @param c
     *            字符
     * @param num
     *            数量
     * @return 新字符串
     */
    public static String dup(char c, int num) {
        if (c == 0 || num < 1)
            return "";
        StringBuilder sb = new StringBuilder(num);
        for (int i = 0; i < num; i++)
            sb.append(c);
        return sb.toString();
    }

}
