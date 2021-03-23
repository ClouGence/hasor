package net.hasor.core.container;
import net.hasor.core.Type;
import net.hasor.core.*;
import net.hasor.core.aop.AsmTools;
import net.hasor.core.info.DefaultBindInfoProviderAdapter;
import net.hasor.utils.BeanUtils;
import net.hasor.utils.ExceptionUtils;
import net.hasor.utils.StringUtils;
import net.hasor.utils.asm.AnnotationVisitor;
import net.hasor.utils.asm.ClassReader;
import net.hasor.utils.asm.ClassVisitor;
import net.hasor.utils.asm.Opcodes;
import net.hasor.utils.convert.ConverterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Named;
import javax.inject.Qualifier;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class ContainerUtils {
    protected static Logger logger = LoggerFactory.getLogger(ContainerUtils.class);

    /** 查找类的默认初始化方法(注解优先) */
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

    /**
     * 查找 Inject 信息
     * @see net.hasor.core.ID
     * @see javax.inject.Named
     * @see javax.inject.Qualifier
     */
    public static Annotation findInject(boolean injectDefault, Annotation[] annotations) {
        if (annotations == null || annotations.length == 0) {
            return null;
        }
        //
        boolean injectBoolean = injectDefault;
        Annotation qualifier = null;
        //
        for (Annotation anno : annotations) {
            // .如果遇到 net.hasor.core.Inject 那么信息是完整的不需要在查看其它注解
            if (anno instanceof net.hasor.core.Inject) {
                injectBoolean = true;
                if (Type.ByName == ((net.hasor.core.Inject) anno).byType()) {
                    qualifier = new NamedImpl(((net.hasor.core.Inject) anno).value());
                }
                if (Type.ByID == ((net.hasor.core.Inject) anno).byType()) {
                    qualifier = new IDImpl(((net.hasor.core.Inject) anno).value());
                }
                break;
            }
            // .遇到InjectSettings那么直接返回
            if (anno instanceof InjectSettings) {
                injectBoolean = true;
                qualifier = anno;
                break;
            }
            //
            // .JSR-330，javax.inject.Inject 单独存在，或者加一个标记了Qualifier的注解
            if (anno instanceof javax.inject.Inject) {
                injectBoolean = true;
                qualifier = (qualifier == null) ? anno : qualifier;
                continue;
            }
            if (anno.annotationType().getAnnotation(Qualifier.class) != null) {
                qualifier = anno;
            }
            if (qualifier != null && injectBoolean) {
                break;// JSR-330 所需的内容已经全部收集全了
            }
        }
        //
        if (injectBoolean) {
            if (qualifier instanceof ID || qualifier instanceof Named || qualifier instanceof InjectSettings) {
                return qualifier;
            }
            if (qualifier instanceof javax.inject.Inject) {
                return new NamedImpl("");
            }
            return new NamedImpl(qualifier.annotationType().getName());
        }
        return null;
    }

    public static boolean isInjectConstructor(Constructor<?> constructor) {
        boolean constructorBy = constructor.getDeclaredAnnotation(ConstructorBy.class) != null;      // Hasor 规范
        boolean javaxInjectBy = constructor.getDeclaredAnnotation(javax.inject.Inject.class) != null;// JSR-330
        return constructorBy || javaxInjectBy;
    }

    public static Object injSettings(AppContext appContext, InjectSettings injectSettings, Class<?> toType) {
        if (injectSettings == null || StringUtils.isBlank(injectSettings.value())) {
            return BeanUtils.getDefaultValue(toType);
        }
        String useNS = injectSettings.ns();
        String defaultVal = injectSettings.defaultValue();
        String settingVar = injectSettings.value();
        //
        String settingValue = null;
        if (settingVar.startsWith("${") && settingVar.endsWith("}")) {
            settingVar = settingVar.substring(2, settingVar.length() - 1);
            settingValue = appContext.getEnvironment().evalString("%" + settingVar + "%");
            if (StringUtils.isBlank(settingValue)) {
                settingValue = defaultVal;
            }
        } else {
            if (StringUtils.isBlank(defaultVal)) {
                defaultVal = null;// 行为保持和 Convert 一致
            }
            Settings settings = appContext.getEnvironment().getSettings();
            if (StringUtils.isNotBlank(useNS)) {
                settings = settings.getSettings(useNS);
            }
            settingValue = settings.getString(settingVar, defaultVal);
        }
        //
        if (settingValue == null && !toType.isPrimitive()) {
            return null; // TODO ConverterUtils.convert 并不能兼容这种场景：ConverterUtils.convert((String)null,java.lang.Byte.class); 注意是 byte 的包装类型
        } else {
            return ConverterUtils.convert(settingValue, toType);
        }
    }

    public static void invokeMethod(Object targetBean, Method initMethod) {
        //
        Class<?>[] paramArray = initMethod.getParameterTypes();
        Object[] paramObject = BeanUtils.getDefaultValue(paramArray);
        //
        try {
            try {
                initMethod.invoke(targetBean, paramObject);
            } catch (IllegalAccessException e) {
                initMethod.setAccessible(true);
                try {
                    initMethod.invoke(targetBean, paramObject);
                } catch (IllegalAccessException e1) {
                    logger.error(e1.getMessage(), e);
                }
            }
        } catch (InvocationTargetException e2) {
            throw ExceptionUtils.toRuntimeException(e2.getTargetException());
        }
    }

    public static void invokeField(Field field, Object targetBean, Object newValue) {
        Class<?> toType = field.getType();
        newValue = ConverterUtils.convert(toType, newValue);
        try {
            field.set(targetBean, newValue);
        } catch (IllegalAccessException e) {
            try {
                field.setAccessible(true);
                field.set(targetBean, newValue);
            } catch (IllegalAccessException e1) {
                logger.error(e1.getMessage(), e);
            }
        } catch (IllegalArgumentException e) {
            throw e;
        }
    }

    /** 检测是否要忽略装配Aop */
    public static <T> boolean testAopIgnore(Class<T> targetType, ClassLoader rootLoader) {
        Boolean ignore = testAopIgnore(targetType, true);
        if (ignore != null) {
            return ignore;
        }
        if (targetType.getPackage() != null) {
            ignore = testAopIgnore(rootLoader, targetType.getPackage().getName());
        }
        return (ignore != null) ? ignore : false;
    }

    private static Boolean testAopIgnore(Class<?> targetType, boolean isRootClass) {
        IgnoreProxy aopIgnore = targetType.getAnnotation(IgnoreProxy.class);
        if (aopIgnore != null) {
            // 1.被测试的类标记了@AopIgnore
            // 2.继承的父类中标记了AopIgnore 注解并且 遗传属性genetic 的值为 true。
            if (isRootClass || aopIgnore.propagate()) {
                return aopIgnore.ignore();
            }
        }
        Class<?> superclass = targetType.getSuperclass();
        if (superclass != null) {
            return testAopIgnore(superclass, false);
        }
        return null;
    }

    private static final String PROP_NAME_PROPAGATE = "propagate";
    private static final String PROP_NAME_IGNORE    = "ignore";

    private static Boolean testAopIgnore(ClassLoader rootLoader, String packageName) {
        if (packageName == null) {
            return null;
        }
        final AtomicBoolean isRootPakcage = new AtomicBoolean(true);
        packageName = packageName.replace(".", "/");
        final Map<String, Object> aopIgnoreInfo = new HashMap<>();
        aopIgnoreInfo.put(PROP_NAME_PROPAGATE, true);   // 注解默认值
        aopIgnoreInfo.put(PROP_NAME_IGNORE, true);      // 注解默认值
        //
        //
        //
        class AopIgnoreFinderVisitor extends AnnotationVisitor {
            private Map<String, Object> collectInfo = new HashMap<String, Object>() {{
                put(PROP_NAME_PROPAGATE, true);   // 注解默认值
                put(PROP_NAME_IGNORE, true);      // 注解默认值
            }};

            public AopIgnoreFinderVisitor(int api, AnnotationVisitor av) {
                super(api, av);
            }

            public void visit(String name, Object value) {
                collectInfo.put(name, value);
            }

            public void visitEnd() {
                boolean propagate = Boolean.parseBoolean(this.collectInfo.get(PROP_NAME_PROPAGATE).toString());
                if (!propagate && !isRootPakcage.get()) {
                    collectInfo.clear(); // 不传播，必须在非 root 情况下才会生效
                }
                //
                aopIgnoreInfo.putAll(collectInfo);
                super.visitEnd();
            }
        }
        for (; ; ) {
            InputStream asStream = rootLoader.getResourceAsStream(packageName + "/package-info.class");
            if (asStream != null) {
                try {
                    ClassReader classReader = new ClassReader(asStream);
                    classReader.accept(new ClassVisitor(Opcodes.ASM7) {
                        @Override
                        public AnnotationVisitor visitAnnotation(final String desc, final boolean visible) {
                            if (!AsmTools.toAsmType(IgnoreProxy.class).equals(desc)) {
                                return super.visitAnnotation(desc, visible);
                            }
                            return new AopIgnoreFinderVisitor(Opcodes.ASM7, super.visitAnnotation(desc, visible));
                        }
                    }, ClassReader.SKIP_CODE);
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
                //
                // 1.被测试的包标记了@AopIgnore
                // 2.包的父包中标记了AopIgnore 注解并且 遗传属性genetic 的值为 true。
                if (isRootPakcage.get() || Boolean.TRUE.equals(aopIgnoreInfo.get(PROP_NAME_PROPAGATE))) {
                    return Boolean.TRUE.equals(aopIgnoreInfo.get(PROP_NAME_IGNORE));
                }
            }
            if (packageName.indexOf('/') == -1) {
                break;
            }
            packageName = StringUtils.substringBeforeLast(packageName, "/");
            if (StringUtils.isBlank(packageName)) {
                break;
            }
            isRootPakcage.set(false);
        }
        return null;
    }
}
