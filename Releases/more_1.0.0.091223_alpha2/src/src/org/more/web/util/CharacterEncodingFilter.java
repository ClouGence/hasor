package org.more.web.util;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
/**
 * 字符过滤器，负责对请求资源的，请求和响应设置其编码格式，该过滤器使用utf-8作为默认编码格式。
 * Date : 2009-5-6
 * @author 赵永春
 */
public class CharacterEncodingFilter implements Filter {
    /** 请求编码 */
    private static final String requestEncoding  = "utf-8";
    /** 响应编码*/
    private static final String responseEncoding = "utf-8";
    /**
     * 过滤器初始化方法，该方法负责初始化请求以及响应编码。
     * @param config 过滤器配置对象。
     */
    public void init(FilterConfig config) {}
    /**
     * 资源过滤器方法。该方法中处理资源的请求和响应编码设置。
     * @param request 请求对象。
     * @param response 响应对象。
     * @param chain 过滤器执行对象。
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        request.setCharacterEncoding(CharacterEncodingFilter.requestEncoding);// 设置请求编码格式
        response.setCharacterEncoding(CharacterEncodingFilter.responseEncoding);// 设置响应编码格式
        chain.doFilter(request, response);//执行过滤器
    }
    /** 销毁过滤器 */
    public void destroy() {}
}