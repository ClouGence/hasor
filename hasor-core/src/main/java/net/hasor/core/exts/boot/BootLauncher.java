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
package net.hasor.core.exts.boot;
import net.hasor.core.ApiBinder;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.core.Module;
import net.hasor.core.context.ContextShutdownListener;
import net.hasor.core.context.ContextStartListener;
import net.hasor.utils.ExceptionUtils;
import net.hasor.utils.ResourcesUtils;
import net.hasor.utils.StringUtils;
import net.hasor.utils.future.BasicFuture;
import net.hasor.utils.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.util.List;
/**
 * 启动器，使用方法 HasorLauncher.run(MyModule.class,args);
 * @version : 2018-08-04
 * @author 赵永春 (zyc@hasor.net)
 */
public final class BootLauncher implements Module, ContextStartListener, ContextShutdownListener {
    private static Logger              logger    = LoggerFactory.getLogger(BootLauncher.class);
    private static BasicFuture<Object> future    = new BasicFuture<Object>();
    static         String[]            mainArgs  = null;
    static         boolean             usingBoot = false;
    //
    private BootLauncher() {
    }
    public static void run(Class<? extends Module> launcherModuleType, String[] args) {
        // .打印 Hello
        try {
            InputStream inputStream = ResourcesUtils.getResourceAsStream("/META-INF/hasor-framework/hello-text.txt");
            List<String> helloText = IOUtils.readLines(inputStream, "utf-8");
            StringBuilder builder = new StringBuilder("\n");
            for (String msg : helloText) {
                builder.append(msg).append("\n");
            }
            logger.info(builder.toString());
        } catch (Exception e) { /**/ }
        mainArgs = args;
        usingBoot = true;
        //
        // .确保有一个SetupModule
        SetupModule setupModule = launcherModuleType.getAnnotation(SetupModule.class);
        if (setupModule == null) {
            setupModule = new SetupModule() {
                public Class<? extends Annotation> annotationType() {
                    return SetupModule.class;
                }
                public Class<? extends CreateBuilder> builder() {
                    return DefaultCreateBuilder.class;
                }
                public String config() {
                    return "hasor-config.xml";
                }
                public boolean join() {
                    return false;
                }
            };
        }
        // .启动
        try {
            Class<? extends CreateBuilder> createBuilder = setupModule.builder();
            CreateBuilder builder = createBuilder.newInstance();
            Hasor hasor = builder.buildHasor(mainArgs);
            String config = setupModule.config();
            if (StringUtils.isNotBlank(config)) {
                hasor.setMainSettings(config);
            }
            //
            hasor.build(new BootLauncher(), launcherModuleType.newInstance());
            if (!setupModule.join()) {
                future.completed(new Object());
            }
            future.get();
        } catch (Exception e) {
            throw ExceptionUtils.toRuntimeException(e);
        }
    }
    public void loadModule(ApiBinder apiBinder) throws Throwable {
        apiBinder.bindType(ContextStartListener.class).toInstance(this);
        apiBinder.bindType(ContextShutdownListener.class).toInstance(this);
        //
    }
    @Override
    public void doStart(AppContext appContext) {
    }
    @Override
    public void doStartCompleted(AppContext appContext) {
        List<CommandLauncherDef> cmdSet = appContext.findBindingBean(CommandLauncherDef.class);
        if (cmdSet == null || cmdSet.isEmpty()) {
            return;
        }
        //
        cmdSet.sort((o1, o2) -> {
            int x = o1.getArgsIndex();
            int y = o2.getArgsIndex();
            return Integer.compare(x, y);
        });
        //
        try {
            for (CommandLauncherDef def : cmdSet) {
                def.run(mainArgs);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
    @Override
    public void doShutdown(AppContext appContext) {
    }
    @Override
    public void doShutdownCompleted(AppContext appContext) {
        future.completed(new Object());
    }
}