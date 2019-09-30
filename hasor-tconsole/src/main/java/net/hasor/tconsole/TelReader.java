/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.hasor.tconsole;
/**
 * 读取一个完整命令。
 * @version : 20169年09月20日
 * @author 赵永春 (zyc@hasor.net)
 */
public interface TelReader {
    /** 期望下一个字符就是回车符。 */
    public default boolean expectBlankLine() {
        return expectChar('\n');
    }
 
    /** 连续读取到两个换行符 */
    public default boolean expectDoubleBlankLines() {
        return expectString("\n\n");
    }

    /** 期待一个结束字符 */
    public default boolean expectChar(int waitChar) {
        return expectChar((char) waitChar);
    }

    /** 期待一个结束字符 */
    public default boolean expectChar(char waitChar) {
        return expectString(String.valueOf(waitChar));
    }

    /** 期待一个结束字符串 */
    public boolean expectString(String waitString);
}