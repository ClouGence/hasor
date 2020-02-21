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
package net.hasor.dataql.udfs;
import net.hasor.dataql.UdfSourceAssembly;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 时间函数。函数库引入 <code>import 'net.hasor.dataql.udfs.DateTimeUdfSource' as time;</code>
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2019-12-12
 */
public class DateTimeUdfSource implements UdfSourceAssembly {
    /** 返回当前时间戳 long 格式 */
    public static long now() {
        return System.currentTimeMillis();
    }

    /** 返回当前系统时区的：年 */
    public static int year(long time) {
        Calendar date = Calendar.getInstance();
        date.setTime(new Date(time));
        return date.get(Calendar.YEAR);
    }

    /** 返回当前系统时区的：月 */
    public static int month(long time) {
        Calendar date = Calendar.getInstance();
        date.setTime(new Date(time));
        return date.get(Calendar.MONTH) + 1;
    }

    /** 返回当前系统时区的：日 */
    public static int day(long time) {
        Calendar date = Calendar.getInstance();
        date.setTime(new Date(time));
        return date.get(Calendar.DAY_OF_MONTH);
    }

    /** 返回当前系统时区的：小时 */
    public static int hour(long time) {
        Calendar date = Calendar.getInstance();
        date.setTime(new Date(time));
        return date.get(Calendar.HOUR);
    }

    /** 返回当前系统时区的：分钟 */
    public static int minute(long time) {
        Calendar date = Calendar.getInstance();
        date.setTime(new Date(time));
        return date.get(Calendar.MINUTE);
    }

    /** 返回当前系统时区的：秒 */
    public static int second(long time) {
        Calendar date = Calendar.getInstance();
        date.setTime(new Date(time));
        return date.get(Calendar.SECOND);
    }

    /** 格式化指定时间 */
    public static String format(long time, String pattern) {
        return new SimpleDateFormat(pattern).format(new Date(time));
    }

    /** 解析一个时间日期数据为 long */
    public static long parser(String time, String pattern) throws ParseException {
        return new SimpleDateFormat(pattern).parse(time).getTime();
    }
}
