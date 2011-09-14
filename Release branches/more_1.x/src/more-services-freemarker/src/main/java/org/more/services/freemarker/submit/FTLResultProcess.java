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
package org.more.services.freemarker.submit;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import org.more.services.submit.ActionStack;
import org.more.services.submit.ResultProcess;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
/**
 * return
 * @version : 2011-7-25
 * @author 赵永春 (zyc@byshell.org)
 */
public class FTLResultProcess implements ResultProcess<FTLResult> {
    private String           cfg_configBean = null;
    private String           cfg_suffix     = null;
    private String           cfg_rootPath   = null;
    private FreeMarkerConfig userConfig     = null;
    private Configuration    freemarkConfig = null;
    //
    public Object invoke(ActionStack onStack, FTLResult res) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, TemplateException {
        //1.创建配置对象
        if (this.freemarkConfig == null)
            this.freemarkConfig = new Configuration();
        if (this.cfg_configBean != null && this.userConfig == null) {
            Class<?> type = Thread.currentThread().getContextClassLoader().loadClass(this.cfg_configBean);
            this.userConfig = (FreeMarkerConfig) type.newInstance();
            this.freemarkConfig.setDirectoryForTemplateLoading(new File(this.cfg_rootPath));
            //this.freemarkConfig.setTemplateLoader(new TLoader()); new TemplateLoader;
            //MultiTemplateLoader loader=new MultiTemplateLoader(loaders);
            this.freemarkConfig = this.userConfig.applyConfiguration(this.freemarkConfig);
        }
        //2.配置对象
        Configuration config = res.applyConfiguration(this.freemarkConfig);
        if (config == null)
            config = this.freemarkConfig;
        String tempName = res.getTemplatePath() + "." + this.cfg_suffix;
        Template temp = this.freemarkConfig.getTemplate(tempName);//获取模板
        if (this.userConfig != null)
            temp = this.userConfig.applyTemplate(temp);
        temp = res.applyTemplate(temp);
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
            this.cfg_configBean = value;
        if (key.equals("suffix") == true)
            this.cfg_suffix = value;
        if (key.equals("rootPath") == true)
            this.cfg_rootPath = value;
    }
}