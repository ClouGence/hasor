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
package net.hasor.rsf.address.route.rule;
import org.more.builder.ToStringStyle;
/**
 *
 * @version : 2015年12月2日
 * @author 赵永春(zyc@hasor.net)
 */
class RuleToStringStyle extends ToStringStyle {
    private static final long serialVersionUID = 1L;
    RuleToStringStyle() {
        super();
        this.setUseShortClassName(true);
        this.setUseIdentityHashCode(false);
    }
    @Override
    protected void appendFieldStart(StringBuffer buffer, String fieldName) {
        if ("logger".equalsIgnoreCase(fieldName) || "routebody".equalsIgnoreCase(fieldName)) {
            return;
        }
        super.appendFieldStart(buffer, fieldName);
    }
    @Override
    protected void appendFieldEnd(StringBuffer buffer, String fieldName) {
        if ("logger".equalsIgnoreCase(fieldName) || "routebody".equalsIgnoreCase(fieldName)) {
            return;
        }
        super.appendFieldEnd(buffer, fieldName);
    }
    @Override
    protected void appendDetail(StringBuffer buffer, String fieldName, Object value) {
        if ("logger".equalsIgnoreCase(fieldName) || "routebody".equalsIgnoreCase(fieldName)) {
            return;
        }
        super.appendDetail(buffer, fieldName, value);
    }
}