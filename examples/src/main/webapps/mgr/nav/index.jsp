<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" import="java.util.*"%>
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
  <div class="panel-heading">导航按钮，下面按钮会以 restful 门面模式这种方式跳转到对应的页面</div>
  <%List menuList= (List)request.getAttribute("menuList"); %>
  <ul class="nav nav-pills">
    <%for (Object menu : menuList){ %><%request.setAttribute("menu", menu); %>
    <li><a href="/mgr/menus/${menu.code}">${menu.name}</a></li>
	<%}%>
  </ul>
</div>
</body>
</html>