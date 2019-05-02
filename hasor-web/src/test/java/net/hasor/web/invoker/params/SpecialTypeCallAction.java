package net.hasor.web.invoker.params;
import net.hasor.web.Invoker;
import net.hasor.web.annotation.MappingTo;
import net.hasor.web.annotation.Post;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
//
@MappingTo("/special_param.do")
public class SpecialTypeCallAction {
    //
    @Post
    public Map<String, Object> execute(HttpServletRequest request, HttpServletResponse response, HttpSession session, Invoker invoker, List listData) {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("request", request);
        dataMap.put("response", response);
        dataMap.put("session", session);
        dataMap.put("invoker", invoker);
        dataMap.put("listData", listData);
        return dataMap;
    }
}
