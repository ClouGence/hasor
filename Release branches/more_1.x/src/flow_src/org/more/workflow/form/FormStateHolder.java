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
package org.more.workflow.form;
import org.more.workflow.context.ApplicationContext;
import org.more.workflow.context.RunContext;
import org.more.workflow.state.AbstractStateHolder;
/**
 * 
 * Date : 2010-5-21
 * @author ’‘”¿¥∫
 */
public class FormStateHolder extends AbstractStateHolder {
    protected FormStateHolder(FormMetadata metadataObject) {
        super(metadataObject);
    };
    @Override
    public void loadState(Object mode, RunContext runContext) {};
    @Override
    public void saveState(Object mode, RunContext runContext) {};
    public void loadForm(ApplicationContext applicationContext, String formID) {};
    public void loadForm(ApplicationContext applicationContext, Form formObject) {};
    public void saveForm(ApplicationContext applicationContext, Form formObject) {};
    public void delete(ApplicationContext applicationContext, Form formObject) {};
};