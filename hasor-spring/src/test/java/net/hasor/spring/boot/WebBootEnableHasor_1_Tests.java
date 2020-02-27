package net.hasor.spring.boot;
import net.hasor.core.AppContext;
import net.hasor.test.spring.mod1.*;
import org.junit.Before;
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

@WebAppConfiguration
@SpringBootTest(classes = WebBootEnableHasor_1.class)
public class WebBootEnableHasor_1_Tests {
    @Autowired
    private AppContext            appContext;
    @Autowired
    private WebApplicationContext applicationContext;
    private MockMvc               mockMvc;

    @Before
    public void before() {
        mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext).build();
        TestModuleA.reset();
        TestModuleB.reset();
        TestModuleC.reset();
        TestModuleD.reset();
        TestDimModuleA.reset();
        TestDimModuleB.reset();
    }

    @Test
    public void login() throws Exception {
        before();
        String contentAsString = mockMvc.perform(MockMvcRequestBuilders//
                .post("/hello")//
                .contentType(MediaType.APPLICATION_JSON)//
                .content("{}"))//
                .andDo(mvcResult -> {
                    MockMvcResultHandlers.print();
                }).andExpect(MockMvcResultMatchers.status().isOk())//
                .andReturn()//
                .getResponse()//
                .getContentAsString();
        System.out.println(contentAsString);
    }
}
