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
package net.hasor.dataql.udfs.collection;
import net.hasor.core.convert.ConverterUtils;
import net.hasor.dataql.UDF;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
/**
 * 截取一部分，返回一个集合。
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-06-09
 */
public class Limit extends AbstractCollectionUDF implements UDF {
    @Override
    public Object call(Object[] values) {
        Collection<Object> objects = super.toCollection(values[0]);
        if (objects.isEmpty()) {
            return null;
        }
        //
        Object start = values[1];
        Object limit = values[2];
        int startInt = (Integer) ConverterUtils.convert(Integer.TYPE, start);
        int limitInt = (Integer) ConverterUtils.convert(Integer.TYPE, limit);
        if (limitInt <= 0) {
            limitInt = Integer.MAX_VALUE;
        }
        //
        int curIndex = 0;
        Iterator<Object> iterator = objects.iterator();
        ArrayList<Object> finalList = new ArrayList<Object>();
        while (iterator.hasNext()) {
            Object curData = iterator.next();
            curIndex++;
            if (startInt <= curIndex && curIndex <= limitInt) {
                finalList.add(curData);
            }
        }
        return finalList;
    }
}