package io.github.biezhi.session.model;

/**
 * Created by biezhi on 2016/12/17.
 */
public class SessionConfig {

    private SessionConfig(){

    }

    private static class ConfigHolder {
        private static final SessionConfig INSTANCE = new SessionConfig();
    }

    public static SessionConfig me(){
        return ConfigHolder.INSTANCE;
    }

    private String  prefix       = "session:";
    private String  globalKey    = "sessionid";

    private String  cookieDomain = "";
    private String  cookiePath   = "/";
    private boolean httpOnly     = true;
    private int     timeout      = 30;

    public String prefix() {
        return prefix;
    }

    public SessionConfig prefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    public String globalKey() {
        return globalKey;
    }

    public SessionConfig globalKey(String globalKey) {
        this.globalKey = globalKey;
        return this;
    }

    public String cookieDomain() {
        return cookieDomain;
    }

    public SessionConfig cookieDomain(String cookieDomain) {
        this.cookieDomain = cookieDomain;
        return this;
    }

    public String cookiePath() {
        return cookiePath;
    }

    public SessionConfig cookiePath(String cookiePath) {
        this.cookiePath = cookiePath;
        return this;
    }

    public boolean httpOnly() {
        return httpOnly;
    }

    public SessionConfig httpOnly(boolean httpOnly) {
        this.httpOnly = httpOnly;
        return this;
    }

    public int timeout() {
        return timeout * 60;
    }

    public SessionConfig timeout(int timeout) {
        this.timeout = timeout;
        return this;
    }
}
