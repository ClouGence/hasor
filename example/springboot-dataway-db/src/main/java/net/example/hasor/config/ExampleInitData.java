package net.example.hasor.config;
import net.hasor.utils.ResourcesUtils;
import net.hasor.utils.io.Charsets;
import net.hasor.utils.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.InputStream;

@Component
public class ExampleInitData {
    @Autowired
    private JdbcTemplate jdbcTemplate = null;

    @PostConstruct
    public void init() throws Throwable {
        InputStream infoStream = ResourcesUtils.getResourceAsStream("/META-INF/hasor-framework/h2/interface_info.sql");
        InputStream releaseStream = ResourcesUtils.getResourceAsStream("/META-INF/hasor-framework/h2/interface_release.sql");
        this.jdbcTemplate.execute(IOUtils.toString(infoStream, Charsets.UTF_8));
        this.jdbcTemplate.execute(IOUtils.toString(releaseStream, Charsets.UTF_8));
    }
}