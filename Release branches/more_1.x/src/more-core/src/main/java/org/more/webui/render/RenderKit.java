package org.more.webui.render;
import java.util.HashMap;
import java.util.Map;
import org.more.webui.tag.TagObject;
/**
 * 
 * @version : 2012-5-22
 * @author 赵永春 (zyc@byshell.org)
 */
public class RenderKit {
    private Map<String, Class<? extends Render>> renderMapping = new HashMap<String, Class<? extends Render>>();
    private Map<String, Object>                  tagMap        = new HashMap<String, Object>();
    //
    /**获取已经注册的标签对象集合*/
    public Map<String, Object> getTags() {
        return this.tagMap;
    }
    public Render getRender(String tagName) {
        try {
            Class<? extends Render> renderType = this.renderMapping.get(tagName);
            return renderType.newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException("标签错误： ‘" + tagName + "’不能被创建.", e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("标签错误：在创建 ‘" + tagName + "’期间遇到一个错误的访问权限.", e);
        }
    }
    /**注册标签类，只能对已经注册render的组建进行注册*/
    public void addTag(String tagName, TagObject tagObject) {
        if (this.renderMapping.containsKey(tagName) == true)
            if (tagObject != null)
                tagMap.put(tagName, tagObject);
            else
                throw new NullPointerException("TagObject类型参数不能为空。");
    }
    public void addRender(String tagName, Class<? extends Render> renderClass) {
        this.renderMapping.put(tagName, renderClass);
        this.tagMap.put(tagName, new TagObject());//输出默认标签
    }
}