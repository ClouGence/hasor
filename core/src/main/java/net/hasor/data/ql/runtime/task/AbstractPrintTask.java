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
package net.hasor.data.ql.runtime.task;
import net.hasor.core.utils.StringUtils;
import net.hasor.data.ql.runtime.TaskStatus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
/**
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-03-23
 */
public abstract class AbstractPrintTask extends AbstractTask {
    public AbstractPrintTask(String nameOfParent, AbstractTask parentTask, AbstractTask dataSource) {
        super(nameOfParent, parentTask, dataSource);
    }
    //
    private static enum PrintType {
        Struts, Task
    }
    //
    /** 打印结构树 */
    public String printStrutsTree() {
        StringBuilder builder = new StringBuilder();
        this.printTaskTree(builder, 0, new ArrayList<AbstractPrintTask>(), PrintType.Struts);
        return builder.toString();
    }
    /** 打印执行任务树 */
    public String printTaskTree() {
        StringBuilder builder = new StringBuilder();
        this.printTaskTree(builder, 0, new ArrayList<AbstractPrintTask>(), PrintType.Task);
        return builder.toString();
    }
    //
    //
    private static String taskStatus(AbstractPrintTask task) {
        return task.getTaskType().name() + ":" + StringUtils.center(task.getTaskStatus().name(), maxStatusStringLength, " ");
    }
    private static String genDepthStr(int depth) {
        return StringUtils.leftPad("", depth * 2, " ");
    }
    private boolean printTaskTree(StringBuilder builder, int depth, List<AbstractPrintTask> depList, PrintType printType) {
        boolean result = !depList.contains(this);
        if (result) {
            if (depth == 0) {
                builder.append("[" + taskStatus(this) + "] ");
            }
            builder.append(this.getClass().getSimpleName());
        }
        //        depList.add(this);
        //
        Collection<AbstractTask> taskSet = null;
        if (PrintType.Struts == printType) {
            taskSet = this.fieldTaskMap.values();
        } else {
            taskSet = this.depTaskList;
        }
        //
        for (AbstractTask task : taskSet) {
            AbstractPrintTask printTask = (AbstractPrintTask) task;
            if (!depList.contains(task)) {
                builder.append("\n[" + taskStatus(printTask) + "] " + genDepthStr(depth) + " -> ");
            }
            int nextDepth = (result) ? (depth + 1) : depth;
            printTask.printTaskTree(builder, nextDepth, depList, printType);
        }
        return result;
    }
    private static int maxStatusStringLength = 0;

    static {
        for (TaskStatus ts : TaskStatus.values()) {
            int length = ts.name().length();
            if (maxStatusStringLength <= length) {
                maxStatusStringLength = length;
            }
        }
        maxStatusStringLength = maxStatusStringLength + 2;
    }
}