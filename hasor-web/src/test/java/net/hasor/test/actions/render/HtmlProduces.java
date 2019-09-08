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
package net.hasor.test.actions.render;
import net.hasor.web.render.RenderInvoker;
import net.hasor.web.annotation.Get;
import net.hasor.web.annotation.MappingTo;
import net.hasor.web.annotation.Post;
import net.hasor.web.annotation.Produces;

@MappingTo("/abc.do")
@MappingTo("/def.do")
public class HtmlProduces {
    @Post
    @Produces("test/html")
    public void testProduces1() {
    }

    @Get
    @Produces("text/javacc_jj")
    public void testProduces2(RenderInvoker invoker) {
        invoker.renderTo("/my/my.html");
    }
}