package org.hasor.test.app.beans;
import javax.inject.Singleton;
import org.hasor.context.anno.Bean;
/**
 * 
 * @version : 2013-7-26
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
@Singleton
@Bean("User")
public class UserBean {
    public String account = "guest";
    public String pasword = "guest";
}