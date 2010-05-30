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
import org.more.workflow.event.Event;
import org.more.workflow.event.EventPhase;
import org.more.workflow.metadata.AbstractMetadata;
/**
 * 当模型被更新时。
 * Date : 2010-5-21
 * @author 赵永春
 */
public class UpdataModeEvnet extends Event {
    /**  */
    private static final long serialVersionUID = 5010075302608463391L;
    private AbstractMetadata  metadata;
    /**当模型被更新时。*/
    public UpdataModeEvnet(Object targetMode, AbstractMetadata metadata) {
        super("UpdataModeEvnet", targetMode);
        this.metadata = metadata;
    };
    @Override
    protected EventPhase[] createEventPhase() {
        return null;
    };
    public AbstractMetadata getMetadata() {
        return metadata;
    };
};