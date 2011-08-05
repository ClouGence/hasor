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
package org.more.web.page.info;
/**
 * 
 * @version 2009-6-19
 * @author 赵永春 (zyc@byshell.org)
 */
public class AutoPageInfoBean {
    //下面5个属性是要求与AutoPageInfo对象中数据保持一致的。
    private String url    = "";        //连接URL
    private int    start  = 0;         //页启始编号
    private int    step   = 1;         //页递增步长_页大小
    private int    count  = 10;        //总共记录数量
    private int    number = 0;         //当前的页码编号
    private String prefix = "autoPage"; //变量前缀
    //============================================================
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public int getStart() {
        return start;
    }
    public void setStart(int start) {
        this.start = start;
    }
    public int getStep() {
        return step;
    }
    public void setStep(int step) {
        this.step = step;
    }
    public int getCount() {
        return count;
    }
    public void setCount(int count) {
        this.count = count;
    }
    public String getPrefix() {
        return prefix;
    }
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
    public int getNumber() {
        return number;
    }
    public void setNumber(int number) {
        this.number = number;
    }
}
