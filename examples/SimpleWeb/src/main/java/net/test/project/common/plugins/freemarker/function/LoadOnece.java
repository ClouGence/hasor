/*
 * Copyright 2008-2009 the original ’‘”¿¥∫(zyc@hasor.net).
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
package net.test.project.common.plugins.freemarker.function;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import net.test.project.common.plugins.freemarker.FmTag;
import net.test.project.common.plugins.freemarker.Tag;
import net.test.project.common.plugins.freemarker.TemplateBody;
import org.more.util.CommonCodeUtils.MD5;
import freemarker.core.Environment;
import freemarker.template.TemplateException;
/**
 * 
 * @version : 2013-9-24
 * @author ’‘”¿¥∫(zyc@hasor.net)
 */
@FmTag("LoadOnece")
public class LoadOnece implements Tag {
    public boolean beforeTag(Environment environment) throws TemplateException {
        return true;
    }
    //
    private String getMark(String dataString) {
        try {
            return MD5.getMD5(dataString);
        } catch (NoSuchAlgorithmException e) {
            return String.valueOf(dataString.hashCode());
        }
    }
    public void doTag(TemplateBody body) throws TemplateException, IOException {
        Writer oriOutWriter = body.getEnvironment().getOut();
        StringWriter2 sw = new StringWriter2(oriOutWriter);
        //
        body.render(sw);
        String renderData = sw.toString();
        String renderDataMark = getMark(renderData);
        //
        HttpServletRequest request = PlatformFilter.getLocalRequest();
        Set<String> marks = (Set<String>) request.getAttribute(LoadOnece.class.getName());
        if (marks == null) {
            marks = new HashSet<String>();
            request.setAttribute(LoadOnece.class.getName(), marks);
        }
        //
        if (marks.contains(renderDataMark) == false) {
            marks.add(renderDataMark);
            Writer testW = body.getEnvironment().getOut();
            if (testW instanceof StringWriter2)
                testW = ((StringWriter2) body.getEnvironment().getOut()).getOriOutWriter();
            testW.write(renderData);
        }
    }
    //
    public void afterTag(Environment environment) throws TemplateException {}
}
class StringWriter2 extends StringWriter {
    private Writer oriOutWriter = null;
    public StringWriter2(Writer oriOutWriter) {
        this.oriOutWriter = oriOutWriter;
    }
    public Writer getOriOutWriter() {
        return oriOutWriter;
    }
}