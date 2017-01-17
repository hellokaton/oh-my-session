package io.github.biezhi.session;

import io.github.biezhi.session.exception.SessionException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * session repository
 */
public interface SessionRepository<S extends Session> {

	/**
	 * get session
	 *
	 * @return
	 */
	S getSession(HttpServletRequest request, boolean create);
	
	/**
	 * return request wrapper
	 *
	 * @param requestEvent
	 * @return
	 */
	HttpServletRequestWrapper getRequestWrapper(RequestEvent requestEvent);

	/**
	 * save session
	 *
	 * @param session
	 * @throws SessionException
     */
	void saveSession(Session session) throws SessionException;
	
	/**
	 * destory session
	 *
	 * @param id
	 */
	void invalidate(String id);
	
	/**
	 * session is validate
	 *
	 * @param id	session id
	 * @return
	 */
	boolean isValidate(String id);

	/**
	 * init
	 */
	void init();

}