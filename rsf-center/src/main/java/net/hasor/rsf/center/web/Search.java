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
package net.hasor.rsf.center.web;
import java.io.IOException;
import org.apache.ibatis.annotations.Param;
import net.hasor.core.Inject;
import net.hasor.plugins.restful.api.MappingTo;
import net.hasor.rsf.center.core.controller.BaseController;
import net.hasor.rsf.center.domain.daos.DaoProvider;
/**
 * @version : 2015年7月27日
 * @author 赵永春(zyc@hasor.net)
 */
@MappingTo("/search.do")
public class Search extends BaseController {
    @Inject
    private DaoProvider daoProvider;
    //
    public void execute(@Param("type") String type, @Param("mode") String mode) throws IOException {
        logger.info("request :" + getRequestURI());
        //
        this.renderTo("/search/result.htm");
    }
}