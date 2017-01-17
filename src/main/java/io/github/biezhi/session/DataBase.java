package io.github.biezhi.session;

import io.github.biezhi.session.exception.SessionException;

import java.util.List;

/**
 * Created by biezhi on 2016/12/6.
 */
public interface DataBase {

    List<Session> sessions();

    Session get(String key);

    int count();

    boolean exists(String key);

    long expire(String key, int timeout);

    void set(String key, Object value);

    void set(String key, Object value, int timeout) throws SessionException;

    void hset(String key, String field, Object value);

    void hset(String key, String field, Object value, int timeout);

    void del(String key);

    void hdel(String key, String field);

}
