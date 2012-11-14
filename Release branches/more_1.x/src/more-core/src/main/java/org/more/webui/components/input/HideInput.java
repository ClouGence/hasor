package org.more.webui.components.input;
import org.more.webui.component.UIInput;
import org.more.webui.component.support.UICom;
import org.more.webui.render.inputs.HideInputRender;
/**
 * <b>作用</b>：生成一个隐藏表单域。
 * <br><b>组建类型</b>：ui_HideInput
 * <br><b>标签</b>：@ui_HideInput
 * <br><b>服务端事件</b>：无
 * <br><b>渲染器</b>：{@link HideInputRender}
 * @version : 2012-5-15
 * @author 赵永春 (zyc@byshell.org)
 */
@UICom(tagName = "ui_HideInput", renderType = HideInputRender.class)
public class HideInput extends UIInput {
    @Override
    public String getComponentType() {
        return "ui_HideInput";
    };
}