package net.test.hasor.web;
import net.hasor.core.Hasor;
import net.hasor.web.mime.MimeTypeSupplier;
import net.hasor.web.startup.RuntimeListener;
import org.junit.Test;

import javax.servlet.ServletContext;
import java.io.IOException;
/**
 * Created by yongchun.zyc on 2017/2/25.
 */
public class ExtRuntimeListener extends RuntimeListener {
    protected Hasor createAppContext(ServletContext sc) throws Throwable {
        Hasor hasor = super.createAppContext(sc);
        hasor.setMainSettings(".....");
        return hasor;
    }
    /**
     * S
     * @version : 2016年2月15日
     * @author 赵永春(zyc@hasor.net)
     */
    public static class MimeTypeTest {
        @Test
        public void mimeTypeTest() throws IOException {
            MimeTypeSupplier mimeTypeContext = new MimeTypeSupplier(null);
            mimeTypeContext.loadStream("/META-INF/mime.types.xml");
            mimeTypeContext.loadStream("mime.types.xml");
            //
            String htmlType = mimeTypeContext.get("html");
            assert "text/html".equalsIgnoreCase(htmlType);
        }
    }
}