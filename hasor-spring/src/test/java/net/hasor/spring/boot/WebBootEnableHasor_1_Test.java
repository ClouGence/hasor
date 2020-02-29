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
