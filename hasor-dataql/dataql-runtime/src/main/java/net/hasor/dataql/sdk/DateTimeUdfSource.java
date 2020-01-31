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
package net.hasor.dataql.sdk;
import net.hasor.core.provider.InstanceProvider;
import net.hasor.dataql.Finder;
import net.hasor.dataql.Udf;
import net.hasor.dataql.UdfSource;

import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * 时间函数。函数库引入 <code>import 'net.hasor.dataql.sdk.DateTimeUdfSource' as time;</code>
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2019-12-12
 */
public class DateTimeUdfSource implements UdfSource {
    @Override
    public Supplier<Map<String, Udf>> getUdfResource(Finder finder) {
        Supplier<?> supplier = () -> finder.findBean(getClass());
        Predicate<Method> predicate = method -> true;
        return InstanceProvider.of(new TypeUdfMap(getClass(), supplier, predicate));
    }
    // ----------------------------------------------------------------------------------

    /** 返回当前时间戳 long 格式 */
    public long now() {
        return System.currentTimeMillis();
    }

    /** 返回当前系统时区的：年 */
    public int year(long time) {
        Calendar date = Calendar.getInstance();
        date.setTime(new Date(time));
        return date.get(Calendar.YEAR);
    }

    /** 返回当前系统时区的：月 */
    public int month(long time) {
        Calendar date = Calendar.getInstance();
        date.setTime(new Date(time));
        return date.get(Calendar.MONTH) + 1;
    }

    /** 返回当前系统时区的：日 */
    public int day(long time) {
        Calendar date = Calendar.getInstance();
        date.setTime(new Date(time));
        return date.get(Calendar.DAY_OF_MONTH);
    }

    /** 返回当前系统时区的：小时 */
    public int hour(long time) {
        Calendar date = Calendar.getInstance();
        date.setTime(new Date(time));
        return date.get(Calendar.HOUR);
    }

    /** 返回当前系统时区的：分钟 */
    public int minute(long time) {
        Calendar date = Calendar.getInstance();
        date.setTime(new Date(time));
        return date.get(Calendar.MINUTE);
    }

    /** 返回当前系统时区的：秒 */
    public int second(long time) {
        Calendar date = Calendar.getInstance();
        date.setTime(new Date(time));
        return date.get(Calendar.SECOND);
    }

    /** 格式化指定时间 */
    public String format(long time, String pattern) {
        return new SimpleDateFormat(pattern).format(new Date(time));
    }

    /** 解析一个时间日期数据为 long */
    public long parser(String time, String pattern) throws ParseException {
        return new SimpleDateFormat(pattern).parse(time).getTime();
    }
}