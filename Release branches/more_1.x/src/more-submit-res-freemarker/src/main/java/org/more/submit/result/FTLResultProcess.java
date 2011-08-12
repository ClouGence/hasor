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
package org.more.submit.result;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import org.more.submit.ResultProcess;
import org.more.submit.impl.DefaultActionStack;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
/**
 * return
 * @version : 2011-7-25
 * @author 赵永春 (zyc@byshell.org)
 */
public class FTLResultProcess implements ResultProcess<FTLResult> {
    private String           configBean = null;
    private String           suffix     = null;
    private String           rootPath   = null;
    private FreeMarkerConfig config     = null;
    //
    public Object invoke(DefaultActionStack onStack, FTLResult res) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, TemplateException {
        Configuration freemarkConfig = new Configuration();
        //1.创建配置对象
        if (this.configBean != null) {
            if (this.config == null) {
                Class<?> type = Thread.currentThread().getContextClassLoader().loadClass(configBean);
                this.config = (FreeMarkerConfig) type.newInstance();
            }
            this.config.applyConfiguration(freemarkConfig);
        }
        res.applyConfiguration(freemarkConfig);
        //2.创建配置对象
        freemarkConfig.setDirectoryForTemplateLoading(new File(this.rootPath));
        String tempName = res.getTemplatePath() + "." + suffix;
        Template temp = freemarkConfig.getTemplate(tempName);
        if (config != null)
            this.config.applyTemplate(temp);
        res.applyTemplate(temp);
        //3.执行模板
        Writer writer = res.getWriter();
        if (writer == null)
            writer = new NoneWriter();
        temp.process(res.toMap(), writer);
        return res.getReturnValue();
    }
    public void addParam(String key, String value) {
        if (value == null || value.equals("") == true)
            return;
        if (key.equals("configBean") == true)
            this.configBean = value;
        if (key.equals("suffix") == true)
            this.suffix = value;
        if (key.equals("rootPath") == true)
            this.rootPath = value;
    }
}