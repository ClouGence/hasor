package org.platform.app.beans;
import javax.inject.Singleton;
import org.hasor.annotation.Bean;
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