package net.hasor.spring.boot;
import net.hasor.core.AppContext;
import net.hasor.core.Environment;
import net.hasor.core.Settings;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = BootShareHasor_1.class)
public class BootShareHasor_1_Test {
    @Autowired
    private AppContext appContext;

    @Test
    public void contextLoads() {
        //
        Environment environment = appContext.getEnvironment();
        Settings settings = environment.getSettings();
        //
        assert "HelloWord".equals(environment.getVariable("msg"));
        assert "HelloWord".equals(settings.getString("msg"));
    }
}
