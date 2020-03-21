package net.hasor.dataway.config;
import com.alibaba.fastjson.JSON;
import net.hasor.web.Invoker;
import net.hasor.web.render.RenderEngine;
import net.hasor.web.render.RenderInvoker;

import javax.inject.Singleton;
import java.io.Writer;

@Singleton
public class JsonRenderEngine implements RenderEngine {
    @Override
    public void process(RenderInvoker invoker, Writer writer) throws Throwable {
        writer.write(JSON.toJSONString(invoker.get(Invoker.RETURN_DATA_KEY)));
    }
}
