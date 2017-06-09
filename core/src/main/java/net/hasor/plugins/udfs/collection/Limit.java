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
package net.hasor.plugins.udfs.collection;
import net.hasor.core.convert.ConverterUtils;
import net.hasor.data.ql.UDF;
import net.hasor.data.ql.Var;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
/**
 * 截取一部分，返回一个集合。
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-06-09
 */
public class Limit extends AbstractCollectionUDF implements UDF {
    @Override
    public Object call(Map<String, Var> values) {
        Var list = values.get("list");
        Collection<Object> objects = super.toCollection(list.getValue());
        if (objects.isEmpty()) {
            return null;
        }
        //
        Var start = values.get("start");
        Var limit = values.get("limit");
        int startInt = (Integer) ConverterUtils.convert(Integer.TYPE, start.getValue());
        int limitInt = (Integer) ConverterUtils.convert(Integer.TYPE, limit.getValue());
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