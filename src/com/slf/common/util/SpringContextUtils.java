package com.slf.common.util;

import javax.servlet.ServletContext;

import org.springframework.web.context.WebApplicationContext;

public class SpringContextUtils {

	/**
	 *  获取spring上下文信息
	 *  name -- spring配置文件bean的id
	 */
	public static Object getSpringContext(ServletContext context,String name)
	{
		WebApplicationContext applicationContext = (WebApplicationContext) context.getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
		return applicationContext.getBean(name);
	}
}
