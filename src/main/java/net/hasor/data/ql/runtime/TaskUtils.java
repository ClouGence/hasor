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
package net.hasor.data.ql.runtime;
import net.hasor.core.utils.BeanUtils;
import net.hasor.data.ql.ObjectResult;
import net.hasor.data.ql.runtime.task.AbstractTask;

import java.lang.reflect.Method;
import java.util.Map;
/**
 * 任务
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-03-23
 */
public class TaskUtils {
    public static Object readProperty(Object object, String fieldName) throws Exception {
        if (object == null) {
            return null;
        }
        if (object instanceof Map) {
            return ((Map) object).get(fieldName);
        }
        if (object instanceof ObjectResult) {
            return ((ObjectResult) object).getOriResult(fieldName);
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
    /** 最近的带有DS的Task */
    public static AbstractTask nearData(AbstractTask atTask) {
        if (TaskType.D == atTask.getTaskType()) {
            return atTask;
        } else {
            while (atTask.getDataSource() == null) {
                atTask = atTask.getParent();
                if (atTask == null) {
                    break;
                }
                if (atTask.getDataSource() != null) {
                    return atTask;
                }
            }
        }
        return null;
    }
    /** 最近的DS */
    public static AbstractTask nearDS(AbstractTask atTask) {
        AbstractTask nearTask = TaskUtils.nearData(atTask);
        if (nearTask != null) {
            return nearTask.getDataSource();
        }
        return null;
    }
}