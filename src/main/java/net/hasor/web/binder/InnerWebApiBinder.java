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
package net.hasor.web.binder;
import net.hasor.core.ApiBinder;
import net.hasor.core.Hasor;
import net.hasor.web.MimeType;
import net.hasor.web.ServletVersion;
import net.hasor.web.WebApiBinder;
import net.hasor.web.pipeline.PipelineWebApiBinder;
/**
 * 该类是{@link WebApiBinder}接口实现。
 * @version : 2013-4-10
 * @author 赵永春 (zyc@hasor.net)
 */
class InnerWebApiBinder extends PipelineWebApiBinder implements WebApiBinder {
    private ServletVersion curVersion;
    private MimeType       mimeType;
    InnerWebApiBinder(ServletVersion curVersion, MimeType mimeType, ApiBinder apiBinder) {
        super(apiBinder);
        this.curVersion = Hasor.assertIsNotNull(curVersion);
        this.mimeType = Hasor.assertIsNotNull(mimeType);
    }
    @Override
    public String getMimeType(String suffix) {
        return this.mimeType.getMimeType(suffix);
    }
    @Override
    public ServletVersion getServletVersion() {
        return this.curVersion;
    }
}