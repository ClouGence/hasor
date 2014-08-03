/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
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
package net.hasor.mvc.controller;
import java.util.Enumeration;
import java.util.Map;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import net.hasor.core.AppContext;
import net.hasor.web.startup.RuntimeFilter;
/**
 * Controller
 * <br>
 * 昨夜西风凋碧树。独上高楼，望尽天涯路。<br>
 * 衣带渐宽终不悔，为伊消得人憔悴。<br>
 * 众里寻她千百度，蓦然回首，那人却在灯火阑珊处。
 * @version : 2013-8-14
 * @author JFinal
 * @author 赵永春 (zyc@hasor.net)
 */
public abstract class AbstractController {
    //无论是单例还是多例,利用ThreadLocal进行线程隔离，都会保证 AbstractController 是线程安全的。
    private ThreadLocal<HttpServletRequest>  httpRequest  = new ThreadLocal<HttpServletRequest>();
    private ThreadLocal<HttpServletResponse> httpResponse = new ThreadLocal<HttpServletResponse>();
    //
    public void initController(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        this.resetController();
        this.httpRequest.set(httpRequest);
        this.httpResponse.set(httpResponse);
    }
    public void resetController() {
        if (this.httpRequest.get() != null)
            this.httpRequest.remove();
        if (this.httpResponse.get() != null)
            this.httpResponse.remove();
    }
    //
    /** Return HttpServletRequest. Do not use HttpServletRequest Object in constructor of Controller */
    public HttpServletRequest getRequest() {
        return this.httpRequest.get();
    }
    /** Return HttpServletResponse. Do not use HttpServletResponse Object in constructor of Controller */
    public HttpServletResponse getResponse() {
        return this.httpResponse.get();
    }
    /** Return AppContext. */
    public AppContext getAppContext() {
        return RuntimeFilter.getLocalAppContext();
    }
    /** Return HttpSession. */
    public HttpSession getSession() {
        return getRequest().getSession();
    }
    /**
     * Return HttpSession.
     * @param create a boolean specifying create HttpSession if it not exists
     */
    public HttpSession getSession(boolean create) {
        return getRequest().getSession(create);
    }
    // --------
    /**设置{@link HttpServletRequest}属性*/
    public AbstractController putAtt(String attKey, Object attValue) {
        this.getRequest().setAttribute(attKey, attValue);
        return this;
    }
    /**设置{@link HttpServletResponse}Header属性*/
    public AbstractController setHeader(String key, String value) {
        this.getResponse().setHeader(key, value);
        return this;
    }
    /**设置{@link HttpServletResponse}Header属性*/
    public AbstractController addHeader(String key, String value) {
        this.getResponse().addHeader(key, value);
        return this;
    }
    /**
     * Stores an attribute in this request
     * @param name a String specifying the name of the attribute
     * @param value the Object to be stored
     */
    public AbstractController setAttr(String name, Object value) {
        this.getRequest().setAttribute(name, value);
        return this;
    }
    /**
     * Removes an attribute from this request
     * @param name a String specifying the name of the attribute to remove
     */
    public AbstractController removeAttr(String name) {
        this.getRequest().removeAttribute(name);
        return this;
    }
    /**
     * Stores attributes in this request, key of the map as attribute name and value of the map as attribute value
     * @param attrMap key and value as attribute of the map to be stored
     */
    public AbstractController setAttrs(Map<String, Object> attrMap) {
        for (Map.Entry<String, Object> entry : attrMap.entrySet())
            this.getRequest().setAttribute(entry.getKey(), entry.getValue());
        return this;
    }
    /**
     * Returns the value of a request parameter as a String, or null if the parameter does not exist.
     * <p>
     * You should only use this method when you are sure the parameter has only one value. If the 
     * parameter might have more than one value, use getParaValues(java.lang.String). 
     * <p>
     * If you use this method with a multivalued parameter, the value returned is equal to the first 
     * value in the array returned by getParameterValues.
     * @param name a String specifying the name of the parameter
     * @return a String representing the single value of the parameter
     */
    public String getPara(String name) {
        return this.getRequest().getParameter(name);
    }
    /**
     * Returns the value of a request parameter as a String, or default value if the parameter does not exist.
     * @param name a String specifying the name of the parameter
     * @param defaultValue a String value be returned when the value of parameter is null
     * @return a String representing the single value of the parameter
     */
    public String getPara(String name, String defaultValue) {
        String result = this.getRequest().getParameter(name);
        return result != null && !"".equals(result) ? result : defaultValue;
    }
    /**
     * Returns the values of the request parameters as a Map.
     * @return a Map contains all the parameters name and value
     */
    public Map<String, String[]> getParaMap() {
        return this.getRequest().getParameterMap();
    }
    /**
     * Returns an Enumeration of String objects containing the names of the parameters
     * contained in this request. If the request has no parameters, the method returns
     * an empty Enumeration.
     * @return an Enumeration of String objects, each String containing the name of 
     *          a request parameter; or an empty Enumeration if the request has no parameters
     */
    public Enumeration<String> getParaNames() {
        return this.getRequest().getParameterNames();
    }
    /**
     * Returns an array of String objects containing all of the values the given request 
     * parameter has, or null if the parameter does not exist. If the parameter has a 
     * single value, the array has a length of 1.
     * @param name a String containing the name of the parameter whose value is requested
     * @return an array of String objects containing the parameter's values
     */
    public String[] getParaValues(String name) {
        return this.getRequest().getParameterValues(name);
    }
    /**
     * Returns an array of Integer objects containing all of the values the given request 
     * parameter has, or null if the parameter does not exist. If the parameter has a 
     * single value, the array has a length of 1.
     * @param name a String containing the name of the parameter whose value is requested
     * @return an array of Integer objects containing the parameter's values
     */
    public Integer[] getParaValuesToInt(String name) {
        String[] values = this.getRequest().getParameterValues(name);
        if (values == null)
            return null;
        Integer[] result = new Integer[values.length];
        for (int i = 0; i < result.length; i++)
            result[i] = Integer.parseInt(values[i]);
        return result;
    }
    /**
     * Returns an Enumeration containing the names of the attributes available to this request.
     * This method returns an empty Enumeration if the request has no attributes available to it. 
     * @return an Enumeration of strings containing the names of the request's attributes
     */
    public Enumeration<String> getAttrNames() {
        return this.getRequest().getAttributeNames();
    }
    /**
     * Returns the value of the named attribute as an Object, or null if no attribute of the given name exists.
     * @param name a String specifying the name of the attribute
     * @return an Object containing the value of the attribute, or null if the attribute does not exist
     */
    public <T> T getAttr(String name) {
        return (T) this.getRequest().getAttribute(name);
    }
    /**
     * Returns the value of the named attribute as an Object, or null if no attribute of the given name exists.
     * @param name a String specifying the name of the attribute
     * @return an String Object containing the value of the attribute, or null if the attribute does not exist
     */
    public String getAttrForStr(String name) {
        return (String) this.getRequest().getAttribute(name);
    }
    /**
     * Returns the value of the named attribute as an Object, or null if no attribute of the given name exists.
     * @param name a String specifying the name of the attribute
     * @return an Integer Object containing the value of the attribute, or null if the attribute does not exist
     */
    public Integer getAttrForInt(String name) {
        return (Integer) this.getRequest().getAttribute(name);
    }
    public Integer toInt(String value, Integer defaultValue) {
        if (value == null)
            return defaultValue;
        if (value.startsWith("N") || value.startsWith("n"))
            return -Integer.parseInt(value.substring(1));
        return Integer.parseInt(value);
    }
    /**
     * Returns the value of a request parameter and convert to Integer.
     * @param name a String specifying the name of the parameter
     * @return a Integer representing the single value of the parameter
     */
    public Integer getParaToInt(String name) {
        return toInt(this.getRequest().getParameter(name), null);
    }
    /**
     * Returns the value of a request parameter and convert to Integer with a default value if it is null.
     * @param name a String specifying the name of the parameter
     * @return a Integer representing the single value of the parameter
     */
    public Integer getParaToInt(String name, Integer defaultValue) {
        return toInt(this.getRequest().getParameter(name), defaultValue);
    }
    public Long toLong(String value, Long defaultValue) {
        if (value == null)
            return defaultValue;
        if (value.startsWith("N") || value.startsWith("n"))
            return -Long.parseLong(value.substring(1));
        return Long.parseLong(value);
    }
    /**
     * Returns the value of a request parameter and convert to Long.
     * @param name a String specifying the name of the parameter
     * @return a Integer representing the single value of the parameter
     */
    public Long getParaToLong(String name) {
        return toLong(this.getRequest().getParameter(name), null);
    }
    /**
     * Returns the value of a request parameter and convert to Long with a default value if it is null.
     * @param name a String specifying the name of the parameter
     * @return a Integer representing the single value of the parameter
     */
    public Long getParaToLong(String name, Long defaultValue) {
        return toLong(this.getRequest().getParameter(name), defaultValue);
    }
    /**
     * Returns the value of a request parameter and convert to Boolean.
     * @param name a String specifying the name of the parameter
     * @return false if the value of the parameter is "false" or "0", true if it is "true" or "1", null if parameter is not exists
     */
    public Boolean getParaToBoolean(String name) {
        String result = this.getRequest().getParameter(name);
        if (result != null) {
            result = result.trim().toLowerCase();
            if (result.equals("1") || result.equals("true"))
                return Boolean.TRUE;
            else if (result.equals("0") || result.equals("false"))
                return Boolean.FALSE;
            // return Boolean.FALSE;    // if use this, delete 2 lines code under
        }
        return null;
    }
    /**
     * Returns the value of a request parameter and convert to Boolean with a default value if it is null.
     * @param name a String specifying the name of the parameter
     * @return false if the value of the parameter is "false" or "0", true if it is "true" or "1", default value if it is null
     */
    public Boolean getParaToBoolean(String name, Boolean defaultValue) {
        Boolean result = getParaToBoolean(name);
        return result != null ? result : defaultValue;
    }
    /**
     * Return a Object from session.
     * @param key a String specifying the key of the Object stored in session
     */
    public <T> T getSessionAttr(String key) {
        HttpSession session = this.getRequest().getSession(false);
        return session != null ? (T) session.getAttribute(key) : null;
    }
    /**
     * Store Object to session.
     * @param key a String specifying the key of the Object stored in session
     * @param value a Object specifying the value stored in session
     */
    public AbstractController setSessionAttr(String key, Object value) {
        this.getRequest().getSession(true).setAttribute(key, value);
        return this;
    }
    /**
     * Remove Object in session.
     * @param key a String specifying the key of the Object stored in session
     */
    public AbstractController removeSessionAttr(String key) {
        HttpSession session = this.getRequest().getSession(false);
        if (session != null)
            session.removeAttribute(key);
        return this;
    }
    /** Get cookie value by cookie name. */
    public String getCookie(String name, String defaultValue) {
        Cookie cookie = getCookieObject(name);
        return cookie != null ? cookie.getValue() : defaultValue;
    }
    /** Get cookie value by cookie name. */
    public String getCookie(String name) {
        return getCookie(name, null);
    }
    /** Get cookie value by cookie name and convert to Integer. */
    public Integer getCookieToInt(String name) {
        String result = getCookie(name);
        return result != null ? Integer.parseInt(result) : null;
    }
    /** Get cookie value by cookie name and convert to Integer. */
    public Integer getCookieToInt(String name, Integer defaultValue) {
        String result = getCookie(name);
        return result != null ? Integer.parseInt(result) : defaultValue;
    }
    /** Get cookie value by cookie name and convert to Long. */
    public Long getCookieToLong(String name) {
        String result = getCookie(name);
        return result != null ? Long.parseLong(result) : null;
    }
    /** Get cookie value by cookie name and convert to Long. */
    public Long getCookieToLong(String name, Long defaultValue) {
        String result = getCookie(name);
        return result != null ? Long.parseLong(result) : defaultValue;
    }
    /** Get cookie object by cookie name. */
    public Cookie getCookieObject(String name) {
        Cookie[] cookies = this.getRequest().getCookies();
        if (cookies != null)
            for (Cookie cookie : cookies)
                if (cookie.getName().equals(name))
                    return cookie;
        return null;
    }
    /** Get all cookie objects. */
    public Cookie[] getCookieObjects() {
        Cookie[] result = this.getRequest().getCookies();
        return result != null ? result : new Cookie[0];
    }
    /** Set Cookie to response. */
    public AbstractController setCookie(Cookie cookie) {
        this.getResponse().addCookie(cookie);
        return this;
    }
    /**
     * Set Cookie to response.
     * @param name cookie name
     * @param value cookie value
     * @param maxAgeInSeconds -1: clear cookie when close browser. 0: clear cookie immediately.  n>0 : max age in n seconds.
     * @param path see Cookie.setPath(String)
     */
    public AbstractController setCookie(String name, String value, int maxAgeInSeconds, String path) {
        setCookie(name, value, maxAgeInSeconds, path, null);
        return this;
    }
    /**
     * Set Cookie to response.
     * @param name cookie name
     * @param value cookie value
     * @param maxAgeInSeconds -1: clear cookie when close browser. 0: clear cookie immediately.  n>0 : max age in n seconds.
     * @param path see Cookie.setPath(String)
     * @param domain the domain name within which this cookie is visible; form is according to RFC 2109
     */
    public AbstractController setCookie(String name, String value, int maxAgeInSeconds, String path, String domain) {
        Cookie cookie = new Cookie(name, value);
        if (domain != null)
            cookie.setDomain(domain);
        cookie.setMaxAge(maxAgeInSeconds);
        cookie.setPath(path);
        this.getResponse().addCookie(cookie);
        return this;
    }
    /** Set Cookie with path = "/". */
    public AbstractController setCookie(String name, String value, int maxAgeInSeconds) {
        setCookie(name, value, maxAgeInSeconds, "/", null);
        return this;
    }
    /** Remove Cookie with path = "/". */
    public AbstractController removeCookie(String name) {
        setCookie(name, null, 0, "/", null);
        return this;
    }
    /** Remove Cookie. */
    public AbstractController removeCookie(String name, String path) {
        setCookie(name, null, 0, path, null);
        return this;
    }
    /** Remove Cookie. */
    public AbstractController removeCookie(String name, String path, String domain) {
        setCookie(name, null, 0, path, domain);
        return this;
    }
    /** Get model from AppContext. */
    protected Object getModel(Class<?> modelClass) {
        return this.getAppContext().getInstance(modelClass);
    }
    /** Get model from AppContext. */
    protected Object getModel(String modelName) {
        return this.getAppContext().getBean(modelName);
    }
    // --------
    //    private MultipartRequest multipartRequest;
    //    /** Get upload file from multipart request. */
    //    public List<UploadFile> getFiles(String saveDirectory, Integer maxPostSize, String encoding) {
    //        if (multipartRequest == null) {
    //            multipartRequest = new MultipartRequest(request, saveDirectory, maxPostSize, encoding);
    //            request = multipartRequest;
    //        }
    //        return multipartRequest.getFiles();
    //    }
    //    public UploadFile getFile(String parameterName, String saveDirectory, Integer maxPostSize, String encoding) {
    //        getFiles(saveDirectory, maxPostSize, encoding);
    //        return getFile(parameterName);
    //    }
    //    public List<UploadFile> getFiles(String saveDirectory, int maxPostSize) {
    //        if (multipartRequest == null) {
    //            multipartRequest = new MultipartRequest(request, saveDirectory, maxPostSize);
    //            request = multipartRequest;
    //        }
    //        return multipartRequest.getFiles();
    //    }
    //    public UploadFile getFile(String parameterName, String saveDirectory, int maxPostSize) {
    //        getFiles(saveDirectory, maxPostSize);
    //        return getFile(parameterName);
    //    }
    //    public List<UploadFile> getFiles(String saveDirectory) {
    //        if (multipartRequest == null) {
    //            multipartRequest = new MultipartRequest(request, saveDirectory);
    //            request = multipartRequest;
    //        }
    //        return multipartRequest.getFiles();
    //    }
    //    public UploadFile getFile(String parameterName, String saveDirectory) {
    //        getFiles(saveDirectory);
    //        return getFile(parameterName);
    //    }
    //    public List<UploadFile> getFiles() {
    //        if (multipartRequest == null) {
    //            multipartRequest = new MultipartRequest(request);
    //            request = multipartRequest;
    //        }
    //        return multipartRequest.getFiles();
    //    }
    //    public UploadFile getFile() {
    //        List<UploadFile> uploadFiles = getFiles();
    //        return uploadFiles.size() > 0 ? uploadFiles.get(0) : null;
    //    }
    //    public UploadFile getFile(String parameterName) {
    //        List<UploadFile> uploadFiles = getFiles();
    //        for (UploadFile uploadFile : uploadFiles) {
    //            if (uploadFile.getParameterName().equals(parameterName)) {
    //                return uploadFile;
    //            }
    //        }
    //        return null;
    //    }
}