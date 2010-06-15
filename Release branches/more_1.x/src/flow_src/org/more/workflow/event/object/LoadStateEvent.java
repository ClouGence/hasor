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
package org.more.workflow.event.object;
import org.more.util.attribute.IAttribute;
import org.more.workflow.event.Event;
import org.more.workflow.event.EventPhase;
/**
 * ½«ÔËÐÐ×´Ì¬´ÓÉÁ´æ×´Ì¬»Ö¸´Ê±¡£
 * Date : 2010-5-21
 * @author ÕÔÓÀ´º
 */
public class LoadStateEvent extends Event {
    /**  */
    private static final long serialVersionUID = -7074329255747484645L;
    private IAttribute        flash            = null;
    /**½«ÔËÐÐ×´Ì¬´ÓÉÁ´æ×´Ì¬»Ö¸´Ê±¡£*/
    public LoadStateEvent(Object targetMode, IAttribute flash) {
        super("LoadStateEvent", targetMode);
        this.flash = flash;
    };
    @Override
    protected EventPhase[] createEventPhase() {
        return new EventPhase[] { new Event.BeforeEventPhase(), new Event.AfterEventPhase() };
    };
    public IAttribute getFlash() {
        return flash;
    };
};