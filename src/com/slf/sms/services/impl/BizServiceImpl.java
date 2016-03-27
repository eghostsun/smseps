package com.slf.sms.services.impl;

import java.sql.SQLException;
import java.util.List;

import com.slf.common.base.BaseDao;
import com.slf.sms.bo.LsDxhk;
import com.slf.sms.services.IBizService;

public class BizServiceImpl implements IBizService {

	private BaseDao baseDao;
	public BaseDao getBaseDao() {
		return baseDao;
	}
	public void setBaseDao(BaseDao baseDao) {
		this.baseDao = baseDao;
	}

	public void updateBatchStatus(List<LsDxhk> list) throws SQLException
	{
		for(LsDxhk lsDxhk : list)
		{
			baseDao.modify(lsDxhk, "updateSend");
		}
	}
	
	

}
