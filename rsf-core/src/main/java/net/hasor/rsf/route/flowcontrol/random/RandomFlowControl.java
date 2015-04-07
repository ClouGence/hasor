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
package net.hasor.rsf.route.flowcontrol.random;
import java.util.List;
import java.util.Random;
import net.hasor.core.Settings;
import net.hasor.rsf.address.InterAddress;
import net.hasor.rsf.route.rule.AbstractRule;
/**
 * 对于一组备选地址，通过随机的方式选取其中一个地址。
 * <pre>
 * 配置实例：
 * &lt;flowControl enable="true|false" type="Random"&gt;
 * &lt;/flowControl&gt;
 * </pre>
 * @version : 2015年4月6日
 * @author 赵永春(zyc@hasor.net)
 */
public class RandomFlowControl extends AbstractRule {
    private Random random = new Random();
    //
    @Override
    public void paserControl(Settings settings) {
        this.random = new Random(System.currentTimeMillis());
    }
    //
    /**使用随机规则选取备选地址中的一个地址。*/
    public InterAddress getServiceAddress(List<InterAddress> addresses) {
        if ((addresses == null) || (addresses.size() == 0)) {
            return null;
        }
        int size = addresses.size();
        if (size == 1) {
            return addresses.get(0);
        }
        int index = this.random.nextInt(size);
        return addresses.get(index);
    }
}