package com.slf.sms.services;

import java.util.List;
import com.slf.common.base.BaseDao;
import com.slf.sms.bo.LsMosms;
import com.slf.sms.bo.Order;
import com.slf.sms.bo.XxQy;

public interface IReqService {

	public Order addCacheSms(Order order,XxQy xxQy);
	
	public Order addCacheSms2(Order order,XxQy xxQy);
	
	public void setBaseDao(BaseDao baseDao);
	
	public List queryRecv(LsMosms lsMosms) throws Exception;
	
	public Order addMmsSms(Order order, XxQy xxQy);
}
