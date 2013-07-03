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
package org.hasor.view.freemarker.support;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import org.hasor.Assert;
import org.hasor.view.freemarker.Tag;
import org.hasor.view.freemarker.Tag2;
import org.hasor.view.freemarker.TemplateBody;
import org.more.util.BeanUtils;
import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.utility.DeepUnwrap;
/**
 * 标签对象。
 * @version : 2012-5-13
 * @author 赵永春 (zyc@byshell.org)
 */
class InternalTagObject implements TemplateDirectiveModel {
    private Tag tagBody = null;
    public InternalTagObject(Tag tagBody) {
        this.tagBody = tagBody;
        Assert.isNotNull(tagBody, "tag Object is null.");
    }
    @Override
    public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body) throws TemplateException, IOException {
        //1.反解过程
        HashMap<String, Object> objMap = new HashMap<String, Object>();
        if (params != null)
            for (Object key : params.keySet()) {
                TemplateModel item = (TemplateModel) params.get(key);
                objMap.put(key.toString(), DeepUnwrap.permissiveUnwrap(item));
            }
        //2.通知-开始执行标签
        this.tagBody.beforeTag(env);
        //3.设置属性&执行标签
        if (params != null) {
            if (this.tagBody instanceof Tag2) {
                ((Tag2) this.tagBody).setup(objMap);
            } else {
                for (Object key : params.keySet())
                    BeanUtils.writePropertyOrField(this.tagBody, (String) key, params.get(key));
            }
        }
        this.tagBody.doTag(new InternalTemplateBody(objMap, body, env));
        //4.通知-结束执行标签
        this.tagBody.afterTag(env);
    }
    private static class InternalTemplateBody implements TemplateBody {
        private Map<String, Object>   tagProperty  = null;
        private TemplateDirectiveBody templateBody = null;
        private Environment           environment  = null;
        //
        public InternalTemplateBody(Map<String, Object> tagProperty, TemplateDirectiveBody templateBody, Environment environment) {
            this.templateBody = templateBody;
            this.environment = environment;
            this.tagProperty = tagProperty;
        }
        public Environment getEnvironment() {
            return this.environment;
        }
        public void render(Writer arg0) throws TemplateException, IOException {
            if (this.templateBody != null)
                this.templateBody.render(arg0);
        }
        @Override
        public Map<String, Object> tagProperty() {
            return this.tagProperty;
        }
    }
}