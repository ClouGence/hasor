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
package test.net.hasor.rsf.rpc.provider;
import java.io.IOException;
import java.net.URISyntaxException;
import net.hasor.rsf.RsfSettings;
import net.hasor.rsf.rpc.context.AbstractRsfContext;
/**
 * 100W 打印一次，证明还活着
 * @version : 2014年11月4日
 * @author 赵永春(zyc@hasor.net)
 */
public class TestClientRsfContext extends AbstractRsfContext {
    public TestClientRsfContext(RsfSettings settings) throws IOException, URISyntaxException {
        this.initContext(settings);
    }
}