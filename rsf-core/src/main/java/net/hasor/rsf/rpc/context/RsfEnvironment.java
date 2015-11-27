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
package net.hasor.rsf.rpc.context;
import java.io.IOException;
import net.hasor.core.environment.StandardEnvironment;
import net.hasor.rsf.RsfSettings;
/**
 * 
 * @version : 2014年11月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class RsfEnvironment extends StandardEnvironment {
    public RsfEnvironment(RsfSettings rsfSettings) throws IOException {
        this(null, rsfSettings);
    }
    public RsfEnvironment(Object context, RsfSettings rsfSettings) throws IOException {
        super(context);
        this.initEnvironment(rsfSettings);
    }
    @Override
    public RsfSettings getSettings() {
        return (RsfSettings) super.getSettings();
    }
}