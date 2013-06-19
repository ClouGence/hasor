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
package org.more.core.log.objects;
import java.util.ArrayList;
import java.util.List;
import org.more.core.log.ILog;
import org.more.core.log.ILogFormater;
import org.more.core.log.ILogWrite;
/**
 * 默认日志源
 * @version 2009-5-13
 * @author 赵永春 (zyc@byshell.org)
 */
public class DefaultLog implements ILog {
    private List<ILogWrite> all_list     = new ArrayList<ILogWrite>(0);
    private List<ILogWrite> debug_list   = new ArrayList<ILogWrite>(0);
    private List<ILogWrite> error_list   = new ArrayList<ILogWrite>(0);
    private List<ILogWrite> info_list    = new ArrayList<ILogWrite>(0);
    private List<ILogWrite> warning_list = new ArrayList<ILogWrite>(0);
    private ILogFormater    formater     = new DefaultLogFormater();
    // ===============================================================
    public void addWrite(ILogWrite logwrite, String level) {
        //        if (ILog.LogLevel_ALL.equals(level))
        //            all_list.add(logwrite);
        //        if (ILog.LogLevel_Debug.equals(level))
        //            debug_list.add(logwrite);
        //        if (ILog.LogLevel_Error.equals(level))
        //            error_list.add(logwrite);
        //        if (ILog.LogLevel_Info.equals(level))
        //            info_list.add(logwrite);
        //        if (ILog.LogLevel_Warning.equals(level))
        warning_list.add(logwrite);
    }
    public void setFormater(ILogFormater formater) {
        this.formater = formater;
    }
    //
    private void sendMsg(List<ILogWrite> list, String level, String msg) {
        for (ILogWrite lw : all_list)
            lw.writeLog(formater.getFormatMessage(ILog.LogLevel_ALL, msg));
        //
        for (ILogWrite lw : list)
            lw.writeLog(formater.getFormatMessage(level, msg));
    }
    //
    public void debug(String msg) {
        sendMsg(this.debug_list, ILog.LogLevel_Debug, msg);
    }
    public void error(String msg) {
        sendMsg(this.error_list, ILog.LogLevel_Error, msg);
    }
    public void info(String msg) {
        sendMsg(this.info_list, ILog.LogLevel_Info, msg);
    }
    public void warning(String msg) {
        sendMsg(this.warning_list, ILog.LogLevel_Warning, msg);
    }
}