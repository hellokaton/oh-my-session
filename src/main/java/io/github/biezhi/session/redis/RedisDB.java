package io.github.biezhi.session.redis;

import io.github.biezhi.session.DataBase;
import io.github.biezhi.session.Session;
import io.github.biezhi.session.SessionContext;
import io.github.biezhi.session.exception.SessionException;
import io.github.biezhi.session.model.SessionConfig;
import io.github.biezhi.session.util.Config;
import io.github.biezhi.session.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by biezhi on 2016/12/6.
 */
public class RedisDB implements DataBase {

    public static final Logger LOGGER = LoggerFactory.getLogger(RedisDB.class);

    private Charset UTF8 = Charset.forName("UTF-8");

    private JedisPool pool;

    private SessionConfig config = SessionConfig.me();

    public RedisDB(){
    }

    public RedisDB(JedisPool pool){
        this.pool = pool;
    }

    public void setPool(JedisPool pool) {
        this.pool = pool;
    }

    private Jedis getResource(){
        if(null != pool){
            return pool.getResource();
        }
        return null;
    }

    private void close(Jedis jedis){
        if (jedis != null) {
            try {
                jedis.close();
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    @Override
    public Session get(String key) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            String value = jedis.get(key);
            if(null != value){
                return Utils.parse(value, Session.class);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            close(jedis);
        }
        return null;
    }

    @Override
    public List<Session> sessions() {
        Jedis jedis = null;
        List<Session> sessions = new ArrayList<Session>();
        try {
            jedis = getResource();
            Set<String> keys = jedis.keys(config.prefix() + '*');
            if(null != keys){
                for(String key : keys){
                    sessions.add(this.get(key));
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            close(jedis);
        }
        return sessions;
    }

    @Override
    public int count() {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.keys(config.prefix() + '*').size();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            close(jedis);
        }
        return 0;
    }

    @Override
    public boolean exists(String key) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.exists(key);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            close(jedis);
        }
        return false;
    }

    @Override
    public void set(String key, Object value) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            byte[] data = Utils.toJSONString(value).getBytes(UTF8);
            jedis.set(key.getBytes(UTF8), data);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            close(jedis);
        }
    }

    @Override
    public void set(String key, Object value, int expires) throws SessionException {
        Jedis jedis = null;
        try {
            jedis = getResource();
            byte[] data = Utils.toJSONString(value).getBytes(UTF8);
            jedis.set(key.getBytes(UTF8), data);
            if (expires > 0) {
                jedis.expire(key, expires);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new SessionException("redis set error", e);
        } finally {
            close(jedis);
        }
    }

    @Override
    public long expire(String key, int timeout) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.expire(key, timeout);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            close(jedis);
        }
        return -1;
    }

    @Override
    public void hset(String key, String field, Object value) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            byte[] data = Utils.toJSONString(value).getBytes(UTF8);
            jedis.hset(key.getBytes(UTF8), field.getBytes(UTF8), data);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            close(jedis);
        }
    }

    @Override
    public void hset(String key, String field, Object value, int expires) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            byte[] data = Utils.toJSONString(value).getBytes(UTF8);
            jedis.hset(key.getBytes(UTF8), field.getBytes(UTF8), data);
            if (expires > 0) {
                jedis.expire(key, expires);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            close(jedis);
        }
    }

    public void del(String key) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            jedis.del(key);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            close(jedis);
        }
    }

    public void hdel(String key, String field) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            jedis.hdel(key, field);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            close(jedis);
        }
    }

}
