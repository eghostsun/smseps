package com.slf.common.client;

import java.util.Map;

public interface IHttpSqsService {

	public boolean putIntoSqs(String name,Map<String, String> obj);
	
	public Map<String, String> getFromSqs(String name);
	
	public boolean resetSqs(String name);
}
