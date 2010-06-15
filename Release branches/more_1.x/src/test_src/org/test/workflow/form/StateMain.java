package org.test.workflow.form;
import org.more.workflow.context.ApplicationContext;
import org.more.workflow.context.ELContext;
import org.more.workflow.context.RunContext;
import org.more.workflow.event.EventListener;
import org.more.workflow.event.EventPhase;
import org.more.workflow.form.Form;
import org.more.workflow.form.FormMetadata;
import org.more.workflow.form.FormStateHolder;
import org.more.workflow.metadata.PropertyMetadata;
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
public class StateMain {
    /**
     * @param args
     * @throws Throwable 
     */
    public static void main(String[] args) throws Throwable {
        FormMetadata fm = new FormMetadata("userMetadata", User.class);
        fm.addProperty("account", "'testAccount'");
        fm.addProperty(new PropertyMetadata("role.name", "this.account + '≤‚ ‘ ˝æ›'"));
        fm.addProperty(new PropertyMetadata("password", "this.account+',password'"));
        //
        FormStateHolder formState = new FormStateHolder(fm);
        //
        Form form = (Form) formState.newInstance(new RunContext());
        User fb = (User) form.getFormBean();
        formState.updataMode(fb, new ELContext());
        System.out.println(fb.getPassword());
        System.out.println(fb.getRole().getName());
        //
        ApplicationContext app = new ApplicationContext();
        formState.loadState(form.getID(), fb, app);
        formState.saveState(form.getID(), fb, app);
        System.out.println(app);
    };
};