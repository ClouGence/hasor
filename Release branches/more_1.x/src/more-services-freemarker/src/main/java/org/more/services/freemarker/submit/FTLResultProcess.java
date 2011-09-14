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
import org.more.services.freemarker.assembler.NoneWriter;
import org.more.services.submit.ActionStack;
import org.more.services.submit.ResultProcess;
import freemarker.template.TemplateException;
/**
 * return
 * @version : 2011-7-25
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
public class FTLResultProcess implements ResultProcess<FTLResult> {
    public Object invoke(ActionStack onStack, FTLResult res) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, TemplateException {
        //3.÷¥––ƒ£∞Â
        Writer writer = res.getWriter();
        if (writer == null)
            writer = new NoneWriter();
        FreemarkerService service = onStack.getSubmitService().getContext().getService(FreemarkerService.class);
        service.process(res, res.toMap(), writer);
        return res.getReturnValue();
    }
    public void addParam(String key, String value) {
        //        if (value == null || value.equals("") == true)
        //            return;
        //        if (key.equals("configBean") == true)
        //            this.cfg_configBean = value;
        //        if (key.equals("suffix") == true)
        //            this.cfg_suffix = value;
        //        if (key.equals("rootPath") == true)
        //            this.cfg_rootPath = value;
    }
}