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
package net.hasor.core.environment;
import net.hasor.core.ApiBinder;
import net.hasor.core.Environment;
import net.hasor.core.Module;
import net.hasor.test.beans.mods.ErrorModule;
import net.hasor.utils.StringUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;
public class EnvTest {
    protected EnvironmentWrap wrap(Environment env) {
        return new EnvironmentWrap(env);
    }
    //
    @Test
    public void envTest1() throws IOException {
        Object context1 = new Object();
        EnvironmentWrap env = wrap(new StandardEnvironment(context1));
        assert env.getContext() == context1;
        //
        Object context2 = new Object();
        assert env.getContext() != context2;
        ((StandardEnvironment) env.getTarget()).setContext(context2);
        assert env.getContext() == context2;
    }
    //
    @Test
    public void envTest2() throws IOException {
        URL resource = Thread.currentThread().getContextClassLoader().getResource("net_hasor_core_environment/simple-config.xml");
        //
        EnvironmentWrap env = wrap(new StandardEnvironment(null, resource));
        assert env.evalString("%MY_ENV%").equals("my my my");
    }
    //
    @Test
    public void envTest3() throws IOException, URISyntaxException {
        URL resource = Thread.currentThread().getContextClassLoader().getResource("net_hasor_core_environment/simple-config.xml");
        //
        EnvironmentWrap env = wrap(new StandardEnvironment(null, resource.toURI()));
        assert env.evalString("%MY_ENV%").equals("my my my");
    }
    //
    @Test
    public void envTest4() throws IOException, URISyntaxException {
        URL resource = Thread.currentThread().getContextClassLoader().getResource("net_hasor_core_environment/simple-config.xml");
        //
        File file = new File(resource.toURI());
        EnvironmentWrap env = wrap(new StandardEnvironment(null, file));
        assert env.evalString("%MY_ENV%").equals("my my my");
    }
    //
    @Test
    public void envTest5() throws IOException {
        EnvironmentWrap env = wrap(new StandardEnvironment(null, "net_hasor_core_environment/simple-config.xml"));
        assert env.evalString("%MY_ENV%").equals("my my my");
    }
    //
    @Test
    public void envTest6() throws IOException {
        EnvironmentWrap env = wrap(new StandardEnvironment(null));
        //
        ((StandardEnvironment) env.getTarget()).setSpanPackage(new String[] { "net.hasor.core.test.mods" });
        assert env.findClass(ErrorModule.class).size() == 1;
        assert env.findClass(Module.class).size() == 3;
    }
    //
    @Test
    public void envTest7() throws IOException, URISyntaxException {
        class StandardEnvironment2 extends StandardEnvironment {
            public StandardEnvironment2(Object context) throws IOException {
                super(context);
            }
            public URI toURIProxy(Object source) {
                return toURI(source);
            }
        }
        //
        StandardEnvironment2 env = new StandardEnvironment2(null);
        //
        try {
            env.toURIProxy(new URL("sssss"));
            assert false;
        } catch (Exception e) {
            assert true;
        }
        //
        env.toURIProxy(new URI("sssss"));
        //
        try {
            env.toURIProxy(new Date());
            assert false;
        } catch (ClassCastException e) {
            assert true;
        }
    }
    //
    @Test
    public void envTest8() throws IOException {
        StandardEnvironment oriEnv = new StandardEnvironment(null);
        EnvironmentWrap env = wrap(oriEnv);
        //
        env.addVariable(null, "");
        env.addVariable("", "");
        //
        env.addVariable("TEST_ENV", "abc");
        assert "abc".equals(env.evalString("%TEST_ENV%"));
        //
        env.addVariable("TEST_ENV", null);
        assert "".equals(env.evalString("%TEST_ENV%"));
        //
        env.addVariable("TEST_ENV", "");
        assert "".equals(env.evalString("%TEST_ENV%"));
        //
        env.addVariable("TEST_ENV", "abc");
        assert "abc".equals(env.evalString("%TEST_ENV%"));
        env.removeVariable("TEST_ENV");
        assert "".equals(env.evalString("%TEST_ENV%"));
        //
        env.addVariable("", "abc");
        //
        //
        System.setProperty("self_self_self", "self");
        assert "self".equals(System.getProperty("self_self_self")) && "self".equals(env.getSystemProperty("self_self_self"));
        //
        assert env.getClassLoader() == oriEnv.getClassLoader();
        assert env.getContext() == oriEnv.getContext();
        assert env.getSpanPackage() == oriEnv.getSpanPackage();
        assert env.getEventContext() == oriEnv.getEventContext();
        assert env.getSettings() == oriEnv.getSettings();
        env.refreshVariables();
    }
    //
    @Test
    public void envTest9() throws IOException {
        EnvironmentWrap env = wrap(new StandardEnvironment());
        //
        assert env.findClass(null) == null;
        assert !env.findClass(ApiBinder.class).isEmpty();
        assert !env.findClass(ApiBinder.class, "").isEmpty();
        assert env.findClass(null, new String[0]) == null;
    }
    @Test
    public void envTest10() throws IOException {
        System.setProperty("MyVar", "hello");
        System.setProperty("JAVA_HOME", "/TTTT/CC");
        EnvironmentWrap env = wrap(new StandardEnvironment(null));
        //
        assert "hello".equals(env.evalString("%MyVar%"));
        assert "i say hello.".equals(env.evalString("i say %MyVar%."));
        //
        //JAVA_HOME
        String java_home = System.getenv().get("JAVA_HOME");
        if (StringUtils.isNotBlank(java_home)) {
            assert java_home.equals(env.evalString("%JAVA_HOME%"));
            assert (java_home + "/bin/javac.exe").equals(env.evalString("%JAVA_HOME%/bin/javac.exe"));
        }
    }
}