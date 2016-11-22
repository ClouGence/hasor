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
package net.hasor.rsf.web;
import net.hasor.rsf.RsfApiBinder;
import net.hasor.rsf.RsfModule;
import net.hasor.rsf.container.InnerRsfApiBinder;
import net.hasor.web.WebApiBinder;
/**
 * Web支持
 * @version : 2016年2月18日
 * @author 赵永春(zyc@hasor.net)
 */
public class RsfWebModule extends RsfModule {
    @Override
    public void loadModule(RsfApiBinder apiBinder) throws Throwable {
        // .只有Web环境才启用该功能
        if (!(apiBinder instanceof InnerRsfApiBinder)) {
            return;
        }
        InnerRsfApiBinder innerBinder = (InnerRsfApiBinder) apiBinder;
        if (!(innerBinder.getSourceBinder() instanceof WebApiBinder)) {
            return;
        }
        WebApiBinder webApiBinder = (WebApiBinder) innerBinder.getSourceBinder();
        //
        logger.info("rsf framework config web.");
        //WebApiBinder webApiBinder = (WebApiBinder) apiBinder;
        //webApiBinder.serve("*.rsf").with(RsfServlet.class);
    }
}