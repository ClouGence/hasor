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
package net.hasor.tconsole.launcher;
import net.hasor.tconsole.TelCommand;
import net.hasor.tconsole.TelExecutor;
import net.hasor.tconsole.TelPhase;
import net.hasor.tconsole.TelSession;

/**
 *
 * @version : 2016年4月3日
 * @author 赵永春 (zyc@hasor.net)
 */
public final class TelCommandObject extends AttributeObject implements TelCommand {
    private TelSession  parentSession;
    private TelExecutor executor;
    private TelPhase    telPhase;
    private String      requestCommand;
    private String[]    requestArgs;
    private String      requestBody;

    TelCommandObject(TelSession parentSession, TelExecutor executor, String requestCommand, String[] requestArgs) {
        this.parentSession = parentSession;
        this.executor = executor;
        this.telPhase = TelPhase.Prepare;
        this.requestCommand = requestCommand;
        this.requestArgs = requestArgs;
        this.requestBody = null;
    }

    @Override
    public TelSession getSession() {
        return this.parentSession;
    }

    @Override
    public String getCommandName() {
        return this.requestCommand;
    }

    @Override
    public String[] getCommandArgs() {
        return this.requestArgs;
    }

    @Override
    public String getCommandBody() {
        return this.requestBody;
    }

    /** 命令状态 */
    public TelPhase curentPhase() {
        return this.telPhase;
    }

    void curentPhase(TelPhase telPhase) {
        this.telPhase = telPhase;
    }

    String doCommand() throws Throwable {
        return this.executor.doCommand(this);
    }

    boolean testReadly(TelReaderObject inputQueue) {
        return this.executor.readCommand(inputQueue);
    }

    void setCommandBody(String commandBody) {
        this.requestBody = commandBody;
    }
}