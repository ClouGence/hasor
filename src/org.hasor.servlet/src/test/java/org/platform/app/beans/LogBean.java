package org.platform.app.beans;
import java.io.IOException;
import org.hasor.annotation.Bean;
import org.hasor.annotation.context.AnnoAppContext;
import org.platform.plugin.log.OutLog;
/**
 * 
 * @version : 2013-7-25
 * @author 赵永春 (zyc@byshell.org)
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