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
package net.hasor.test.tconsole;
import net.hasor.tconsole.TelCommand;
import net.hasor.tconsole.TelCommandOption;
import net.hasor.tconsole.spi.TelAfterExecutorListener;
import net.hasor.tconsole.spi.TelBeforeExecutorListener;

import java.util.ArrayList;
import java.util.List;

public class ExecutorListenerBean implements TelBeforeExecutorListener, TelAfterExecutorListener {
    private List<TelCommand> beforeExecCommand = new ArrayList<>();
    private List<TelCommand> afterExecCommand  = new ArrayList<>();

    public List<TelCommand> getBeforeExecCommand() {
        return beforeExecCommand;
    }

    public List<TelCommand> getAfterExecCommand() {
        return afterExecCommand;
    }

    @Override
    public void beforeExecCommand(TelCommandOption telCommand) {
        beforeExecCommand.add(telCommand);
    }

    @Override
    public void afterExecCommand(TelCommand telCommand) {
        afterExecCommand.add(telCommand);
    }
}
