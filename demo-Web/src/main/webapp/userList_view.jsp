<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<!-- 以下三个资源，保存在 Jar包中 -->
<title>Demo</title>
</head>
<body>
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
    <c:forEach var="user" items="${userList}">
    <tr>
      <td>${user.userUUID}</td>
      <td>${user.name}</td>
      <td>${user.loginName}</td>
      <td>${user.email}</td>
    </tr>
    </c:forEach>
  </tbody>
</table>
</body>
</html>