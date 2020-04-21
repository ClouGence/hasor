/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.hasor.dataway.service;
import com.alibaba.fastjson.JSON;
import net.hasor.core.provider.SingleProvider;
import net.hasor.core.spi.SpiTrigger;
import net.hasor.dataql.DataQL;
import net.hasor.dataql.Finder;
import net.hasor.dataql.QueryResult;
import net.hasor.dataql.compiler.qil.QIL;
import net.hasor.dataql.domain.DomainHelper;
import net.hasor.dataql.domain.ObjectModel;
import net.hasor.dataql.runtime.ThrowRuntimeException;
import net.hasor.dataway.config.DatawayUtils;
import net.hasor.dataway.config.LoggerUtils;
import net.hasor.dataway.daos.ReleaseDetailQuery;
import net.hasor.dataway.spi.ApiCompilerListener;
import net.hasor.dataway.spi.ApiInfo;
import net.hasor.dataway.spi.ApiResultListener;
import net.hasor.dataway.spi.PreExecuteListener;
import net.hasor.utils.StringUtils;
import net.hasor.utils.future.BasicFuture;
import net.hasor.web.Invoker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import static net.hasor.dataway.config.DatawayModule.ISOLATION_CONTEXT;

/**
 * 服务调用。
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-03-20
 */
@Singleton
public class ApiCallService {
    protected static Logger     logger = LoggerFactory.getLogger(ApiCallService.class);
    @Inject
    private          SpiTrigger spiTrigger;
    @Inject
    @Named(ISOLATION_CONTEXT)
    private          DataQL     dataQL;
    @Inject
    private          DataQL     executeDataQL;

    public Map<String, Object> doCall(Invoker invoker) {
        long resetLocalTime = DatawayUtils.resetLocalTime();
        LoggerUtils loggerUtils = LoggerUtils.create();
        HttpServletRequest httpRequest = invoker.getHttpRequest();
        String httpMethod = httpRequest.getMethod().toUpperCase().trim();
        String requestURI = httpRequest.getRequestURI();
        //
        loggerUtils.addLog("apiMethod", httpMethod);
        loggerUtils.addLog("apiPath", requestURI);
        //
        String releaseID;
        String apiID;
        String script;
        try {
            QueryResult queryResult = new ReleaseDetailQuery(this.dataQL).execute(new HashMap<String, String>() {{
                put("apiMethod", httpMethod);
                put("apiPath", URLDecoder.decode(requestURI, "UTF-8"));
            }});
            ObjectModel dataModel = (ObjectModel) queryResult.getData();
            script = dataModel.getValue("script").asString();
            releaseID = dataModel.getValue("releaseID").asString();
            apiID = dataModel.getValue("apiID").asString();
            loggerUtils.addLog("scriptType", dataModel.getValue("scriptType").asString());
            loggerUtils.addLog("releaseID", releaseID);
            loggerUtils.addLog("apiID", apiID);
        } catch (Exception e) {
            logger.error("requestFailed - " + loggerUtils.logException(e).toJson(), e);
            return DatawayUtils.exceptionToResult(e).getResult();
        }
        //
        // .准备参数
        Map<String, Object> jsonParam;
        if ("GET".equalsIgnoreCase(httpMethod)) {
            jsonParam = new HashMap<>();
            Enumeration<String> parameterNames = httpRequest.getParameterNames();
            while (parameterNames.hasMoreElements()) {
                String paramName = parameterNames.nextElement();
                jsonParam.put(paramName + "Arrays", httpRequest.getParameterValues(paramName));
                jsonParam.put(paramName, httpRequest.getParameter(paramName));
            }
        } else {
            String jsonBody = invoker.getJsonBodyString();
            if (StringUtils.isNotBlank(jsonBody)) {
                jsonParam = JSON.parseObject(jsonBody);
            } else {
                jsonParam = new HashMap<>();
            }
        }
        if (jsonParam != null) {
            loggerUtils.addLog("paramRootKeys", jsonParam.keySet());
        }
        ApiInfo apiInfo = new ApiInfo() {{
            setStartTime(resetLocalTime);
            setApiID(apiID);
            setReleaseID(releaseID);
            setMethod(httpMethod);
            setApiPath(requestURI);
            setParameterMap(jsonParam);
        }};
        //
        try {
            // .编译查询
            final Set<String> varNames = this.executeDataQL.getShareVarMap().keySet();
            final Finder finder = this.executeDataQL.getFinder();
            QIL compiler = this.spiTrigger.chainSpi(ApiCompilerListener.class, (listener, lastResult) -> {
                return listener.compiler(script, varNames, finder);
            });
            if (compiler == null) {
                compiler = ApiCompilerListener.DEFAULT.compiler(script, varNames, finder);
            }
            loggerUtils.addLog("compilerTime", DatawayUtils.currentLostTime());
            //
            // .执行查询
            //  - 1.首先将 API 调用封装为 单例的 Supplier
            //  - 2.准备一个 Future 然后，触发 PreExecuteListener SPI。
            //  - 3.如果 Future 被设置那么获取设置的值，否则就用之前封装好的 Supplier 中取值
            QIL finalCompiler = compiler;
            Supplier<QueryResult> queryResult = SingleProvider.of(() -> {
                return this.executeDataQL.createQuery(finalCompiler).execute(jsonParam);
            });
            BasicFuture<Object> newResult = new BasicFuture<>();
            this.spiTrigger.chainSpi(PreExecuteListener.class, (listener, lastResult) -> {
                if (!newResult.isDone()) {
                    listener.preExecute(apiInfo, newResult);
                }
                return lastResult;
            });
            QueryResult execute = null;
            if (newResult.isDone()) {
                // 使用preExecute的结果
                Object data = newResult.get();
                if (data instanceof QueryResult) {
                    execute = (QueryResult) data;
                } else {
                    execute = QueryResultInfo.of(           //
                            0,                    // 状态码
                            DomainHelper.convertTo(data),   // 结果
                            DatawayUtils.currentLostTime()  // 耗时
                    );
                }
            } else {
                execute = queryResult.get(); // 发起真正的调用
            }
            loggerUtils.addLog("executionTime", execute.executionTime());
            loggerUtils.addLog("lifeCycleTime", DatawayUtils.currentLostTime());
            loggerUtils.addLog("code", execute.getCode());
            logger.info("requestSuccess - " + loggerUtils.toJson());
            //
            // .callAfter
            Object resultData = this.spiTrigger.chainSpi(ApiResultListener.class, (listener, lastResult) -> {
                return listener.callAfter(apiInfo, lastResult);
            }, execute.getData().unwrap());
            //
            // .返回值
            return DatawayUtils.queryResultToResultWithSpecialValue(execute, resultData).getResult();
        } catch (Exception e) {
            Object value = null;
            if (e instanceof ThrowRuntimeException) {
                value = ((ThrowRuntimeException) e).getResult().unwrap();
            } else {
                value = e.getMessage();
            }
            logger.error("requestFailed - " + loggerUtils.logException(e).toJson(), e);
            value = this.spiTrigger.chainSpi(ApiResultListener.class, (listener, lastResult) -> {
                return listener.callError(apiInfo, e);
            }, value);
            return DatawayUtils.exceptionToResultWithSpecialValue(e, value).getResult();
        }
    }
}