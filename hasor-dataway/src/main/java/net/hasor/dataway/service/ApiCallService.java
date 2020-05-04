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
import net.hasor.dataql.Finder;
import net.hasor.dataql.QueryResult;
import net.hasor.dataql.compiler.qil.QIL;
import net.hasor.dataql.domain.DataModel;
import net.hasor.dataql.domain.DomainHelper;
import net.hasor.dataql.runtime.ThrowRuntimeException;
import net.hasor.dataway.config.DatawayUtils;
import net.hasor.dataway.config.LoggerUtils;
import net.hasor.dataway.spi.ApiInfo;
import net.hasor.dataway.spi.CompilerSpiListener;
import net.hasor.dataway.spi.PreExecuteChainSpi;
import net.hasor.dataway.spi.ResultProcessChainSpi;
import net.hasor.utils.future.BasicFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;
import java.util.Set;
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
        if (apiInfo.getParameterMap() != null) {
            loggerUtils.addLog("paramRootKeys", apiInfo.getParameterMap().keySet());
        } else {
            loggerUtils.addLog("paramRootKeys", "empty.");
        }
        //
        // .执行查询
        //  - 1.首先将 API 调用封装为 单例的 Supplier
        //  - 2.准备一个 Future 然后，触发 PreExecuteListener SPI
        //  - 3.如果 Future 被设置那么获取设置的值，否则就用之前封装好的 Supplier 中取值
        BasicFuture<Object> newResult = new BasicFuture<>();
        QueryResult execute = null;
        try {
            this.spiTrigger.chainSpi(PreExecuteChainSpi.class, (listener, lastResult) -> {
                if (!newResult.isDone()) {
                    listener.preExecute(apiInfo, newResult);
                }
                return lastResult;
            });
            if (newResult.isDone()) {
                // - 使用preExecute的结果
                //
                Object data = newResult.get();
                if (data instanceof QueryResult) {
                    execute = (QueryResult) data;
                } else {
                    execute = QueryResultInfo.of(//
                            0,                   // 状态码
                            DomainHelper.convertTo(data),   // 结果
                            DatawayUtils.currentLostTime()  // 耗时
                    );
                }
            } else {
                // .编译DataQL查询，并执行查询
                //
                final String scriptBody = scriptBuild.buildScript(apiInfo.getParameterMap());
                final Set<String> varNames = this.executeDataQL.getShareVarMap().keySet();
                final Finder finder = this.executeDataQL.getFinder();
                QIL compiler = this.spiTrigger.notifySpi(CompilerSpiListener.class, (listener, lastResult) -> {
                    return listener.compiler(apiInfo, scriptBody, varNames, finder);
                }, null);
                if (compiler == null) {
                    compiler = CompilerSpiListener.DEFAULT.compiler(apiInfo, scriptBody, varNames, finder);
                }
                loggerUtils.addLog("compilerTime", DatawayUtils.currentLostTime());
                execute = this.executeDataQL.createQuery(compiler).execute(apiInfo.getParameterMap());
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
            resultData = this.spiTrigger.chainSpi(ResultProcessChainSpi.class, (listener, lastResult) -> {
                if (lastResult instanceof DataModel) {
                    lastResult = ((DataModel) lastResult).unwrap();
                }
                return listener.callAfter(newResult.isDone(), apiInfo, lastResult);
            }, resultData);
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
        try {
            Throwable finalE = e;
            value = this.spiTrigger.chainSpi(ResultProcessChainSpi.class, (listener, lastResult) -> {
                return listener.callError(isFormPre, apiInfo, finalE);
            }, value);
        } catch (Throwable ee) {
            logger.error(ee.getMessage(), ee);
            e = ee;
        }
        //
        if (needThrow) {
            throw e;
        } else {
            return DatawayUtils.exceptionToResultWithSpecialValue(apiInfo.getOptionMap(), e, value).getResult();
        }
    }
}