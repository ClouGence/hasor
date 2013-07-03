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
package org.hasor.servlet.action.support;
import java.lang.annotation.Annotation;
import org.hasor.context.AppContext;
import org.hasor.servlet.action.ResultProcess;
/**
 * 
 * @version : 2013-4-11
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
class InternalResultProcess {
    private Class<? extends Annotation>    annoType          = null;
    private Class<? extends ResultProcess> targetProcessType = null;
    private ResultProcess                  targetProcess     = null;
    //
    public InternalResultProcess(Class<? extends Annotation> annoType, Class<? extends ResultProcess> targetProcessType) {
        this.annoType = annoType;
        this.targetProcessType = targetProcessType;
    }
    public InternalResultProcess(Class<? extends Annotation> annoType, ResultProcess targetProcess) {
        this.annoType = annoType;
        this.targetProcess = targetProcess;
        this.targetProcessType = targetProcess.getClass();
    }
    public boolean matchAnno(Annotation anno) {
        if (anno == null)
            return false;
        return annoType.isInstance(anno);
    }
    public ResultProcess getInstance(AppContext appContext) {
        if (this.targetProcess == null)
            this.targetProcess = appContext.getInstance(this.targetProcessType);
        return this.targetProcess;
    }
}