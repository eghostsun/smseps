package com.slf.sms.services;

import java.sql.SQLException;
import java.util.List;

import com.slf.common.base.BaseDao;
import com.slf.sms.bo.LsDxhk;
import com.slf.sms.bo.Order;
import com.slf.sms.bo.XxQy;
import com.slf.sms.exception.CheckException;

public interface IBizService {

	public void updateBatchStatus(List<LsDxhk> list) throws SQLException;
	public void setBaseDao(BaseDao baseDao);
}
