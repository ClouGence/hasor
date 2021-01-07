package net.example.cli;
import net.hasor.core.DimModule;
import net.hasor.spring.SpringModule;
import net.hasor.tconsole.ConsoleApiBinder;
import net.hasor.tconsole.Tel;
import net.hasor.tconsole.TelModule;
import net.hasor.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Component;

import java.util.Set;

@DimModule
@Component
public class ExampleCliModule implements TelModule, SpringModule {
    @Autowired
    private ApplicationArguments applicationArguments;

    @Override
    public void loadModule(ConsoleApiBinder apiBinder) throws Throwable {
        Set<Class<?>> telSet = apiBinder.findClass(Tel.class, "net.example.cli.tels.*");
        String cmdLine = StringUtils.join(applicationArguments.getSourceArgs(), " ");
        //
        apiBinder.asHostWithSTDO()      //
                .answerExit()           //
                .preCommand(cmdLine)    //
                .loadExecutor(telSet, (s) -> true, springTypeSupplier(apiBinder));
    }
}
