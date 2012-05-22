package org.more.webui.web;
/**
 * 常量表
 * @version : 2012-5-21
 * @author 赵永春 (zyc@byshell.org)
 */
public enum PostFormEnum {
    /**发生事件的组建*/
    PostForm_TargetParamKey("WebUI_PF_Target"),
    //    /**发生的事件*/
    //    PostForm_EventParamKey,
    /**执行渲染的类型*/
    PostForm_RenderParamKey("WebUI_PF_Render"),
    /**回传状态的状态数据*/
    PostForm_StateDataParamKey("WebUI_PF_State"), ;
    //
    //
    //
    //
    private String value = null;
    PostFormEnum(String value) {
        this.value = value;
    }
    public String value() {
        return this.value;
    }
}
