<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" import="java.util.*"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<!-- 以下三个资源，保存在 Jar包中 -->
<title>Demo</title>
</head>
<body>
<%List userList= (List)request.getAttribute("userList"); %>
<%for (Object user : userList){ %>
<%request.setAttribute("user", user); %>
	<b>UUID</b>：${user.userUUID}，<b>loginName</b>：${user.loginName}<br>
<%}%>
</body>
</html>