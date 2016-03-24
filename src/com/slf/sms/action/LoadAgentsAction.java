package com.slf.sms.action;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.BaseAction;

import com.slf.common.base.BaseDao;
import com.slf.common.util.SpringContextUtils;
import com.slf.sms.bo.XxQy;
import com.slf.sms.common.CommonContants;
import com.slf.sms.common.SysContext;

/**
 * 加载代理商
 * @author Administrator
 *
 */
public class LoadAgentsAction extends BaseAction {

	private static final Logger log = Logger.getLogger(LoadAgentsAction.class);
	
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			 {
		// TODO Auto-generated method stub
		if(CommonContants.LOCK_IP.indexOf(request.getRemoteAddr()) == -1)
		{
			request.setAttribute("msg", "加载失败");
			return mapping.findForward("success");
		}
		log.log(Priority.INFO, "=====开始加载企业代理信息=====");
		try {
			BaseDao baseDao = (BaseDao) SpringContextUtils.getSpringContext(this.servlet.getServletContext(), "baseDao");
			List<XxQy> list = baseDao.getList("getAllQydl");
			if(list != null)
			{
				synchronized (SysContext.AGENTS_MAP) {
					SysContext.AGENTS_MAP.clear();
					for(int i = 0; i < list.size(); i++)
					{
						XxQy xxQy = list.get(i);
						SysContext.AGENTS_MAP.put(xxQy.getDlm(), xxQy);
					}
				}
			}
			request.setAttribute("msg", "加载成功");
			log.log(Priority.INFO, "=====加载企业代理信息【"+ list.size()+"】完成=====");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			log.log(Priority.ERROR, "数据库连接失败");
			request.setAttribute("msg", "加载失败");
		}
		return mapping.findForward("success");
	}

}
