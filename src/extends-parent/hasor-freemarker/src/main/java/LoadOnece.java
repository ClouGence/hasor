package org.noe.biz.common.ftl;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import org.noe.biz.common.startup.PlatformFilter;
import org.noe.platform.modules.freemarker.FmTag;
import org.noe.platform.modules.freemarker.Tag;
import org.noe.platform.modules.freemarker.TemplateBody;
import org.noe.platform.util.CommonCodeUtils.MD5;
import freemarker.core.Environment;
import freemarker.template.TemplateException;
/**
 * 
 * @version : 2013-9-24
 * @author 赵永春(zyc@hasor.net)
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