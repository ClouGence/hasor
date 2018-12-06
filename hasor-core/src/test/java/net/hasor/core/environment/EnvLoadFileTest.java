package net.hasor.core.environment;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
public class EnvLoadFileTest {
    @Test
    public void envTest0() throws IOException {
        System.setProperty("CONFLICTS_VAR", "with app");
        StandardEnvironment env = null;
        //
        env = new StandardEnvironment(null, "/net_hasor_core_settings/simple-config.xml");
        assert "my my my".equals(env.evalString("%MY_ENV%"));
        assert "with app".equals(env.evalString("%CONFLICTS_VAR%"));
        //
        //
        Map<String, String> envMap = new HashMap<String, String>();
        envMap.put("CONFLICTS_VAR", "with env");
        env = new StandardEnvironment(null, "/net_hasor_core_settings/simple-config.xml", envMap, Thread.currentThread().getContextClassLoader());
        assert "with env".equals(env.evalString("%CONFLICTS_VAR%"));
    }
}