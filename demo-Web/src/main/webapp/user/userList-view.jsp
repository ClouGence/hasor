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
        <td>操作</td>
      </tr>
    </thead>
    <tbody> 
      <c:if test="${pageData.success eq false}">
      <tr>
        <td colspan="4">Error:${pageData.message}</td>
      </tr>
      </c:if>
      <c:if test="${pageData.success eq true}">
      <c:forEach var="user" items="${pageData.result}">
      <tr>
        <td>${user.userUUID}</td>
        <td>${user.name}</td>
        <td>${user.loginName}</td>
        <td>${user.email}</td>
        <td>[修改]&nbsp;&nbsp;[删除]</td>
      </tr>
      </c:forEach>
      </c:if>
    </tbody>
  </table>
</div>
</body>
</html>