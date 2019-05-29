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
package net.hasor.web.definition.beans;
import net.hasor.core.AppContext;
import net.hasor.web.RenderEngine;
import net.hasor.web.RenderInvoker;

import java.io.IOException;
import java.io.Writer;
import java.util.concurrent.atomic.AtomicBoolean;
//
public class TestRenderEngine implements RenderEngine {
    private static AtomicBoolean initEngineCall = new AtomicBoolean(false);
    private static AtomicBoolean processCall    = new AtomicBoolean(false);
    private static AtomicBoolean existCall      = new AtomicBoolean(false);
    //
    public static boolean isInitEngineCall() {
        return initEngineCall.get();
    }
    public static boolean isProcessCall() {
        return processCall.get();
    }
    public static boolean isExistCall() {
        return existCall.get();
    }
    public static void resetCalls() {
        initEngineCall.set(false);
        processCall.set(false);
        existCall.set(false);
    }
    //
    @Override
    public void initEngine(AppContext appContext) throws Throwable {
        initEngineCall.set(true);
    }
    @Override
    public void process(RenderInvoker invoker, Writer writer) throws Throwable {
        processCall.set(true);
    }
    @Override
    public boolean exist(String template) throws IOException {
        existCall.set(true);
        return false;
    }
}
