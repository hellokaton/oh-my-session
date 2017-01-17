package io.github.biezhi.session;

import io.github.biezhi.session.model.SessionConfig;
import io.github.biezhi.session.redis.RedisSessionRepository;
import io.github.biezhi.session.util.Config;
import io.github.biezhi.session.util.Utils;
import io.github.biezhi.session.wrapper.HttpRequestWrapper;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

public class OnMySessionFilter implements Filter {

    private static final HashSet<String> IGNORE_SUFFIX = new HashSet<String>();
    protected SessionRepository sessionRepository;
    protected Config config;

    static {
        IGNORE_SUFFIX.addAll(Arrays.asList("gif,jpg,jpeg,png,bmp,swf,js,css,html,htm".split(",")));
    }

    @Override
    public void init(FilterConfig fc) throws ServletException {
        if(null == sessionRepository){
                        
            String config_path=fc.getInitParameter("config_path");
            config = Utils.load(config_path);

            /*---------------redis config-------------------*/
            String redisIp = config.get("redis.host", "127.0.0.1");
            String sessionKey = config.get("session.key", "sessionid");
            String redisPass = config.get("redis.pass", "");
            int redisPort = config.getInt("redis.port", 6379);
            int redisTimeout = config.getInt("redis.timeout");

            int maxTotal = config.getInt("redis.max_total", GenericObjectPoolConfig.DEFAULT_MAX_TOTAL);
            int minIdle = config.getInt("redis.min_idle", GenericObjectPoolConfig.DEFAULT_MIN_IDLE);
            int maxIdle = config.getInt("redis.max_idle", GenericObjectPoolConfig.DEFAULT_MAX_IDLE);
            long maxWaitMins = config.getLong("redis.max_wait_time", GenericObjectPoolConfig.DEFAULT_MAX_WAIT_MILLIS);

            /*-----------------cookie config------------------*/
            String cookieDomain = config.get("session.cookie_domain", "");
            String cookiePath = config.get("session.cookie_path", "/");
            Boolean httpOnly = Boolean.valueOf(config.get("session.httponly", "true"));
            int timeout = config.getInt("session.timeout", 30);

            JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
            jedisPoolConfig.setMaxTotal(maxTotal);
            jedisPoolConfig.setMinIdle(minIdle);
            jedisPoolConfig.setMaxIdle(maxIdle);
            jedisPoolConfig.setMaxWaitMillis(maxWaitMins);

            JedisPool jedisPool = null;
            if(redisPass.length() > 0){
                jedisPool = new JedisPool(jedisPoolConfig, redisIp, redisPort, redisTimeout, redisPass);
            } else {
                jedisPool = new JedisPool(jedisPoolConfig, redisIp, redisPort, redisTimeout);
            }

            SessionConfig sessionConfig = SessionConfig.me();
            sessionConfig.globalKey(sessionKey);
            sessionConfig.timeout(timeout);
            sessionConfig.httpOnly(httpOnly);

            if(Utils.isNotEmpty(cookieDomain)) {
                sessionConfig.cookieDomain(cookieDomain.trim());
            }
            if(null != cookiePath) {
                sessionConfig.cookiePath(cookiePath);
            }

            SessionRepository<Session> sessionRepository = new RedisSessionRepository(jedisPool);
            sessionRepository.init();
            this.sessionRepository = sessionRepository;
        }
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain fc) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        if( !shouldFilter(request) ||
                (request instanceof HttpRequestWrapper) ||
                SessionContext.SERVER_IS_DOWN) {
            fc.doFilter(servletRequest, servletResponse);
            return;
        }

        HttpServletResponse response = (HttpServletResponse) servletResponse;
        RequestEvent requestEvent = new RequestEvent(request, response, sessionRepository);
        HttpServletRequestWrapper requestWrapper = sessionRepository.getRequestWrapper(requestEvent);

        try {
            fc.doFilter(requestWrapper, servletResponse);
        } finally {
            requestEvent.commit();
        }
    }

    private boolean shouldFilter(HttpServletRequest request) {
        String uri = request.getRequestURI().toLowerCase();
        int idx = uri.lastIndexOf(".");
        if (idx > 0) {
            String suffix = uri.substring(idx);
            if (suffix.length() < 8 && IGNORE_SUFFIX.contains(suffix)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void destroy() {

    }
}