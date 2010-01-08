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
package org.more.submit.support.web;
import javax.servlet.ServletContext;
import org.more.submit.CasingBuild;
import org.more.submit.CasingDirector;
import org.more.submit.Config;
import org.more.submit.SubmitContext;
/**
 * 该类是负责创建web外壳环境的SubmitContext对象。
 * @version 2009-12-28
 * @author 赵永春 (zyc@byshell.org)
 */
public class WebCasingDirector extends CasingDirector {
    //========================================================================================Field
    private ServletContext servletContext;
    //==================================================================================Constructor
    public WebCasingDirector(Config config, ServletContext servletContext) {
        super(config);
        this.servletContext = servletContext;
    }
    //==========================================================================================Job
    @Override
    protected SubmitContext buildContext(CasingBuild build, Config config) {
        return new WebSubmitContext(build.getActionFactory(), this.servletContext);
    }
}