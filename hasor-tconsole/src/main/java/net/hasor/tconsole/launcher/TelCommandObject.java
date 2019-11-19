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
import net.hasor.tconsole.*;

/**
 * 准备要执行的命令
 * @version : 2016年09月20日
 * @author 赵永春 (zyc@hasor.net)
 */
final class TelCommandObject extends AttributeObject implements TelCommand, TelCommandOption {
    private TelSession  parentSession;
    private TelExecutor executor;
    private TelPhase    telPhase;
    private String      requestCommand;
    private String[]    requestArgs;
    private String      requestBody;
    private boolean     cancel;

    TelCommandObject(TelSession parentSession, TelExecutor executor, String requestCommand, String[] requestArgs) {
        this.parentSession = parentSession;
        this.executor = executor;
        this.telPhase = TelPhase.Prepare;
        this.requestCommand = requestCommand;
        this.requestArgs = requestArgs;
        this.requestBody = null;
        this.cancel = false;
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
        if (this.cancel) {
            return "";
        }
        return this.executor.doCommand(this);
    }

    boolean testReadly(TelReaderObject inputQueue) {
        return this.executor.readCommand(this, inputQueue);
    }

    void setCommandBody(String commandBody) {
        this.requestBody = commandBody;
    }

    @Override
    public boolean isCancel() {
        return this.cancel;
    }

    @Override
    public void cancel() {
        this.cancel = true;
    }
}