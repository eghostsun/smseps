package com.slf.common.datasource;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.slf.common.util.CryptUtils;


public class MyDataSource extends BasicDataSource {

	@Override
	public synchronized int getInitialSize() {
		// TODO Auto-generated method stub
		return super.getInitialSize();
	}

	@Override
	public synchronized int getMaxActive() {
		// TODO Auto-generated method stub
		return super.getMaxActive();
	}

	@Override
	public synchronized int getMaxIdle() {
		// TODO Auto-generated method stub
		return super.getMaxIdle();
	}

	@Override
	public synchronized long getMaxWait() {
		// TODO Auto-generated method stub
		return super.getMaxWait();
	}

	@Override
	public synchronized int getMinIdle() {
		// TODO Auto-generated method stub
		return super.getMinIdle();
	}

	@Override
	public synchronized void setInitialSize(int initialSize) {
		// TODO Auto-generated method stub
		super.setInitialSize(initialSize);
	}

	@Override
	public synchronized void setMaxActive(int maxActive) {
		// TODO Auto-generated method stub
		super.setMaxActive(maxActive);
	}

	@Override
	public synchronized void setMaxIdle(int maxIdle) {
		// TODO Auto-generated method stub
		super.setMaxIdle(maxIdle);
	}

	@Override
	public synchronized void setMaxWait(long maxWait) {
		// TODO Auto-generated method stub
		super.setMaxWait(maxWait);
	}

	@Override
	public synchronized void setMinIdle(int minIdle) {
		// TODO Auto-generated method stub
		super.setMinIdle(minIdle);
	}

	@Override
	public String getDriverClassName() {
		// TODO Auto-generated method stub
		return super.getDriverClassName();
	}

	@Override
	public String getUrl() {
		// TODO Auto-generated method stub
		return super.getUrl();
	}

	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return super.getUsername();
	}

	@Override
	public void setDriverClassName(String arg0) throws CannotGetJdbcConnectionException {
		// TODO Auto-generated method stub
		super.setDriverClassName(arg0);
	}


	@Override
	public synchronized String getPassword() {
		// TODO Auto-generated method stub
		return super.getPassword();
	}

	@Override
	public synchronized void setPassword(String password) {
		// TODO Auto-generated method stub
		if(password.startsWith("{3DES}")){
			super.setPassword(CryptUtils.decrypt(password.replace("{3DES}", ""),CryptUtils.PASSWORD_CRYPT_KEY));
		} else {
			super.setPassword(password);		
		}
	}

	@Override
	public void setUrl(String arg0) {
		// TODO Auto-generated method stub
		super.setUrl(arg0);
	}

	@Override
	public void setUsername(String arg0) {
		// TODO Auto-generated method stub
		super.setUsername(arg0);
	}

}
