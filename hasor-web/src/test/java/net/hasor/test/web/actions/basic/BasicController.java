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
package net.hasor.test.web.actions.basic;
import net.hasor.web.Controller;
import net.hasor.web.Invoker;

public class BasicController implements Controller {
    private boolean execute;
    private boolean doInit;

    public boolean isExecute() {
        return execute;
    }

    public void execute() {
        this.execute = true;
    }

    @Override
    public void initController(Invoker renderData) {
        this.doInit = true;
    }
}