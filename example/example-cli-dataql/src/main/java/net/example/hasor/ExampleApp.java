package net.example.hasor;
import net.example.hasor.config.ExampleModule;
import net.hasor.core.Hasor;
import net.hasor.spring.boot.EnableHasor;
import net.hasor.tconsole.ConsoleApiBinder;
import net.hasor.tconsole.ConsoleApiBinder.HostBuilder;
import net.hasor.tconsole.Tel;
import net.hasor.tconsole.TelModule;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableHasor
@SpringBootApplication
public class ExampleApp {
    public static void main(String[] args) {
        Hasor.create().build(new ExampleModule(), (TelModule) apiBinder -> {
            HostBuilder hostBuilder = apiBinder.tryCast(ConsoleApiBinder.class).asHostWithSTDO().answerExit();
            hostBuilder.preCommand(args).loadExecutor(apiBinder.findClass(Tel.class));
        }).joinSignal();
    }
}
