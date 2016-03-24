package com.slf.sms.biz.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

import com.slf.common.base.BaseDao;
import com.slf.sms.biz.ITimestenBiz;
import com.slf.sms.bo.LsDxhk;

public class TimestenBizImpl implements ITimestenBiz {

	private static final Logger log = Logger.getLogger(TimestenBizImpl.class);
	
	private BaseDao ttBaseDao;
	public BaseDao getTtBaseDao() {
		return ttBaseDao;
	}
	public void setTtBaseDao(BaseDao ttBaseDao) {
		this.ttBaseDao = ttBaseDao;
	}
	
	public List<LsDxhk> querySmsStatus(LsDxhk lsDxhk) throws Exception
	{
//		long start = System.currentTimeMillis();
		List<LsDxhk> list = ttBaseDao.getList(lsDxhk, "querySmsStatus");
		if(list != null && !list.isEmpty())
		{
			for(LsDxhk lsDxhk2 : list)
			{
				ttBaseDao.delete(lsDxhk2, "deleteSend");
			}
//			log.log(Priority.INFO, "status->" + (System.currentTimeMillis() - start));
			return list;
		}
		return null;
	}
}
