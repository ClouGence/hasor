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
package org.platform.api.upfile;
import java.util.List;
import org.platform.api.event.InitEvent;
/**
 * 上传服务策略执行接口。
 * @version : 2013-3-12
 * @author 赵永春 (zyc@byshell.org)
 */
public interface IUpFilePolicy {
    /**初始化策略对象。*/
    public void initPolicy(InitEvent event);
    /**
     * 进行策略检查，返回策略执行的结果。
     * @param userInfo 用户信息
     * @param upData 上传的数据
     * @param list 传上来的文件列表
     */
    public PolicyResult runPolicy(IUpInfo upData, List<IFileItem> list);
    /**
     * 策略执行结果，该解决预示着策略系统该如何进行下一步处理。
     * @version : 2013-3-26
     * @author 赵永春 (zyc@byshell.org)
     */
    public static enum PolicyResult {
        /**继续执行后面的策略。*/
        ContinuePolicy,
        /**退出策略的执行，引发上传服务事件。*/
        ExitPolicy,
        /**放弃后续的所有操作。*/
        Return
    }
}