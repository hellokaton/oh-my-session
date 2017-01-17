package io.github.biezhi.session.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * 对Java-Servlet 2.5 进行扩展支持Expires属性和HttpOnly属性 key=value; Expires=date;
 * Path=path; Domain=domain; Secure; HttpOnly
 * Expires的格式：Thu, 28-May-15 04:19:07 GMT
 *
 * @author sunyujia@aliyun.com
 */
public class HttpCookie extends Cookie {
	private String expires = null;
	private Boolean httpOnly = Boolean.TRUE;

	public void setExpires(Date date) {
		this.expires = toGMT(date);
	}

	public void setHttpOnly(boolean b) {
		this.httpOnly = b;
	}

	public HttpCookie(String name, String value) {
		super(name, value);
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getName() + "=" + getValue());
		if (getVersion() > 0) {
			sb.append("; Version=" + getVersion());
		}
		if (getComment() != null && !"".equals(getComment())) {
			sb.append("; Comment=" + getComment());
		}
		if (getDomain() != null && !"".equals(getDomain())) {
			sb.append("; Domain=" + getDomain());
		}
		if (getMaxAge() > 0) {
			sb.append("; Max-Age=" + getMaxAge());
		}
		if (expires != null) {
			sb.append("; Expires=" + expires);
		}
		if (getPath() != null && !"".equals(getPath())) {
			sb.append("; Path=" + getPath());
		}

		if (Boolean.TRUE.equals(getSecure())) {
			sb.append("; Secure");
		}
		if (Boolean.TRUE.equals(httpOnly)) {
			sb.append("; HTTPOnly");
		}
		return sb.toString();
	}

	public void writeResponse(HttpServletResponse httpResponse) {
		httpResponse.setHeader("Set-Cookie", this.toString());
	}

	/**
	 * 转换为GMT格式的时间,cookie之所以使用GMT格式,是因为客户端可能遍布全球,各种时区,不可能与服务器保持相同时区,只能使用GMT
	 * @param date
	 * @return
	 */
	public String toGMT(Date date) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(
					"EEE, dd-MMM-yyyy HH:mm:ss 'GMT'", Locale.US);
			sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
			return sdf.format(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
