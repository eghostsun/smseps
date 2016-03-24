package com.slf.sms.control;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.RequestProcessor;
import org.apache.struts.actions.DispatchAction;

import com.slf.common.base.BaseResultMap;
import com.slf.common.util.ResultUtils;
import com.slf.sms.bo.Order;
import com.slf.sms.bo.XxQy;
import com.slf.sms.common.ErrorCode;
import com.slf.sms.common.SysContext;


/**
 * 权限校验过滤器
 * @author 孙立凡
 *
 */
public class PerformControlProcessor extends RequestProcessor{

	private static final Logger log = Logger.getLogger(PerformControlProcessor.class);

	@Override
	protected ActionForward processActionPerform(HttpServletRequest request,
			HttpServletResponse response, Action action, ActionForm form,
			ActionMapping mapping) throws IOException, ServletException {
		// TODO Auto-generated method stub
		String classPath = action.getClass().getName();
		if(classPath.equals("com.slf.sms.action.LoadAgentsAction"))
		{
			try {
				return action.execute(mapping, form, request, response);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				return (processException(request, response, e, form, mapping));
			}
		}
		String BACK_MESSAGE = "";
		Order order = (Order)form;
		BaseResultMap RESULT_MAP = new BaseResultMap();
		
		if(!SysContext.AGENTS_MAP.containsKey(order.getUserId()))
		{
			log.log(Priority.INFO, "非法访问ip:" + this.getIpAddr(request) + " userid:" + order.getUserId());
			RESULT_MAP.setRetCode(ErrorCode.SUBMIT_ERROR.getCode());
			RESULT_MAP.setRetMsg( ErrorCode.SUBMIT_ERROR.getMsg());
			try {
				response.setContentType("text/xml;charset=utf-8");
				PrintWriter out = response.getWriter();
				if(out != null)
				{
					BACK_MESSAGE = ResultUtils.makeResult(RESULT_MAP, "json");
					out.write(BACK_MESSAGE);
					out.flush();
					out.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
			}
			return null;
		}
		try {
			return action.execute(mapping, form, request, response);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return (processException(request, response, e, form, mapping));
		}
	}
	
	/**
	 * 负载均衡情况下，获取真实IP
	 */
	 private String getIpAddr(HttpServletRequest request) 
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
}
