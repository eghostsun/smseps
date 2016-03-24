package com.slf.common.base;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;



public interface BaseDao {

	       /** Get list info by sqlMap
	         * @param obj
	         * @param sqlMapID
	         * @return
	         * @throws DoMyEngineException 
	         * @throws NetosException
	         */
	        public List getList(BaseObj obj,String sqlMapID) throws SQLException;
	        
	        
	        /**
	         * Get list by sqlMapId
	         * @param obj
	         * @param sqlMapId
	         * @param searchVO
	         * @return
	         * @throws NetosException
	         */
	        //public List getList(BaseObj obj,String sqlMapId, SearchVO searchVO);
	        

	        /**
	         * add by sqlMapId
	         * @param obj
	         * @param sqlMapId
	         * @return
	         * @throws DoMyEngineException 
	         * @throws NetosException
	         */
	        public void add(BaseObj obj,String sqlMapId) throws SQLException;
	        
	        /**
	         * Modify by sqlMapId
	         * @param obj
	         * @param sqlMapId
	         * @throws DoMyEngineException 
	         * @throws NetosException
	         */
	        public void modify(BaseObj obj,String sqlMapId) throws SQLException;
	        
	        /**
	         * Delete by sqlMap Id
	         * @param obj
	         * @param sqlMapId
	         * @throws DoMyEngineException 
	         * @throws NetosException
	         */
	        public void delete(BaseObj obj,String sqlMapId) throws SQLException;
	        /**
	         * Delete by sqlMapID
	         * @param parameter
	         * @param sqlMapId
	         * @throws DoMyEngineException 
	         * @throws NetosException
	         */
	        
	        public void delete(String sqlMapId) throws SQLException;
	        

	        /**
	         * Get object by sqlMap
	         * @param obj PObject
	         * @param sqlMapId ibatis sqlmap Id
	         * @return
	         * @throws DoMyEngineException 
	         * @throws NetosException
	         */
	        public BaseObj getObject(BaseObj obj,String sqlMapId) throws SQLException;
	        /**
	         * Get count by sqlMap and obj
	         * @param Id  recordId
	         * @param sqlMapID ibatis sqlmap Id
	         * @return
	         * @throws DoMyEngineException 
	         * @throws NetosException
	         */
	        public Integer getCount(BaseObj object,String sqlMapId) throws SQLException;
	        /**
	         * Get List by sqlMap
	         * @param Id  recordId
	         * @param sqlMapID ibatis sqlmap Id
	         * @return
	         * @throws DoMyEngineException 
	         * @throws NetosException
	         */
	        public List getList(String sqlMapID) throws SQLException;
	        /**
	         * 获取前N条数据集合
	         */
	        public List getTopList(BaseObj obj, String sqlMapId,int start, int end) throws SQLException;
	        
	        public Map callProcedure(Map map, String sqlMapId) throws SQLException;
}
