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
package net.hasor.core.container;
import net.hasor.core.AppContext;
import net.hasor.core.classcode.ASMEngineTools;
import net.hasor.core.classcode.aop.AopClassConfig;
import net.hasor.core.classcode.aop.AopMatcher;
import net.hasor.core.info.AopBindInfoAdapter;
import net.hasor.utils.ExceptionUtils;
import net.hasor.utils.StringUtils;
import net.hasor.utils.asm.AnnotationVisitor;
import net.hasor.utils.asm.ClassReader;
import net.hasor.utils.asm.ClassVisitor;
import net.hasor.utils.asm.Opcodes;
import net.hasor.utils.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
/**
 * 负责根据Class或BindInfo创建BeanType。
 *  ** 不支持热部署，该类会造成类型无法被回收。
 * @version : 2015年6月26日
 * @author 赵永春 (zyc@hasor.net)
 */
class ClassEngine {
    private static Logger                                      logger         = LoggerFactory.getLogger(ClassEngine.class);
    private static ConcurrentHashMap<Class<?>, AopClassConfig> buildEngineMap = new ConcurrentHashMap<Class<?>, AopClassConfig>();
    public static Class<?> buildType(Class<?> targetType, ClassLoader rootLoader,//
            List<AopBindInfoAdapter> aopList, AppContext appContext) {
        //
        //        if (!AopClassConfig.isSupport(targetType)) {
        //            return targetType;
        //        }
        // .动态代理
        Class<?> newType = targetType;
        AopClassConfig engine = buildEngineMap.get(targetType);
        if (engine == null) {
            // .检查是否忽略Aop
            boolean aopIgnoreClass = testAopIgnore(targetType);
            boolean aopIgnorePackage = testAopIgnore(rootLoader, targetType.getPackage().getName());
            if (aopIgnorePackage || aopIgnoreClass) {
                aopList = Collections.emptyList();
            }
            //
            engine = new AopClassConfig(targetType, rootLoader);
            for (AopBindInfoAdapter aop : aopList) {
                if (!aop.getMatcherClass().matches(targetType)) {
                    continue;
                }
                AopMatcher aopMatcher = new ClassAopMatcher(aop.getMatcherMethod());
                engine.addAopInterceptor(aopMatcher, aop);
            }
            engine = buildEngineMap.putIfAbsent(targetType, engine);
            if (engine == null) {
                engine = buildEngineMap.get(targetType);
            }
            if ("true".equals(System.getProperty("net.hasor.core.container.classengine.debug")) && engine.hasChange()) {
                String fileName = engine.getClassName();
                String cacheDir = appContext.getEnvironment().evalString("%HASOR_TEMP_PATH%/debug/aopclasses");
                FileOutputStream fos = null;
                try {
                    File outFile = new File(cacheDir, fileName + ".class");
                    outFile.getParentFile().mkdirs();
                    fos = new FileOutputStream(outFile, false);
                    byte[] buildBytes = engine.buildBytes();
                    fos.write(buildBytes);
                    fos.flush();
                } catch (IOException e) {
                    logger.debug(e.getMessage(), e);
                } finally {
                    IOUtils.closeQuietly(fos);
                }
            }
        }
        if (engine.hasChange()) {
            try {
                newType = engine.toClass();
            } catch (Exception e) {
                throw ExceptionUtils.toRuntimeException(e);
            }
        } else {
            newType = engine.getSuperClass();
        }
        //
        return newType;
    }
    //
    //
    //
    private static boolean testAopIgnore(Class<?> targetType) {
        return testAopIgnore(targetType, true);
    }
    private static boolean testAopIgnore(ClassLoader rootLoader, String targetPackage) {
        return testAopIgnore(rootLoader, targetPackage, true);
    }
    private static boolean testAopIgnore(Class<?> targetType, boolean isRootClass) {
        AopIgnore aopIgnore = targetType.getAnnotation(AopIgnore.class);
        if (aopIgnore != null) {
            // 1.被测试的类标记了@AopIgnore
            // 2.继承的父类中标记了AopIgnore 注解并且 遗传属性genetic 的值为 true。
            if (isRootClass || aopIgnore.inherited()) {
                return aopIgnore.ignore();
            }
        }
        Class<?> superclass = targetType.getSuperclass();
        if (superclass != null) {
            return testAopIgnore(superclass, false);
        }
        return false;
    }
    private static boolean testAopIgnore(ClassLoader rootLoader, String packageName, boolean isRootPakcage) {
        if (packageName == null) {
            return false;
        }
        packageName = packageName.replace(".", "/");
        //
        final Map<String, Object> aopIgnoreInfo = new HashMap<String, Object>();
        aopIgnoreInfo.put("inherited", true);
        aopIgnoreInfo.put("ignore", true);
        class AopIgnoreFinderVisitor extends AnnotationVisitor {
            public AopIgnoreFinderVisitor(int api, AnnotationVisitor av) {
                super(api, av);
            }
            @Override
            public void visit(String name, Object value) {
                aopIgnoreInfo.put(name, value);
            }
        }
        //
        for (; ; ) {
            InputStream asStream = rootLoader.getResourceAsStream(packageName + "/package-info.class");
            if (asStream != null) {
                try {
                    ClassReader classReader = new ClassReader(asStream);
                    classReader.accept(new ClassVisitor(Opcodes.ASM4) {
                        @Override
                        public AnnotationVisitor visitAnnotation(final String desc, final boolean visible) {
                            if (!ASMEngineTools.toAsmType(AopIgnore.class).equals(desc)) {
                                return super.visitAnnotation(desc, visible);
                            }
                            return new AopIgnoreFinderVisitor(Opcodes.ASM4, super.visitAnnotation(desc, visible));
                        }
                    }, ClassReader.SKIP_CODE);
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
                //
                // 1.被测试的包标记了@AopIgnore
                // 2.包的父包中标记了AopIgnore 注解并且 遗传属性genetic 的值为 true。
                if (isRootPakcage || Boolean.TRUE.equals(aopIgnoreInfo.get("inherited"))) {
                    return Boolean.TRUE.equals(aopIgnoreInfo.get("ignore"));
                }
            }
            if (packageName.indexOf('/') == -1) {
                break;
            }
            packageName = StringUtils.substringBeforeLast(packageName, "/");
            if (StringUtils.isBlank(packageName)) {
                break;
            }
        }
        return false;
    }
}