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
package org.more.submit;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.more.core.global.Global;
import org.more.util.attribute.AttBase;
import org.more.util.config.AttributeConfigBridge;
import org.more.util.config.Config;
/**
 * submit利用build模式创建{@link SubmitService}。
 * @version : 2011-7-15
 * @author 赵永春 (zyc@byshell.org)
 */
public class SubmitBuild extends AttBase<Object> {
    //========================================================================================Field
    private static final long                     serialVersionUID = 2922698361958221493L;
    private Object                                context          = null;
    private String                                defaultNS        = null;
    private List<ActionContextBuilder>            config_acb       = new ArrayList<ActionContextBuilder>();
    private Map<String, ResultProcess<Result<?>>> config_res       = new HashMap<String, ResultProcess<Result<?>>>();
    private Global                                config           = null;
    public static final String                    ConfigPath       = "META-INF/resource/submit/config.propertys";
    //==========================================================================================Job
    public SubmitBuild() throws IOException {
        this.config = Global.createForFile(ConfigPath);
    };
    public SubmitBuild(Object context) throws IOException {
        this();
        this.context = context;
    };
    /**将config的参数添加到SubmitBuild的环境中，config参数中如果指定了Context则会的替换构造方法传入的Context.*/
    public void setConfig(Config<?> config) {
        Enumeration<String> e = config.getInitParameterNames();
        while (e.hasMoreElements()) {
            String name = e.nextElement();
            this.setAttribute(name, config.getInitParameter(name));
        }
        this.context = config.getContext();
    };
    public void setContext(Object context) {
        this.context = context;
    };
    /**调用生成器生成SubmitContext对象，生成的SubmitContext的对象可以需要通过getResult方法获取。*/
    public SubmitService build() throws Throwable {
        //1.创建SubmitContext实例。        
        SubmitService service = null;
        Object serviceImplType = this.getAttribute(SubmitService.class.getName());
        if (serviceImplType != null)
            service = (SubmitService) Thread.currentThread().getContextClassLoader().loadClass(serviceImplType.toString()).newInstance();
        else {
            String serviceImpl = (String) this.config.getString("ServiceImpl");
            if (serviceImpl == null || serviceImpl.equals(""))
                serviceImpl = "org.more.submit.impl.DefaultSubmitService";
            service = (SubmitService) Thread.currentThread().getContextClassLoader().loadClass(serviceImpl).newInstance();
        }
        //2.注册ActionContext
        Config<?> config = new AttributeConfigBridge(this, this.context);
        for (ActionContextBuilder builder : this.config_acb) {
            builder.init(config);
            service.regeditNameSpace(builder.getPrefix(), builder.builder());
        };
        //4.设置默认命名空间
        service.changeDefaultNameSpace(defaultNS);
        //5.装配初始化ResultProcess
        for (String key : this.config_res.keySet())
            service.addResult(key, this.config_res.get(key));
        return service;
    };
    /**设置默认命名空间*/
    public void setDefaultNameSpace(String defaultNS) {
        this.defaultNS = defaultNS;
    };
    /**添加{@link ActionContextBuilder}*/
    public void addActionContexBuilder(ActionContextBuilder acb) {
        if (acb != null)
            this.config_acb.add(acb);
    };
    public ResultProcess<Result<?>> addResult(String match, String process) {
        ResultProcess<Result<?>> rp = new Propxy_ResultProcess(process);
        this.config_res.put(match, rp);
        return rp;
    };
    public ResultProcess<Result<?>> addResult(String match, ResultProcess<Result<?>> process) {
        this.config_res.put(match, process);
        return process;
    };
};
class Propxy_ResultProcess implements ResultProcess<Result<?>> {
    private String                   process       = null;
    private ResultProcess<Result<?>> processObject = null;
    private HashMap<String, String>  configParam   = new HashMap<String, String>();
    public Propxy_ResultProcess(String process) {
        this.process = process;
    };
    public Object invoke(ActionStack onStack, Result<?> res) throws Throwable {
        if (this.processObject == null) {
            this.processObject = (ResultProcess) Thread.currentThread().getContextClassLoader().loadClass(process).newInstance();
            for (String key : this.configParam.keySet())
                this.processObject.addParam(key, this.configParam.get(key));
        }
        return this.processObject.invoke(onStack, res);
    };
    public void addParam(String key, String value) {
        this.configParam.put(key, value);
    };
};