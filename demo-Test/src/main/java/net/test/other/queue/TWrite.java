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
package net.test.other.queue;
/**
 * 写模式的列车
 * @version : 2014年11月4日
 * @author 赵永春(zyc@hasor.net)
 */
public interface TWrite {
    /**货物是否装满了*/
    public boolean isFull();
    /**推送一个货物到列车上。*/
    public boolean pushGood(Object good);
    /**列车容量*/
    public int getCapacity();
}