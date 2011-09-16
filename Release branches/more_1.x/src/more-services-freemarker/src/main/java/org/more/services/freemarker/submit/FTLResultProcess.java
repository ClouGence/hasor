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
import java.io.IOException;
import java.io.Writer;
import org.more.services.freemarker.FreemarkerService;
import org.more.services.submit.ActionStack;
import org.more.services.submit.ResultProcess;
import org.more.util.attribute.SequenceStack;
import freemarker.template.Template;
import freemarker.template.TemplateException;
/**
 * return
 * @version : 2011-7-25
 * @author 赵永春 (zyc@byshell.org)
 */
public class FTLResultProcess implements ResultProcess<FTLResult> {
    public Object invoke(ActionStack onStack, FTLResult res) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, TemplateException {
        //1.准备环境
        Writer writer = res.getWriter();//返回空无所谓
        FreemarkerService service = onStack.getSubmitService().getContext().getService(FreemarkerService.class);
        Template temp = service.getProcess().getTemplate(res);
        temp = res.applyTemplate(temp);
        //2.准备参数堆栈
        SequenceStack<Object> stack = new SequenceStack<Object>();
        stack.putStack(res);//第一搜索顺序
        stack.putStack(service);//第二搜索顺序
        //3.执行模板
        temp.process(stack.toMap(), writer);
        return res.getReturnValue();
    }
    public void addParam(String key, String value) {}
}