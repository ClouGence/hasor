function callBack(actionStack, results, paramArray) {
	var request = actionStack.getRequest();
	var response = actionStack.getResponse();
	if (paramArray.length == 2 && paramArray[1] == "server")
		request.getRequestDispatcher(paramArray[0]).forward(request, response);
	else
		response.sendRedirect(paramArray[0]);
	return results;
}
// 参数 1 转发地址，参数2 转发方式 server 代表服务端转发，其他客户端重定向。
