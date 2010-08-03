<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<script type="text/javascript" src="action!sys_actionjs.config?min=${param.min}"></script>
<title>Insert title here</title>
</head>
<body>
试试看切换不同的min属性，在点击下面按钮会有什么效果。<br/>
<a href="/index.jsp?min=true">【min=true】该模式下login_1登陆会出错</a><br/>
<a href="/index.jsp?min=false">【min=false】</a>
<br/><br/>
帐号：<input id="acc" type="text" value="administrator"><br/>
密码：<input id="pwd" type="password" value="123"><br/>
<input type="button" value="login_1" onclick="login_1();">
<input type="button" value="login_2" onclick="login_2();">
<script type="text/javascript">
//使用自动生成的提交
function login_1(){
  var msg=more.server.safety.login({account:acc.value,password:pwd.value});
  alert(msg);
}
//通用递交
function login_2(){
  var msg=more.retain.callServerFunction("safety.login",{account:acc.value,password:pwd.value});
  alert(msg);
}
</script>
</body>
</html>