package com.slf.sms.action;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;

import com.ibatis.sqlmap.client.SqlMapException;
import com.slf.common.base.BaseDao;
import com.slf.common.base.BaseResultMap;
import com.slf.common.util.DateUtils;
import com.slf.common.util.KeyedDigestMD5;
import com.slf.common.util.SpringContextUtils;
import com.slf.common.util.Utils;
import com.slf.sms.action.helper.ActionHelper;
import com.slf.sms.biz.ITimestenBiz;
import com.slf.sms.bo.LsDxhc;
import com.slf.sms.bo.LsDxhk;
import com.slf.sms.bo.LsMosms;
import com.slf.sms.bo.LsMtsms;
import com.slf.sms.bo.LsQydx;
import com.slf.sms.bo.Order;
import com.slf.sms.bo.XtKzcs;
import com.slf.sms.bo.XxDlqb;
import com.slf.sms.bo.XxQy;
import com.slf.sms.common.CommonContants;
import com.slf.sms.common.ErrorCode;
import com.slf.sms.exception.CheckException;
import com.slf.sms.services.IBizService;
import com.slf.sms.services.IReqService;
import com.slf.sms.services.impl.BizServiceImpl;
import com.slf.sms.services.impl.ReqServiceImpl;


/** 
 * @author 孙立凡 E-mail:eghostsun@gmail.com 
 * @version 创建时间：2013-2-19 下午04:38:27 
 * 类说明 请求入口
 */
public class ReqInAction extends com.slf.common.base.BaseAction {

	private static final Logger log = Logger.getLogger(ReqInAction.class);
	private static final String QUERY_TIME = "query.time";
	/**
	 * 短信提交请求
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	public ActionForward reqSmsSend(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) 
	{
		RedisTemplate redisTemplate = (RedisTemplate) SpringContextUtils.getSpringContext(this.getContext(),"redisTemplate");
		Order order = (Order)form;
		final String orderId = "orderId" + order.getOrderId();
		BaseResultMap RESULT_MAP = new BaseResultMap();
		log.log(Priority.INFO, "提交请求[smssend]userId:" + order.getUserId() + " orderId:" + order.getOrderId() + " msgType:" + order.getMsgType()
				+ " dateOrder:" + order.getDateOrder() + " number:" + order.getNumber() + " ip:" + this.getIpAddr(request));
		XxQy xxQy = this.getUserToken(order.getUserId());
		try{
			RESULT_MAP.put("orderId", order.getOrderId());
			if(xxQy == null)
			{
				RESULT_MAP.setRetCode(ErrorCode.SUBMIT_ERROR.getCode());
				RESULT_MAP.setRetMsg( ErrorCode.SUBMIT_ERROR.getMsg());
				return null;
			}
			if(xxQy.getYxfsbz() == 0)
			{
				RESULT_MAP.setRetCode(ErrorCode.SUBMIT_ERROR.getCode());
				RESULT_MAP.setRetMsg( ErrorCode.SUBMIT_ERROR.getMsg());
				return null;
			}
			if(order.getOrderId() == null || "".equals(order.getOrderId().trim()))
			{
				RESULT_MAP.setRetCode(ErrorCode.SUBMIT_ERROR.getCode());
				RESULT_MAP.setRetMsg( ErrorCode.SUBMIT_ERROR.getMsg());
				return null;
			}
			Long orderNum = (Long) redisTemplate.execute(new RedisCallback() {
				@Override
				public Object doInRedis(RedisConnection connection)
						throws DataAccessException {
					// TODO Auto-generated method stub
					return connection.incr(orderId.getBytes());
				}
			});
			if(orderNum > 1)
			{
				RESULT_MAP.setRetCode(ErrorCode.REPEAT_REQ.getCode());
				RESULT_MAP.setRetMsg(ErrorCode.REPEAT_REQ.getMsg());
				return null;
			}
			if(order.getMsgs() == null || "".equals(order.getMsgs().trim()))
			{
				RESULT_MAP.setRetCode(ErrorCode.SUBMIT_ERROR.getCode());
				RESULT_MAP.setRetMsg( ErrorCode.SUBMIT_ERROR.getMsg());
				return null;
			}
			if(order.getMsgType() == 1) //预约发送
			{
				if(order.getDateOrder() == null || "".equals(order.getDateOrder()))
				{
					RESULT_MAP.setRetCode(ErrorCode.SUBMIT_ERROR.getCode());
					RESULT_MAP.setRetMsg( ErrorCode.SUBMIT_ERROR.getMsg());
					return null;
				}else if(Long.parseLong(order.getDateOrder()) < Long.parseLong(DateUtils.strDate("yyyyMMddHHmmss"))){
					RESULT_MAP.setRetCode(ErrorCode.SUBMIT_ERROR.getCode());
					RESULT_MAP.setRetMsg( ErrorCode.SUBMIT_ERROR.getMsg());
					return null;
				}
			}
			if(order.getNumber() != null && !"".equals(order.getNumber()))
			{
				if(!Utils.check4Num(order.getNumber()))
				{
					RESULT_MAP.setRetCode(ErrorCode.SUBMIT_ERROR.getCode());
					RESULT_MAP.setRetMsg( ErrorCode.SUBMIT_ERROR.getMsg());
					return null;
				}
			}
			
			if(order.getSign() == null || "".equals(order.getSign().trim()))
			{
				RESULT_MAP.setRetCode(ErrorCode.SUBMIT_ERROR.getCode());
				RESULT_MAP.setRetMsg( ErrorCode.SUBMIT_ERROR.getMsg());
				return null;
			}
			String sign = ActionHelper.makeSmsSendSign(order, xxQy);
			if(!sign.equals(order.getSign()))
			{
				RESULT_MAP.setRetCode(ErrorCode.SIGN_ERROR.getCode());
				RESULT_MAP.setRetMsg( ErrorCode.SIGN_ERROR.getMsg());
				return null;
			}
			
			//校验通过，进行解析msgs
			IReqService reqService = (IReqService) SpringContextUtils.getSpringContext(this.getContext(), "reqService");
			order = reqService.addCacheSms(order, xxQy);
			
			RESULT_MAP.setRetCode(order.getStatus());
			RESULT_MAP.setRetMsg(order.getResult());
		}catch (Exception e) {
			// TODO: handle exception
			RESULT_MAP.setRetCode(ErrorCode.SYS_ERROR.getCode());
			RESULT_MAP.setRetMsg(ErrorCode.SYS_ERROR.getMsg());
		}finally{
			RESULT_MAP.put("sign", KeyedDigestMD5.getKeyedDigest(order.getOrderId() + RESULT_MAP.getRetCode() + RESULT_MAP.getRetMsg() + xxQy.getJym(), ""));
			backMsg(response ,RESULT_MAP);
			redisTemplate.execute(new RedisCallback() {
				@Override
				public Object doInRedis(RedisConnection connection)
						throws DataAccessException {
					// TODO Auto-generated method stub
					return connection.expire(orderId.getBytes(), CommonContants.ORDER_TIMEOUT);
				}
				
			});
		}
		return null;
	} 
	
	/**
	 * 短信提交请求-加密action
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	public ActionForward reqSendSms(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) 
	{
		RedisTemplate redisTemplate = (RedisTemplate) SpringContextUtils.getSpringContext(this.getContext(),"redisTemplate");
		Order order = (Order)form;
		final String orderId = "orderId" + order.getOrderId();
		BaseResultMap RESULT_MAP = new BaseResultMap();
		log.info("提交请求[sendsms]->orderId:" + order.getOrderId() + " dateOrder:" + order.getDateOrder() + " msgType" + order.getMsgType() + " number" + order.getNumber() + 
				" userId:" + order.getUserId() + " msg:" + order.getMsgs());
		XxQy xxQy = this.getUserToken(order.getUserId());
		try{
			RESULT_MAP.put("orderId", order.getOrderId());
			if(xxQy == null)
			{
				RESULT_MAP.setRetCode(ErrorCode.SUBMIT_ERROR.getCode());
				RESULT_MAP.setRetMsg( ErrorCode.SUBMIT_ERROR.getMsg());
				return null;
			}
			if(order.getOrderId() == null || "".equals(order.getOrderId().trim()))
			{
				RESULT_MAP.setRetCode(ErrorCode.SUBMIT_ERROR.getCode());
				RESULT_MAP.setRetMsg( ErrorCode.SUBMIT_ERROR.getMsg());
				return null;
			}
			Long orderNum = (Long) redisTemplate.execute(new RedisCallback() {
				@Override
				public Object doInRedis(RedisConnection connection)
						throws DataAccessException {
					// TODO Auto-generated method stub
					return connection.incr(orderId.getBytes());
				}
			});
			if(orderNum > 1)
			{
				RESULT_MAP.setRetCode(ErrorCode.REPEAT_REQ.getCode());
				RESULT_MAP.setRetMsg(ErrorCode.REPEAT_REQ.getMsg());
				return null;
			}
			if(order.getMsgs() == null || "".equals(order.getMsgs().trim()))
			{
				RESULT_MAP.setRetCode(ErrorCode.SUBMIT_ERROR.getCode());
				RESULT_MAP.setRetMsg( ErrorCode.SUBMIT_ERROR.getMsg());
				return null;
			}
			if(order.getMsgType() == 1) //预约发送
			{
				if(order.getDateOrder() == null || "".equals(order.getDateOrder()))
				{
					RESULT_MAP.setRetCode(ErrorCode.SUBMIT_ERROR.getCode());
					RESULT_MAP.setRetMsg( ErrorCode.SUBMIT_ERROR.getMsg());
					return null;
				}else if(Long.parseLong(order.getDateOrder()) < Long.parseLong(DateUtils.strDate("yyyyMMddHHmmss"))){
					RESULT_MAP.setRetCode(ErrorCode.SUBMIT_ERROR.getCode());
					RESULT_MAP.setRetMsg( ErrorCode.SUBMIT_ERROR.getMsg());
					return null;
				}
			}
			if(order.getNumber() != null && !"".equals(order.getNumber()))
			{
				if(!Utils.check4Num(order.getNumber()))
				{
					RESULT_MAP.setRetCode(ErrorCode.SUBMIT_ERROR.getCode());
					RESULT_MAP.setRetMsg( ErrorCode.SUBMIT_ERROR.getMsg());
					return null;
				}
			}
			if(order.getSign() == null || "".equals(order.getSign().trim()))
			{
				RESULT_MAP.setRetCode(ErrorCode.SUBMIT_ERROR.getCode());
				RESULT_MAP.setRetMsg( ErrorCode.SUBMIT_ERROR.getMsg());
				return null;
			}
			String sign = ActionHelper.makeSmsSendSign(order, xxQy);
			if(!sign.equals(order.getSign()))
			{
				RESULT_MAP.setRetCode(ErrorCode.SIGN_ERROR.getCode());
				RESULT_MAP.setRetMsg( ErrorCode.SIGN_ERROR.getMsg());
				return null;
			}
			
			//校验通过，进行解析msgs
			IReqService reqService = (IReqService) SpringContextUtils.getSpringContext(this.getContext(), "reqService");
			order = reqService.addCacheSms2(order, xxQy);
			
			RESULT_MAP.setRetCode(order.getStatus());
			RESULT_MAP.setRetMsg(order.getResult());
		}catch (Exception e) {
			// TODO: handle exception
			RESULT_MAP.setRetCode(ErrorCode.SYS_ERROR.getCode());
			RESULT_MAP.setRetMsg(ErrorCode.SYS_ERROR.getMsg());
		}finally{
			RESULT_MAP.put("sign", KeyedDigestMD5.getKeyedDigest(order.getOrderId() + RESULT_MAP.getRetCode() + RESULT_MAP.getRetMsg() + xxQy.getJym(), ""));
			backMsg(response ,RESULT_MAP);
			log.info("[smssend]->" + RESULT_MAP);
			redisTemplate.execute(new RedisCallback() {
				@Override
				public Object doInRedis(RedisConnection connection)
						throws DataAccessException {
					// TODO Auto-generated method stub
					return connection.expire(orderId.getBytes(), CommonContants.ORDER_TIMEOUT);
				}
				
			});
		}
		return null;
	} 
	
	/**
	 * 短信状态查询
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	public ActionForward reqSmsQuery(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) 
	{
		Order order = (Order)form;
		BaseResultMap RESULT_MAP = new BaseResultMap();
		log.log(Priority.INFO, "提交请求userId:" + order.getUserId() + " orderId:" + order.getOrderId() 
				+ " phone:" + order.getPhone() + " date:" + order.getDate() + " ip:" + this.getIpAddr(request));
		XxQy xxQy = this.getUserToken(order.getUserId());
		try{
			if(null == request.getSession().getAttribute(QUERY_TIME))
			{
				request.getSession().setAttribute(QUERY_TIME, System.currentTimeMillis());
			}else{
				Long time = (Long)request.getSession().getAttribute(QUERY_TIME);
				if(System.currentTimeMillis() - time.longValue() < 200)
				{
					RESULT_MAP.setRetCode(ErrorCode.QUERY_OFTEN.getCode());
					RESULT_MAP.setRetMsg( ErrorCode.QUERY_OFTEN.getMsg());
					return null;
				}
				request.getSession().setAttribute(QUERY_TIME, System.currentTimeMillis());
			}
			RESULT_MAP.put("orderId", order.getOrderId());
			if(order.getOrderId() == null || "".equals(order.getOrderId().trim()))
			{
				RESULT_MAP.setRetCode(ErrorCode.SUBMIT_ERROR.getCode());
				RESULT_MAP.setRetMsg( ErrorCode.SUBMIT_ERROR.getMsg());
				return null;
			}
			if(order.getDate() == null || "".equals(order.getDate().trim()))
			{
				RESULT_MAP.setRetCode(ErrorCode.SUBMIT_ERROR.getCode());
				RESULT_MAP.setRetMsg( ErrorCode.SUBMIT_ERROR.getMsg());
				return null;
			}
			BaseDao baseDao = (BaseDao) SpringContextUtils.getSpringContext(this.getContext(), "baseDao");
			LsDxhc lsDxhc = new LsDxhc();
			lsDxhc.setDlflsh(order.getOrderId());
			lsDxhc.setDlid(xxQy.getDlid());
			int status = -1;
			if(order.getDate().equals(DateUtils.strDate("yyyyMMdd")))
			{
				status = baseDao.getCount(lsDxhc, "queryCacheStatus").intValue();
			}else if(Integer.parseInt(order.getDate()) < Integer.parseInt(DateUtils.strDate("yyyyMMdd"))){
				status = baseDao.getCount(lsDxhc, "queryCacheStatusM").intValue();
			}
			if(status >= 1)
			{
				//未处理完
				RESULT_MAP.setRetCode(ErrorCode.DEAL_RUNNING.getCode());
				RESULT_MAP.setRetMsg(ErrorCode.DEAL_RUNNING.getMsg());
				if(order.getPhone() != null && !"".equals(order.getPhone()))
				{
					List<LsMtsms> msgs = null;
					LsMtsms lsMtsms = new LsMtsms();
					lsMtsms.setFshm(order.getPhone());
					lsMtsms.setDlflsh(order.getOrderId());
					lsMtsms.setDlid(xxQy.getDlid());
					if(order.getDate().equals(DateUtils.strDate("yyyyMMdd")))
					{
						msgs = baseDao.getList(lsMtsms, "queryPhoneStatus");
					}else if(Integer.parseInt(order.getDate()) < Integer.parseInt(DateUtils.strDate("yyyyMMdd"))){
						msgs = baseDao.getList(lsMtsms, "queryPhoneStatusM");
					}
					RESULT_MAP.put("msgs", ActionHelper.queryPhoneStatus(msgs));
				}
			}else if(status == 0){
				//已处理完
				RESULT_MAP.setRetCode(ErrorCode.DEAL_SUCCESS.getCode());
				RESULT_MAP.setRetMsg(ErrorCode.DEAL_SUCCESS.getMsg());
				//查询号码不为空
				List<LsMtsms> msgs = null;
				LsMtsms lsMtsms = new LsMtsms();
				lsMtsms.setFshm(order.getPhone());
				lsMtsms.setDlid(xxQy.getDlid());
				lsMtsms.setDlflsh(order.getOrderId());
				if(order.getDate().equals(DateUtils.strDate("yyyyMMdd")))
				{
					msgs = baseDao.getList(lsMtsms, "queryPhoneStatus");
				}else if(Integer.parseInt(order.getDate()) < Integer.parseInt(DateUtils.strDate("yyyyMMdd"))){
					msgs = baseDao.getList(lsMtsms, "queryPhoneStatusM");
				}
				RESULT_MAP.put("msgs", ActionHelper.queryPhoneStatus(msgs));
			}else{
				RESULT_MAP.setRetCode(ErrorCode.UNFOUND.getCode());
				RESULT_MAP.setRetMsg(ErrorCode.UNFOUND.getMsg());
			}
		}catch (Exception e) {
			// TODO: handle exception
			RESULT_MAP.setRetCode(ErrorCode.SYS_ERROR.getCode());
			RESULT_MAP.setRetMsg(ErrorCode.SYS_ERROR.getMsg());
		}finally{
			RESULT_MAP.put("sign", KeyedDigestMD5.getKeyedDigest(order.getOrderId() + RESULT_MAP.getRetCode() + RESULT_MAP.getRetMsg() + xxQy.getJym(), ""));
			backMsg(response ,RESULT_MAP);
		}
		return null;
	} 
	
	/**
	 * 资金余额查询
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	public ActionForward reqSmsMoney(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) 
	{
		Order order = (Order)form;
		BaseResultMap RESULT_MAP = new BaseResultMap();
		log.log(Priority.INFO, "提交请求userId:" + order.getUserId());
		XxQy xxQy = this.getUserToken(order.getUserId());
		String money = "0";
		try{
			BaseDao baseDao = (BaseDao) SpringContextUtils.getSpringContext(this.getContext(), "baseDao");
			XxDlqb xxDlqb = new XxDlqb();
			xxDlqb.setDlid(xxQy.getDlid());
			xxDlqb = (XxDlqb) baseDao.getObject(xxDlqb,"getDlzj");
			if(xxDlqb != null)
			{
				money = String.valueOf(xxDlqb.getZjye().doubleValue());
				RESULT_MAP.setRetCode(ErrorCode.SUBMIT_SUCCESS.getCode());
				RESULT_MAP.setRetMsg(ErrorCode.SUBMIT_SUCCESS.getMsg());
			}else{
				RESULT_MAP.setRetCode(ErrorCode.SUBMIT_ERROR.getCode());
				RESULT_MAP.setRetMsg(ErrorCode.SUBMIT_ERROR.getMsg());
			}
			RESULT_MAP.put("money", money);
		}catch (SQLException e) {
			// TODO: handle exception
			RESULT_MAP.setRetCode(ErrorCode.SYS_ERROR.getCode());
			RESULT_MAP.setRetMsg(ErrorCode.SYS_ERROR.getMsg());
		}finally{
			RESULT_MAP.put("sign", KeyedDigestMD5.getKeyedDigest(money + RESULT_MAP.getRetCode() + RESULT_MAP.getRetMsg() + xxQy.getJym(), ""));
			backMsg(response ,RESULT_MAP);
		}
		return null;
	} 
	
	/**
	 * 消息状态查询
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	public ActionForward reqSmsStatus(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) 
	{
		BaseResultMap RESULT_MAP = new BaseResultMap();
		Order order = (Order)form;
		List list = null;
		log.info("sms status->" + request.getParameterMap());
		try {
			XxQy xxQy = this.getUserToken(order.getUserId());
			if(xxQy.getPretime() != 0 && System.currentTimeMillis() - xxQy.getPretime() <= 20)
			{
				RESULT_MAP.put("result", "0");
				return null;
			}
			list = new ArrayList();
			xxQy.setPretime(System.currentTimeMillis());
			ITimestenBiz timestenBiz = (ITimestenBiz) SpringContextUtils.getSpringContext(this.getContext(), "timestenBiz");
			LsDxhk lsDxhk = new LsDxhk();
			lsDxhk.setDlid(xxQy.getDlid());
			IBizService bizService = (IBizService)SpringContextUtils.getSpringContext(this.getContext(), "bizService");
			for(int i = 0; i < CommonContants.STATUS_NUM; i++)
			{
				List<LsDxhk> dxhkList = timestenBiz.querySmsStatus(lsDxhk);
				if(dxhkList == null || dxhkList.isEmpty())
				{
					RESULT_MAP.put("result", "0");
					return null;
				}
				for(LsDxhk lsDxhk2 : dxhkList)
				{
					Map<String, Object> obj = new HashMap<String, Object>();
					obj.put("ORDERID", lsDxhk2.getDlflsh());
					obj.put("PHONE", lsDxhk2.getFshm());
					if("ok".equals(lsDxhk2.getCljg()))
					{
						obj.put("STATUS", "1");
					}else{
						obj.put("STATUS", "-1");
					}
					obj.put("CODE", lsDxhk2.getCljg());
					obj.put("DXHKID", lsDxhk2.getDxhkid());
					obj.put("DESC", lsDxhk2.getFhms());
					obj.put("TIME", lsDxhk2.getFhsj());
					list.add(obj);
//					long updatestart = System.currentTimeMillis();
					bizService.updateBatchStatus(dxhkList);
//					log.log(Priority.INFO, "update finish:" + (System.currentTimeMillis() - updatestart));
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			RESULT_MAP.put("result", "0");
			e.printStackTrace();
		} finally{
			if(list != null && !list.isEmpty())
			{
				RESULT_MAP.put("result", list);
			}else{
				RESULT_MAP.put("result", "0");
			}
			backMsg(response ,RESULT_MAP);
		}
		return null;
	}
	
	
	/**
	 * 消息回复查询
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	public ActionForward reqQueryRecv(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) 
	{
		BaseResultMap RESULT_MAP = new BaseResultMap();
		Order order = (Order)form;
		log.log(Priority.INFO, "sms recv->userId:" + order.getUserId());
		
		XxQy xxQy = this.getUserToken(order.getUserId());
		IReqService reqService = (IReqService) SpringContextUtils.getSpringContext(this.getContext(), "reqService");
		LsMosms lsMosms = new LsMosms();
		lsMosms.setDlid(xxQy.getDlid());
		List list = null;
		try {
			list = reqService.queryRecv(lsMosms);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			RESULT_MAP.put("result", "0");
		} finally{
			if(list != null)
			{
				RESULT_MAP.put("result", list);
			}else{
				RESULT_MAP.put("result", "0");
			}
			backMsg(response ,RESULT_MAP);
		}
		return null;
		
	}
	
	/**
	 * 彩信发送接口
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	public ActionForward reqSendMMS(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) 
	{
		Order order = (Order)form;
		BaseResultMap RESULT_MAP = new BaseResultMap();
		log.log(Priority.INFO, "提交请求userId:" + order.getUserId() + " orderId:" + order.getOrderId() + " msgType:" + order.getMsgType()
				+ " dateOrder:" + order.getDateOrder() + " number:" + order.getNumber() + " subject:" + order.getSubject() + " ip:" + this.getIpAddr(request));
		XxQy xxQy = this.getUserToken(order.getUserId());
		RESULT_MAP.put("orderId", order.getOrderId());
		try{
			if(xxQy == null)
			{
				RESULT_MAP.setRetCode(ErrorCode.SUBMIT_ERROR.getCode());
				RESULT_MAP.setRetMsg( ErrorCode.SUBMIT_ERROR.getMsg());
				return null;
			}
			if(order.getOrderId() == null || "".equals(order.getOrderId().trim()))
			{
				RESULT_MAP.setRetCode(ErrorCode.SUBMIT_ERROR.getCode());
				RESULT_MAP.setRetMsg( ErrorCode.SUBMIT_ERROR.getMsg());
				return null;
			}
			if(order.getSubject() == null || "".equals(order.getSubject().trim()))
			{
				RESULT_MAP.setRetCode(ErrorCode.SUBMIT_ERROR.getCode());
				RESULT_MAP.setRetMsg( ErrorCode.SUBMIT_ERROR.getMsg());
				return null;
			}
			if(order.getMsgs() == null || "".equals(order.getMsgs().trim()))
			{
				RESULT_MAP.setRetCode(ErrorCode.SUBMIT_ERROR.getCode());
				RESULT_MAP.setRetMsg( ErrorCode.SUBMIT_ERROR.getMsg());
				return null;
			}
			if(order.getMsgType() == 1) //预约发送
			{
				if(order.getDateOrder() == null || "".equals(order.getDateOrder()))
				{
					RESULT_MAP.setRetCode(ErrorCode.SUBMIT_ERROR.getCode());
					RESULT_MAP.setRetMsg( ErrorCode.SUBMIT_ERROR.getMsg());
					return null;
				}else if(Integer.parseInt(order.getDateOrder()) < Integer.parseInt(DateUtils.strDate("yyyyMMddHHmmss"))){
					RESULT_MAP.setRetCode(ErrorCode.SUBMIT_ERROR.getCode());
					RESULT_MAP.setRetMsg( ErrorCode.SUBMIT_ERROR.getMsg());
					return null;
				}
			}
			if(order.getNumber() != null && !"".equals(order.getNumber()))
			{
				if(!Utils.check4Num(order.getNumber()))
				{
					RESULT_MAP.setRetCode(ErrorCode.SUBMIT_ERROR.getCode());
					RESULT_MAP.setRetMsg( ErrorCode.SUBMIT_ERROR.getMsg());
					return null;
				}
			}
			
			if(order.getSign() == null || "".equals(order.getSign().trim()))
			{
				RESULT_MAP.setRetCode(ErrorCode.SUBMIT_ERROR.getCode());
				RESULT_MAP.setRetMsg( ErrorCode.SUBMIT_ERROR.getMsg());
				return null;
			}
			String sign = ActionHelper.makeSmsSendSign(order, xxQy);
			if(!sign.equals(order.getSign()))
			{
				RESULT_MAP.setRetCode(ErrorCode.SIGN_ERROR.getCode());
				RESULT_MAP.setRetMsg( ErrorCode.SIGN_ERROR.getMsg());
				return null;
			}
			
			IReqService reqService = (IReqService) SpringContextUtils.getSpringContext(this.getContext(), "reqService");
			order = reqService.addMmsSms(order, xxQy);
			
			RESULT_MAP.setRetCode(order.getStatus());
			RESULT_MAP.setRetMsg(order.getResult());
		} catch (Exception e) {
			// TODO: handle exception
			RESULT_MAP.setRetCode(ErrorCode.SYS_ERROR.getCode());
			RESULT_MAP.setRetMsg(ErrorCode.SYS_ERROR.getMsg());
		} finally {
			RESULT_MAP.put("sign", KeyedDigestMD5.getKeyedDigest(order.getOrderId() + RESULT_MAP.getRetCode() + RESULT_MAP.getRetMsg() + xxQy.getJym(), ""));
			backMsg(response ,RESULT_MAP);
		}
		return null;
	}
}
