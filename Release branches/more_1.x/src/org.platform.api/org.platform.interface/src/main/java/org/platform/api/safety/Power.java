package org.platform.api.safety;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * 权限配置，可以配置到类级别和方法级别上。
 * @version : 2013-3-12
 * @author 赵永春 (zyc@byshell.org)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
public @interface Power {
    /**权限点代码*/
    public String value() default "";
    /**权限认证等级，*/
    public Level level() default Level.PassLogin;
    /**
     * 认证级别枚举
     * @version : 2013-3-12
     * @author 赵永春 (zyc@byshell.org)
     */
    public static enum Level {
        /**自由访问。Level 0*/
        Free,
        /**需要经过登陆。Level 1*/
        PassLogin,
        /**
         * 需要通过策略检查。Level 2
         * @see org.platform.api.safety.IPowerPolicy
         */
        PassPolicy
    }
}