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
import net.hasor.core.spi.SpiTrigger;
import net.hasor.dataql.DataQL;
import net.hasor.dataql.Query;
import net.hasor.dataql.QueryResult;
import net.hasor.dataql.compiler.qil.QIL;
import net.hasor.dataql.domain.DataModel;
import net.hasor.dataql.domain.DomainHelper;
import net.hasor.dataql.runtime.ThrowRuntimeException;
import net.hasor.dataql.runtime.mem.ExitType;
import net.hasor.dataway.authorization.PermissionType;
import net.hasor.dataway.config.DatawayUtils;
import net.hasor.dataway.config.LoggerUtils;
import net.hasor.dataway.spi.*;
import net.hasor.utils.StringUtils;
import net.hasor.utils.future.BasicFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

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
    private          DataQL     executeDataQL;

    public Object doCallWithoutError(ApiInfo apiInfo, QueryScriptBuild scriptBuild) throws Throwable {
        return this._doCall(apiInfo, scriptBuild, false);
    }

    public Object doCall(ApiInfo apiInfo, QueryScriptBuild scriptBuild) throws Throwable {
        return this._doCall(apiInfo, scriptBuild, true);
    }

    private Object _doCall(ApiInfo apiInfo, QueryScriptBuild scriptBuild, boolean needThrow) throws Throwable {
        LoggerUtils loggerUtils = LoggerUtils.create();
        loggerUtils.addLog("apiMethod", apiInfo.getMethod());
        loggerUtils.addLog("apiPath", apiInfo.getApiPath());
        Map<String, Object> parameterMap = apiInfo.getParameterMap();
        if (parameterMap != null) {
            loggerUtils.addLog("paramRootKeys", parameterMap.keySet());
        } else {
            loggerUtils.addLog("paramRootKeys", "empty.");
        }
        // .执行查询
        //  - 0.权限检查
        //  - 1.首先将 API 调用封装为 单例的 Supplier
        //  - 2.准备一个 Future 然后，触发 PreExecuteListener SPI
        //  - 3.如果 Future 被设置那么获取设置的值，否则就用之前封装好的 Supplier 中取值
        BasicFuture<Object> newResult = new BasicFuture<>();
        QueryResult execute = null;
        try {
            // .执行权限检查SPI
            if (this.spiTrigger.hasSpi(AuthorizationChainSpi.class)) {
                Boolean checkResult = spiTrigger.chainSpi(AuthorizationChainSpi.class, (listener, lastResult) -> {
                    return listener.doCheck(PermissionType.ApiExecute, apiInfo, lastResult);
                }, true);
                if (checkResult == null || !checkResult) {
                    throw new StatusMessageException(401, "no permission of api " + apiInfo.getApiPath());
                }
            }
            // .前置拦截器
            if (this.spiTrigger.hasSpi(PreExecuteChainSpi.class)) {
                this.spiTrigger.chainSpi(PreExecuteChainSpi.class, (listener, lastResult) -> {
                    if (!newResult.isDone()) {
                        listener.preExecute(apiInfo, newResult);
                    }
                    return lastResult;
                });
            }
            //
            if (newResult.isDone()) {
                // - 使用preExecute的结果
                Object data = newResult.get();
                if (data instanceof QueryResult) {
                    execute = (QueryResult) data;
                } else {
                    execute = QueryResultInfo.of(//
                            ExitType.Return,                //
                            0,                     // 状态码
                            DomainHelper.convertTo(data),   // 结果
                            DatawayUtils.currentLostTime()  // 耗时
                    );
                }
            } else {
                // .全参数包裹,这个包裹参数不加入到 SCHEMA 和 SAMPLE 中
                String wrapParameterName = DatawayUtils.wrapParameterName(apiInfo.getOptionMap());
                if (StringUtils.isNotBlank(wrapParameterName)) {
                    Map<String, Object> tmpParameterMap = new HashMap<>();
                    tmpParameterMap.put(wrapParameterName, parameterMap);
                    parameterMap = tmpParameterMap;
                }
                // .编译DataQL查询，并执行查询
                final String scriptBody = scriptBuild.buildScript(parameterMap);
                QIL compiler = this.spiTrigger.notifySpi(CompilerSpiListener.class, (listener, lastResult) -> {
                    return listener.compiler(apiInfo, scriptBody, this.executeDataQL);
                }, null);
                if (compiler == null) {
                    compiler = CompilerSpiListener.DEFAULT.compiler(apiInfo, scriptBody, this.executeDataQL);
                }
                //
                loggerUtils.addLog("compilerTime", DatawayUtils.currentLostTime());
                loggerUtils.addLog("prepareHint", apiInfo.getPrepareHint());
                Query query = this.executeDataQL.createQuery(compiler);
                if (apiInfo.getPrepareHint() != null) {
                    apiInfo.getPrepareHint().forEach((hint, value) -> {
                        if (value == null) {
                            query.setHint(hint, (String) null);
                        } else if (value instanceof Boolean) {
                            query.setHint(hint, (Boolean) value);
                        } else if (value instanceof Number) {
                            query.setHint(hint, (Number) value);
                        } else {
                            query.setHint(hint, value.toString());
                        }
                    });
                }
                execute = query.execute(parameterMap);
            }
            loggerUtils.addLog("executionTime", execute.executionTime());
            loggerUtils.addLog("lifeCycleTime", DatawayUtils.currentLostTime());
            loggerUtils.addLog("code", execute.getCode());
            logger.info("requestSuccess - " + loggerUtils.toJson());
        } catch (Throwable e) {
            logger.error("requestFailed - " + loggerUtils.logException(e).toJson());
            return doError(needThrow, newResult.isDone(), e, apiInfo, loggerUtils);
        }
        //
        // .返回值
        try {
            Object resultData = execute.getData();
            if (this.spiTrigger.hasSpi(ResultProcessChainSpi.class)) {
                resultData = this.spiTrigger.chainSpi(ResultProcessChainSpi.class, (listener, lastResult) -> {
                    if (lastResult instanceof DataModel) {
                        lastResult = ((DataModel) lastResult).unwrap();
                    }
                    return listener.callAfter(newResult.isDone(), apiInfo, lastResult);
                }, resultData);
            }
            return DatawayUtils.queryResultToResultWithSpecialValue(apiInfo.getOptionMap(), execute, resultData).getResult();
        } catch (Throwable e) {
            logger.error("requestFailed - " + loggerUtils.logException(e).toJson());
            return doError(needThrow, newResult.isDone(), e, apiInfo, loggerUtils);
        }
    }

    private Object doError(boolean needThrow, boolean isFormPre, Throwable e, ApiInfo apiInfo, LoggerUtils loggerUtils) throws Throwable {
        Object value = null;
        if (e instanceof ExecutionException) {
            e = e.getCause();
        }
        if (e instanceof ThrowRuntimeException) {
            value = ((ThrowRuntimeException) e).getResult().unwrap();
        } else {
            value = e.getMessage();
        }
        logger.error("requestFailed - " + loggerUtils.logException(e).toJson());
        //
        // .如果注册了 SPI 那么就执行 callError
        if (this.spiTrigger.hasSpi(ResultProcessChainSpi.class)) {
            try {
                Throwable finalE = e;
                value = this.spiTrigger.chainSpi(ResultProcessChainSpi.class, (listener, lastResult) -> {
                    return listener.callError(isFormPre, apiInfo, finalE);
                }, value);
            } catch (Throwable ee) {
                logger.error(ee.getMessage(), ee);
                e = ee;
            }
        } else {
            logger.error(e.getMessage(), e);
        }
        //
        if (needThrow) {
            throw e;
        } else {
            return DatawayUtils.exceptionToResultWithSpecialValue(apiInfo.getOptionMap(), e, value).getResult();
        }
    }
}
