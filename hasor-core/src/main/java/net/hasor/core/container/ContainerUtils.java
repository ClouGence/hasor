package net.hasor.core.container;
import net.hasor.core.*;
import net.hasor.core.aop.AsmTools;
import net.hasor.core.info.AbstractBindInfoProviderAdapter;
import net.hasor.core.info.DefaultBindInfoProviderAdapter;
import net.hasor.utils.BeanUtils;
import net.hasor.utils.StringUtils;
import net.hasor.utils.asm.AnnotationVisitor;
import net.hasor.utils.asm.ClassReader;
import net.hasor.utils.asm.ClassVisitor;
import net.hasor.utils.asm.Opcodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
//
class ContainerUtils {
    protected static Logger logger = LoggerFactory.getLogger(ContainerUtils.class);
    //
    /** 查找类的默认初始化方法*/
    public static Method findInitMethod(Class<?> targetBeanType, BindInfo<?> bindInfo) {
        Method initMethod = null;
        //a.注解形式（注解优先）
        if (targetBeanType != null) {
            List<Method> methodList = BeanUtils.findALLMethods(targetBeanType);
            if (methodList != null) {
                for (Method method : methodList) {
                    Init initAnno1 = method.getAnnotation(Init.class);
                    PostConstruct initAnno2 = method.getAnnotation(PostConstruct.class);
                    if (initAnno1 == null && initAnno2 == null) {
                        continue;
                    }
                    if (Modifier.isPublic(method.getModifiers())) {
                        initMethod = method;
                        break;
                    }
                }
            }
        }
        //b.可能存在的配置。
        if (initMethod == null && bindInfo instanceof DefaultBindInfoProviderAdapter) {
            DefaultBindInfoProviderAdapter<?> defBinder = (DefaultBindInfoProviderAdapter<?>) bindInfo;
            initMethod = defBinder.getInitMethod(targetBeanType);
        }
        return initMethod;
    }
    //
    /** 查找类的默认销毁方法*/
    public static Method findDestroyMethod(Class<?> targetBeanType, BindInfo<?> bindInfo) {
        Method destroyMethod = null;
        //a.注解形式（注解优先）
        if (targetBeanType != null) {
            List<Method> methodList = BeanUtils.findALLMethods(targetBeanType);
            if (methodList != null) {
                for (Method method : methodList) {
                    Destroy destroyAnno1 = method.getAnnotation(Destroy.class);
                    PreDestroy destroyAnno2 = method.getAnnotation(PreDestroy.class);
                    if (destroyAnno1 == null && destroyAnno2 == null) {
                        continue;
                    }
                    if (Modifier.isPublic(method.getModifiers())) {
                        destroyMethod = method;
                        break;
                    }
                }
            }
        }
        //b.可能存在的配置。
        if (destroyMethod == null && bindInfo instanceof DefaultBindInfoProviderAdapter) {
            DefaultBindInfoProviderAdapter<?> defBinder = (DefaultBindInfoProviderAdapter<?>) bindInfo;
            destroyMethod = defBinder.getDestroyMethod(targetBeanType);
        }
        return destroyMethod;
    }
    //
    /** 查找实现类 */
    public static <T> Class<T> findImplClass(final Class<?> notSureType) {
        Class<?> tmpType = notSureType;
        ImplBy implBy = null;
        do {
            implBy = tmpType.getAnnotation(ImplBy.class);
            if (implBy != null) {
                tmpType = implBy.value();
            }
            if (tmpType == notSureType) {
                break;
            }
        } while (implBy != null);
        return (Class<T>) tmpType;
    }
    //
    /** 检测是否为单例（注解优先）*/
    public static boolean testSingleton(Class<?> targetType, BindInfo<?> bindInfo, Settings settings) {
        Prototype prototype = targetType.getAnnotation(Prototype.class);
        Singleton singleton = targetType.getAnnotation(Singleton.class);
        SingletonMode singletonMode = null;
        if (bindInfo instanceof AbstractBindInfoProviderAdapter) {
            singletonMode = ((AbstractBindInfoProviderAdapter) bindInfo).getSingletonMode();
        }
        //
        if (SingletonMode.Singleton == singletonMode) {
            return true;
        } else if (SingletonMode.Prototype == singletonMode) {
            return false;
        } else if (SingletonMode.Clear == singletonMode) {
            prototype = null;
            singleton = null;
        } else {
            targetType = findImplClass(targetType);
        }
        //
        if (prototype != null && singleton != null) {
            throw new IllegalArgumentException(targetType + " , @Prototype and @Singleton appears only one.");
        }
        //
        boolean isSingleton = (singleton != null);
        if (!isSingleton && prototype == null) {
            isSingleton = settings.getBoolean("hasor.default.asEagerSingleton", isSingleton);
        }
        return isSingleton;
    }
    //
    /** 检测是否要忽略装配Aop */
    public static <T> boolean testAopIgnore(Class<T> targetType, ClassLoader rootLoader) {
        Boolean ignore = testAopIgnore(targetType, true);
        if (ignore != null) {
            return ignore;
        }
        ignore = testAopIgnore(rootLoader, targetType.getPackage().getName(), true);
        return (ignore != null) ? ignore : false;
    }
    private static Boolean testAopIgnore(Class<?> targetType, boolean isRootClass) {
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
        return null;
    }
    private static Boolean testAopIgnore(ClassLoader rootLoader, String packageName, boolean isRootPakcage) {
        if (packageName == null) {
            return null;
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
                    classReader.accept(new ClassVisitor(Opcodes.ASM7) {
                        @Override
                        public AnnotationVisitor visitAnnotation(final String desc, final boolean visible) {
                            if (!AsmTools.toAsmType(AopIgnore.class).equals(desc)) {
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
        return null;
    }
}
