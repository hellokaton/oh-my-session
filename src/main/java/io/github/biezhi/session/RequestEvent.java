package io.github.biezhi.session;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Observable;

public class RequestEvent extends Observable {

    private SessionRepository sessionRepository;
    private HttpServletResponse response;
    private HttpServletRequest request;

    public RequestEvent(HttpServletRequest request, HttpServletResponse response, SessionRepository sessionRepository) {
        super();
        this.sessionRepository = sessionRepository;
        this.response = response;
        this.request = request;
    }

    public SessionRepository getSessionRepository() {
        return sessionRepository;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    /**
     * 请求结束后通知所有的观察者
     */
    public void commit() {
        this.setChanged();
        this.notifyObservers();
    }

}