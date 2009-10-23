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
package org.more.task;
import java.io.Serializable;
import java.util.UUID;
import org.more.log.ILog;
import org.more.log.LogFactory;
import org.more.util.attribute.AttBase;
/**
 * 任务项的基类，任何任务对象都必须继承该接口，在more.task系统中任务拥有独立的唯一标识ID。
 * 这个ID的标识格式是 任务类名 + | + UUID。开发人员可以通过这个ID在系统环境中对任务进行操作。
 * Date : 2009-5-15
 * @author 赵永春
 */
public abstract class Task extends AttBase implements Runnable, Serializable {
    /**  */
    private static final long serialVersionUID = -5330043409377797416L;
    /** 输出日志 */
    protected ILog            log              = LogFactory.getLog("org_more_task");
    /** 标识任务的UUID */
    private String            uuid             = null;
    /** 任务名 */
    private String            name             = null;
    /** 任务说明 */
    private String            description      = null;
    /** 当前任务的执行状态。 */
    private TaskState         state            = null;
    /** 创建任务项对象 */
    protected Task() {
        this.log.debug("create Task name=" + this.name + " class=" + this.getClass().getSimpleName());
        //设置UUID
        this.uuid = UUID.randomUUID().toString();
        //设置名称
        this.name = this.getClass().getSimpleName() + "|" + this.uuid.replace("-", "");
        //设置运行状态
        this.state = TaskState.New;
    }
    /**
     * 获得任务说明信息。
     * @return 返回任务说明信息。
     */
    public String getDescription() {
        return description;
    }
    /**
     * 设置任务说明信息。
     * @param description 要设置的任务说明信息。
     */
    public void setDescription(String description) {
        this.description = description;
    }
    /**
     * 获得任务名称。默认任务名称是任务任务类型+ID值。
     * @return 返回任务名称。
     */
    public String getName() {
        return this.name;
    }
    /**
     * 设置任务名称。
     * @param name 要设置的任务名称。
     */
    public void setName(String name) {
        this.name = name;
    }
    /**
     * 获得任务ID。该ID值不可设置并且该ID值是自动生成的。除非子类重写该方法以返回自定义的ID值。
     * @return 返回任务ID。该ID值不可设置并且该ID值是自动生成的。除非子类重写该方法以返回自定义的ID值。
     */
    public String getUuid() {
        return uuid;
    }
    /**
     * 获得当前任务的执行状态。
     * @return 返回当前任务的执行状态。
     */
    public TaskState getState() {
        return state;
    }
    @Override
    public void run() {
        //通知开始执行
        this.beginRun();
        try {
            //执行
            this.doRun();
        } catch (Exception e) {
            //在执行时发生异常
            this.onException(e);
        }
        //通知结束执行
        this.endRun();
    }
    /**
     * 获得当前任务中正在执行的任务对象，Task类默认见该方法实现为返回this。
     * 子类可以重写该方法以实现返回任务正在执行的子任务Task对象。
     * @return 获得当前任务中正在执行的任务对象。
     */
    public abstract Task getCurrent();
    /**
     * 测试当前任务是否正在执行。如果返回true则表示正在执行。返回为false表示任务没有执行。
     * @return 返回测试当前任务是否正在执行。如果返回true则表示正在执行。返回为false表示任务没有执行。
     */
    public boolean isRun() {
        return (this.state == TaskState.Run) ? true : false;//是否正在执行
    };
    /** 修改状态为开始执行。 */
    private void beginRun() {
        this.log.debug("Task[" + this.name + "] : beginRun");
        this.state = TaskState.Run;
    };
    /**
     * 任务方法体，子类实现该方法以完成某项特定的任务。
     * @throws Exception 如果在执行过程中发生异常。
     */
    protected abstract void doRun() throws Exception;
    /**
     * 在执行doRun方法时发生异常。
     * @param e 执行doRun方法发生的异常对象。
     */
    protected void onException(Exception e) {
        this.log.debug("Task[" + this.name + "] : onException message = " + e.getMessage());
    };
    /** 修改状态为执行完毕。 */
    private void endRun() {
        this.log.debug("Task[" + this.name + "] : endRun");
        this.state = TaskState.RunEnd;
    }
}
