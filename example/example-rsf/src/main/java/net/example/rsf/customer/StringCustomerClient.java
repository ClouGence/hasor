package net.example.rsf.customer;
import net.example.rsf.service.EchoService;
import net.example.rsf.service.MessageService;
import net.hasor.rsf.RsfResult;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
/**
 *
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class StringCustomerClient {
    public static void main(String[] args) throws Throwable {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("spring-consumer-config.xml");
        //
        EchoService echoService = (EchoService) applicationContext.getBean("echoService");
        for (int i = 0; i < 10; i++) {
            Thread.sleep(100);
            String res = echoService.sayHello("Hello Word");
            System.out.println(res);
        }
        System.out.println("@@@@@@@@@@@@@@");
        //
        //
        MessageService messageService = (MessageService) applicationContext.getBean("messageService");
        for (int i = 0; i < 10; i++) {
            RsfResult res = messageService.sayHello("Hello Word");
            System.out.println(res);
        }
        System.out.println("@@@@@@@@@@@@@@");
        //
        System.out.println("server start.");
    }
}