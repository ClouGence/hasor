package org.platform.api.services;
import org.platform.api.safety.Power;
/**
 * 声明一个Services对象。
 * @version : 2013-3-12
 * @author 赵永春 (zyc@byshell.org)
 */
public @interface Service {
    /**Servlet所属作用域。*/
    public String scope() default "";
    /** The description of the filter */
    public String description() default "";
    /** The display name of the filter */
    public String displayName() default "";
    /**Service 的公开范围。*/
    public Access access() default Access.Private;
    /**Service 名称。*/
    public String[] value();
    /**是否延迟初始化。*/
    public boolean lazyInit() default false;
    /**
     * 公开范围枚举
     * @version : 2013-3-12
     * @author 赵永春 (zyc@byshell.org)
     */
    public static enum Access {
        /**完全公开。*/
        Public,
        /**
         * 需要通过认证之后才可以使用。如果限制到特定的权限的配置请使用{@link Power}注解进行配置。
         * @see org.platform.faces.safety.Power
         */
        Protected,
        /**只限应用程序内部使用，不设立对外公开。*/
        Private,
    }
}