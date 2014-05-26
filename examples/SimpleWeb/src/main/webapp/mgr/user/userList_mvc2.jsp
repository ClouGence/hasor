<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="hs" uri="http://project.hasor.net/hasor/schema/jstl" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<!-- 以下三个资源，保存在 Jar包中 -->
<script src="/jslib/jquery_v1.10.1/jquery-v1.10.0.js"></script>
<script src="/jslib/bootstrap_v2.3.2/js/bootstrap.min.js"></script>
<link href="/jslib/bootstrap_v2.3.2/css/bootstrap.min.css" rel="stylesheet">
<title>Demo</title>
</head>
<body>
<div class="panel panel-default">
  <div class="panel-heading">用户列表</div>
  <table class="table">
    <thead>
      <tr>
        <td width="290px;">ID</td>
        <td width="120px;">姓名</td>
        <td width="290px;">帐号</td>
        <td>邮箱</td>
      </tr>
    </thead>
    <tbody>
      <hs:defineBean bean="UserService" var="userService"/>
      <c:forEach var="user" items="${userService.userList}">
      <tr>
        <td>${user.userUUID}</td>
        <td>${user.name}</td>
        <td>${user.loginName}</td>
        <td>${user.email}</td>
      </tr>
      </c:forEach>
    </tbody>
  </table>
</div>
</body>
</html>