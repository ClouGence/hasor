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
package org.more.beans.resource.annotation;
import java.lang.annotation.Annotation;
import javax.xml.stream.XMLStreamReader;
import org.more.beans.resource.annotation.core.Main;
import org.more.beans.resource.xml.TagProcess;
import org.more.beans.resource.xml.XmlContextStack;
/**
 * 
 * @version 2010-1-10
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
public class Tag_Anno extends TagProcess implements AnnoProcess {
    private PackageUtil util = new PackageUtil();
    @Override
    public void doStartEvent(String xPath, XMLStreamReader xmlReader, XmlContextStack context) {
        AnnoEngine ae = new AnnoEngine();
        ae.runTask(Main.class, new Tag_Anno());
    }
    @Override
    public void doAnnotation(Annotation anno, Object atObject, AnnoScopeEnum annoScope, AnnoContextStack context) {
        System.out.println(anno);
    }
}