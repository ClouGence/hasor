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
package net.hasor.core.classcode.aop;
import net.hasor.core.classcode.ASMEngineTools;
import net.hasor.core.classcode.AbstractClassConfig;
import net.hasor.utils.asm.ClassVisitor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 *
 * @version : 2014年9月7日
 * @author 赵永春(zyc@hasor.net)
 */
public class AopClassConfig extends AbstractClassConfig {
    private List<InnerAopInterceptorDefine> aopList = null; //Aop
    //
    /**创建{@link AopClassConfig}类型对象。 */
    public AopClassConfig() {
        super(DefaultSuperClass);
    }
    /**创建{@link AopClassConfig}类型对象。 */
    public AopClassConfig(Class<?> superClass) {
        super(superClass);
    }
    /**创建{@link AopClassConfig}类型对象。 */
    public AopClassConfig(Class<?> superClass, ClassLoader parentLoader) {
        super(superClass, parentLoader);
    }
    //
    protected String initClassName() {
        return this.getSuperClass().getName() + "$A_" + index();
    }
    @Override
    protected ClassVisitor buildClassVisitor(ClassVisitor parentVisitor) {
        return new AopClassAdapter(parentVisitor, this);
    }
    ;
    //
    /**是否包含改变*/
    public boolean hasChange() {
        return this.aopList != null && !this.aopList.isEmpty();
    }
    //
    /**添加Aop拦截器。*/
    public void addAopInterceptors(AopMatcher aopMatcher, AopInterceptor... aopInterceptor) {
        for (AopInterceptor aop : aopInterceptor) {
            this.addAopInterceptor(aopMatcher, aop);
        }
    }
    /**添加Aop拦截器。*/
    public void addAopInterceptor(AopInterceptor aopInterceptor) {
        this.addAopInterceptor(new AopMatcher() {
            public boolean matcher(Method target) {
                return true;
            }
        }, aopInterceptor);
    }
    /**添加Aop拦截器。*/
    public void addAopInterceptor(AopMatcher aopMatcher, AopInterceptor aopInterceptor) {
        if (this.aopList == null) {
            this.aopList = new ArrayList<InnerAopInterceptorDefine>();
        }
        this.aopList.add(new InnerAopInterceptorDefine(aopMatcher, aopInterceptor));
    }
    //
    //
    //bytecode called.
    private Map<String, Method>           $methodMapping   = null;
    private Map<String, AopInterceptor[]> $finalAopMapping = new HashMap<String, AopInterceptor[]>();
    //
    private void initMapping() {
        if (this.$methodMapping != null) {
            return;
        }
        this.$methodMapping = new HashMap<String, Method>();
        Method[] methodSet = this.getSuperClass().getMethods();
        for (Method tMethod : methodSet) {
            String tmDesc = ASMEngineTools.toAsmFullDesc(tMethod);
            this.$methodMapping.put(tmDesc, tMethod);
        }
    }
    //
    AopInterceptor[] findInterceptor(String tmDesc) {
        AopInterceptor[] aopArrays = this.$finalAopMapping.get(tmDesc);
        if (aopArrays == null) {
            //
            this.initMapping();
            List<AopInterceptor> aopList = new ArrayList<AopInterceptor>();
            //
            Method targetMethod = this.$methodMapping.get(tmDesc);
            if (targetMethod != null && this.aopList != null) {
                for (InnerAopInterceptorDefine inner : this.aopList) {
                    if (inner.matcher(targetMethod) == true) {
                        aopList.add(inner);
                    }
                }
            }
            //
            aopArrays = aopList.toArray(new AopInterceptor[aopList.size()]);
            this.$finalAopMapping.put(tmDesc, aopArrays);
        }
        return aopArrays;
    }
}