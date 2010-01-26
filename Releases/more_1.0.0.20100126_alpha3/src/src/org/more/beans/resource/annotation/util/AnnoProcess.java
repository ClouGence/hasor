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
package org.more.beans.resource.annotation.util;
import java.lang.annotation.Annotation;
/**
 * 注解的处理，beans的所有注解解析都是通过该类的子类实现的。子类需要关注注解出现的在类的哪个层次中然后在对应AnnoContextStack层次进行操作。
 * @version 2010-1-10
 * @author 赵永春 (zyc@byshell.org)
 */
public interface AnnoProcess {
    /** 遇到一个注解时 */
    public void beginAnnotation(Annotation anno, Object atObject, AnnoContextStack context);
    public void endAnnotation(Annotation anno, Object atObject, AnnoContextStack context);
}