<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ taglib prefix="hs" uri="http://project.hasor.net/hasor/schema/jstl" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
</head>
<body>

<hs:findType var="userManager" type="net.demo.hasor.manager.UserManager"/>
<c:set var="userInfo" scope="request" value="${userManager.getUserById(1234)}"/>
姓名:${userInfo.name}<br/>
年龄:${userInfo.age}<br/>

</body>
</html>