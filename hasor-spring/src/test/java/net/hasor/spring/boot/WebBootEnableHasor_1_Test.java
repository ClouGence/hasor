/*
 * Copyright 2008-2009 the original author or authors.
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
package net.hasor.spring.boot;
import com.alibaba.fastjson.JSONObject;
import net.hasor.core.AppContext;
import net.hasor.web.binder.OneConfig;
import net.hasor.web.startup.RuntimeFilter;
import net.hasor.web.startup.RuntimeListener;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

@WebAppConfiguration
@SpringBootTest(classes = WebBootEnableHasor_1.class)
public class WebBootEnableHasor_1_Test {
    @Autowired
    private AppContext            appContext;
    @Autowired
    private WebApplicationContext applicationContext;
    private MockMvc               mockMvc;

    public MockMvc mockMvc() throws Exception {
        if (mockMvc != null) {
            return mockMvc;
        }
        RuntimeListener runtimeListener = new RuntimeListener(this.appContext);
        RuntimeFilter runtimeFilter = new RuntimeFilter(this.appContext);
        //
        mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext)//
                .addFilter(runtimeFilter, "/*")//
                .build();
        //
        ServletContext servletContext = mockMvc.getDispatcherServlet().getServletContext();
        runtimeListener.contextInitialized(new ServletContextEvent(servletContext));
        runtimeFilter.init(new OneConfig("abc", () -> appContext));
        //
        return mockMvc;
    }

    @Test
    public void login() throws Exception {
        String contentAsString = mockMvc().perform(MockMvcRequestBuilders//
                .post("/hello")//
                .contentType(MediaType.APPLICATION_JSON)//
                .content("{}"))//
                .andDo(mvcResult -> {
                    MockMvcResultHandlers.print();
                }).andExpect(MockMvcResultMatchers.status().isOk())//
                .andReturn()//
                .getResponse()//
                .getContentAsString();
        //
        JSONObject jsonObject = JSONObject.parseObject(contentAsString);
        assert jsonObject.getBoolean("spring");
        assert jsonObject.getString("message").equals("HelloWord");
    }
}
