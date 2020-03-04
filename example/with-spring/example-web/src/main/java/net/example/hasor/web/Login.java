package net.example.hasor.web;
import net.hasor.utils.StringUtils;
import net.hasor.web.annotation.Get;
import net.hasor.web.annotation.MappingTo;
import net.hasor.web.annotation.Post;
import net.hasor.web.annotation.RequestParameter;
import net.hasor.web.objects.ForwardTo;

@MappingTo("/login.do")
public class Login {
    @Post
    @Get
    @ForwardTo
    public String execute(//
            @RequestParameter("username") String username, //
            @RequestParameter("password") String password//
    ) {
        if (StringUtils.isNotBlank(username) && StringUtils.equals(username, password)) {
            return "/succeed.htm";
        } else {
            return "/failed.htm";
        }
    }
}
