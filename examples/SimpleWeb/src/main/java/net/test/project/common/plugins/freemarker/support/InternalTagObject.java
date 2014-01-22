package org.noe.platform.modules.freemarker.support;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import org.noe.platform.Noe;
import org.noe.platform.modules.freemarker.Tag;
import org.noe.platform.modules.freemarker.TemplateBody;
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
public class InternalTagObject implements TemplateDirectiveModel {
    private Tag tagBody = null;
    public InternalTagObject(Tag tagBody) {
        this.tagBody = tagBody;
        Noe.assertIsNotNull(tagBody, "tag Object is null.");
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
        //3.通知-开始执行标签
        boolean toDo = this.tagBody.beforeTag(objMap, env);
        if (toDo)
            this.tagBody.doTag(objMap, new InternalTemplateBody(objMap, body, env));
        //4.通知-结束执行标签
        this.tagBody.afterTag(objMap, env);
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
        public void doBody(Writer arg0) throws TemplateException, IOException {
            if (this.templateBody != null)
                this.templateBody.render(arg0);
        }
        public void doBody() throws TemplateException, IOException {
            if (this.templateBody != null)
                this.templateBody.render(environment.getOut());
        }
        @Override
        public Map<String, Object> tagProperty() {
            return this.tagProperty;
        }
    }
}