package com.slf.sms.listener;



import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletContextEvent;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;

import com.slf.common.base.BaseDao;
import com.slf.sms.bo.XxQy;
import com.slf.sms.common.SysContext;

public class BossListener extends ContextLoaderListener{

	private static final Logger log = Logger.getLogger(BossListener.class);
	private BaseDao baseDao = null;
	public void contextInitialized(ServletContextEvent event) {
		
		if(null == event.getServletContext().getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE))
		{
			super.contextInitialized(event);
		}
		
		WebApplicationContext context = (WebApplicationContext) event.getServletContext().getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
		
		baseDao = (BaseDao)context.getBean("baseDao");
		loadAllQydl();
	}
	
	
	public void loadAllQydl()
	{
		log.log(Priority.INFO, "=====开始加载企业代理信息=====");
		try {
			List<XxQy> list = baseDao.getList("getAllQydl");
			if(list != null)
			{
				for(int i = 0; i < list.size(); i++)
				{
					XxQy xxQy = list.get(i);
					SysContext.AGENTS_MAP.put(xxQy.getDlm(), xxQy);
				}
			}
			log.log(Priority.INFO, "=====加载企业代理信息【"+ list.size()+"】完成=====");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			log.log(Priority.ERROR, "数据库连接失败");
		}
	}
	
}
