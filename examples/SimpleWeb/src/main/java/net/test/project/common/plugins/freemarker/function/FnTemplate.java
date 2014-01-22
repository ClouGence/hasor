package org.noe.platform.modules.freemarker.function;
import java.io.IOException;
import java.util.Map;
import javax.inject.Inject;
import org.noe.platform.modules.freemarker.FmMethod;
import org.noe.platform.modules.freemarker.FreemarkerService;
import freemarker.template.TemplateException;
/**
 * 
 * @version : 2013-7-22
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
public class FnTemplate {
    @Inject
    private FreemarkerService fmContext;
    //
    @FmMethod("fnStrTemplate")
    public String fnStrTemplate(String stringBody, Map<String, Object> params) throws TemplateException, IOException {
        return this.fmContext.processString(stringBody, params);
    }
    @FmMethod("fnNull")
    public boolean fnNull(Object body) throws TemplateException, IOException {
        return body == null;
    }
}