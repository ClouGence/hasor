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
package org.more.beans.resource.annotation.core;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import org.more.beans.resource.annotation.util.AnnoContextStack;
import org.more.beans.resource.annotation.util.AnnoProcess;
/**
 * 扫描一个class并且解析它的注解配置。以生成BeanDefinition对象
 * @version 2010-1-14
 * @author 赵永春 (zyc@byshell.org)
 */
public class Scan_ClassAnno implements AnnoProcess {
    private HashMap<Class<? extends Annotation>, AnnoProcess> annoProcess;
    @Override
    public void beginAnnotation(Annotation anno, Object atObject, AnnoContextStack context) {
        AnnoProcess ap = this.annoProcess.get(anno.annotationType());
        if (ap != null)
            ap.beginAnnotation(anno, atObject, context);
    }
    @Override
    public void endAnnotation(Annotation anno, Object atObject, AnnoContextStack context) {
        AnnoProcess ap = this.annoProcess.get(anno.annotationType());
        if (ap != null)
            ap.endAnnotation(anno, atObject, context);
    }
    public void init() {
        this.annoProcess = new HashMap<Class<? extends Annotation>, AnnoProcess>();
    }
    public void destroy() {
        this.annoProcess.clear();
        this.annoProcess = null;
    }
    public void regeditAnno(Class<? extends Annotation> forAnno, AnnoProcess process) {
        if (this.annoProcess.containsKey(forAnno) == false)
            annoProcess.put(forAnno, process);
    }
}