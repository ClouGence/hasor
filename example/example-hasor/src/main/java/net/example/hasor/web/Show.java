package net.example.hasor.web;
import net.hasor.web.RenderInvoker;
import net.hasor.web.annotation.MappingTo;
/**
 * 首页打印，使用的数据库驱动名
 * @version : 2016年11月07日
 * @author 赵永春(zyc@hasor.net)
 */
@MappingTo("/show.jsp")
public class Show {
    public void execute(RenderInvoker invoker) {
        invoker.put("abc", "xxxxxx");
    }
}