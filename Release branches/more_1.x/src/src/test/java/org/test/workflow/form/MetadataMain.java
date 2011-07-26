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
package org.test.workflow.form;
import org.more.workflow.context.ELContext;
import org.more.workflow.context.RunContext;
import org.more.workflow.event.EventListener;
import org.more.workflow.event.EventPhase;
import org.more.workflow.form.Form;
import org.more.workflow.form.FormMetadata;
import org.more.workflow.form.FormStateHolder;
import org.more.workflow.metadata.PropertyMetadata;
public class MetadataMain {
    /**
     * @param args
     * @throws Throwable
     */
    public static void main(String[] args) throws Throwable {
        FormMetadata fm = new FormMetadata("userMetadata", User.class);
        fm.addProperty("account", "'testAccount'");
        fm.addProperty(new PropertyMetadata("role.name", "this.account + '≤‚ ‘ ˝æ›'"));
        fm.addProperty(new PropertyMetadata("password", "this.account+',password'"));
        fm.getProperty("role.name").addListener(new EventListener() {
            public void doListener(EventPhase event) {
                System.out.println("prop [role.name]\t" + event.getEvent());
            }
        });
        fm.addListener(new EventListener() {
            public void doListener(EventPhase event) {
                System.out.println(event.getEventPhaseType() + "---" + event.getEvent());
            }
        });
        //
        FormStateHolder formState = new FormStateHolder(fm);
        Form formBean = (Form) formState.newInstance(new RunContext());
        formState.updataMode(formBean, new ELContext());
        //
        User fb = (User) ((Form) formState.newInstance(new RunContext())).getTargetBean();
        System.out.println(fb.getPassword());
        System.out.println(fb.getRole().getName());
    };
};