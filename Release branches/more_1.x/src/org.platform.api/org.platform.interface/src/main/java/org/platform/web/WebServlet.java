package org.platform.web;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.servlet.http.HttpServlet;
/**
 * 声明一个Servlet，该Servlet需要继承{@link HttpServlet}类。
 * @version : 2013-3-12
 * @author 赵永春 (zyc@byshell.org)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface WebServlet {
    /**对服务的描述信息。
     * <br/><b>注：</b><i>该值可以通过管理控制台中重新设置。</i>*/
    public String description() default "";
    /**在管理控制台显示服务时使用displayName属性。
     * <br/><b>注：</b><i>该值可以通过管理控制台中重新设置。</i>*/
    public String displayName() default "";
    /**Servlet在过滤器链上的顺序。默认：0，数字越大启动越延迟。
     * <br/><b>注：</b><i>该值可以通过管理控制台中重新设置。</i>*/
    public int loadOnStartup() default 0;
    /** 服务的启动参数。
     * <br/><b>注：</b><i>该值可以通过管理控制台中重新设置。</i>*/
    public WebInitParam[] initParams() default {};
    /** Servlet名称或ID */
    public String servletName() default "";
    /** The small-icon of the filter.
     * <br/><b>注：</b><i>该值可以通过管理控制台中重新设置。</i>*/
    public String smallIcon() default "";
    /** The large-icon of the filter.
     * <br/><b>注：</b><i>该值可以通过管理控制台中重新设置。</i>*/
    public String largeIcon() default "";
    /**
     * URL匹配规则，与{@link WebServlet#urlPatterns()}属性表示同样功效。
     * @see org.platform.web.WebServlet#urlPatterns()
     */
    public String[] value() default {};
    /**
     * URL匹配规则，与{@link WebServlet#value()}属性表示同样功效。
     * @see org.platform.web.WebServlet#value()
     */
    public String[] urlPatterns() default {};
}