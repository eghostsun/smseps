package com.slf.sms.biz;


import java.util.List;

import com.slf.common.base.BaseDao;
import com.slf.sms.bo.LsDxhk;

public interface ITimestenBiz {

	public List<LsDxhk> querySmsStatus(LsDxhk lsDxhk) throws Exception;
	public void setTtBaseDao(BaseDao ttBaseDao);
}
