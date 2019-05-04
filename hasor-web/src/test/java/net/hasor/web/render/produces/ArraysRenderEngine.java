package net.hasor.web.render.produces;
import com.alibaba.fastjson.JSON;
import net.hasor.core.AppContext;
import net.hasor.utils.StringUtils;
import net.hasor.web.Invoker;
import net.hasor.web.RenderEngine;
import net.hasor.web.RenderInvoker;
import net.hasor.web.valid.ValidInvoker;

import java.io.IOException;
import java.io.Writer;
import java.util.*;
import java.util.stream.Collectors;
//
public class ArraysRenderEngine implements RenderEngine {
    private Set<String> templateSet = new HashSet<>();
    public ArraysRenderEngine(List<String> readLines) {
        this.templateSet.addAll(readLines.stream().filter(StringUtils::isNotBlank).collect(Collectors.toList()));
    }
    @Override
    public void initEngine(AppContext appContext) throws Throwable {
    }
    @Override
    public void process(RenderInvoker invoker, Writer writer) throws Throwable {
        //
        Map<String, Object> jsonData = new HashMap<>();
        invoker.keySet().stream().filter(s -> {                     //
            return !Invoker.ROOT_DATA_KEY.equalsIgnoreCase(s) &&    //
                    !ValidInvoker.VALID_DATA_KEY.equalsIgnoreCase(s) &&     //
                    !Invoker.REQUEST_KEY.equalsIgnoreCase(s) &&     //
                    !Invoker.RESPONSE_KEY.equalsIgnoreCase(s);      //
        }).peek(s -> {
            Object valueData = invoker.get(s);
            try {
                jsonData.put(s, JSON.parseObject((valueData == null) ? null : valueData.toString()));
            } catch (Exception e) {
                jsonData.put(s, valueData);
            }
        }).forEach(s -> {
            //
        });
        //
        jsonData.put("engine_renderTo", invoker.renderTo());
        jsonData.put("engine_viewType", invoker.viewType());
        writer.write(JSON.toJSONString(jsonData));
    }
    @Override
    public boolean exist(String template) throws IOException {
        return templateSet.contains(template);
    }
}