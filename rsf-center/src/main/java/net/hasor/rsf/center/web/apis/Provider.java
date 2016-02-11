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
package net.hasor.rsf.center.web.apis;
import net.hasor.plugins.restful.api.MappingTo;
import net.hasor.plugins.restful.api.Params;
import net.hasor.plugins.valid.Valid;
import net.hasor.rsf.center.core.controller.BaseController;
import net.hasor.rsf.center.domain.form.apis.PushServiceForm;
/**
 * @version : 2015年5月5日
 * @author 赵永春(zyc@hasor.net)
 */
@MappingTo("/apis/provider")
public class Provider extends BaseController {
    public void execute(@Valid("Access") @Params PushServiceForm pushServiceForm) {
        System.out.println("/apis/provider");
    }
}