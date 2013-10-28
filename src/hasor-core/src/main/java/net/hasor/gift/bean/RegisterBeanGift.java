package net.hasor.gift.bean;
import java.util.List;
import net.hasor.Hasor;
import net.hasor.core.ApiBinder;
import net.hasor.core.XmlNode;
import net.hasor.core.gift.Gift;
import net.hasor.core.gift.GiftFace;
import org.more.convert.ConverterUtils;
import org.more.util.StringUtils;
import com.google.inject.binder.LinkedBindingBuilder;
/**
 * 
 * @version : 2013-10-28
 * @author 赵永春(zyc@hasor.net)
 */
@Gift
public class RegisterBeanGift implements GiftFace {
    public void loadGift(ApiBinder apiBinder) {
        //
        XmlNode[] xmlProp = apiBinder.getEnvironment().getSettings().getXmlPropertyArray("beans");
        for (XmlNode item : xmlProp) {
            List<XmlNode> xmlList = item.getChildren("bean");
            for (XmlNode e : xmlList) {
                String beanName = e.getAttribute("name");
                String beanType = e.getAttribute("class");
                if (StringUtils.isBlank(beanName)) {
                    Hasor.warning("missing Bean name %s", beanType);
                    continue;
                }
                Class<?> clazz = this.loadClass(beanType);
                if (clazz == null)
                    continue;
                /*将BeanInfo绑定到Guice身上，在正式使用时利用findBindingsByType方法将其找回来。*/
                LinkedBindingBuilder<?> beanBuilder = apiBinder.newBean(beanName).bindType(clazz);
                //
                String singleMark = e.getAttribute("singleton");
                if (!StringUtils.isBlank(singleMark)) {
                    boolean singleton = (Boolean) ConverterUtils.convert(Boolean.class, singleMark);
                    if (singleton) {
                        beanBuilder.asEagerSingleton();
                    }
                }
                Hasor.info("RegisterBean %s bind %s", beanName, beanType);
            }
        }
    }
    private Class<?> loadClass(String beanType) {
        try {
            return Thread.currentThread().getContextClassLoader().loadClass(beanType);
        } catch (ClassNotFoundException e) {
            Hasor.error("%s", e);
            return null;
        }
    }
}