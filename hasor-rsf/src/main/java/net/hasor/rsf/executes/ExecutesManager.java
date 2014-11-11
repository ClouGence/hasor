/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
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
package net.hasor.rsf.executes;
import net.hasor.rsf.executes.queue.TWrite;
import net.hasor.rsf.executes.queue.TrackManager;
/**
 * 
 * @version : 2014年11月11日
 * @author 赵永春(zyc@hasor.net)
 */
public class ExecutesManager {
    private static final int CAPACITY     = 4096;
    private TrackManager     trackManager = new TrackManager(TMEnum.values(), 20, CAPACITY);
    private static enum TMEnum {
        MsgIn
    }
    /**推送消息进来。*/
    public boolean pushMessage(Object msgData) {
        try {
            TWrite write = this.trackManager.waitForWrite(TMEnum.MsgIn);
            return write.pushGood(msgData);
        } finally {
            this.trackManager.switchNext(TMEnum.MsgIn);
        }
    }
    //
    //    public void addMessageProcessing(Class<?> processType, MessageProcessing processing) {
    //        
    //        
    //        
    //        
    //    }
}