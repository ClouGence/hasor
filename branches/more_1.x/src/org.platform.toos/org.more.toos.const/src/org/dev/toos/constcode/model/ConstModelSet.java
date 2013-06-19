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
package org.dev.toos.constcode.model;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.dev.toos.internal.util.Message;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
/**
 * 用于管理每个项目模型下的总控类。
 * @version : 2013-2-2
 * @author 赵永春 (zyc@byshell.org)
 */
public class ConstModelSet {
    private static Map<String, ConstModel> modeBeanMap   = new LinkedHashMap<String, ConstModel>();
    private static List<ConstModel>        modeBeanList  = new ArrayList<ConstModel>();
    private static ConstModel              activateModel = null;
    //
    //
    //
    /**获取活动的模型。*/
    public static ConstModel getActivateModel() {
        return activateModel;
    }
    /**尝试激活某个配置文件，如果目标配置文件不存在则装载它*/
    public static ConstModel activateModel(String projectName) {
        if (modeBeanMap.containsKey(projectName) == false)
            return null;
        activateModel = modeBeanMap.get(projectName);
        return activateModel;
    }
    /**判断是否存在这个项目。*/
    public static boolean existConstModel(IJavaProject javaProject) {
        String projectName = javaProject.getElementName();
        return modeBeanMap.containsKey(projectName);
    }
    /**创建模型。 */
    public static ConstModel newConstModel(IJavaProject javaProject, IProgressMonitor monitor) throws CoreException {
        if (existConstModel(javaProject) == true) {
            ConstModel constModel = getConstModel(javaProject);
            constModel.refresh(monitor);//通知模型进行刷新。
            return constModel;
        }
        ConstModel modelBean = new ConstModel(javaProject);
        modelBean.initLoad(monitor);//执行初始化装载，该操作如果装载成功会将模型置为生效状态
        modeBeanMap.put(modelBean.getProjectName(), modelBean);
        modeBeanList.add(modelBean);
        return modelBean;
    }
    /**获取模型。*/
    public static ConstModel getConstModel(IJavaProject javaProject) {
        String projectName = javaProject.getElementName();
        return modeBeanMap.get(projectName);
    }
    /**重新载入工作空间中所有项目。*/
    public static void refresh(IProgressMonitor monitor) {
        activateModel = null;
        /* 1.扫描projects，并且载入xml
         *   1.1.新的载入请求，执行载入，并且标记为生效。
         *   1.2.已经存在的项目，将失效标记为生效。
         * 2.激活默认项目
         */
        //
        HashMap<String, ConstModel> oldModeMap = new HashMap<String, ConstModel>(modeBeanMap);
        Message.updateTask(monitor, "scanning projects...", 3, 1);
        //
        IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
        if (projects != null) {
            for (int i = 0; i < projects.length; i++) {
                IProject pro = projects[i];
                Message.updateTask(monitor, "scanning projects(“" + pro.getName() + "”)", projects.length, i);
                if (pro.isOpen() == false)
                    continue;
                if (pro.exists() == false)
                    continue;
                IJavaProject javaProject = JavaCore.create(pro);
                if (javaProject == null || javaProject.exists() == false)
                    continue;
                //转换为Java项目，并且查找core-codes.xml配置文件。
                try {
                    IJavaElement[] javaElements = javaProject.getChildren();
                    if (javaElements == null)
                        continue;
                    oldModeMap.remove(javaProject.getElementName());
                    if (existConstModel(javaProject) == true)
                        getConstModel(javaProject).refresh(monitor);//通知刷新项目
                    else
                        newConstModel(javaProject, monitor).refresh(monitor);//通知刷新项目
                } catch (Exception e) {
                    Message.errorInfo("Refresh Job", e);
                }
            }
        }
        //
        for (ConstModel constModel : getModeBeanList())
            if (constModel.getGroups().size() == 0)
                oldModeMap.put(constModel.getProjectName(), constModel);
        for (Entry<String, ConstModel> ent : oldModeMap.entrySet())
            ConstModelSet.removeModel(ent.getValue().getProject());
        //
        Message.updateTask(monitor, "activate current Project...", 3, 2);
        if (getModeBeanList().size() != 0) {
            String projectName = null;
            IProject currentProject = ResourcesPlugin.getWorkspace().getRoot().getProject();
            if (currentProject != null) {
                ConstModel currentModel = modeBeanMap.get(currentProject.getName());
                if (currentModel != null)
                    projectName = currentModel.getProjectName();
            }
            if (projectName == null)
                projectName = getModeBeanList().get(0).getProjectName();
            activateModel(projectName);
        }
        //
        Message.updateTask(monitor, "process callBack function...", 3, 3);
    }
    public static void removeModel(IJavaProject javaProject) {
        modeBeanList.remove(modeBeanMap.remove(javaProject.getElementName()));
    }
    /**获取已经转载的模型列表，返回的只读列表。*/
    public static List<ConstModel> getModeBeanList() {
        return Collections.unmodifiableList(modeBeanList);
    }
}