<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@taglib prefix="more" uri="/WEB-INF/more-lib.tld" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title></title>
</head>
<body>
page:${pageScope.var}<br/>
request:${requestScope.var}<br/>
session:${sessionScope.var}<br/>
context:${applicationScope.var}<br/>
cookie:${cookie.var.value}<br/>
</body>
</html>