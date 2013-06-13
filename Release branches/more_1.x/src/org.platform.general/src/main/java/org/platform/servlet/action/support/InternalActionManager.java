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
package org.platform.servlet.action.support;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.platform.context.AppContext;
import org.platform.servlet.action.ResultProcess;
import com.google.inject.Binding;
import com.google.inject.TypeLiteral;
/**
 * 
 * @version : 2013-5-11
 * @author 赵永春 (zyc@byshell.org)
 */
class InternalActionManager implements ActionManager {
    /*经过倒序排序之后的命名空间管理器*/
    private InternalActionNameSpace[] nameSpaceManager     = null;
    private ResultProcessManager      resultProcessManager = null;
    @Override
    public void initManager(AppContext appContext) {
        this.resultProcessManager = appContext.getInstance(ResultProcessManager.class);
        this.nameSpaceManager = collectNameSpace(appContext);
        for (InternalActionNameSpace space : this.nameSpaceManager)
            space.initNameSpace(appContext);
    }
    private InternalActionNameSpace[] collectNameSpace(AppContext appContext) {
        ArrayList<InternalActionNameSpace> spaceDefinitionList = new ArrayList<InternalActionNameSpace>();
        TypeLiteral<InternalActionNameSpace> SPACE_DEFS = TypeLiteral.get(InternalActionNameSpace.class);
        for (Binding<InternalActionNameSpace> entry : appContext.getGuice().findBindingsByType(SPACE_DEFS))
            spaceDefinitionList.add(entry.getProvider().get());
        return spaceDefinitionList.toArray(new InternalActionNameSpace[spaceDefinitionList.size()]);
    }
    @Override
    public void destroyManager(AppContext appContext) {
        for (InternalActionNameSpace space : nameSpaceManager)
            space.destroyNameSpace(appContext);
    }
    @Override
    public ActionNameSpace getNameSpace(String actionNS) {
        ActionNameSpace findSpace = null;
        for (ActionNameSpace space : nameSpaceManager) {
            if (actionNS.startsWith(space.getNameSpace()) == true) {
                findSpace = space;
                break;
            }
        }
        return findSpace;
    }
    @Override
    public ActionNameSpace[] getNameSpaceList() {
        return this.nameSpaceManager;
    }
    @Override
    public void processResult(Method targetMethod, Object result, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Annotation[] annoArray = targetMethod.getAnnotations();
        for (Annotation anno : annoArray) {
            ResultProcess resProcess = this.resultProcessManager.getResultProcess(anno);
            if (resProcess != null) {
                resProcess.process(request, response, anno, result);
                return;
            }
        }
        annoArray = targetMethod.getDeclaringClass().getAnnotations();
        for (Annotation anno : annoArray) {
            ResultProcess resProcess = this.resultProcessManager.getResultProcess(anno);
            if (resProcess != null) {
                resProcess.process(request, response, anno, result);
                return;
            }
        }
    }
}