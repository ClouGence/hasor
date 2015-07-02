/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.hasor.rsf.center.core.freemarker;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
/**
 * 
 * @version : 2015年7月1日
 * @author 赵永春(zyc@hasor.net)
 */
public class FreemarkerView implements Filter {
    TemplateLoader loader = null;
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // TODO Auto-generated method stub
    }
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        // TODO Auto-generated method stub
    }
    @Override
    public void destroy() {
        // TODO Auto-generated method stub
    }
    private Configuration cfg = null;
    public final Configuration getFreemarker() {
        if (this.cfg == null) {
            this.cfg = new Configuration(Configuration.VERSION_2_3_22);
            cfg.setDefaultEncoding("");
            cfg.setOutputEncoding("");
            cfg.setLocalizedLookup(true);
            //
            TemplateLoader[] loaders = null;
            if (cfg.getTemplateLoader() != null) {
                loaders = new TemplateLoader[2];
                loaders[1] = cfg.getTemplateLoader();
            } else
                loaders = new TemplateLoader[1];
            loaders[0] = this.loader;
            cfg.setTemplateLoader(new MultiTemplateLoader(loaders));
        }
        return this.cfg;
    }
}