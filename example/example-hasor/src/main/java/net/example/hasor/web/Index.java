package net.example.hasor.web;
import net.example.hasor.services.UserManager;
import net.hasor.core.Inject;
import net.hasor.core.InjectSettings;
import net.hasor.web.RenderInvoker;
import net.hasor.web.annotation.MappingTo;
/**
 * 首页打印，使用的数据库驱动名
 * @version : 2016年11月07日
 * @author 赵永春(zyc@hasor.net)
 */
@MappingTo("/index.htm")
public class Index {
    @Inject
    private UserManager userManager;
    @InjectSettings("${jdbc.driver}")  // <- 注入的配置来自于 “env.config” 或者环境变量
    private String      thisServerDriver;
    //
    /* 当访问“/index.htm”页面时，Hasor框架会寻找默认执行方法：execute，去执行 */
    public void execute(RenderInvoker invoker) {
        invoker.put("thisServerDriver", thisServerDriver);
    }
}