package io.github.biezhi.session.model;

/**
 * rest返回对象
 *
 * @param <T>
 */
public class RestResponse<T> {

    /**
     * 服务器响应数据
     */
    private T payload;

    /**
     * 请求是否成功
     */
    private boolean success;

    /**
     * 错误信息
     */
    private String msg;

    /**
     * 服务器响应时间
     */
    private long timestamp;

    public RestResponse() {
        this.timestamp = System.currentTimeMillis() / 1000;
    }

    public RestResponse(T payload) {
        this.success = true;
        this.payload = payload;
        this.timestamp = System.currentTimeMillis() / 1000;
    }

    public RestResponse(boolean success) {
        this.success = success;
        this.timestamp = System.currentTimeMillis() / 1000;
    }

    public T getPayload() {
        return payload;
    }

    public void setPayload(T payload) {
        this.payload = payload;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

}
