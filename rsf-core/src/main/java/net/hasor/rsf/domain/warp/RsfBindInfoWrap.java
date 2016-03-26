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
package net.hasor.rsf.domain.warp;
import net.hasor.rsf.RsfBindInfo;
/**
 * {@link RsfBindInfo}包装形式。
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class RsfBindInfoWrap<T> implements RsfBindInfo<T> {
    private RsfBindInfo<T> target;
    public RsfBindInfoWrap(RsfBindInfo<T> target) {
        this.target = target;
    }
    @Override
    public Object getMetaData(String key) {
        return this.target.getMetaData(key);
    }
    @Override
    public String getBindID() {
        return this.target.getBindID();
    }
    @Override
    public String getBindName() {
        return this.target.getBindName();
    }
    @Override
    public String getBindGroup() {
        return this.target.getBindGroup();
    }
    @Override
    public String getBindVersion() {
        return this.target.getBindVersion();
    }
    @Override
    public Class<T> getBindType() {
        return this.target.getBindType();
    }
    @Override
    public int getClientTimeout() {
        return this.target.getClientTimeout();
    }
    @Override
    public String getSerializeType() {
        return this.target.getSerializeType();
    }
    @Override
    public String toString() {
        return "Wrap-" + this.target.toString();
    }
}