/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.hasor.web;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.core.Settings;
import net.hasor.utils.IOUtils;
import net.hasor.utils.StringUtils;
import net.hasor.web.annotation.Produces;
import net.hasor.web.startup.RuntimeListener;
import net.hasor.web.upload.FileUpload;
import net.hasor.web.upload.factorys.disk.DiskFileItemFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;
/**
 * Controller <br>
 * 昨夜西风凋碧树。独上高楼，望尽天涯路。 <br>
 * 衣带渐宽终不悔，为伊消得人憔悴。 <br>
 * 众里寻她千百度，蓦然回首，那人却在灯火阑珊处。
 *
 * @version : 2013-8-14
 * @author JFinal
 * @author 赵永春 (zyc@hasor.net)
 */
public class WebController implements Controller {
    private ThreadLocal<Invoker> invoker = new ThreadLocal<Invoker>();
    //
    public void initController(Invoker renderData) {
        if (this.invoker.get() != null) {
            this.invoker.remove();
        }
        if (renderData != null) {
            this.invoker.set(renderData);
        }
    }
    protected Invoker getInvoker() {
        return this.invoker.get();
    }
    //
    /** @return Return HttpServletRequest. Do not use HttpServletRequest Object in constructor of Controller */
    public HttpServletRequest getRequest() {
        return this.getInvoker().getHttpRequest();
    }
    /** @return Return HttpServletResponse. Do not use HttpServletResponse Object in constructor of Controller */
    public HttpServletResponse getResponse() {
        return this.getInvoker().getHttpResponse();
    }
    /** @return Return AppContext. */
    public AppContext getAppContext() {
        return RuntimeListener.getAppContext(this.getRequest().getSession().getServletContext());
    }
    //
    //
    //------------------------
    /**
     * 设置{@link HttpServletResponse}Header属性
     * @param key 参数 key
     * @param value 参数值
     * @return 返回this.
     */
    protected WebController setHeader(String key, String value) {
        this.getResponse().setHeader(key, value);
        return this;
    }
    /**
     * 设置{@link HttpServletResponse}Header属性
     * @param key 参数 key
     * @param value 参数值
     * @return 返回this.
     */
    protected WebController addHeader(String key, String value) {
        this.getResponse().addHeader(key, value);
        return this;
    }
    //
    //------------------------
    /**
     * Returns the value of a request parameter as a String, or null if the parameter does not exist.
     * <p>
     * You should only use this method when you are sure the parameter has only one value. If the parameter might have more than one value, use getParaValues(java.lang.String).
     * <p>
     * If you use this method with a multivalued parameter, the value returned is equal to the first value in the array returned by getParameterValues.
     * @param name a String specifying the name of the parameter
     * @return a String representing the single value of the parameter
     */
    protected String getPara(String name) {
        return this.getRequest().getParameter(name);
    }
    /**
     * Returns the value of a request parameter as a String, or default value if the parameter does not exist.
     * @param name a String specifying the name of the parameter
     * @param defaultValue a String value be returned when the value of parameter is null
     * @return a String representing the single value of the parameter
     */
    protected String getPara(String name, String defaultValue) {
        String result = this.getRequest().getParameter(name);
        return result != null && !"".equals(result) ? result : defaultValue;
    }
    /**
     * Returns the values of the request parameters as a Map.
     * @return a Map contains all the parameters name and value
     */
    protected Map<String, String[]> getParaMap() {
        return this.getRequest().getParameterMap();
    }
    /**
     * Returns an Enumeration of String objects containing the names of the parameters contained in this request. If the request has no parameters, the method returns an empty Enumeration.
     * @return an Enumeration of String objects, each String containing the name of a request parameter; or an empty Enumeration if the request has no parameters
     */
    protected Enumeration<String> getParaNames() {
        return this.getRequest().getParameterNames();
    }
    /**
     * Returns an array of String objects containing all of the values the given request parameter has, or null if the parameter does not exist. If the parameter has a single value, the array has a length of 1.
     * @param name a String containing the name of the parameter whose value is requested
     * @return an array of String objects containing the parameter's values
     */
    protected String[] getParaValues(String name) {
        return this.getRequest().getParameterValues(name);
    }
    /**
     * Returns an array of Integer objects containing all of the values the given request parameter has, or null if the parameter does not exist. If the parameter has a single value, the array has a length of 1.
     * @param name a String containing the name of the parameter whose value is requested
     * @return an array of Integer objects containing the parameter's values
     */
    protected Integer[] getParaValuesToInt(String name) {
        String[] values = this.getRequest().getParameterValues(name);
        if (values == null)
            return null;
        Integer[] result = new Integer[values.length];
        for (int i = 0; i < result.length; i++)
            result[i] = Integer.parseInt(values[i]);
        return result;
    }
    //
    //------------------------
    /**
     * Returns the value of a request parameter and convert to Integer.
     * @param name a String specifying the name of the parameter
     * @return a Integer representing the single value of the parameter
     */
    protected Integer getParaToInt(String name) {
        return toInt(this.getRequest().getParameter(name), null);
    }
    /**
     * Returns the value of a request parameter and convert to Integer with a default value if it is null.
     * @param name a String specifying the name of the parameter
     * @param defaultValue default value for the parameter
     * @return a Integer representing the single value of the parameter
     */
    protected Integer getParaToInt(String name, Integer defaultValue) {
        return toInt(this.getRequest().getParameter(name), defaultValue);
    }
    /* 字符串转换为Integer */
    private Integer toInt(String value, Integer defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        if (value.startsWith("N") || value.startsWith("n")) {
            return -Integer.parseInt(value.substring(1));
        }
        return Integer.parseInt(value);
    }
    /**
     * Returns the value of a request parameter and convert to Long.
     * @param name a String specifying the name of the parameter
     * @return a Integer representing the single value of the parameter
     */
    protected Long getParaToLong(String name) {
        return toLong(this.getRequest().getParameter(name), null);
    }
    /**
     * Returns the value of a request parameter and convert to Long with a default value if it is null.
     * @param name a String specifying the name of the parameter
     * @param defaultValue default value for the parameter
     * @return a Integer representing the single value of the parameter
     */
    protected Long getParaToLong(String name, Long defaultValue) {
        return toLong(this.getRequest().getParameter(name), defaultValue);
    }
    /* 字符串转换为long */
    private long toLong(String value, Long defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        if (value.startsWith("N") || value.startsWith("n")) {
            return -Long.parseLong(value.substring(1));
        }
        return Long.parseLong(value);
    }
    /**
     * Returns the value of a request parameter and convert to Boolean.
     * @param name a String specifying the name of the parameter
     * @return false if the value of the parameter is "false" or "0", true if it is "true" or "1", null if parameter is not exists
     */
    protected Boolean getParaToBoolean(String name) {
        String result = this.getRequest().getParameter(name);
        if (result != null) {
            result = result.trim().toLowerCase();
            if (result.equals("1") || result.equals("true")) {
                return Boolean.TRUE;
            } else if (result.equals("0") || result.equals("false")) {
                return Boolean.FALSE;
            }
            // return Boolean.FALSE; // if use this, delete 2 lines code under
        }
        return null;
    }
    /**
     * Returns the value of a request parameter and convert to Boolean with a default value if it is null.
     * @param name a String specifying the name of the parameter
     * @param defaultValue default value for the parameter
     * @return false if the value of the parameter is "false" or "0", true if it is "true" or "1", default value if it is null
     */
    protected Boolean getParaToBoolean(String name, Boolean defaultValue) {
        Boolean result = getParaToBoolean(name);
        return result != null ? result : defaultValue;
    }
    //
    //------------------------
    /** @return Return HttpSession. */
    protected HttpSession getSession() {
        return getRequest().getSession();
    }
    /**
     * Return HttpSession.
     * @param create a boolean specifying create HttpSession if it not exists
     * @return Return HttpSession.
     */
    protected HttpSession getSession(boolean create) {
        return getRequest().getSession(create);
    }
    /**
     * Return a Object from session.
     * @param key a String specifying the key of the Object stored in session
     * @return return session attribute data.
     */
    protected <T> T getSessionAttr(String key) {
        HttpSession session = this.getRequest().getSession(false);
        return session != null ? (T) session.getAttribute(key) : null;
    }
    /**
     * Store Object to session.
     * @param key a String specifying the key of the Object stored in session
     * @param value a Object specifying the value stored in session
     * @return 返回this.
     */
    protected WebController setSessionAttr(String key, Object value) {
        this.getRequest().getSession(true).setAttribute(key, value);
        return this;
    }
    /**
     * Remove Object in session.
     * @param key a String specifying the key of the Object stored in session
     * @return 返回this.
     */
    protected WebController removeSessionAttr(String key) {
        HttpSession session = this.getRequest().getSession(false);
        if (session != null) {
            session.removeAttribute(key);
        }
        return this;
    }
    //
    //------------------------
    /**
     * Get cookie value by cookie name.
     * @param name cookie name
     * @param defaultValue default value
     * @return return cookie value or default value.
     */
    protected String getCookie(String name, String defaultValue) {
        Cookie cookie = getCookieObject(name);
        return cookie != null ? cookie.getValue() : defaultValue;
    }
    /**
     * Get cookie value by cookie name.
     * @param name cookie name
     * @return return cookie value or null.
     */
    protected String getCookie(String name) {
        return getCookie(name, null);
    }
    /**
     * Get cookie value by cookie name and convert to Integer.
     * @param name cookie name
     * @return return cookie value or null.
     */
    protected Integer getCookieToInt(String name) {
        String result = getCookie(name);
        return result != null ? Integer.parseInt(result) : null;
    }
    /**
     * Get cookie value by cookie name and convert to Integer.
     * @param name cookie name
     * @param defaultValue default value
     * @return return cookie value or default value.
     */
    protected Integer getCookieToInt(String name, Integer defaultValue) {
        String result = getCookie(name);
        return result != null ? Integer.parseInt(result) : defaultValue;
    }
    /**
     * Get cookie value by cookie name and convert to Long.
     * @param name cookie name
     * @return return cookie value or null.
     */
    protected Long getCookieToLong(String name) {
        String result = getCookie(name);
        return result != null ? Long.parseLong(result) : null;
    }
    /**
     * Get cookie value by cookie name and convert to Long.
     * @param name cookie name
     * @param defaultValue default value
     * @return return cookie value or default value.
     */
    protected Long getCookieToLong(String name, Long defaultValue) {
        String result = getCookie(name);
        return result != null ? Long.parseLong(result) : defaultValue;
    }
    /**
     * Get cookie object by cookie name.
     * @param name cookie name
     * @return Cookie object
     */
    protected Cookie getCookieObject(String name) {
        Cookie[] cookies = this.getRequest().getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    return cookie;
                }
            }
        }
        return null;
    }
    /** @return Get all cookie objects. */
    protected Cookie[] getCookieObjects() {
        Cookie[] result = this.getRequest().getCookies();
        return result != null ? result : new Cookie[0];
    }
    /**
     * Set Cookie to response.
     * @param cookie new cookie.
     * @return 返回this.
     */
    protected WebController setCookie(Cookie cookie) {
        this.getResponse().addCookie(cookie);
        return this;
    }
    /**
     * Set Cookie to response.
     * @param name cookie name
     * @param value cookie value
     * @param maxAgeInSeconds -1: clear cookie when close browser. 0: clear cookie immediately. n &gt; 0 : max age in n seconds.
     * @param path see Cookie.setPath(String)
     * @return 返回this.
     */
    protected WebController setCookie(String name, String value, int maxAgeInSeconds, String path) {
        setCookie(name, value, maxAgeInSeconds, path, null);
        return this;
    }
    /**
     * Set Cookie to response.
     * @param name cookie name
     * @param value cookie value
     * @param maxAgeInSeconds -1: clear cookie when close browser. 0: clear cookie immediately. n &gt; 0 : max age in n seconds.
     * @param path see Cookie.setPath(String)
     * @param domain the domain name within which this cookie is visible; form is according to RFC 2109
     * @return 返回this.
     */
    protected WebController setCookie(String name, String value, int maxAgeInSeconds, String path, String domain) {
        Cookie cookie = new Cookie(name, value);
        if (domain != null) {
            cookie.setDomain(domain);
        }
        cookie.setMaxAge(maxAgeInSeconds);
        cookie.setPath(path);
        this.getResponse().addCookie(cookie);
        return this;
    }
    /**
     * Set Cookie with path = "/".
     * @param name cookie name
     * @param value cookie value
     * @param maxAgeInSeconds max age
     * @return 返回this.
     */
    protected WebController setCookie(String name, String value, int maxAgeInSeconds) {
        setCookie(name, value, maxAgeInSeconds, "/", null);
        return this;
    }
    /**
     * Remove Cookie with path = "/".
     * @param name cookie name
     * @return 返回this.
     */
    protected WebController removeCookie(String name) {
        setCookie(name, null, 0, "/", null);
        return this;
    }
    /**
     * Remove Cookie.
     * @param name cookie name
     * @param path cookie with path = "/".
     * @return 返回this.
     */
    protected WebController removeCookie(String name, String path) {
        setCookie(name, null, 0, path, null);
        return this;
    }
    /**
     * Remove Cookie.
     * @param name cookie name
     * @param path cookie with path = "/".
     * @param domain domain
     * @return 返回this.
     */
    protected WebController removeCookie(String name, String path, String domain) {
        setCookie(name, null, 0, path, domain);
        return this;
    }
    //
    //------------------------
    /**
     * Returns the value of the named attribute as an Object, or null if no attribute of the given name exists.
     * @param name a String specifying the name of the attribute
     * @return an Object containing the value of the attribute, or null if the attribute does not exist
     */
    protected <T> T getAttr(String name) {
        return (T) this.getRequest().getAttribute(name);
    }
    /**
     * Stores an attribute in this request
     * @param name a String specifying the name of the attribute
     * @param value the Object to be stored
     * @return 返回this.
     */
    protected WebController setAttr(String name, Object value) {
        this.getRequest().setAttribute(name, value);
        return this;
    }
    /**
     * Removes an attribute from this request
     * @param name a String specifying the name of the attribute to remove
     * @return 返回this.
     */
    protected WebController removeAttr(String name) {
        this.getRequest().removeAttribute(name);
        return this;
    }
    /**
     * Stores attributes in this request, key of the map as attribute name and value of the map as attribute value
     * @param attrMap key and value as attribute of the map to be stored
     * @return 返回this.
     */
    protected WebController setAttrs(Map<String, Object> attrMap) {
        for (Map.Entry<String, Object> entry : attrMap.entrySet()) {
            this.getRequest().setAttribute(entry.getKey(), entry.getValue());
        }
        return this;
    }
    /**
     * Returns an Enumeration containing the names of the attributes available to this request. This method returns an empty Enumeration if the request has no attributes available to it.
     * @return an Enumeration of strings containing the names of the request's attributes
     */
    protected Enumeration<String> getAttrNames() {
        return this.getRequest().getAttributeNames();
    }
    //
    //------------------------
    /**
     *  返回 Invoker 保存的数据。
     *  @return 返回数据
     */
    protected <T> T getData(String name) {
        return (T) this.getInvoker().get(name);
    }
    /**
     *  设置 Invoker 保存的数据。
     *  @return 返回 this
     */
    protected WebController putData(String name, Object value) {
        this.getInvoker().put(name, value);
        return this;
    }
    /**
     *  删除 Invoker 保存的数据。
     *  @return 返回 this
     */
    protected WebController removeData(String name) {
        this.getInvoker().remove(name);
        return this;
    }
    /**
     *  设置 Invoker 保存的数据。
     *  @return 返回 this
     */
    protected WebController setDatas(Map<String, Object> attrMap) {
        for (Map.Entry<String, Object> entry : attrMap.entrySet()) {
            this.getInvoker().put(entry.getKey(), entry.getValue());
        }
        return this;
    }
    /**
     *  返回 Invoker 保存的数据keys。
     *  @return 返回数据
     */
    protected Set<String> getDataNames() {
        return this.getInvoker().keySet();
    }
    //
    //------------------------
    /** @return Get model from AppContext. */
    protected <T> T getInstance(Class<T> modelClass, String modelName) {
        return this.getAppContext().findBindingBean(modelName, modelClass);
    }
    /** @return Get model from AppContext. */
    protected <T> T getInstance(Class<T> modelClass) {
        return this.getAppContext().getInstance(modelClass);
    }
    /** @return Get model from AppContext. */
    protected Object getInstance(String bindID) {
        return this.getAppContext().getInstance(bindID);
    }
    //
    //------------------------
    /** 更新渲染模版。*/
    protected void renderTo(String viewName) {
        Invoker invoker = getInvoker();
        if (invoker != null && invoker instanceof RenderInvoker) {
            RenderInvoker render = (RenderInvoker) invoker;
            render.renderTo(viewName);
        }
    }
    /**
     * 更新渲染模版。
     * @param renderType 如果注释了 {@link Produces}注解那么该参数将会失效。
     * @param viewName 模版名称
     */
    protected void renderTo(String renderType, String viewName) {
        Invoker invoker = getInvoker();
        if (invoker != null && invoker instanceof RenderInvoker) {
            RenderInvoker render = (RenderInvoker) invoker;
            render.renderTo(renderType, viewName);
        }
    }
    //
    //------------------------
    /**
     * 返回为 Multipart 请求,通常 Multipart 请求都包含一个或多个的文件上传。
     * @return 返回为 Multipart 请求,通常 Multipart 请求都包含一个或多个的文件上传。
     * @see FileUpload#isMultipartContent(HttpServletRequest)
     */
    protected boolean isMultipart() {
        return FileUpload.isMultipartContent(this.getRequest());
    }
    /**
     * 将 multipart 请求数据缓存到一个目录下,同时返回 FileItem 列表.
     */
    protected List<FileItem> getMultipartList() throws IOException {
        return this.getMultipartList((String) null, null, null);
    }
    /**
     * 将 Multipart 请求数据缓存到一个目录下,同时返回 FileItem 列表.
     * @param maxPostSize 最大单个 body 大小
     */
    protected List<FileItem> getMultipartList(Integer maxPostSize) throws IOException {
        return this.getMultipartList((String) null, maxPostSize, null);
    }
    /**
     * 将 Multipart 请求数据缓存到一个目录下,同时返回 FileItem 列表.
     * @param cacheDirectory 缓存目录
     * @param maxPostSize 最大单个 body 大小
     */
    protected List<FileItem> getMultipartList(String cacheDirectory, Integer maxPostSize) throws IOException {
        return this.getMultipartList(cacheDirectory, maxPostSize, null);
    }
    /**
     * 将 Multipart 请求数据缓存到一个目录下,同时返回 FileItem 列表。
     * @param cacheDirectory 缓存目录(默认配置位于:"hasor.fileupload.cacheDirectory")
     * @param maxPostSize 最大单个 body 大小
     * @param encoding 字符编码。
     */
    protected List<FileItem> getMultipartList(String cacheDirectory, Integer maxPostSize, String encoding) throws IOException {
        cacheDirectory = IOUtils.normalizeNoEndSeparator(cacheDirectory);
        if (StringUtils.isBlank(cacheDirectory)) {
            Settings settings = this.getAppContext().getEnvironment().getSettings();
            cacheDirectory = settings.getDirectoryPath("hasor.fileupload.cacheDirectory");
        }
        return this.getMultipartList(new DiskFileItemFactory(cacheDirectory), maxPostSize, encoding);
    }
    /**
     * 将 Multipart 请求数据缓存到一个目录下,同时返回 FileItem 列表。
     * @param factory 缓存策略
     */
    protected List<FileItem> getMultipartList(FileItemFactory factory) throws IOException {
        return this.getMultipartList(factory, null, null);
    }
    /**
     * 将 Multipart 请求数据缓存到一个目录下,同时返回 FileItem 列表。
     * @param factory 缓存策略
     * @param maxPostSize 最大单个 body 大小
     */
    protected List<FileItem> getMultipartList(FileItemFactory factory, Integer maxPostSize) throws IOException {
        return this.getMultipartList(factory, maxPostSize, null);
    }
    /**
     * 将 Multipart 请求数据缓存到一个目录下,同时返回 FileItem 列表。
     * @param factory 缓存策略
     * @param maxPostSize 最大单个 body 大小
     * @param encoding 字符编码。
     */
    protected List<FileItem> getMultipartList(FileItemFactory factory, Integer maxPostSize, String encoding) throws IOException {
        FileUpload upload = this.newFileUpload(maxPostSize, encoding);
        if (upload == null) {
            return null;
        }
        return upload.parseRequest(this.getRequest(), factory);
    }
    /**
     * 将 Multipart 请求数据缓存到一个目录下,同时返回 FileItem 列表。
     * @param parameterName 要获取的制定参数表单名
     */
    protected List<FileItem> getMultipart(String parameterName) throws IOException {
        return this.getMultipart(parameterName, null, null, null);
    }
    /**
     * 将 Multipart 请求数据缓存到一个目录下,同时返回 FileItem 列表,
     * @param parameterName 要获取的制定参数表单名
     * @param maxPostSize 最大单个 body 大小
     */
    protected List<FileItem> getMultipart(String parameterName, Integer maxPostSize) throws IOException {
        return this.getMultipart(parameterName, null, maxPostSize, null);
    }
    /**
     * 将 Multipart 请求数据缓存到一个目录下,同时返回 FileItem 列表.
     * @param parameterName 要获取的制定参数表单名
     * @param cacheDirectory 缓存目录
     * @param maxPostSize 最大单个 body 大小
     */
    protected List<FileItem> getMultipart(String parameterName, String cacheDirectory, Integer maxPostSize) throws IOException {
        return this.getMultipart(parameterName, cacheDirectory, maxPostSize, null);
    }
    /**
     * 将 Multipart 请求数据缓存到一个目录下,同时返回 FileItem 列表。
     * @param parameterName 要获取的制定参数表单名
     * @param cacheDirectory 缓存目录
     * @param maxPostSize 最大单个 body 大小
     * @param encoding 字符编码。
     */
    protected List<FileItem> getMultipart(String parameterName, String cacheDirectory, Integer maxPostSize, String encoding) throws IOException {
        Hasor.assertIsNotNull(parameterName);
        List<FileItem> itemList = this.getMultipartList(cacheDirectory, maxPostSize, encoding);
        if (itemList == null || itemList.isEmpty()) {
            return null;
        }
        List<FileItem> resultData = new ArrayList<FileItem>();
        for (FileItem item : itemList) {
            if (parameterName.equals(item.getFieldName())) {
                resultData.add(item);
            } else {
                item.deleteOrSkip();
            }
        }
        return resultData;
    }
    /**
     * 将 Multipart 请求数据缓存到一个目录下,同时返回 FileItem。
     * @param parameterName 要获取的制定参数表单名
     */
    protected FileItem getOneMultipart(String parameterName) throws IOException {
        return this.getOneMultipart(parameterName, null, null, null);
    }
    /**
     * 将 Multipart 请求数据缓存到一个目录下,同时返回 FileItem。
     * @param parameterName 要获取的制定参数表单名
     * @param maxPostSize 最大单个 body 大小
     */
    protected FileItem getOneMultipart(String parameterName, Integer maxPostSize) throws IOException {
        return this.getOneMultipart(parameterName, null, maxPostSize, null);
    }
    /**
     * 将 Multipart 请求数据缓存到一个目录下,同时返回 FileItem。
     * @param parameterName 要获取的制定参数表单名
     * @param cacheDirectory 缓存目录
     * @param maxPostSize 最大单个 body 大小
     */
    protected FileItem getOneMultipart(String parameterName, String cacheDirectory, Integer maxPostSize) throws IOException {
        return this.getOneMultipart(parameterName, cacheDirectory, maxPostSize, null);
    }
    /**
     * 将 Multipart 请求数据缓存到一个目录下,同时返回 FileItem。
     * @param parameterName 要获取的制定参数表单名
     * @param cacheDirectory 缓存目录
     * @param maxPostSize 最大单个 body 大小
     * @param encoding 字符编码。
     */
    protected FileItem getOneMultipart(String parameterName, String cacheDirectory, Integer maxPostSize, String encoding) throws IOException {
        Hasor.assertIsNotNull(parameterName);
        List<FileItem> itemList = this.getMultipartList(cacheDirectory, maxPostSize, encoding);
        if (itemList == null || itemList.isEmpty()) {
            return null;
        }
        FileItem findItem = null;
        for (FileItem item : itemList) {
            if (findItem == null && parameterName.equals(item.getFieldName())) {
                findItem = item;
            } else {
                item.deleteOrSkip();
            }
        }
        return findItem;
    }
    /**
     * 返回流式处理文件上传的迭代器。
     */
    protected Iterator<FileItemStream> getMultipartIterator() throws IOException {
        return this.getMultipartIterator(null, null);
    }
    /**
     * 返回流式处理文件上传的迭代器。
     * @param maxPostSize 最大单个 body 大小
     */
    protected Iterator<FileItemStream> getMultipartIterator(Integer maxPostSize) throws IOException {
        return this.getMultipartIterator(maxPostSize, null);
    }
    /**
     * 返回流式处理文件上传的迭代器。
     * @param maxPostSize 最大单个 body 大小
     * @param encoding 字符编码。
     */
    protected Iterator<FileItemStream> getMultipartIterator(Integer maxPostSize, String encoding) throws IOException {
        FileUpload upload = this.newFileUpload(maxPostSize, encoding);
        if (upload == null) {
            return null;
        }
        return upload.getItemIterator(this.getRequest());
    }
    /**
     * 创建原始的FileUpload对象。
     * @param maxPostSize 最大单个 body 大小
     * @param encoding 字符编码。
     */
    protected FileUpload newFileUpload(Integer maxPostSize, String encoding) {
        if (!this.isMultipart()) {
            return null;
        }
        //
        FileUpload upload = new FileUpload(this.getAppContext().getEnvironment().getSettings());
        if (maxPostSize != null) {
            upload.setSizeMax(maxPostSize);
        }
        if (encoding != null) {
            upload.setHeaderEncoding(encoding);
        }
        return upload;
    }
}