/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
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
package net.test.simple.mvc._01_simple;
import net.hasor.mvc.MappingTo;
import net.hasor.mvc.ModelController;
/**
 * 
 * @version : 2014年8月27日
 * @author 赵永春(zyc@hasor.net)
 */
public class OperAction implements ModelController {
    @MappingTo("/oper/add")
    public void doAdd() {
        System.out.println("add called.");
    }
    @MappingTo("/oper/del")
    public void doDelete() {
        System.out.println("del called.");
    }
}