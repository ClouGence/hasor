package net.hasor.dataway.config;
import com.alibaba.fastjson.JSON;
import net.hasor.dataql.DataQL;
import net.hasor.dataql.QueryResult;
import net.hasor.dataway.daos.ApiQuery;
import net.hasor.web.Invoker;
import net.hasor.web.InvokerChain;
import net.hasor.web.InvokerConfig;
import net.hasor.web.InvokerFilter;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

class InterfaceApiFilter implements InvokerFilter {
    @Inject
    private DataQL   dataQL;
    @Inject
    private ApiQuery apiQuery;
    private String   apiBaseUri;

    public InterfaceApiFilter(String apiBaseUri) {
        this.apiBaseUri = apiBaseUri;
    }

    @Override
    public void init(InvokerConfig config) {
        config.getAppContext().justInject(this);
    }

    @Override
    public Object doInvoke(Invoker invoker, InvokerChain chain) throws Throwable {
        HttpServletRequest httpRequest = invoker.getHttpRequest();
        HttpServletResponse httpResponse = invoker.getHttpResponse();
        String requestURI = httpRequest.getRequestURI();
        if (!requestURI.startsWith(this.apiBaseUri)) {
            return chain.doNext(invoker);
        }
        //
        httpRequest.setCharacterEncoding("UTF-8");
        httpResponse.setCharacterEncoding("UTF-8");
        String requestUrl = invoker.getRequestPath();
        String queryApi = apiQuery.queryApi(requestUrl);
        QueryResult execute = dataQL.createQuery(queryApi).execute();
        //
        httpResponse.getWriter().write(JSON.toJSONString(execute.getData().unwrap()));
        //
        return null;
    }
}