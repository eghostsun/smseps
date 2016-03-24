package com.slf.common.base.impl;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import com.slf.common.base.BaseDao;
import com.slf.common.base.BaseObj;
import com.slf.sms.common.CommonContants;



public class BaseDaoImpl extends SqlMapClientDaoSupport implements BaseDao
{

	public void add(BaseObj obj, String sqlMapId) throws SQLException {
		// TODO 自动生成方法存根
		try{
			this.getSqlMapClientTemplate().insert(sqlMapId, obj);
		}catch (Exception e) {
			// TODO: handle exception
			throw new SQLException(e);
		}
	}

	public void delete(BaseObj obj, String sqlMapId) throws SQLException {
		// TODO 自动生成方法存根
		this.getSqlMapClientTemplate().delete(sqlMapId, obj);
	}

	public void delete(String sqlMapId) throws SQLException {
		// TODO 自动生成方法存根
		this.getSqlMapClientTemplate().delete(sqlMapId, CommonContants.ORACLE_TABLEUSER);
	}

	public Integer getCount(BaseObj object, String sqlMapId) throws SQLException {
		// TODO 自动生成方法存根
		Integer i = (Integer)this.getSqlMapClientTemplate().queryForObject(sqlMapId, object);
		return i == null ? new Integer(0) : i;
	}
	
	public List getList(BaseObj obj, String sqlMapID) throws SQLException {
		// TODO 自动生成方法存根
		return this.getSqlMapClientTemplate().queryForList(sqlMapID, obj);
	}

	public List getList(String sqlMapID) throws SQLException {
		// TODO 自动生成方法存根
		return this.getSqlMapClientTemplate().queryForList(sqlMapID, CommonContants.ORACLE_TABLEUSER);
	}

	public BaseObj getObject(BaseObj obj, String sqlMapId) throws SQLException {
		// TODO 自动生成方法存根
		return (BaseObj) this.getSqlMapClientTemplate().queryForObject(sqlMapId, obj);
	}

	public List getTopList(BaseObj obj, String sqlMapId, int start, int end) throws SQLException {
		// TODO 自动生成方法存根
		return this.getSqlMapClientTemplate().queryForList(sqlMapId, obj, start, end);
	}

	public void modify(BaseObj obj, String sqlMapId) throws SQLException {
		// TODO 自动生成方法存根
		this.getSqlMapClientTemplate().update(sqlMapId, obj);
	}

	public Map callProcedure(Map map, String sqlMapId) throws SQLException {
		// TODO Auto-generated method stub
		return (Map) this.getSqlMapClientTemplate().queryForObject(sqlMapId, map);
	}
}
