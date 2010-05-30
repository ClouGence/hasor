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
package org.test.workflow.propertybinding;
import org.more.workflow.el.PropertyBinding;
import org.more.workflow.event.EventListener;
import org.more.workflow.event.EventPhase;
import org.more.workflow.event.object.SetValueEvent;
import org.test.workflow.form.User;
public class Main {
    /**
     * @param args
     * @throws  Exception 
     */
    public static void main(String[] args) throws Exception {
        User us = new User();
        PropertyBinding pb = new PropertyBinding("role.name", us);
        //
        pb.addListener(new EventListener() {
            @Override
            public void doListener(EventPhase event) {
                System.out.println(event.getEvent());
                /**添加断点查看event对象。*/
                if (event.getEvent() instanceof SetValueEvent) {
                    SetValueEvent se = (SetValueEvent) event.getEvent();
                    se.setNewValue(se.getNewValue() + " newValue");
                }
            }
        });
        System.out.println(pb.getValue());
        pb.setValue("asdf");
        System.out.println(pb.getValue());
    }
}