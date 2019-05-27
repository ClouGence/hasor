package net.hasor.web.invoker.params;
import net.hasor.web.annotation.MappingTo;
import net.hasor.web.annotation.ParameterGroup;
import net.hasor.web.annotation.Post;

import java.util.Map;
//
@MappingTo("/bean_param.do")
public class ParamsCallAction {
    //
    @Post
    public Map<String, Object> execute(@ParameterGroup() ParamBean paramBean) {
        return paramBean.buildParams();
    }
}
