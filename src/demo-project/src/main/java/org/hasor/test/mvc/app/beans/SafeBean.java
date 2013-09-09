package org.hasor.test.mvc.app.beans;
import java.io.IOException;
import net.hasor.core.anno.Bean;
import net.hasor.core.anno.context.AnnoAppContext;
import org.hasor.test.mvc.plugin.log.OutLog;
import org.hasor.test.mvc.plugin.safety.Power;
import org.hasor.test.mvc.plugin.safety.SafetyContext;
/**
 * 
 * @version : 2013-7-25
 * @author 赵永春 (zyc@hasor.net)
 */
@Bean("SafeBean")
public class SafeBean {
    @OutLog
    @Power("abc")
    public void print() {
        System.out.println("在此之前输出日志!");
    }
    //
    //
    public static void main(String[] args) throws IOException {
        AnnoAppContext aac = new AnnoAppContext();
        aac.start();
        //
        SafeBean safeBean = (SafeBean) aac.getBean("SafeBean");
        SafetyContext sc = aac.getInstance(SafetyContext.class);
        System.out.println("--------无权限调用------");
        try {
            safeBean.print();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("--------有权限调用------");
        try {
            sc.addPower("abc");
            safeBean.print();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //
        aac.destroy();
    }
}