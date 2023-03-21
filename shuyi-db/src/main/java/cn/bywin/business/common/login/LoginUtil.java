package cn.bywin.business.common.login;

import cn.bywin.business.common.base.UserDo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.beans.PropertyDescriptor;
import java.sql.Timestamp;
import java.util.Date;

/**
 * 取用户信息
 */
@Slf4j
public class LoginUtil {
	public static final String AUTHORIZATION = "Authorization";
	 public final static String sessionName="sessionUserVO";

	public static String readToken(HttpServletRequest request){
		String uri=request.getRequestURI();
		String token = request.getHeader(AUTHORIZATION);
		String type = "AUTHORIZATION";
		if( StringUtils.isBlank(token) || token.length()<10 ) {
			token = request.getParameter("token");
			type = "token";
		}
		if( StringUtils.isBlank(token) || token.length()<10 )
		{
			type = "ticket";
			token = request.getParameter("ticket");
		}
		if( StringUtils.isBlank(token) || token.length()<10 ) {
			UserDo tempu = LoginUtil.getUser(request);
			//log.debug("session User:"+tempu);
			if( tempu!= null) {
				type = "session User tokenId: ";
				token = tempu.getTokenId();
			}
		}
		if( StringUtils.isBlank(token) || token.length()<10 ) {
			token = "";
		}
		log.debug( "uri:{},{} token:{},para:{},session id:{}",uri,type,token,request.getQueryString(),request.getSession().getId());
		return token;
	}

	public static String getTokenId(HttpServletRequest request) {
		UserDo userDo = getUser(request);
		if (userDo == null)
			throw new IllegalArgumentException("用户未登录");
		return userDo.getTokenId();
	}

	/**
	 * 获取当前缓存登录用户 不超过10分钟
	 * @param request
	 * @return
	 */
	 public static UserDo getUser(HttpServletRequest request){
		 UserDo user = (UserDo) request.getSession().getAttribute(sessionName);
		 if( user == null )
		 	return null;
		 else
		 	return user;
//		 if( user.getLastAct()+600000L > System.currentTimeMillis())
//		 	return user;
//		 request.getSession().setAttribute(sessionName,null);
//		 return null;
	 }

	public static HttpServletRequest getRequest() {
		return ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
	}

	/**
	 * 获取当前缓存登录用户 不超过10分钟
	 * @return
	 */
	public static UserDo getUser(){
		UserDo user = (UserDo) getSession().getAttribute(sessionName);
		if( user == null )
			return null;
		else
			return user;
	}

	/**
	 * 设置缓存用户
	 * @param request
	 * @param user
	 */
	public static void setUser(HttpServletRequest request,UserDo user){
		request.getSession().setAttribute(sessionName,user);
	}

	public static void setBeanInsertUserInfo( Object data, HttpServletRequest request){
		UserDo  user = getUser(request);
		if( user != null )
		setBeanInsertUserInfo(data,user);
	}
	public static void setBeanInsertUserInfo( Object data, UserDo user){
		if( user == null || data == null )
			return;
		BeanWrapper bw = new BeanWrapperImpl(data);
		PropertyDescriptor[] pro = bw.getPropertyDescriptors();
		for (int i = 0; i < pro.length; i++) {
			String name = pro[i].getName();
			if(pro[i].getWriteMethod()!= null){
				switch (name){
					case  "creatorId":
						bw.setPropertyValue(name,user.getUserId());
						break;
					case  "creatorAccount":
						bw.setPropertyValue(name,user.getUserName());

						break;
					case  "creatorName":
						bw.setPropertyValue(name,user.getChnName());
						break;
					case  "createdTime":
						bw.setPropertyValue(name,new Timestamp( new Date().getTime()));
						break;
				}
			}
		}
	}

	public static void setBeanInsertUserInfo( Object data){
		UserDo user = getUser();
		if( user == null || data == null )
			return;
		BeanWrapper bw = new BeanWrapperImpl(data);
		PropertyDescriptor[] pro = bw.getPropertyDescriptors();
		for (int i = 0; i < pro.length; i++) {
			String name = pro[i].getName();
			if(pro[i].getWriteMethod()!= null){
				switch (name){
					case  "creatorId":
						bw.setPropertyValue(name,user.getUserId());
						break;
					case  "creatorAccount":
						bw.setPropertyValue(name,user.getUserName());
						break;
					case  "creatorName":
						bw.setPropertyValue(name,user.getChnName());
						break;
					case  "createdTime":
						bw.setPropertyValue(name,new Timestamp( new Date().getTime()));
						break;
				}
			}
		}
	}

	/**
	 * 获取session
	 */
	public static HttpSession getSession() {
		return getRequest().getSession();
	}

	public static ServletRequestAttributes getServletRequestAttributes() {
		try {
			RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
			return (ServletRequestAttributes) attributes;
		} catch (Exception e) {
			return null;
		}
	}
}
