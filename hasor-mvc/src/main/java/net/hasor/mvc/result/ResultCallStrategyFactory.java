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
package net.hasor.mvc.result;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.hasor.core.ApiBinder;
import net.hasor.core.AppContext;
import net.hasor.core.AppContextAware;
import net.hasor.core.BindInfo;
import net.hasor.core.Hasor;
import net.hasor.mvc.strategy.CallStrategy;
import net.hasor.mvc.strategy.SimpleCallStrategyFactory;
/**
 * 
 * @version : 2014年8月29日
 * @author 赵永春(zyc@hasor.net)
 */
public class ResultCallStrategyFactory extends SimpleCallStrategyFactory implements AppContextAware {
    public ResultCallStrategyFactory(ApiBinder apiBinder) {
        apiBinder.autoAware(this);
        //1.ResultDefine
        this.loadResultCallStrategy(apiBinder);
    }
    //
    /**装载所有 ResultDefine*/
    protected void loadResultCallStrategy(ApiBinder apiBinder) {
        //1.获取
        Set<Class<?>> resultDefineSet = apiBinder.findClass(ResultDefine.class);
        if (resultDefineSet == null)
            return;
        //2.注册服务
        for (Class<?> resultDefineType : resultDefineSet) {
            if (ResultProcess.class.isAssignableFrom(resultDefineType) == false) {
                Hasor.logWarn("loadResultDefine : not implemented ResultProcess. class=%s", resultDefineType);
                continue;
            }
            ResultDefine resultDefineAnno = resultDefineType.getAnnotation(ResultDefine.class);
            Class<ResultProcess> defineType = (Class<ResultProcess>) resultDefineType;
            Class<?> resultType = resultDefineAnno.value();
            Hasor.logInfo("loadResultDefine annoType is %s toInstance %s", resultType, resultDefineType);
            //
            BindInfo<ResultProcess> processBindInfo = apiBinder.bindType(ResultProcess.class).uniqueName()//
                    .to(defineType).asEagerSingleton().toInfo();/*单例*/
            ResultPrcocessDefine define = apiBinder.autoAware(new ResultPrcocessDefine(resultType, processBindInfo));
            apiBinder.bindType(ResultPrcocessDefine.class).uniqueName().toInstance(define);
        }
    }
    //
    /**初始化*/
    public void setAppContext(AppContext appContext) {
        List<ResultPrcocessDefine> defineList = appContext.findBindingBean(ResultPrcocessDefine.class);
        if (defineList == null || defineList.isEmpty() == true) {
            return;
        }
        for (ResultPrcocessDefine define : defineList) {
            this.defineMap.put(define.getResultType(), define);
        }
    }
    //
    private Map<Class<?>, ResultProcess> defineMap = new HashMap<Class<?>, ResultProcess>();
    public CallStrategy createStrategy(CallStrategy parentCall) {
        parentCall = super.createStrategy(parentCall);
        return new ResultCallStrategy(parentCall, this.defineMap);
    }
}