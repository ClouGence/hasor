package org.hasor.test.mvc.app.beans;
import javax.inject.Singleton;
import net.hasor.core.gift.bean.Bean;
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