package org.platform.freemarker.support;
///*
// * Copyright 2008-2009 the original author or authors.
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package org.platform.freemarker.support;
//import javax.servlet.http.HttpServletRequest;
//import org.platform.context.AppContext;
//import org.platform.freemarker.FreemarkerManager;
//import freemarker.template.ObjectWrapper;
//import freemarker.template.TemplateHashModel;
//import freemarker.template.TemplateModel;
//import freemarker.template.TemplateModelException;
///**
// * 
// * @version : 2013-5-20
// * @author ’‘”¿¥∫ (zyc@byshell.org)
// */
//class TemplateRootMap implements TemplateHashModel {
//    private HttpServletRequest httpRequest       = null;
//    private AppContext         appContext        = null;
//    private FreemarkerManager  freemarkerManager = null;
//    public TemplateRootMap(HttpServletRequest httpRequest, AppContext appContext, FreemarkerManager freemarkerManager) {
//        this.httpRequest = httpRequest;
//        this.appContext = appContext;
//        this.freemarkerManager = freemarkerManager;
//    }
//    @Override
//    public TemplateModel get(String key) throws TemplateModelException {
//        boolean has = false;
//        Object returnData = null;
//        //
//        if (has == false)
//            returnData = appContext.getBean(key);
//        //
//        if (returnData != null) {
//            ObjectWrapper wrapper = freemarkerManager.getFreemarker().getObjectWrapper();
//            return wrapper != null ? wrapper.wrap(returnData) : ObjectWrapper.DEFAULT_WRAPPER.wrap(returnData);
//        } else
//            return null;
//    }
//    @Override
//    public boolean isEmpty() throws TemplateModelException {
//        boolean has = httpRequest.getParameterMap().isEmpty();
//        if (has == false)
//            has = httpRequest.getAttributeNames().hasMoreElements();
//        // TODO Auto-generated method stub
//        return has;
//    }
//}