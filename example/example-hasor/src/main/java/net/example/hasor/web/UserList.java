package net.example.hasor.web;
import net.example.domain.domain.UserDO;
import net.example.hasor.services.UserManager;
import net.hasor.core.Inject;
import net.hasor.web.WebController;
import net.hasor.web.annotation.Get;
import net.hasor.web.annotation.MappingTo;

import java.util.Date;
import java.util.List;
/**
 * 查询用户列表
 * @version : 2016年11月07日
 * @author 赵永春(zyc@hasor.net)
 */
@MappingTo("/user_list.htm")
public class UserList extends WebController {
    @Inject
    private UserManager userManager;
    //
    /* 当http get 方式访问“/user_list.htm”页面时，执行该方法. */
    @Get
    public void list() throws Exception {
        // .查询数据并放入 request 属性中
        List<UserDO> userDOs = userManager.queryList();
        this.setAttr("userList", userDOs);
        this.setAttr("responseTime", new Date());
    }
}