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
import net.hasor.dataql.InvokerProcessException;
import net.hasor.dataql.ProcessException;
import net.hasor.dataql.result.ObjectModel;
import net.hasor.dataql.runtime.InsetProcess;
import net.hasor.dataql.runtime.InstSequence;
import net.hasor.dataql.runtime.ProcessContet;
import net.hasor.dataql.runtime.mem.FindData;
import net.hasor.dataql.runtime.mem.MemStack;
import net.hasor.dataql.runtime.mem.StackStruts;
import net.hasor.utils.BeanUtils;
import net.hasor.utils.StringUtils;

import java.lang.reflect.Method;
import java.util.Map;
/**
 * ROU，寻值
 * --
 *  寻值机制：ROU会先尝试在当前结果中寻找值，如果找不到则到上一层数据节点进行查找，直到根节点（默认：自下而上）。
 *
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-07-19
 */
class ROU implements InsetProcess {
    /*路由类型*/
    private static enum RouType {
        Type_1, // @ (Default)  在 '数据' 上路由
        Type_2, // #
        Type_3, // $            在 '结果' 上路由
        Type_4, // %
        Type_5, // &
    }
    @Override
    public int getOpcode() {
        return ROU;
    }
    //
    @Override
    public void doWork(InstSequence sequence, MemStack memStack, StackStruts local, ProcessContet context) throws ProcessException {
        String rouPath = sequence.currentInst().getString(0);
        if (StringUtils.isBlank(rouPath)) {
            memStack.push(null);
            return;
        }
        // 确定路由类型  @、#、$、%、&
        rouPath = rouPath.trim();
        RouType rouType = RouType.Type_1;
        String routeExpression = rouPath;
        switch (rouPath.charAt(0)) {
        case '@':   //
            rouPath = rouPath.substring(1);
            rouType = RouType.Type_1;
            break;
        case '#':   //
            rouPath = rouPath.substring(1);
            rouType = RouType.Type_2;
            break;
        case '$':   //
            rouPath = rouPath.substring(1);
            rouType = RouType.Type_3;
            break;
        case '%':   //
            rouPath = rouPath.substring(1);
            rouType = RouType.Type_4;
            break;
        case '&':   //
            rouPath = rouPath.substring(1);
            rouType = RouType.Type_5;
            break;
        }
        // .拆大括号
        if (rouPath.startsWith("{") && rouPath.endsWith("}")) {
            rouPath = rouPath.substring(1, rouPath.length() - 1);
        }
        // .数据路由(一般出现在 ASX...ASE 之间使用)
        if (rouType == RouType.Type_1) {
            memStack.push(this.routeByStack(rouPath, local, 0));
            return;
        }
        if (rouType == RouType.Type_3) {
            memStack.push(this.routeByStack(rouPath, memStack, 0));
            return;
        }
        //
        throw new InvokerProcessException(getOpcode(), "does not support routing expressions -> " + routeExpression);
    }
    //
    protected Object routeByStack(String routePath, FindData local, int startDepth) throws InvokerProcessException {
        if (startDepth > local.getLayerDepth()) {
            return null;
        }
        Object useData = local.dataOfDepth(startDepth);
        if (useData == null) {
            return null;
        }
        //
        String[] routeSplit = routePath.split("\\.");
        for (String nodeName : routeSplit) {
            if (useData == null) {
                return this.routeByStack(routePath, local, startDepth + 1);
            }
            try {
                useData = readProperty(useData, nodeName);
            } catch (Exception e) {
                throw new InvokerProcessException(getOpcode(), e.getMessage(), e);
            }
        }
        //
        //        Object data = local.dataOfHead();
        //        SelfData self = memStack.findSelf();
        //        memStack.findSelf();
        return useData;
    }
    public static Object readProperty(Object object, String fieldName) throws Exception {
        if (object == null) {
            return null;
        }
        if (object instanceof Map) {
            return ((Map) object).get(fieldName);
        }
        if (object instanceof ObjectModel) {
            return ((ObjectModel) object).getOriResult(fieldName);
        }
        //
        Method[] allMethod = object.getClass().getMethods();
        for (Method m : allMethod) {
            Class<?>[] parameterTypes = m.getParameterTypes();
            if ("get".equalsIgnoreCase(m.getName()) && parameterTypes.length == 1 && parameterTypes[0] == String.class) {
                return m.invoke(object, fieldName);
            }
        }
        return BeanUtils.readPropertyOrField(object, fieldName);
    }
}