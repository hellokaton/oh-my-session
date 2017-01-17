package io.github.biezhi.session.redis;

import io.github.biezhi.session.RequestEvent;
import io.github.biezhi.session.Session;
import io.github.biezhi.session.SessionContext;
import io.github.biezhi.session.SessionRepository;
import io.github.biezhi.session.model.SessionConfig;
import io.github.biezhi.session.util.Utils;
import io.github.biezhi.session.wrapper.HttpRequestWrapper;
import redis.clients.jedis.JedisPool;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.UUID;

/**
 * redis session存储实现
 */
public class RedisSessionRepository implements SessionRepository<Session> {

    private JedisPool jedisPool;
    private RedisDB redisDB;
    private SessionConfig config = SessionConfig.me();

    public RedisSessionRepository(){
    }

    public RedisSessionRepository(JedisPool jedisPool){
        this.jedisPool = jedisPool;
    }

    @Override
    public void init() {
        this.redisDB=new RedisDB(jedisPool);
        SessionContext.dataBase = redisDB;
    }

    @Override
    public Session getSession(HttpServletRequest request, boolean create) {
        String sid = getSessionId(request);
        Session session = null;
        if(null == sid && create){
            session = createSession();
        } else {
            String key = config.prefix() + sid;
            boolean exists = redisDB.exists(key);
            if(exists){
                session = redisDB.get(key);
                // session续期
                long expireRs = redisDB.expire(key, config.timeout());
                if (expireRs == 1) {
                    session = redisDB.get(key);
                    session.setFirst(false);
                }
            } else {
                if (create) {
                    session = createSession();
                }
            }
        }
        if(null != request && null != session){
            session.setHost(Utils.getIPAddr(request));
            updateSessionMap(request, session);
        }
        return session;
    }

    @Override
    public HttpServletRequestWrapper getRequestWrapper(RequestEvent requestEvent) {
        return new HttpRequestWrapper(requestEvent);
    }

    @Override
    public void invalidate(final String id) {
        String key = config.prefix() + id;
        redisDB.del(key);
    }

    @Override
    public boolean isValidate(final String id) {
        String key = config.prefix() + id;
        return redisDB.exists(key);
    }

    /**
     * 将session数据保存到redis中。
     *
     * @param session
     */
    @Override
    public void saveSession(Session session) {
        if (session == null) return;
        String key = config.prefix() + session.getId();
        int timeout = config.timeout();
        redisDB.set(key, session, timeout);
    }

    /**
     * 创建一个新的session数据值对象
     *
     * @return
     */
    private Session createSession() {
        Session session = new Session();
        session.setId(io.github.biezhi.session.util.UUID.UU32());
        session.setCreated(System.currentTimeMillis());
        session.setFirst(true);
        return session;
    }

    /**
     * 更新session的部分属性，例如末次访问时间
     * @param request
     * @param session
     * @return
     */
    private void updateSessionMap(HttpServletRequest request, Session session) {
        session.setLasted(System.currentTimeMillis());
    }

    /**
     * 获取会话id
     *
     * @param request
     * @return
     */
    private String getSessionId(HttpServletRequest request) {
        String sid = Utils.getCookieValue(request.getCookies(), config.globalKey(), null);
        if ("".equals(sid)) {
            sid = null;
        }
        return sid;
    }

    public void setJedisPool(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

}
