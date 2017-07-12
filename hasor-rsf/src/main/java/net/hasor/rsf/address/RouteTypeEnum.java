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
package net.hasor.rsf.address;
/**
 *
 * @version : 2015年10月20日
 * @author 赵永春(zyc@hasor.net)
 */
public enum RouteTypeEnum {
    /***/
    ServiceLevel(1, "服务级"), /***/
    MethodLevel(2, "方法级"), /***/
    ArgsLevel(3, "/参数级"),;
    //
    //
    private int    type;
    private String desc;
    RouteTypeEnum(int type, String desc) {
        this.type = type;
        this.desc = desc;
    }
    @Override
    public String toString() {
        return "Enum[type = " + this.type + " , desc = " + this.desc + "]";
    }
    //
    static boolean updateScript(RouteTypeEnum routeType, String script, RuleRef ref) {
        /*  */
        if (RouteTypeEnum.ServiceLevel.equals(routeType)) {
            return ref.getServiceLevel().update(script);
        } else if (RouteTypeEnum.MethodLevel.equals(routeType)) {
            return ref.getMethodLevel().update(script);
        } else if (RouteTypeEnum.ArgsLevel.equals(routeType)) {
            return ref.getArgsLevel().update(script);
        }
        return false;
    }
}