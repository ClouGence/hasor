package net.example.hasor.web;
import net.hasor.web.WebController;
import net.hasor.web.annotation.Any;
import net.hasor.web.annotation.MappingTo;
import net.hasor.web.render.RenderType;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Component
@MappingTo("/interface/exec")
public class DataQLExec extends WebController {
    @Any
    @RenderType("json")
    public Object execute(HttpServletResponse response) throws IOException {
        Map<String, String[]> paraMap = getParaMap();
        //        Swagger swagger = new Swagger();
        //        swagger.setBasePath("/127.0.0.1");
        //        //
        //        Operation operation = new Operation();
        //        Path apiPath = new Path();
        //        apiPath.setPost(operation);
        //        //        apiPath.setPost();
        //        swagger.setPaths(new LinkedHashMap<String, Path>() {{
        //            put("/aaa", apiPath);
        //        }});
        //        //
        //        //
        //        String asString = Json.pretty().writeValueAsString(swagger);
        //        PrintWriter writer = response.getWriter();
        //        writer.write(asString);
        //        writer.flush();
        return paraMap;
    }
}
