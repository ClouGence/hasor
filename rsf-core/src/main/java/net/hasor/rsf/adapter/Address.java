/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
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
package net.hasor.rsf.adapter;
import java.net.URL;
import net.hasor.core.EventListener;
/**
 * 
 * @version : 2015年1月3日
 * @author 赵永春(zyc@hasor.net)
 */
public interface Address {
    /** @return 地址*/
    public URL getAddress();
    /** @return 表示当前地址是否可用*/
    public boolean isInvalid();
    /** @return 是否为静态数据（静态数据是指在程序中明确指定的IP）*/
    public boolean isStatic();
    /**
     * 两个 Address 可以比较是否相等
     * @param obj 另一个对象
     * @return 返回结果。
     */
    public boolean equals(Object obj);
    /**标记地址为失效的*/
    public void setInvalid();
    //
    /**
     * 添加监听器
     * @param listener 监听器
     */
    public void addListener(EventListener listener);
    /**
     * 移除监听器
     * @param listener 监听器
     */
    public void removeListener(EventListener listener);
    /** @return 被比较为失效的次数，当超过100之后会被 Hasor 永久放弃（静态数据不受此影响）*/
    public int invalidCount();
}