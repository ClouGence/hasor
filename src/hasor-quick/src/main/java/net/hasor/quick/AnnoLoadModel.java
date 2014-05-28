package net.hasor.quick;
import java.util.Set;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.core.Module;
import net.hasor.core.ModuleInfo;
import org.more.util.StringUtils;
/**
 * 
 * @author yongchun.zyc
 *
 */
public class AnnoLoadModel {
    /**装载模块*/
    public static void loadModule(AppContext appContext) {
        //1.扫描classpath包
        Set<Class<?>> initHookSet = appContext.findClass(AnnoModule.class);
        if (Hasor.isInfoLogger()) {
            StringBuffer sb = new StringBuffer();
            for (Class<?> e : initHookSet)
                sb.append("\n  " + e.getName());
            String outData = (sb.length() == 0 ? "nothing." : sb.toString());
            Hasor.logInfo("find Module : " + outData);
        }
        //2.过滤未实现HasorModule接口的类
        for (Class<?> modClass : initHookSet) {
            if (!Module.class.isAssignableFrom(modClass)) {
                Hasor.logWarn("not implemented net.hasor.core.Module :%s", modClass);
                continue;/*错误*/
            }
            /*Hasor 模块*/
            Module modObject = createModule(modClass);
            ModuleInfo moduleInfo = appContext.addModule(modObject);
            //
            AnnoModule modAnno = modClass.getAnnotation(AnnoModule.class);
            String dispName = StringUtils.isBlank(modAnno.displayName()) ? modClass.getSimpleName() : modAnno.displayName();
            String description = StringUtils.isBlank(modAnno.description()) ? modClass.getName() : modAnno.description();
            moduleInfo.setDisplayName(dispName);
            moduleInfo.setDescription(description);
        }
    }
    private static <T> T createModule(Class<?> listenerClass) {
        try {
            return (T) listenerClass.newInstance();
        } catch (Exception e) {
            Hasor.logError("create %s an error!%s", listenerClass, e);
            return null;
        }
    }
}
