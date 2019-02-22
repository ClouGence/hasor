package net.hasor.web.invoker.call;
import net.hasor.web.annotation.Async;
import net.hasor.web.annotation.Get;
import net.hasor.web.annotation.MappingTo;
import net.hasor.web.annotation.Post;
//
@MappingTo("/async.do")
public class AsyncCallAction {
    private static boolean staticCall = false;
    public static void resetInit() {
        staticCall = false;
    }
    public static boolean isStaticCall() {
        return staticCall;
    }
    //
    @Post
    @Async
    public Object execute() {
        staticCall = true;
        return "CALL";
    }
    @Get
    @Async
    public Object doThrow() {
        staticCall = true;
        throw new NullPointerException("CALL");
    }
}
