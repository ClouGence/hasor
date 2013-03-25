package org.platform.api.web;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.http.HttpServlet;
/**
 * 声明一个Servlet，该Servlet需要继承{@link HttpServlet}类。
 * @version : 2013-3-12
 * @author 赵永春 (zyc@byshell.org)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface WebServlet {
    /** The name of the servlet */
    public String name() default "";
    /**
     * The URL patterns of the servlet
     * @see org.platform.api.web.WebServlet#urlPatterns()
     */
    public String[] value() default {};
    /**
     * The URL patterns of the servlet
     * @see org.platform.api.web.WebServlet#value()
     */
    public String[] urlPatterns() default {};
    /** The load-on-startup order of the servlet */
    public int loadOnStartup() default -1;
    /** The init parameters of the servlet */
    public WebInitParam[] initParams() default {};
    /** The small-icon of the servlet */
    public String smallIcon() default "";
    /** The large-icon of the servlet */
    public String largeIcon() default "";
    /** The description of the servlet */
    public String description() default "";
    /** The display name of the servlet */
    public String displayName() default "";
    //    /**
    //     * Declares whether the servlet supports asynchronous operation mode.
    //     *
    //     * @see javax.servlet.ServletRequest#startAsync
    //     * @see javax.servlet.ServletRequest#startAsync(ServletRequest,
    //     * ServletResponse)
    //     */
    //    boolean asyncSupported() default false;
}