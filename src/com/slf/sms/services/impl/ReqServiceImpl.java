package com.slf.sms.services.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

import com.slf.common.base.BaseDao;
import com.slf.common.client.HttpSqsServiceImpl;
import com.slf.common.client.IHttpSqsService;
import com.slf.common.util.CryptUtils;
import com.slf.common.util.DateUtils;
import com.slf.common.util.ReadRespUtils;
import com.slf.common.util.Utils;
import com.slf.common.util.ZipUtils;
import com.slf.sms.action.helper.CallProcedureHelper;
import com.slf.sms.bo.CallPMap;
import com.slf.sms.bo.LsDxhc;
import com.slf.sms.bo.LsMosms;
import com.slf.sms.bo.Order;
import com.slf.sms.bo.XxDxgjz;
import com.slf.sms.bo.XxQy;
import com.slf.sms.bo.XxYkyq;
import com.slf.sms.common.CommonContants;
import com.slf.sms.common.ErrorCode;
import com.slf.sms.services.IReqService;
import com.slf.sms.services.helper.ServiceHelper;

public class ReqServiceImpl implements IReqService {

	private static final Logger log = Logger.getLogger(ReqServiceImpl.class);
	
	private BaseDao baseDao;
	
	public BaseDao getBaseDao() {
		return baseDao;
	}

	public void setBaseDao(BaseDao baseDao) {
		this.baseDao = baseDao;
	}

	public Order addCacheSms(Order order,XxQy xxQy)
	{
		List<Object> msgs = new ReadRespUtils().readJsonResult(order.getMsgs()); //解析短信内容
		if(msgs.size() > 300 || msgs.size() <= 0)
		{
			order.setResult(ErrorCode.MSGS_ERROR.getMsg());
			order.setStatus(ErrorCode.MSGS_ERROR.getCode());
			return order;
		}
		
		//计算号码数量
		List<String> list = new ArrayList<String>();
		int sum = 0;
		for(int i = 0; i < msgs.size(); i++)
		{
			Map<String, String> msg = (Map<String, String>) msgs.get(i);
			String phones[] = msg.get("phone").split("\\,");
			/**
			 * 校验号码是否存在重复
			 */
			for(int j = 0; j < phones.length; j++)
			{
				String phone = phones[j].split("\\:")[0];
				if(!Utils.checkHmq(phone))
				{
					order.setResult(ErrorCode.SUBMIT_ERROR.getMsg() + ":" + phone);
					order.setStatus(ErrorCode.SUBMIT_ERROR.getCode());
					return order;
				}
				if(!list.contains(phone))
				{
					list.add(phone);
				}else{
					order.setResult(ErrorCode.PHONE_NUM.getMsg());
					order.setStatus(ErrorCode.PHONE_NUM.getCode());
					return order;
				}
				sum += phones.length;
			}
		}
		
		list.clear();
		if(sum > CommonContants.MAX_NUM || sum <= 0)
		{
			order.setResult(ErrorCode.PHONE_NUM.getMsg());
			order.setStatus(ErrorCode.PHONE_NUM.getCode());
			return order;
		}
		IHttpSqsService httpSqsService = new HttpSqsServiceImpl();
		List<XxDxgjz> gjzList = null;
		try {
			if(!"1".equals(xxQy.getGjzjyzt()))
			{ //判断用户是否需要匹配关键字
				XxDxgjz xxDxgjz = new XxDxgjz();
				xxDxgjz.setDlid(xxQy.getDlid());
				gjzList = baseDao.getList(xxDxgjz, "getAllGjz");
			}
			for(int i = 0; i < msgs.size(); i++)
			{
				Map<String, String> msg = (Map<String, String>) msgs.get(i);
				//获取需要匹配的关键字
				String content = msg.get("content");
				String key = ServiceHelper.compareGjz(gjzList, content);
				if(null != key)
				{
					order.setStatus(ErrorCode.KEY_ERROR.getCode());
					order.setResult(ErrorCode.KEY_ERROR.getMsg() + ":" +key);
					return order;
				}
				if("1".equals(xxQy.getHzbmdbz()) && Utils.hasSuffix(content) != 1)//已开通后缀白名单,已经要在内容中加上后缀签名
				{
					order.setStatus(ErrorCode.MSGS_ERROR.getCode());
					order.setResult(ErrorCode.MSGS_ERROR.getMsg());
					return order;
				}
				//扣保证金
				CallPMap pMap = CallProcedureHelper.makebzj(order,msg, xxQy);
				baseDao.callProcedure(pMap, "TRAN_KBZJ");
				if(pMap.isSuccess())
				{
					LsDxhc lsDxhc = new LsDxhc();
					lsDxhc.setDlid(xxQy.getDlid());
					lsDxhc.setDxhmq(msg.get("phone"));
					lsDxhc.setClbz("3");
					lsDxhc.setSpjrh(order.getNumber());
					lsDxhc.setDxhcid(Long.valueOf(pMap.get("DXHCID") == null ? "0" : pMap.get("DXHCID").toString()));
					baseDao.modify(lsDxhc, "updateHmq"); //更新号码群
					Map<String, String> obj = new HashMap<String, String>();
					obj.put("dxhcid", String.valueOf(lsDxhc.getDxhcid()));
					httpSqsService.putIntoSqs(CommonContants.DXHC_NAME, obj);
				}else{
					log.log(Priority.ERROR, "保证金操作失败code：" + pMap.getRetCode() + " ret:" + pMap.getRetMsg());
					switch (pMap.getRetCode()) {
					case 80000003:
						order.setResult(ErrorCode.NO_MONEY.getMsg());
						order.setStatus(ErrorCode.NO_MONEY.getCode());
						return order;
					default:
						order.setResult(ErrorCode.SUBMIT_ERROR.getMsg());
						order.setStatus(ErrorCode.SUBMIT_ERROR.getCode());
						return order;
					}
				}
			}
			order.setStatus(ErrorCode.SUBMIT_SUCCESS.getCode());
			order.setResult(ErrorCode.SUBMIT_SUCCESS.getMsg());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.log(Priority.ERROR, "报文解析异常：" + order.getOrderId() + " msgs:" + order.getMsgs());
			order.setStatus(ErrorCode.SUBMIT_ERROR.getCode());
			order.setResult(ErrorCode.SUBMIT_ERROR.getMsg());
		} 
		return order;
	}
	public List queryRecv(LsMosms lsMosms) throws Exception
	{
		List list = baseDao.getList(lsMosms, "queryrecv");
		if(list != null && !list.isEmpty())
		{
			StringBuffer dxids = new StringBuffer();
			for(int i = 0; i < list.size(); i++)
			{
				Map<String, Object> obj = (Map<String, Object>) list.get(i);
				dxids.append(obj.get("dxid"));
				if(i < list.size() - 1)
				{
					dxids.append(",");
				}
			}
			lsMosms.setDxids(dxids.toString());
			baseDao.modify(lsMosms, "recvHasSend");
			return list;
		}
		return null;
	}

	@Override
	public Order addCacheSms2(Order order, XxQy xxQy) {
		// TODO Auto-generated method stub
//		long begindeal = System.currentTimeMillis();
		List<Object> msgs = new ReadRespUtils().readJsonResult(order.getMsgs()); //解析短信内容
		if(msgs.size() > 1 || msgs.size() <= 0)
		{
			order.setResult(ErrorCode.MSGS_ERROR.getMsg());
			order.setStatus(ErrorCode.MSGS_ERROR.getCode());
			return order;
		}
		//计算号码数量
		List<String> list = new ArrayList<String>();
		int sum = 0;
		for(int i = 0; i < msgs.size(); i++)
		{
			Map<String, String> msg = (Map<String, String>) msgs.get(i);
			String phones[] = msg.get("phone").split("\\,");
			/**
			 * 校验号码是否存在重复
			 */
			for(int j = 0; j < phones.length; j++)
			{
				String phone = phones[j].split("\\:")[0];
				if(!Utils.checkHmq(phone))
				{
					order.setResult(ErrorCode.SUBMIT_ERROR.getMsg() + ":" + phone);
					order.setStatus(ErrorCode.SUBMIT_ERROR.getCode());
					return order;
				}
				if(!list.contains(phone))
				{
					list.add(phone);
				}else{
					order.setResult(ErrorCode.PHONE_NUM.getMsg());
					order.setStatus(ErrorCode.PHONE_NUM.getCode());
					return order;
				}
			}
			sum += phones.length;
		}
		list.clear();
		if(sum > CommonContants.MAX_NUM || sum <= 0)
		{
			order.setResult(ErrorCode.PHONE_NUM.getMsg());
			order.setStatus(ErrorCode.PHONE_NUM.getCode());
			return order;
		}
		List<XxDxgjz> gjzList = null;
		IHttpSqsService httpSqsService = new HttpSqsServiceImpl();
		try {
			if(!"1".equals(xxQy.getGjzjyzt()))
			{ //判断用户是否需要匹配关键字
				XxDxgjz xxDxgjz = new XxDxgjz();
				xxDxgjz.setDlid(xxQy.getDlid());
				gjzList = baseDao.getList(xxDxgjz, "getAllGjz");
			}
			for(int i = 0; i < msgs.size(); i++)
			{
				
				Map<String, String> msg = (Map<String, String>) msgs.get(i);
				String content = CryptUtils.decrypt(msg.get("content"), xxQy.getJym());
				log.info("orderId:" + order.getOrderId() + ", content:" + content);
				/**
				 * 校验一客一签
				 */
				if(xxQy.getKqbz() == 1 && order.getNumber() != null && !"".equals(order.getNumber()))
				{
					XxYkyq xxYkyq = new XxYkyq();
					xxYkyq.setDlid(xxQy.getDlid());
					xxYkyq.setHm(Integer.parseInt(order.getNumber()));
					String suffix = Utils.getSuffix(content);
					xxYkyq.setQm(suffix);
					if(baseDao.getCount(xxYkyq, "checkHasSuffix").intValue() <= 0)
					{
						order.setStatus(ErrorCode.MSGS_ERROR.getCode());
						order.setResult(ErrorCode.MSGS_ERROR.getMsg() + ":" +suffix);
						return order; 
					}
				}
				//获取需要匹配的关键字
				String key = ServiceHelper.compareGjz(gjzList, content);
				if(null != key)
				{
					order.setStatus(ErrorCode.KEY_ERROR.getCode());
					order.setResult(ErrorCode.KEY_ERROR.getMsg() + ":" +key);
					return order;
				}
				if("1".equals(xxQy.getHzbmdbz()) && Utils.hasSuffix(content) != 1)//已开通后缀白名单,已经要在内容中加上后缀签名
				{
					order.setStatus(ErrorCode.MSGS_ERROR.getCode());
					order.setResult(ErrorCode.MSGS_ERROR.getMsg());
					return order;
				}
				//扣保证金
				CallPMap pMap = CallProcedureHelper.makebzj2(order,msg, xxQy);
				baseDao.callProcedure(pMap, "TRAN_KBZJ");
				if(pMap.isSuccess())
				{
					LsDxhc lsDxhc = new LsDxhc();
					lsDxhc.setDlid(xxQy.getDlid());
					lsDxhc.setDxhmq(msg.get("phone"));
					lsDxhc.setClbz("3");
					lsDxhc.setSpjrh(order.getNumber());
					lsDxhc.setDxhcid(Long.valueOf(pMap.get("DXHCID") == null ? "0" : pMap.get("DXHCID").toString()));
					baseDao.modify(lsDxhc, "updateHmq"); //更新号码群
					Map<String, String> obj = new HashMap<String, String>();
					obj.put("dxhcid", String.valueOf(lsDxhc.getDxhcid()));
					httpSqsService.putIntoSqs(CommonContants.DXHC_NAME, obj);
				}else{
					log.log(Priority.ERROR, "保证金操作失败code：" + pMap.getRetCode() + " ret:" + pMap.getRetMsg());
					switch (pMap.getRetCode()) {
					case 80000003:
						order.setResult(ErrorCode.NO_MONEY.getMsg());
						order.setStatus(ErrorCode.NO_MONEY.getCode());
						return order;
					default:
						order.setResult(ErrorCode.SUBMIT_ERROR.getMsg());
						order.setStatus(ErrorCode.SUBMIT_ERROR.getCode());
						return order;
					}
				}
			}
			order.setStatus(ErrorCode.SUBMIT_SUCCESS.getCode());
			order.setResult(ErrorCode.SUBMIT_SUCCESS.getMsg());
		} catch (Exception e) {
			// TODO Auto-generated catch block
//			if(e.getMessage().contains("U_QYDX_DLFLSH"))
//			{
//				order.setStatus(ErrorCode.REPEAT_REQ.getCode());
//				order.setResult(ErrorCode.REPEAT_REQ.getMsg());
//				return order;
//			}
			e.printStackTrace();
			log.log(Priority.ERROR, "报文解析异常：" + order.getOrderId() + " msgs:" + order.getMsgs());
			order.setStatus(ErrorCode.SUBMIT_ERROR.getCode());
			order.setResult(ErrorCode.SUBMIT_ERROR.getMsg());
		} finally{
//			log.log(Priority.INFO, "dxpc deal time:" + (System.currentTimeMillis() - begindeal));
		}
		return order;
	}
	
	
	/**
	 * 添加彩信
	 * @param order
	 * @param xxQy
	 * @return
	 */
	public Order addMmsSms(Order order, XxQy xxQy)
	{
		Map<String,String> msg = new ReadRespUtils().readJsonObject(order.getMsgs()); //解析短信内容
		//计算号码数量
		int sum = 0;
		List<String> list = new ArrayList<String>();
		String phones[] = msg.get("phone").split("\\,");
		sum += phones.length;
		/**
		 * 校验号码是否存在重复
		 */
		for(int j = 0; j < phones.length; j++)
		{
			String phone = phones[j];
			if(!Utils.checkHmq(phone))
			{
				order.setResult(ErrorCode.SUBMIT_ERROR.getMsg());
				order.setStatus(ErrorCode.SUBMIT_ERROR.getCode());
				return order;
			}
			if(!list.contains(phone))
			{
				list.add(phone);
			}else{
				order.setResult(ErrorCode.SUBMIT_ERROR.getMsg());
				order.setStatus(ErrorCode.SUBMIT_ERROR.getCode());
				return order;
			}
		}
		list.clear();
		if(sum > 100)
		{
			order.setResult(ErrorCode.SUBMIT_ERROR.getMsg());
			order.setStatus(ErrorCode.SUBMIT_ERROR.getCode());
			return order;
		}
		//生成文件名
		String fileName = DateUtils.strDate("yyyyMMddHHmmss") + RandomStringUtils.randomNumeric(3) + ".zip";
		FileOutputStream out = null;
		File file = null;
		try {
			//保存文件到临时目录
			byte b[] = CryptUtils.decrypt(CryptUtils.hex2byte(msg.get("content").getBytes()), xxQy.getJym().getBytes());
			file = new File(CommonContants.ZIP_DIR + fileName);
			out = new FileOutputStream(file);
			out.write(b);
			out.flush();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			order.setStatus(ErrorCode.SUBMIT_ERROR.getCode());
			order.setResult(ErrorCode.SUBMIT_ERROR.getMsg());
			return order;
		} finally{
			if(out != null)
			{
				try {
					out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
				}
			}
		}
		//判断文件实际大小
		long size = ZipUtils.getZipFileSize(file);
		if(size == 0)
		{
			order.setStatus(ErrorCode.ZIPFILE_NULL.getCode());
			order.setResult(ErrorCode.ZIPFILE_NULL.getMsg());
			file.delete();
		}
		if((double)size/1024 > 100)
		{
			order.setStatus(ErrorCode.ZIPFILE_TOBIG.getCode());
			order.setResult(ErrorCode.ZIPFILE_TOBIG.getMsg());
			file.delete();
		}
		//上传压缩文件到ftp
		FTPClient ftp = new FTPClient();
		try {
			ftp.connect(CommonContants.FTP_IP, CommonContants.FTP_PORT);
			if(ftp.isConnected())
			{
				if(ftp.login(CommonContants.FTP_NAME, CommonContants.FTP_PWD))
				{
					//创建目录
					ftp.makeDirectory(xxQy.getDlm());
					//上传文件
					ftp.changeWorkingDirectory(xxQy.getDlm());
					String dirName = DateUtils.strDate("yyyyMMdd");
					ftp.makeDirectory(dirName);
					ftp.changeWorkingDirectory(dirName);
					ftp.enterLocalPassiveMode();
					ftp.setFileType(ftp.BINARY_FILE_TYPE);
					ftp.setControlEncoding(ftp.DEFAULT_CONTROL_ENCODING);
					ftp.storeFile(new String(file.getName().getBytes("utf-8"),"iso-8859-1"), new FileInputStream(file));
				}
			}
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			order.setStatus(ErrorCode.SUBMIT_ERROR.getCode());
			order.setResult(ErrorCode.SUBMIT_ERROR.getMsg());
			return order;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			order.setStatus(ErrorCode.SUBMIT_ERROR.getCode());
			order.setResult(ErrorCode.SUBMIT_ERROR.getMsg());
			return order;
		} finally{
			try {
				if(ftp.isConnected())
				{
					ftp.logout();
					ftp.disconnect();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
			}
		}
		order.setStatus(ErrorCode.SUBMIT_SUCCESS.getCode());
		order.setResult(ErrorCode.SUBMIT_SUCCESS.getMsg());
		
		return order;
	}
	
}