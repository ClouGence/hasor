package org.noe.platform.modules.freemarker.support;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import org.noe.platform.Noe;
import org.noe.platform.util.convert.ConverterUtils;
import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateModelException;
/**
 * ±Í«©∂‘œÛ°£
 * @version : 2012-5-13
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
public class InternalMethodObject implements TemplateMethodModel {
    private Method fmMethodType = null;
    private Object target       = null;
    //
    public InternalMethodObject(Method fmMethodType, Object target) {
        Noe.assertIsNotNull(target, "method Object is null.");
        this.fmMethodType = fmMethodType;
        this.target = target;
    }
    @Override
    public Object exec(List arguments) throws TemplateModelException {
        Class<?>[] paramTypes = fmMethodType.getParameterTypes();
        Object[] paramObjects = new Object[paramTypes.length];
        //
        for (int i = 0; i < paramTypes.length; i++) {
            if (i > arguments.size())
                paramObjects[i] = null;
            else
                paramObjects[i] = ConverterUtils.convert(paramTypes[i], arguments.get(i));
        }
        try {
            return this.fmMethodType.invoke(this.target, paramObjects);
        } catch (InvocationTargetException e) {
            Throwable ee = e.getCause();
            TemplateModelException tme = new TemplateModelException(ee instanceof Exception ? (Exception) ee : e);
            tme.initCause(e.getCause());
            throw tme;
        } catch (Exception e) {
            throw new TemplateModelException(e);
        }
    }
}