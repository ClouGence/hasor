package net.hasor.web.invoker.beans;
import net.hasor.web.annotation.Get;
import net.hasor.web.annotation.HttpMethod;
import net.hasor.web.annotation.Post;
public class HttpsTestAction {
    //
    @Get
    public void execute1() {
    }
    //
    @Post
    public void execute2() {
    }
    //
    @HttpMethod({ "ADD", HttpMethod.DELETE })
    public void execute3() {
    }
}
