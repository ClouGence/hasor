package org.hasor.test.app.beans;
import javax.inject.Singleton;
import net.hasor.context.anno.Bean;
/**
 * 
 * @version : 2013-7-26
 * @author ’‘”¿¥∫ (zyc@hasor.net)
 */
@Singleton
@Bean("User")
public class UserBean {
    public String account = "guest";
    public String pasword = "guest";
}