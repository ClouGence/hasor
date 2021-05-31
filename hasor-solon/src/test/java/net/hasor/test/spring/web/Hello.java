package net.hasor.test.spring.web;
import net.hasor.web.annotation.Any;
import net.hasor.web.annotation.MappingTo;
import net.hasor.web.render.RenderType;
import org.noear.solon.annotation.Component;

import java.util.HashMap;

@Component
@MappingTo("/hello")
public class Hello {

    @Any
    @RenderType("json")
    public Object execute() {
        return new HashMap<String, String>() {{
            put("spring", "1");
            put("message", "HelloWord");
        }};
    }
}
