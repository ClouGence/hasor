package org.platform.api.web;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.servlet.Filter;
import javax.servlet.annotation.WebInitParam;
/**
 * 声明一个Filter，该Filter需要实现{@link Filter}接口。
 * @version : 2013-3-12
 * @author 赵永春 (zyc@byshell.org)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface WebFilter {
    /**Filter在过滤器链上的顺序。*/
    public int sort();
    /** The description of the filter */
    public String description() default "";
    /** The display name of the filter */
    public String displayName() default "";
    /** The init parameters of the filter */
    public WebInitParam[] initParams() default {};
    /** The name of the filter */
    public String filterName() default "";
    /** The small-icon of the filter */
    public String smallIcon() default "";
    /** The large-icon of the filter */
    public String largeIcon() default "";
    /** The names of the servlets to which the filter applies. */
    public String[] servletNames() default {};
    /**
     * The URL patterns to which the filter applies
     * @see org.platform.api.web.WebFilter#urlPatterns()
     */
    public String[] value() default {};
    /**
     * The URL patterns to which the filter applies
     * @see org.platform.api.web.WebFilter#value()
     */
    public String[] urlPatterns() default {};
    ///** The dispatcher types to which the filter applies */
    //public DispatcherType[] dispatcherTypes() default { DispatcherType.REQUEST };
    //    /**
    //     * Declares whether the filter supports asynchronous operation mode.
    //     *
    //     * @see javax.servlet.ServletRequest#startAsync
    //     * @see javax.servlet.ServletRequest#startAsync(ServletRequest,
    //     * ServletResponse)
    //     */
    //    boolean asyncSupported() default false;
}