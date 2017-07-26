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
package net.hasor.dataql.runtime.inset;
import net.hasor.core.utils.StringUtils;
import net.hasor.dataql.ProcessException;
import net.hasor.dataql.runtime.InsetProcess;
import net.hasor.dataql.runtime.InstSequence;
import net.hasor.dataql.runtime.ProcessContet;
import net.hasor.dataql.runtime.mem.LocalData;
import net.hasor.dataql.runtime.mem.MemStack;
import net.hasor.dataql.runtime.struts.SelfData;
/**
 * ROU，寻值
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-07-19
 */
class ROU implements InsetProcess {
    private static enum RouType {
        /*根结果节点*/
        Root,       //
        /*当结果前节点*/
        Self,       //
        /*当前数据(默认)*/
        DataSource  //
    }
    @Override
    public int getOpcode() {
        return ROU;
    }
    @Override
    public void doWork(InstSequence sequence, MemStack memStack, LocalData local, ProcessContet context) throws ProcessException {
        String rouPath = sequence.currentInst().getString(0);
        if (StringUtils.isBlank(rouPath)) {
            memStack.push(null);
            return;
        }
        //
        rouPath = rouPath.trim();
        RouType rouType = RouType.DataSource;
        if (rouPath.startsWith("${")) {
            rouType = RouType.Root;
            rouPath = rouPath.substring(2, rouPath.length() - 1);
        } else if (rouPath.startsWith("%{")) {
            rouType = RouType.Self;
            rouPath = rouPath.substring(2, rouPath.length() - 1);
        }
        //
        Object data = local.getData();
        SelfData self = memStack.findSelf();
        //        memStack.findSelf();
        //        memStack.findSelf(Integer.MAX_VALUE);
        //        local.getData();
        memStack.push("data:" + rouPath);
    }
}