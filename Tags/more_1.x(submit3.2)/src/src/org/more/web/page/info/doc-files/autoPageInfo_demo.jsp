<%@ page contentType="text/html; charset=utf-8" language="java"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="more" uri="/WEB-INF/more-page-lib.tld"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>My JSP 'index.jsp' starting page</title>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8"><style type="text/css">
<!--
.font_0:link, .font_0:visited {color: #660099;}
.font_1:link, .font_1:visited {color: #0000CC;}
.font_2:link, .font_2:visited {color: #00CCFF;}
.font_3:link, .font_3:visited {color: #339900;}
.font_4:link, .font_4:visited {color: #FFFF00;}
.font_5:link, .font_5:visited {color: #FF6600;}
.font_6:link, .font_6:visited {color: #FF0000;}
.font_default:link, .font_default:visited {color: #999999;}
-->
</style></head>
  <body style="font-size:14px; font-weight:600;">
    <!-- ============================================================================================================ -->
     测试1：
  	<jsp:useBean id="pageinfo" class="org.more.web.page.info.AutoPageInfo"/>
  	<%pageinfo.setCount(15); %>
  	<%pageinfo.setRequest(request); %>
    <more:page info="${pageinfo}" expressionCurrent="number=${param.autoPage_current}">
    	<more:first occupyFirst="true">[<a href="${page_item.url}">第一条</a>]</more:first>
    	<more:current><font color="#000000">[${page_item.number}]</font></more:current>
        <more:next first="0" last="0">[<a href="${page_item.url}" class="font_0">${page_item.number}</a>]</more:next>
        <more:next first="1" last="1">[<a href="${page_item.url}" class="font_1">${page_item.number}</a>]</more:next>
        <more:next first="2" last="2">[<a href="${page_item.url}" class="font_2">${page_item.number}</a>]</more:next>
        <more:next first="3" last="3">[<a href="${page_item.url}" class="font_3">${page_item.number}</a>]</more:next>
        <more:next first="4" last="4">[<a href="${page_item.url}" class="font_4">${page_item.number}</a>]</more:next>
        <more:next first="5" last="5">[<a href="${page_item.url}" class="font_5">${page_item.number}</a>]</more:next>
    	<more:next first="6" last="6">[<a href="${page_item.url}" class="font_6">${page_item.number}</a>]</more:next>
        <more:next>[<a href="${page_item.url}" class="font_default">${page_item.number}</a>]</more:next>
    	<more:last occupyLast="true">[<a href="${page_item.url}">后一条</a>]</more:last>
    </more:page>
    <!-- ============================================================================================================ -->
    <br/><br/>
     测试2：
  	<jsp:useBean id="testPage_1" class="org.more.web.page.info.AutoPageInfo"/>
  	<%testPage_1.setCount(15); %>
  	<%testPage_1.setRequest(request); %>
    <more:page info="${testPage_1}" expressionCurrent="number=${param.autoPage_current}">
    	<more:first>[<a href="${page_item.url}">第一条</a>]</more:first>
    	<more:previous>[<a href="${page_item.url}">上一页</a>]</more:previous>
    	<more:current><font color="#000000">[${page_item.number}]</font></more:current>
        <more:next>[<a href="${page_item.url}" class="font_default">${page_item.number}</a>]</more:next>
        <more:down>[<a href="${page_item.url}">下一页</a>]</more:down>
    	<more:last>[<a href="${page_item.url}">后一条</a>]</more:last>
    </more:page>
    <!-- ============================================================================================================ -->
    <br/><br/>
     测试3：
  	<jsp:useBean id="testPage_2" class="org.more.web.page.info.AutoPageInfo"/>
  	<%testPage_2.setCount(15); %>
  	<%testPage_2.setRequest(request); %>
    <more:page info="${testPage_2}" expressionCurrent="number=${param.autoPage_current}">
    	<more:first>[<a href="${page_item.url}">第一条</a>]</more:first>
    	<more:current><font color="#000000">[${page_item.number}]</font></more:current>
        <more:next first="0">[<a href="${page_item.url}" class="font_0">${page_item.number}</a>]</more:next>
        <more:next first="1">[<a href="${page_item.url}" class="font_1">${page_item.number}</a>]</more:next>
        <more:next first="2">[<a href="${page_item.url}" class="font_2">${page_item.number}</a>]</more:next>
        <more:next first="3">[<a href="${page_item.url}" class="font_3">${page_item.number}</a>]</more:next>
        <more:next first="4">[<a href="${page_item.url}" class="font_4">${page_item.number}</a>]</more:next>
        <more:next first="5">[<a href="${page_item.url}" class="font_5">${page_item.number}</a>]</more:next>
    	
    	<more:next last="1">[<a href="${page_item.url}" class="font_2">${page_item.number}</a>]</more:next>
    	<more:next last="0">[<a href="${page_item.url}" class="font_1">${page_item.number}</a>]</more:next>
    	<more:last>[<a href="${page_item.url}">后一条</a>]</more:last>
    </more:page>
  </body>
</html>