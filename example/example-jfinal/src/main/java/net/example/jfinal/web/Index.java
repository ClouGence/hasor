package net.example.jfinal.web;
import com.jfinal.core.Controller;
import net.example.jfinal.domain.UserDTO;
import net.example.jfinal.services.UserManager;
import net.hasor.core.Inject;
import net.hasor.core.InjectSettings;

import java.util.Date;
import java.util.List;
/**
 * 首页打印，使用的数据库驱动名
 * @version : 2016年11月07日
 * @author 赵永春(zyc@hasor.net)
 */
public class Index extends Controller {
    @Inject
    private UserManager userManager;
    @InjectSettings("${jdbc.driver}")  // <- 注入的配置来自于 “env.config” 或者环境变量
    private String      thisServerDriver;
    //
    //
    public void index() {
        //
        this.setAttr("thisServerDriver", thisServerDriver);
        render("index.htm");
    }
    //
    public void list() throws Exception {
        // .查询数据并放入 request 属性中
        List<UserDTO> userDOs = userManager.queryList();
        this.setAttr("userList", userDOs);
        this.setAttr("responseTime", new Date());
        render("user_list.htm");
    }
    //
    public void addUser() throws Exception {
        //
        UserDTO userParam = this.getModel(UserDTO.class);
        userParam.set("create_time", new Date());
        userParam.set("modify_time", new Date());
        userManager.addUser(userParam);
        //
        String contextPath = getRequest().getContextPath();
        getResponse().sendRedirect(contextPath + "/list");
    }
}