package com.slf.common.base;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.actions.DispatchAction;

import com.slf.common.util.ResultUtils;
import com.slf.sms.bo.XxQy;
import com.slf.sms.common.SysContext;

public class BaseAction extends DispatchAction{

	
	/**
	 * 获取session会话
	 * @param request
	 * @return
	 */
	protected HttpSession getSession(HttpServletRequest request)
	{
		return request.getSession();
	}
	/**
	 * 获取context上下文信息
	 * @param request
	 * @return
	 */
	protected ServletContext getContext()
	{
		return this.servlet.getServletContext();
	}
	
	/**
	 * 获取用户token信息
	 * @param request
	 * @return
	 */
	protected XxQy getUserToken(String userId)
	{
		if(SysContext.AGENTS_MAP.containsKey(userId))
		{
			return SysContext.AGENTS_MAP.get(userId);
		}
		return null;
	}
	
	/**
	 * 负载均衡情况下，获取真实IP
	 */
	protected String getIpAddr(HttpServletRequest request) 
	{
		String ip = request.getHeader("X-FORWARDED-FOR");
		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
		{
			ip = request.getHeader("Proxy-Client-IP");
		}else{
			ip = ip.split("\\,")[0];
		}
		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
		{
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if(ip == null || ip.length() == 0  || "unknown".equalsIgnoreCase(ip)) 
		{
			ip = request.getRemoteAddr();
		}
			return ip;
	}
	protected String BACK_MESSAGE = "";
	
	protected void backMsg(HttpServletResponse response,BaseResultMap baseResultMap)
	{
		try {
			response.setContentType("text/xml;charset=utf-8");
			PrintWriter out = response.getWriter();
			if(out != null)
			{
				BACK_MESSAGE = ResultUtils.makeResult(baseResultMap, "json");
				out.write(BACK_MESSAGE);
				out.flush();
				out.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
		}
	}
}
