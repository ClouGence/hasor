package net.hasor.test.spring.web;
import net.hasor.web.annotation.Any;
import net.hasor.web.annotation.MappingTo;
import net.hasor.web.annotation.Produces;
import net.hasor.web.render.RenderType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
@MappingTo("/hello")
public class Hello {
    @Autowired
    private ApplicationContext applicationContext;

    @Any
    @Produces("application/json")
    @RenderType("json")
    public Object execute() {
        return new HashMap<String, String>() {{
            put("spring", String.valueOf(applicationContext != null));
            put("message", "HelloWord");
        }};
    }
}
