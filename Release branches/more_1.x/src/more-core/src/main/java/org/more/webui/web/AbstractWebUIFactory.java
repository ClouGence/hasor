package org.more.webui.web;
import org.more.webui.context.FacesConfig;
import org.more.webui.context.FacesContextFactory;
import org.more.webui.lifestyle.Lifecycle;
import org.more.webui.lifestyle.phase.ApplyRequestValue_Phase;
import org.more.webui.lifestyle.phase.InitView_Phase;
import org.more.webui.lifestyle.phase.InvokeApplication_Phase;
import org.more.webui.lifestyle.phase.Render_Phase;
import org.more.webui.lifestyle.phase.RestoreView_Phase;
import org.more.webui.lifestyle.phase.UpdateModules_Phase;
import org.more.webui.lifestyle.phase.Validation_Phase;
/**
 * 
 * @version : 2012-6-27
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class AbstractWebUIFactory {
    /**创建生命周期对象*/
    public Lifecycle createLifestyle(FacesConfig config) {
        /*创建生命周期对象*/
        Lifecycle lifestyle = new Lifecycle() {};
        {
            //第1阶段，用于初始化视图中的组件模型树。
            lifestyle.addPhase(new InitView_Phase());
            //第2阶段，重塑UI组件状态。
            lifestyle.addPhase(new RestoreView_Phase());
            //第3阶段，将请求参数中要求灌入的属性值灌入到属性上。
            lifestyle.addPhase(new ApplyRequestValue_Phase());
            //第4阶段，对组件模型中的数据进行验证。
            lifestyle.addPhase(new Validation_Phase());
            //第5阶段，将组件模型中的值设置到映射的bean中。
            lifestyle.addPhase(new UpdateModules_Phase());
            //第6阶段，处理请求消息。诸如Command，Click事件。等动作。
            lifestyle.addPhase(new InvokeApplication_Phase());
            //第7阶段，将执行完的UI信息渲染到客户机中。
            lifestyle.addPhase(new Render_Phase());
        }
        return lifestyle;
    }
    public FacesContextFactory createFacesContextFactory() {
        return new DefaultFacesContext();
    }
}
///*添加生命周期监听器*/
//Set<Class<?>> classSet = config.getClassSet(UIPhase.class);
//for (Class<?> type : classSet)
//    if (PhaseListener.class.isAssignableFrom(type) == false)
//        throw new ClassCastException(type + " to PhaseListener");
//    else
//        lifestyle.addPhaseListener((PhaseListener) AppUtil.getObj(type));