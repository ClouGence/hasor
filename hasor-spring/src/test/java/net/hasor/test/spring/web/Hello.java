package net.hasor.test.spring.web;
import net.hasor.web.annotation.Any;
import net.hasor.web.annotation.MappingTo;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Component
@MappingTo("/hello")
public class Hello {
    @Any
    public void execute(HttpServletResponse response) throws IOException {
        PrintWriter writer = response.getWriter();
        writer.write("Hello Word.");
        writer.flush();
    }
}
