package com.slf.sms.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.slf.sms.bo.XxQy;


public class SysContext {
	
	public static Map<String,XxQy> AGENTS_MAP = Collections.synchronizedMap(new HashMap<String,XxQy>());
	
}
