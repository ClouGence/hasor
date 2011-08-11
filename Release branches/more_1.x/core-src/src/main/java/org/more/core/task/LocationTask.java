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
package org.more.core.task;
/**
 * 该接口是用于定位任务已经执行的步骤数量和还有多少没有执行。通过该接口的getRatio方法还可以获得指定精度的比率值。
 * @version 2009-5-16
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class LocationTask extends Task {
    private static final long serialVersionUID = 5140955177850271048L;
    /** 总共任务数 */
    private int               count            = 0;
    /** 当前执行的任务编号 */
    private int               index            = 0;
    /** 被缓存的任务正在执行的子任务对象。 */
    private Task              currentTask      = null;
    /**
     * 设置可定位任务的任务总数。如果设置的值小于 1 则newCount参数值会被 1 取代。
     * 同时如果当前getPosition()方法返回值大于最新设置的newCount参数值则系统自动采用
     * newCount参数值作为getPosition()方法返回值。
     * @param newCount 设置的总数值。
     */
    protected void setCountTask(int newCount) {
        this.log.debug(this.index + "/" + this.count + " setCountTask " + newCount);
        if (newCount < 1)
            this.count = 1;
        else
            this.count = newCount;
        if (this.index > this.count)
            this.index = this.count;
    }
    /**
     * 设置当前定位任务的任务位置。如果设置的值小于 0 则index参数值会被 0 取代。
     * 如果设置的值大于getCount()的返回值则采用getCount()返回值作为设置的参数。
     * @param index 设置的总数值。
     */
    protected void setIndexTask(int index) {
        this.log.debug(this.index + "/" + this.count + " setIndexTask " + index);
        if (index < 0)
            this.index = 0;
        else if (index > this.count)
            this.index = this.count;
        else
            this.index = index;
    }
    /** 定位索引自动增长1，如果已经增长到最大则忽略操作。该方法相当于setIndexTask(getPosition()++)。 */
    protected void step() {
        this.log.debug(this.index + "/" + this.count + " step");
        this.index++;
        if (this.index > this.count)
            this.index = this.count;
    }
    /** 定位索引增长到最大。该方法相当于setIndexTask(getCount())。 */
    protected void stepToEnd() {
        this.log.debug(this.index + "/" + this.count + " stepToEnd");
        this.index = this.count;
    }
    /** 更新任务中正在执行的子任务对象。该方法可以缓存getCurrent()方法的结果以用于getCount、getPosition、getRatio方法的调用。 */
    public void updateCurrent() {
        this.currentTask = this.getCurrent();
    }
    /** 该方法是为了getCount、getPosition、getRatio在调用过程中使用的是同一个任务对象而准备的。因为在多线程情况下很可能调用上述三个方法时使用的任务对象不是一个。*/
    private LocationTask getCurrentLocationTask() {
        //如果当前任务对象为空。则调用更新当前任务对象方法更新。
        if (this.currentTask == null)
            this.updateCurrent();
        if (this.currentTask == null)
            return null;
        else if (this.currentTask instanceof LocationTask)
            return (LocationTask) this.currentTask;
        else
            return null;
    }
    /**
     * 获得任务执行的步骤总数，该数是大于0的一个数，并且该返回值不会出现负数以表示任务执行时需要经过的总步骤数。
     * @return 返回任务执行的步骤总数，该数是大于0的一个数，并且该返回值不会出现负数以表示任务执行时需要经过的总步骤数。
     */
    public int getCount() {
        //如果showChildTaskLocation属性为false则返回当前任务的相关属性值
        if (this.showChildTaskLocation == false)
            return this.count;
        //获取当前执行任务对象
        LocationTask lt = this.getCurrentLocationTask();
        if (lt == null)
            return this.count;
        //如果当前执行对象为空则返回当前任务的count
        return (lt == this) ? this.count : lt.getCount();
    }
    /**
     * 获得任务当前的执行步骤，该数是界于 0 和 getCount。
     * @return 返回任务当前的执行步骤，该数是界于 0 和 getCount。
     */
    public int getPosition() {
        //如果showChildTaskLocation属性为false则返回当前任务的相关属性值
        if (this.showChildTaskLocation == false)
            return this.index;
        //获取当前执行任务对象
        LocationTask lt = this.getCurrentLocationTask();
        if (lt == null)
            return this.index;
        //如果当前执行对象为空则返回当前任务的index
        return (lt == this) ? this.index : lt.getPosition();
    }
    /**
     * 获得当前任务的执行的步骤与总步骤的比值，该值是一个未经过处理的浮点数。如果showChildTaskLocation属性被设置为true。
     * 则该方法将返回正在执行的任务的进度值。当任务有许多子任务而且子任务仍然有子任务时。该该方法将直接返回getPosition()
     * 与getCount()的比值。
     * @return 返回getPosition()与getCount()的比值。该值是一个未经过处理的浮点数。
     */
    public float getRatio() {
        float percentage = (float) this.getPosition() / (float) this.getCount();//计算比值
        return percentage;//返回指定精度的比例值
    }
    //=============================================================================================
    /**  */
    private boolean showChildTaskLocation = false;
    /**
     * 获得当使用getCount()、getPosition()、getRatio()三个方法定位当前任务执行进度时，显示的是子任务进度还是当前任务的进度。
     * 如果子任务不是LocationTask类的子类则显示当前任务的进度。
     * @return 返回当前是否显示子任务进度的值。
     */
    public boolean isShowChildTaskLocation() {
        return showChildTaskLocation;
    }
    /**
     * 设置当使用getCount()、getPosition()、getRatio()三个方法定位当前任务执行进度时，显示的是子任务进度还是当前任务的进度。
     * 如果子任务不是LocationTask类的子类则显示当前任务的进度。
     * 例：有如下伪代码<pre>
     * LocationTask t1 = new LocationTask();//1
     * t1.addTask(new Task());//2
     * t1.addTask(new Task());//3
     * t1.addTask(new Task());//4
     * LocationTask t2 new LocationTask();//5
     * t2.addTask(new Task());//6
     * t2.addTask(t1);
     * t2.addTask(new Task());//7
     * t2.run();
     * t2.setShowChildTaskLocation(true);
     * </pre>
     * 上述任务的处理过程是6,1[2,3,4],7。当任务执行在6状态时getRatio()方法显示的是6任务的执行进度。如果任务执行到2，显示的是
     * 2任务的执行进度，如果该属性被设置为false则执行在6状态时getRatio()方法显示的是6任务的执行进度。如果任务执行到2，显示的是
     * 1任务的执行进度。
     * @param showChildTaskLocation
     */
    public void setShowChildTaskLocation(boolean showChildTaskLocation) {
        this.showChildTaskLocation = showChildTaskLocation;
    }
}