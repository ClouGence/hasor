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
package net.hasor.rsf.console;
/**
 * RSF命令
 * @version : 2016年4月3日
 * @author 赵永春(zyc@hasor.net)
 */
public interface RsfInstruct {
    public static final String AFTER_CLOSE_SESSION = "Session_AfterClose";
    //

    /**帮助信息.*/
    public String helpInfo();

    /**命令是否启用多行输入，启用多行输入之后。两次回车即可完成整个命令。*/
    public boolean inputMultiLine(RsfCommandRequest request);

    /**执行命令*/
    public String doCommand(RsfCommandRequest request) throws Throwable;
}