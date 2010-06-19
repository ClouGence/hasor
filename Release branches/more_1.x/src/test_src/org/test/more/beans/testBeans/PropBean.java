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
package org.test.more.beans.testBeans;
/**
 * 
 * <br/>Date : 2009-11-25
 * @author Administrator
 */
public class PropBean extends CollectionBean {
    private int    a;
    private float  b;
    private String c;
    //===================
    public int getA() {
        return a;
    }
    public void setA(int a) {
        this.a = a;
    }
    public float getB() {
        return b;
    }
    public void setB(float b) {
        this.b = b;
    }
    public String getC() {
        return c;
    }
    public void setC(String c) {
        this.c = c;
    }
    //===================================================
    private PropBean bean;
    public PropBean getBean() {
        return bean;
    }
    public void setBean(PropBean bean) {
        this.bean = bean;
    }
    //===================================================
    private String arg_0;
    private Object arg_1;
    private String arg_2;
    public String getArg_0() {
        return arg_0;
    }
    public void setArg_0(String arg_0) {
        this.arg_0 = arg_0;
    }
    public Object getArg_1() {
        return arg_1;
    }
    public void setArg_1(Object arg_1) {
        this.arg_1 = arg_1;
    }
    public String getArg_2() {
        return arg_2;
    }
    public void setArg_2(String arg_2) {
        this.arg_2 = arg_2;
    }
    //===================================================
    private int[]      intArray;
    private String[]   stringArray;
    private PropBean[] propArray;
    private Object[]   objectArray;
    public int[] getIntArray() {
        return intArray;
    }
    public void setIntArray(int[] intArray) {
        this.intArray = intArray;
    }
    public String[] getStringArray() {
        return stringArray;
    }
    public void setStringArray(String[] stringArray) {
        this.stringArray = stringArray;
    }
    public PropBean[] getPropArray() {
        return propArray;
    }
    public void setPropArray(PropBean[] propArray) {
        this.propArray = propArray;
    }
    public Object[] getObjectArray() {
        return objectArray;
    }
    public void setObjectArray(Object[] objectArray) {
        this.objectArray = objectArray;
    }
    //===================================================
}