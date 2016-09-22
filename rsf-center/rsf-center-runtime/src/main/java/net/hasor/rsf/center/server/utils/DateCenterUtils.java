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
package net.hasor.rsf.center.server.utils;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.hasor.rsf.center.server.domain.ErrorCode;
import net.hasor.rsf.center.server.domain.Result;
import net.hasor.rsf.center.server.domain.ResultDO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * @version : 2015年7月6日
 * @author 赵永春(zyc@hasor.net)
 */
public class DateCenterUtils {
    protected static    Logger logger      = LoggerFactory.getLogger(DateCenterUtils.class);
    public static final String DATA_FORMAT = "yyyyMMdd-hhmmss";
    //
    /** 生成RSF-Center服务器心跳数据 */
    public static String timestamp(Date atDate) {
        return new SimpleDateFormat(DATA_FORMAT).format(atDate);
    }
    public static String timestamp() {
        return timestamp(new Date());
    }
    public static String beatData() {
        return DateCenterUtils.timestamp() + "@" + String.valueOf(System.currentTimeMillis());
    }
    /* 处理失败的情况 */
    public static <T> Result<T> buildFailedResult(Result<?> resultInfo) {
        ResultDO<T> result = new ResultDO<>();
        if (resultInfo == null || resultInfo.getResult() == null) {
            result.setErrorInfo(ErrorCode.EmptyResult);
        } else {
            result.setErrorInfo(resultInfo.getErrorInfo());
            result.setThrowable(resultInfo.getThrowable());
        }
        result.setSuccess(false);
        return result;
    }
}