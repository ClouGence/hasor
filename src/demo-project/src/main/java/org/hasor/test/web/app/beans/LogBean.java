package org.hasor.test.web.app.beans;
import java.io.IOException;
import net.hasor.core.context.AnnoStandardAppContext;
import net.hasor.core.gift.bean.Bean;
import org.hasor.test.plugins.log.OutLog;
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
        AnnoStandardAppContext aac = new AnnoStandardAppContext();
        aac.start();
        //
        LogBean logBean = (LogBean) aac.getBean("LogBean");
        logBean.print();
        //
    }
}