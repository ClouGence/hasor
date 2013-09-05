package org.hasor.test.mvc.app.beans;
import java.io.IOException;
import net.hasor.context.anno.Bean;
import net.hasor.context.anno.context.AnnoAppContext;
import org.hasor.test.mvc.plugin.log.OutLog;
/**
 * 
 * @version : 2013-7-25
 * @author 赵永春 (zyc@hasor.net)
 */
@Bean("LogBean")
public class LogBean {
    @OutLog
    public void print() {
        System.out.println("在此之前输出日志!");
    }
    //
    //
    public static void main(String[] args) throws IOException {
        AnnoAppContext aac = new AnnoAppContext();
        aac.start();
        //
        LogBean logBean = (LogBean) aac.getBean("LogBean");
        logBean.print();
        //
        aac.destroy();
    }
}