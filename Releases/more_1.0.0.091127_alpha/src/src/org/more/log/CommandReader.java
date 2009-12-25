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
package org.more.log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
/**
 * 命令读取器
 * Date : 2009-5-13
 * @author 赵永春
 */
class CommandReader extends BufferedReader {
    public CommandReader(Reader in) {
        super(in);
    }
    public CommandReader(String config) {
        super(new StringReader(config));
    }
    // ===============================================================
    /**
     * 读取一个属性条目
     * @return 返回读取到的日志属性。
     * @throws IOException 如果发生I/O异常
     */
    public CommandAttribut readCommandAttribut() throws IOException {
        String command_line = null;
        String[] cmd_data = null;
        //
        String cmd_str = null;
        String value_str = null;
        String comments_str = null;
        //
        while (true) {
            command_line = this.readLine();
            if (command_line == null)
                return null;
            cmd_data = command_line.split("=");
            if (cmd_data.length < 2)
                continue;
            //
            cmd_str = cmd_data[0];
            //
            if (cmd_str == null || cmd_str.equals(""))
                continue;
            break;
        }
        //
        String[] cmd_value_line = cmd_data[1].split("//");
        if (cmd_value_line.length == 2)
            comments_str = cmd_value_line[1];
        value_str = cmd_value_line[0];
        //
        CommandAttribut ca = new CommandAttribut();
        ca.setName(cmd_str.trim());
        ca.setValue((value_str != null) ? value_str.trim() : null);
        ca.setComments((comments_str != null) ? comments_str.trim() : null);
        return ca;
    }
}