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
package org.more.hypha.beans.assembler.factory;
/**
 * 字节码缓存器，该接口的职责是负责缓存那些动态生成或者装载的字节码信息。
 * 缓存的字节码不代表可以单独在另外一个新的环境中正常的运行。这是由于类的引用依赖特征。
 * 但是缓存的确可以给字节码生成器减少相当大的压力。
 * @version 2010-12-29
 * @author 赵永春 (zyc@byshell.org)
 */
public interface ByteCodeCache {
    /**清空缓存*/
    public void clearCache();
    /**获取缓存大小*/
    public int cacheSize();
    /**保存字节码*/
    public void saveCode(String classID, byte[] code);
    /**装载字节码*/
    public byte[] loadCode(String classID);
};