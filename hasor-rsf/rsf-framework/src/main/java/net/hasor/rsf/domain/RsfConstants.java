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
package net.hasor.rsf.domain;
/**
 * 各种常量
 * <p>一个RSF数据包头的定义如下:
 *  <li>第 1 个二进制位:表示是否为 RSF 数据包,合法的数据为 0x80 (1000 0000)<li/>
 *  <li>第 2 ~ 4 个二进制位:表示数据包的分类,加上包头合法的数据为 0x80 ~ 0xF0 (1000 0000 ~ 1111 0000)<li/>
 *  <li>最后的 4 个二进制位,用于表示该分类包的版本。可选范围为:0~15 (0000 0000 ~ 0000 1111) <li/>
 * </p>
 * @version : 2014年9月20日
 * @author 赵永春 (zyc@hasor.net)
 */
public interface RsfConstants {
    public static final String AddressList_ZipEntry        = "address.sal";
    public static final String FlowControlRef_ZipEntry     = "flow-control.xml";
    public static final String ServiceLevelScript_ZipEntry = "service-level.groovy";
    public static final String MethodLevelScript_ZipEntry  = "method-level.groovy";
    public static final String ArgsLevelScript_ZipEntry    = "args-level.groovy";
    public static final String AddrPoolStoreName           = "addr-pool-";
    //
    public static final String SnapshotPath                = "/snapshot";
    public static final String SnapshotIndex               = "address.index";
    //
    public static final long   OneHourTime                 = 1 * 60 * 60 * 1000;
    public static final long   SevenDaysTime               = 7 * 24 * OneHourTime;
    //
    // ---------------------------------------------------------------------------------------------
    public static final String LoggerName_Invoker          = "rsf-invoker";
    public static final String LoggerName_Address          = "rsf-address";
}