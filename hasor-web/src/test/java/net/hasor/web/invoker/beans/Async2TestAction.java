package net.hasor.web.invoker.beans;
import net.hasor.web.annotation.Async;
import net.hasor.web.annotation.Post;
public class Async2TestAction {
    //
    @Post
    @Async
    public void execute() {
    }
}
