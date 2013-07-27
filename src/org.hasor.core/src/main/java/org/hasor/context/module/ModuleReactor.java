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
package org.hasor.context.module;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.hasor.Hasor;
import org.hasor.context.Dependency;
import org.hasor.context.ModuleInfo;
import org.more.util.StringUtils;
/**
 * 模块反应堆，该类的目的是对模块进行排序。
 * @version : 2013-7-26
 * @author 赵永春 (zyc@byshell.org)
 */
public class ModuleReactor {
    private ModuleInfo[] modules = null;
    public ModuleReactor(ModuleInfo[] modules) {
        this.modules = modules;
    }
    //
    private String getTreeInfo(List<ReactorModuleInfoElement> stackArray) {
        StringBuilder sb = new StringBuilder("");
        int size = String.valueOf(stackArray.size() - 1).length();
        for (int i = 0; i < stackArray.size(); i++) {
            ReactorModuleInfoElement element = stackArray.get(i);
            sb.append(element.getMark());
            sb.append(String.format("%0" + size + "d", i));
            sb.append('.');
            sb.append("-->");
            sb.append(StringUtils.fixedString(element.getDepth() * 2, ' '));
            sb.append(element.getInfo().getDisplayName());
            sb.append(" (");
            sb.append(element.getInfo().getModuleObject().getClass());
            sb.append(")\n");
        }
        return sb.toString();
    }
    /**检查模块的依赖是否正确。*/
    public void checkModule(ModuleInfo info) {
        List<ModuleInfo> checkArray = new ArrayList<ModuleInfo>();
        List<ReactorModuleInfoElement> stackArray = new ArrayList<ReactorModuleInfoElement>();
        try {
            this.checkModuleInDependency(1, info, checkArray, stackArray);
        } catch (RuntimeException e) {
            String treeInfo = getTreeInfo(stackArray);
            Hasor.error("%s module depend on the loop.\n%s", info.getDisplayName(), treeInfo);
            throw e;
        }
    }
    private void checkModuleInDependency(int depth, ModuleInfo info, List<ModuleInfo> checkArray, List<ReactorModuleInfoElement> stackList) {
        if (checkArray.contains(info) == true) {
            stackList.add(new ReactorModuleInfoElement(depth, info, "[Error] "));
            throw new RuntimeException(info.getDisplayName() + " modules depend on the loop.");
        } else
            stackList.add(new ReactorModuleInfoElement(depth, info, "[OK]    "));
        //
        checkArray.add(info);
        for (Dependency dep : info.getDependency()) {
            ModuleInfo targetInfo = dep.getModuleInfo();
            checkModuleInDependency(depth + 1, targetInfo, checkArray, stackList);
        }
    }
    /**按照明确的模块启动顺序对模块进行排序，该方法同时还进行了循环检测。*/
    public ModuleInfo[] process() {
        /*1.进行循环检查，确保模块的稳定依赖*/
        Hasor.info("begin cycle check...mods %s.", new Object[] { this.modules });
        for (ModuleInfo info : this.modules)
            this.checkModule(info);
        /*2.构建ModuleInfo对象中的依赖树*/
        Hasor.info("build dependency tree for ModuleInfo.");
        for (ModuleInfo info : this.modules) {
            for (Dependency dep : info.getDependency())
                this.updateModuleDependency(dep);
        }
        /*3.确定启动顺序*/
        ArrayList<ReactorModuleInfoElement> tree = new ArrayList<ReactorModuleInfoElement>();
        this.getStartArray(tree);
        String treeInfo = getTreeInfo(tree);
        Hasor.info("startup sequence.\n%s", treeInfo);
        //
        ModuleInfo[] finalData = new ModuleInfo[tree.size()];
        for (int i = 0; i < tree.size(); i++)
            finalData[i] = tree.get(i).getInfo();
        return finalData;
    }
    /*确定启动顺序*/
    private void getStartArray(List<ReactorModuleInfoElement> infoList) {
        ArrayList<ModuleInfo> allModule = new ArrayList<ModuleInfo>(Arrays.asList(this.modules));
        /*去掉所有被依赖的项目*/
        for (ModuleInfo info : this.modules) {
            for (Dependency dep : info.getDependency())
                allModule.remove(dep.getModuleInfo());
        }
        for (ModuleInfo info : allModule)
            this.addStartArray(1, info, infoList);
    }
    private void addStartArray(int depth, ModuleInfo info, List<ReactorModuleInfoElement> infoList) {
        infoList.add(new ReactorModuleInfoElement(depth, info));
        for (Dependency dep : info.getDependency())
            addStartArray(depth + 1, dep.getModuleInfo(), infoList);
    }
    //
    /*构建ModuleInfo对象中的依赖树*/
    private void updateModuleDependency(Dependency dep) {
        DependencyBean depBean = (DependencyBean) dep;
        List<Dependency> refDep = depBean.getModuleInfo().getDependency();
        depBean.updateDependency(new ArrayList<Dependency>(refDep));
        for (Dependency e : refDep)
            this.updateModuleDependency(e);
    }
}
class ReactorModuleInfoElement {
    private String     mark  = "";
    private int        depth = 0;
    private ModuleInfo info  = null;
    public ReactorModuleInfoElement(int depth, ModuleInfo info) {
        this.depth = depth;
        this.info = info;
    }
    public ReactorModuleInfoElement(int depth, ModuleInfo info, String mark) {
        this.depth = depth;
        this.info = info;
        this.mark = mark;
    }
    public int getDepth() {
        return depth;
    }
    public ModuleInfo getInfo() {
        return info;
    }
    public String getMark() {
        return mark;
    }
    @Override
    public int hashCode() {
        return info.hashCode();
    }
    @Override
    public boolean equals(Object obj) {
        return info.equals(obj);
    }
}