/*
 * Copyright 2008-2009 the original ’‘”¿¥∫(zyc@hasor.net).
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
package org.hasor.context.setting;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.xml.stream.XMLStreamException;
import org.more.xml.XmlNamespaceParser;
import org.more.xml.XmlStackDecorator;
import org.more.xml.stream.XmlStreamEvent;
/**
 * 
 * @version : 2013-7-13
 * @author ’‘”¿¥∫ (zyc@hasor.net)
 */
class InternalHasorXmlParserPropxy implements XmlNamespaceParser {
    private List<HasorXmlParser> parserList    = new ArrayList<HasorXmlParser>();
    private Map<String, Object>  dataContainer = null;
    private HasorSettings        context       = null;
    //
    public InternalHasorXmlParserPropxy(HasorSettings context, Map<String, Object> dataContainer) {
        this.context = context;
        this.dataContainer = dataContainer;
    }
    void addTarget(HasorXmlParser newInstance) {
        if (newInstance != null)
            this.parserList.add(newInstance);
    }
    @Override
    public void beginAccept() {
        for (HasorXmlParser par : parserList)
            par.beginAccept(this.context, this.dataContainer);
    }
    @Override
    public void endAccept() {
        for (HasorXmlParser par : parserList)
            par.endAccept(this.context, this.dataContainer);
    }
    @Override
    public void sendEvent(XmlStackDecorator<Object> context, String xpath, XmlStreamEvent event) throws IOException, XMLStreamException {
        for (HasorXmlParser par : this.parserList)
            par.sendEvent(context, xpath, event);
    }
}