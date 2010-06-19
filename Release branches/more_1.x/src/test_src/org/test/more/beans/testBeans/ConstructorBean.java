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
public class ConstructorBean {
    public ConstructorBean(int a, float b, String c) {
        System.out.println("create ConstructorBean :" + a + "," + b + "," + c);
    }
    public ConstructorBean(PropBean bean) {
        System.out.println("create ConstructorBean : A=" + bean.getA());
    }
    public ConstructorBean(String arg_0, Object arg_1, String arg_2) {
        System.out.println("create ConstructorBean");
    }
    //===============================================================
    private int[]      intArray;
    private String[]   stringArray;
    private PropBean[] objectArray;
    public ConstructorBean(int[] intArray, String[] stringArray, PropBean[] objectArray) {
        this.intArray = intArray;
        this.stringArray = stringArray;
        this.objectArray = objectArray;
    }
    public int[] getIntArray() {
        return intArray;
    }
    public String[] getStringArray() {
        return stringArray;
    }
    public PropBean[] getObjectArray() {
        return objectArray;
    }
}