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
package org.more.workflow.context;
import java.util.UUID;
import org.more.workflow.form.FormBean;
import org.more.workflow.form.FormMetadata;
import org.more.workflow.runtime.Runtime;
import org.more.workflow.runtime.RuntimeMetadata;
/**
 * 
 * Date : 2010-5-17
 * @author Administrator
 */
public class ApplicationContext {
    private FlashSession flashSession;
    /**获取硬Session，硬Session是将数据存放在磁盘文件。*/
    public FlashSession getHardSession() {
        return flashSession;
    };
    /**获取软Session，软Session是将数据存放在内存。*/
    public FlashSession getSoftSession() {
        return flashSession;
    };
    public FormFactory getFormFactory() {
        return new FormFactory() {
            @Override
            public FormBean getFormBean(RunContext runContext, FormMetadata formMetadata) {
                try {
                    return formMetadata.getFormType().newInstance();
                } catch (Exception e) {}
                return null;
            }
            @Override
            public String generateID(RunContext runContext, FormBean formBean) {
                return UUID.randomUUID().toString();
            }
        };
    };
    public NodeFactory getNodeFactory() {
        return null;
    };
    public RuntimeFactory getRuntimeFactory() {
        return new RuntimeFactory() {
            @Override
            public Runtime getRuntime(RunContext runContext, RuntimeMetadata runtimeMetadata) {
                try {
                    return runtimeMetadata.getRuntimeType().newInstance();
                } catch (Exception e) {}
                return null;
            }
            @Override
            public String generateID(RunContext runContext, Runtime runtime) {
                return UUID.randomUUID().toString();
            }
        };
    };
};