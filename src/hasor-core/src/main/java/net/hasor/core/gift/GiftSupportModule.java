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
package net.hasor.core.gift;
import java.util.Set;
import net.hasor.Hasor;
import net.hasor.core.ApiBinder;
import net.hasor.core.AppContext;
import net.hasor.core.Module;
import net.hasor.core.context.AnnoModule;
/**
 * 支持Bean注解功能。
 * @version : 2013-4-8
 * @author 赵永春 (zyc@hasor.net)
 */
@AnnoModule(description = "net.hasor.core.gift软件包功能支持。")
public class GiftSupportModule implements Module {
    /**初始化.*/
    public void init(ApiBinder apiBinder) {
        boolean giftEnable = apiBinder.getEnvironment().getSettings().getBoolean("hasor.giftSupport", true);
        if (giftEnable == false) {
            Hasor.warning("gift is disable.");
            return;
        }
        Set<Class<?>> giftSet = apiBinder.getEnvironment().getClassSet(Gift.class);
        if (giftSet == null)
            return;
        Hasor.info("find Gift : " + Hasor.logString(giftSet));
        for (Class<?> giftClass : giftSet) {
            if (GiftFace.class.isAssignableFrom(giftClass) == false) {
                Hasor.warning("not implemented GiftFace :%s", giftClass);
                continue;
            }
            try {
                GiftFace giftFace = (GiftFace) giftClass.newInstance();
                Hasor.info("loadGift %s.", giftClass);
                giftFace.loadGift(apiBinder);
            } catch (Throwable e) {
                Hasor.error("config Gift error at %s.%s", giftClass, e);
            }
        }
    }
    /***/
    public void start(AppContext appContext) {}
    /***/
    public void stop(AppContext appContext) {}
}