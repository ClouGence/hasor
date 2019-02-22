package net.hasor.web.invoker.call;
import net.hasor.web.annotation.Get;
import net.hasor.web.annotation.MappingTo;
import net.hasor.web.annotation.Post;
//
@MappingTo("/sync.do")
public class SyncCallAction {
    private static boolean staticCall = false;
    public static void resetInit() {
        staticCall = false;
    }
    public static boolean isStaticCall() {
        return staticCall;
    }
    //
    @Post
    public Object execute() {
        staticCall = true;
        return "CALL";
    }
    @Get
    public Object doThrow() {
        staticCall = true;
        throw new NullPointerException("CALL");
    }
}
