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
package org.hasor.web.encoding;
import org.hasor.annotation.Module;
import org.hasor.servlet.WebApiBinder;
import org.hasor.servlet.WebHasorModule;
/**
 * 请求响应编码。启动级别：Lv0
 * @version : 2013-4-8
 * @author 赵永春 (zyc@byshell.org)
 */
@Module(displayName = "EncodingFilterListener", description = "org.hasor.web.encoding软件包功能支持。", startIndex = Module.Lv_0)
public class EncodingFilterListener extends WebHasorModule {
    @Override
    public void init(WebApiBinder apiBinder) {
        apiBinder.filter("*").through(EncodingFilter.class);
    }
}