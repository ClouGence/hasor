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
package net.hasor.search.client.rsf;
import java.lang.reflect.Method;
import net.hasor.rsf.RsfOptionSet;
import net.hasor.search.client.Commit;
import net.hasor.search.domain.OptionConstant;
import org.more.classcode.delegate.faces.MethodDelegate;
/**
 * 
 * @version : 2015年1月8日
 * @author 赵永春(zyc@hasor.net)
 */
class ServiceWarp implements MethodDelegate, OptionConstant {
    private String            coreName     = null;
    private Commit            commit       = null;
    //
    private WriteOptionFilter optionFilter = null;
    private Object            targetObject = null;
    //
    ServiceWarp(String coreName, WriteOptionFilter optionFilter, Object targetObject) {
        this.coreName = coreName;
        this.optionFilter = optionFilter;
        this.targetObject = targetObject;
    }
    @Override
    public Object invoke(Method callMethod, Object target, Object[] params) throws Throwable {
        RsfOptionSet localOption = this.optionFilter.localOptionSet();
        localOption.addOption(CORE_NAME_KEY, coreName);
        if (this.commit != null) {
            localOption.addOption(COMMIT_KEY, COMMIT_VALUE);
            localOption.addOption(WAIT_FLUSH_KEY, Boolean.toString(this.commit.waitFlush()));
            localOption.addOption(WAIT_SEARCHER_KEY, Boolean.toString(this.commit.waitSearcher()));
            localOption.addOption(SOFT_COMMIT_KEY, Boolean.toString(this.commit.softCommit()));
        }
        return callMethod.invoke(this.targetObject, params);
    }
}