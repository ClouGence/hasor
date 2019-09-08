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
package net.hasor.web;
import net.hasor.core.BindInfo;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * 控制器映射信息
 * @version : 2016-12-26
 * @author 赵永春 (zyc@hasor.net)
 */
public interface Mapping {
    /**
     * 获取目标类型
     */
    public BindInfo<?> getTargetType();

    /** 获取映射的地址 */
    public String getMappingTo();

    /** 获取映射的地址的正则表达式形式 */
    public String getMappingToMatches();

    /**
     * 首先测试路径是否匹配，然后判断Restful实例是否支持这个 请求方法。
     * @return 返回测试结果。
     */
    public boolean matchingMapping(HttpServletRequest request);

    /** 获取方法 */
    public String[] getHttpMethodSet();

    /**
     * 获取调用目标的方法
     */
    public default Method findMethod(HttpServletRequest request) {
        return findMethod(request.getMethod().trim().toUpperCase());
    }

    public Method findMethod(String requestMethod);

    public String getSpecialContentType(String requestMethod);

    public boolean isAsync(HttpServletRequest request);
}