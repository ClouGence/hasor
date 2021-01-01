package net.example.hasor.web;
import io.swagger.models.Operation;
import io.swagger.models.Path;
import io.swagger.models.Swagger;
import io.swagger.util.Json;
import net.example.hasor.web.annotation.Any;
import net.example.hasor.web.annotation.MappingTo;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedHashMap;

@Component
@MappingTo("/interface/api-docs")
public class Hello {
    @Any
    public void execute(HttpServletResponse response) throws IOException {
        Swagger swagger = new Swagger();
        swagger.setBasePath("/127.0.0.1");
        //
        Operation operation = new Operation();
        Path apiPath = new Path();
        apiPath.setPost(operation);
        //        apiPath.setPost();
        swagger.setPaths(new LinkedHashMap<String, Path>() {{
            put("/aaa", apiPath);
        }});
        //
        //
        String asString = Json.pretty().writeValueAsString(swagger);
        PrintWriter writer = response.getWriter();
        writer.write(asString);
        writer.flush();
    }
}
