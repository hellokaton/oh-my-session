package io.github.biezhi.session.monitor;

import io.github.biezhi.session.Session;
import io.github.biezhi.session.SessionContext;
import io.github.biezhi.session.model.RestResponse;
import io.github.biezhi.session.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Created by biezhi on 2016/12/13.
 */
public class MonitorServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(MonitorServlet.class);

    private String username;
    private String password;

    @Override
    public void init() throws ServletException {
        this.username = readInitParam("username");
        this.password = readInitParam("password");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        LOGGER.debug("request session monitor");

        String uri = req.getRequestURI();

        String contexPath = req.getContextPath();
        resp.setCharacterEncoding("utf-8");
        req.setCharacterEncoding("utf-8");
        req.setAttribute("ctx", contexPath);
        req.setAttribute("uri", uri.endsWith("/") ? uri.substring(0, uri.length()-1) : uri);

        // 跳转到登录页面
        if(null != username && null != password && !SessionContext.isLogin && !uri.contains("login")){
            to_login(req, resp);
            return;
        }

        // http post 请求
        if(uri.endsWith(".json")){

            RestResponse restResponse = new RestResponse();
            if(uri.contains("login")){
                String username = req.getParameter("username");
                String password = req.getParameter("password");
                if(null == username || null == password){
                    restResponse.setMsg("请输入用户名或密码");
                    printJSON(restResponse, resp);
                    return;
                }
                if(!this.username.equals(username) || !this.password.equals(password)){
                    restResponse.setMsg("用户名或密码错误");
                    printJSON(restResponse, resp);
                    return;
                }
                restResponse.setPayload(req.getContextPath());
                restResponse.setSuccess(true);
                SessionContext.isLogin = true;
                printJSON(restResponse, resp);
                return;
            }

            if(uri.contains("logout")){
                restResponse.setSuccess(true);
                SessionContext.isLogin = false;
                printJSON(restResponse, resp);
                return;
            }

            if(uri.contains("sessions")){
                List<Session> sessionList = SessionContext.dataBase.sessions();
                restResponse.setPayload(sessionList);
                restResponse.setSuccess(true);
                printJSON(restResponse, resp);
                return;
            }
        }
        // redirect to index page
        this.to_index(req, resp);
    }

    private void to_login(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/jsp/login.jsp").forward(req, resp);
    }

    private void to_index(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int sessionCount = SessionContext.dataBase.count();
        req.setAttribute("sessionCount", sessionCount);
        req.getRequestDispatcher("/jsp/index.jsp").forward(req, resp);
    }

    private void printJSON(RestResponse restResponse, HttpServletResponse response){
        try {
            response.setContentType("application/json");
            response.getWriter().write(Utils.toJSONString(restResponse));
        } catch (Exception e){
        }
    }

    /**
     * 读取servlet中的配置参数.
     *
     * @param key 配置参数名
     * @return 配置参数值，如果不存在当前配置参数，或者为配置参数长度为0，将返回null
     */
    private String readInitParam(String key) {
        String value = null;
        try {
            String param = getInitParameter(key);
            if (param != null) {
                param = param.trim();
                if (param.length() > 0) {
                    value = param;
                }
            }
        } catch (Exception e) {
            String msg = "initParameter config [" + key + "] error";
            LOGGER.warn(msg, e);
        }
        return value;
    }

}
