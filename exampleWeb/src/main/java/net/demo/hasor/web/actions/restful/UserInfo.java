package net.demo.hasor.web.actions.restful;
import net.demo.hasor.daos.UserDao;
import net.demo.hasor.web.forms.UserForm;
import net.hasor.core.Inject;
import net.hasor.restful.RenderData;
import net.hasor.restful.api.*;
/**
 * Created by zhaoyongchun on 16/10/2.
 */
@MappingTo("/restful/{userID}/info.json")
public class UserInfo {
    @Inject
    public UserDao userDao;
    @Get
    public void info(@PathParam("userID") long userID, RenderData renderData) {
    }
    @Post
    public void update(@PathParam("userID") long userID, @Params() UserForm userForm, RenderData renderData) {
        //
    }
}