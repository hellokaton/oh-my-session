package io.github.biezhi.session.wrapper;

import io.github.biezhi.session.Session;
import io.github.biezhi.session.SessionContext;
import io.github.biezhi.session.SessionRepository;
import io.github.biezhi.session.model.SessionConfig;
import io.github.biezhi.session.util.HttpCookie;
import io.github.biezhi.session.util.Utils;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Observable;
import java.util.Observer;

@SuppressWarnings("deprecation")
public class HttpSessionWrapper implements HttpSession, Observer{

	private Session session;
	private HttpServletRequest request;
	private HttpServletResponse response;
	private SessionRepository sessionRepository;
	private SessionConfig config = SessionConfig.me();

	HttpSessionWrapper(HttpServletRequest request,
							  HttpServletResponse response,
							  Session session,
							  SessionRepository sessionRepository) {
		super();
		this.session = session;
		this.request = request;
		this.response = response;
		this.sessionRepository = sessionRepository;
	}

	@Override
	public Object getAttribute(String key) {
		if (this.session == null)
			reBuildSession();
		return session.get(key);
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Enumeration getAttributeNames() {
		if (this.session == null)
			reBuildSession();
		return Collections.enumeration(this.session.keySet());
	}

	@Override
	public long getCreationTime() {
		if (this.session == null)
			reBuildSession();
		return this.session.getCreated();
	}

	@Override
	public String getId() {
		if (this.session == null)
			reBuildSession();
		return this.session.getId();
	}

	@Override
	public long getLastAccessedTime() {
		if (this.session == null)
			reBuildSession();
		return this.session.getLasted();
	}

	@Override
	public int getMaxInactiveInterval() {
		if (this.session == null)
			reBuildSession();
		return config.timeout() / 60;
	}

	@Override
	public Object getValue(String key) {
		if (this.session == null)
			reBuildSession();
		return this.session.get(key);
	}

	@Override
	public String[] getValueNames() {
		if (this.session == null)
			reBuildSession();
		return (String[]) this.session.keySet().toArray(new String[0]);
	}

	@Override
	public void invalidate() {
		if(session !=null){
			sessionRepository.invalidate(session.getId());
		}
		this.session = null;
	}

	public boolean isValidate() {
		return this.session != null && sessionRepository.isValidate(session.getId());
	}

	@Override
	public boolean isNew() {
		return session.isFirst();
	}

	@Override
	public void putValue(String key, Object val) {
		if (this.session == null)
			reBuildSession();
		session.put(key, val);
	}

	@Override
	public void removeAttribute(String key) {
		if (this.session == null)
			reBuildSession();
		session.remove(key);
	}

	@Override
	public void removeValue(String key) {
		if (this.session == null)
			reBuildSession();
		session.remove(key);
	}

	@Override
	public void setAttribute(String key, Object val) {
		if (this.session == null)
			reBuildSession();
		session.put(key, val);
	}

	@Override
	public ServletContext getServletContext() {
		if (this.session == null)
			reBuildSession();
		return this.request.getSession().getServletContext();
	}

	@Override
	public HttpSessionContext getSessionContext() {
		return null;
	}

	@Override
	public void setMaxInactiveInterval(int maxAge) {

	}

	/**
	 *  重建session
	 */
	private void reBuildSession() {
		this.session = sessionRepository.getSession(request, true);
		this.writeCookie();
	}

	/**
	 * 向浏览器写入cookie
	 */
	public void writeCookie() {
		String id = getId();
		request.setAttribute(config.globalKey(), id);
		HttpCookie cookie = new HttpCookie(config.globalKey(), id);
		String path = config.cookiePath();
		if (path != null && path.trim().length() > 0) {
			cookie.setPath(path);
		}
		String domain = config.cookieDomain();
		if (domain != null && domain.trim().length() > 0) {
			cookie.setDomain(domain);
		}
		cookie.setHttpOnly(config.httpOnly());

		Utils.setCookie(cookie, response);
	}
	/**
	 * 观察者模式：观察者
	 * 利用观察者模式实现松耦合，redis包下面的类和外面的类隔离。
	 */
	@Override
	public void update(Observable o, Object arg) {
		try {
			sessionRepository.saveSession(this.session);
		} catch (Exception e){
			SessionContext.SERVER_IS_DOWN = true;
		}
	}
 
}