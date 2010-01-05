<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib uri="/WEB-INF/more-lib.tld" prefix="m" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title></title>
</head>
<body>
<m:ActionTag process="action.hello" result="returnMsg" scope="request">
	<m:ActionParamTag key="msg" value="HelloWord 页面预处理！"/>
</m:ActionTag>
<b>returnMsg:${returnMsg}</b><br/>
<form method="post">
	帐号：<input name="acc" type="text" value="administrator"><br/>
	密码：<input name="pwd" type="text" value="123"><br/>
	<input type="submit" value="递交">
</form>
<br/>
试着输入相同和不同的帐号和密码点击登陆看看。该网页的表单将递交给当前页面由页面预处理负责处理登陆认证。
</body>
</html>