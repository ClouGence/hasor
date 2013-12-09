/*
 * Copyright 2002-2006 the original author or authors.
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
package net.hasor.plugins.dao.simple;
import org.more.util.ArrayUtils;
/**
 * 
 * @version : 2013-12-2
 * @author ’‘”¿¥∫(zyc@hasor.net)
 */
class Condition {
    private String        fieldName;
    private Object[]      values;
    private ConditionEnum pattern;
    //
    public Condition() {}
    //
    public Condition(String fieldName, Object value, ConditionEnum pattern) {
        this(fieldName, new Object[] { value }, pattern);
    }
    //
    public Condition(String fieldName, Object[] values, ConditionEnum pattern) {
        this.fieldName = fieldName;
        this.values = values;
        this.pattern = pattern;
    }
    //
    public String getFieldName() {
        return fieldName;
    }
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }
    public ConditionEnum getPattern() {
        return pattern;
    }
    public void setPattern(ConditionEnum pattern) {
        this.pattern = pattern;
    }
    public Object[] getValues() {
        return values;
    }
    public void setValues(Object[] values) {
        this.values = values;
    }
    //
    public String toWereString() {
        StringBuilder queryBuilder = new StringBuilder();
        String fName = getFieldName();
        Object[] fValue = getValues();
        //
        queryBuilder.append(fName);
        queryBuilder.append(" ");
        if (fValue == null || fValue.length == 0)
            queryBuilder.append("is null");
        else {
            if (fValue.length == 1)
                queryBuilder.append(getPattern().getVal() + " ?");
            else {
                queryBuilder.append("in (");
                for (int i = 0; i < fValue.length; i++)
                    queryBuilder.append("?,");
                queryBuilder.deleteCharAt(queryBuilder.length() - 1);
                queryBuilder.append(")");
            }
        }
        //
        return queryBuilder.toString();
    }
    //
    public Object getValue() {
        if (values == null || values.length == 0)
            return null;
        return values[0];
    }
    public void setValue(Object value) {
        if (value == null)
            this.values = null;
        else
            this.values = new Object[] { value };
    }
    public void addValue(Object value) {
        if (this.values != null) {
            if (ArrayUtils.contains(this.values, value) == false)
                this.values = ArrayUtils.add(this.values, value);
        } else {
            this.values = new Object[] { value };
        }
    }
}