package org.more.webui.components.page;
import org.more.webui.support.UICom;
/**
 * 分页组建：上一页
 * @version : 2012-5-15
 * @author 赵永春 (zyc@byshell.org)
 */
@UICom(tagName = "ui_pPrev")
public class PrevPageCom extends AbstractItemCom {
    @Override
    public String getComponentType() {
        return "ui_pPrev";
    }
}