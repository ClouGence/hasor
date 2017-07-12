package net.demo.spring.provider;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
/**
 *
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class ProviderClient {
    public static void main(String[] args) throws Throwable {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("provider-config.xml");
        System.out.println("server start.");
        System.in.read();
    }
}