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
import net.hasor.core.provider.SingleProvider;
import net.hasor.core.spi.SpiTrigger;
import net.hasor.dataql.DataQL;
import net.hasor.dataql.Finder;
import net.hasor.dataql.QueryResult;
import net.hasor.dataql.compiler.qil.QIL;
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
import java.util.function.Supplier;

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

    public Map<String, Object> doCall(ApiInfo apiInfo, String script, Map<String, Object> jsonParam) {
        LoggerUtils loggerUtils = LoggerUtils.create();
        loggerUtils.addLog("apiMethod", apiInfo.getMethod());
        loggerUtils.addLog("apiPath", apiInfo.getApiPath());
        //
        // .准备参数
        if (jsonParam != null) {
            loggerUtils.addLog("paramRootKeys", jsonParam.keySet());
        }
        //
        // .编译DataQL查询
        QIL compiler = null;
        try {
            final Set<String> varNames = this.executeDataQL.getShareVarMap().keySet();
            final Finder finder = this.executeDataQL.getFinder();
            compiler = this.spiTrigger.notifySpi(CompilerSpiListener.class, (listener, lastResult) -> {
                return listener.compiler(apiInfo, script, varNames, finder);
            }, null);
            if (compiler == null) {
                compiler = CompilerSpiListener.DEFAULT.compiler(apiInfo, script, varNames, finder);
            }
            loggerUtils.addLog("compilerTime", DatawayUtils.currentLostTime());
        } catch (Exception e) {
            return doError(e, apiInfo, loggerUtils);
        }
        //
        // .执行查询
        //  - 1.首先将 API 调用封装为 单例的 Supplier
        //  - 2.准备一个 Future 然后，触发 PreExecuteListener SPI。
        //  - 3.如果 Future 被设置那么获取设置的值，否则就用之前封装好的 Supplier 中取值
        BasicFuture<Object> newResult = new BasicFuture<>();
        QueryResult execute = null;
        try {
            QIL finalCompiler = compiler;
            Supplier<QueryResult> queryResult = SingleProvider.of(() -> {
                return this.executeDataQL.createQuery(finalCompiler).execute(jsonParam);
            });
            this.spiTrigger.chainSpi(PreExecuteChainSpi.class, (listener, lastResult) -> {
                if (!newResult.isDone()) {
                    listener.preExecute(apiInfo, newResult);
                }
                return lastResult;
            });
            if (newResult.isDone()) {
                Object data = newResult.get();
                if (data instanceof QueryResult) {
                    execute = (QueryResult) data;           // 使用preExecute的结果
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
        } catch (Exception e) {
            return doError(e, apiInfo, loggerUtils);
        }
        //
        // .返回值
        Object resultData = execute.getData();
        resultData = this.spiTrigger.chainSpi(ResultProcessChainSpi.class, (listener, lastResult) -> {
            return listener.callAfter(apiInfo, newResult.isDone(), lastResult);
        }, resultData);
        return DatawayUtils.queryResultToResultWithSpecialValue(execute, resultData).getResult();
    }

    private Map<String, Object> doError(Exception e, ApiInfo apiInfo, LoggerUtils loggerUtils) {
        Object value = null;
        if (e instanceof ThrowRuntimeException) {
            value = ((ThrowRuntimeException) e).getResult().unwrap();
        } else {
            value = e.getMessage();
        }
        logger.error("requestFailed - " + loggerUtils.logException(e).toJson(), e);
        value = this.spiTrigger.chainSpi(ResultProcessChainSpi.class, (listener, lastResult) -> {
            return listener.callError(apiInfo, e);
        }, value);
        return DatawayUtils.exceptionToResultWithSpecialValue(e, value).getResult();
    }
}