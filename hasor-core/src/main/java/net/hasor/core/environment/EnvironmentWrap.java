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
import net.hasor.core.Environment;
import net.hasor.core.EventContext;
import net.hasor.core.Settings;

import java.util.Set;
/**
 * {@link Environment}接口包装器。
 * @version : 2013-9-11
 * @author 赵永春 (zyc@hasor.net)
 */
public class EnvironmentWrap implements Environment {
    private final Environment environment;
    public EnvironmentWrap(Environment environment) {
        this.environment = environment;
    }
    //
    protected Environment getTarget() {
        return environment;
    }
    //
    @Override
    public String[] getSpanPackage() {
        return this.getTarget().getSpanPackage();
    }
    @Override
    public Set<Class<?>> findClass(Class<?> featureType) {
        return this.getTarget().findClass(featureType);
    }
    @Override
    public boolean isSmaller() {
        return this.getTarget().isSmaller();
    }
    @Override
    public Set<Class<?>> findClass(Class<?> featureType, String loadPackages) {
        return this.getTarget().findClass(featureType, loadPackages);
    }
    @Override
    public Set<Class<?>> findClass(Class<?> featureType, String[] loadPackages) {
        return this.getTarget().findClass(featureType, loadPackages);
    }
    @Override
    public Object getContext() {
        return this.getTarget().getContext();
    }
    @Override
    public ClassLoader getClassLoader() {
        return this.getTarget().getClassLoader();
    }
    @Override
    public EventContext getEventContext() {
        return this.getTarget().getEventContext();
    }
    @Override
    public Settings getSettings() {
        return this.getTarget().getSettings();
    }
    @Override
    public String[] getVariableNames() {
        return this.getTarget().getVariableNames();
    }

    @Override
    public String getVariable(String varName) {
        return this.getTarget().getVariable(varName);
    }

    @Override
    public String evalString(String eval) {
        return this.getTarget().evalString(eval);
    }
    @Override
    public void addVariable(String varName, String value) {
        this.getTarget().addVariable(varName, value);
    }
    @Override
    public void removeVariable(String varName) {
        this.getTarget().removeVariable(varName);
    }
    @Override
    public void refreshVariables() {
        this.getTarget().refreshVariables();
    }
    @Override
    public String getSystemProperty(String property) {
        return this.getTarget().getSystemProperty(property);
    }
}