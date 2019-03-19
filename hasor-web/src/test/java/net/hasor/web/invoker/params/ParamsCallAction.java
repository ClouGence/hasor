package net.hasor.web.invoker.params;
import net.hasor.web.annotation.MappingTo;
import net.hasor.web.annotation.ParameterForm;
import net.hasor.web.annotation.PathParameter;
import net.hasor.web.annotation.Post;

import java.util.Map;
//
@MappingTo("/bean_param.do")
public class ParamsCallAction {
    //
    @Post
    public Map<String, Object> execute(@ParameterForm() ParamBean paramBean) {
        return paramBean.buildParams();
    }
}
