package net.hasor.web.render.produces;
import net.hasor.web.annotation.Get;
import net.hasor.web.annotation.MappingTo;
import net.hasor.web.annotation.Post;
import net.hasor.web.annotation.Produces;
//
@MappingTo("/abc.do")
@MappingTo("/def.do")
public class HtmlProduces {
    @Post
    @Produces("html")
    public void testProduces1() {
    }
    @Get
    @Produces("aabbcc")
    public void testProduces2() {
    }
}