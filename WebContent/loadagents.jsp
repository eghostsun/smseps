<%@page import="com.slf.tao80.common.CommonContants"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<% if(CommonContants.LOCK_IP.indexOf(request.getRemoteAddr()) == -1)
{
	response.sendRedirect("");
}
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>企业级代理商加载</title>
<script type="text/javascript">
<% if(request.getAttribute("msg") != null){%>
window.onload=function()
{
	alert('<%=request.getAttribute("msg") %>');
}
<%}%>

function loadagents()
{
	if(window.confirm("确定要加载企业代理商吗？"))
	{
		window.location.href="<%=request.getContextPath()%>/loadagents.do";
	}
}
</script>
</head>
<body>
<input type="button" value="加载企业代理" onclick="loadagents();"/>
</body>
</html>