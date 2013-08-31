/*
 * Copyright 2008-2009 the original ’‘”¿¥∫(zyc@hasor.net).
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
package org.hasor.action;
import java.util.HashMap;
import org.hasor.mvc.controller.Controller;
import org.hasor.mvc.controller.Get;
import org.hasor.mvc.controller.Path;
import org.hasor.mvc.controller.PathParam;
import org.hasor.mvc.controller.Post;
import org.hasor.mvc.controller.plugins.result.core.Json;
import org.hasor.mvc.controller.support.AbstractController;
/**
 * 
 * @version : 2013-8-23
 * @author ’‘”¿¥∫(zyc@hasor.net)
 */
@Controller()
public class UserAction extends AbstractController {
    @Get//
    @Json//
    @Path("/userMag/{uid}")//
    public Object getUserObject(@PathParam("uid") String userID) {
        System.out.println(String.format("get user %s.", userID));
        HashMap mapData = new HashMap();
        mapData.put("userID", userID);
        mapData.put("name", "”√ªß√˚≥∆");
        return mapData;
    }
    @Post//
    @Path("/userMag/{uid}")//
    public void updateUser(@PathParam("uid") String userID) {
        String name = this.getPara("name");
        System.out.println(String.format("update user %s new Name is %s", userID, name));
    }
}