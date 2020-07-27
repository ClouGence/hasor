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
package net.hasor.dataway.web;
import net.hasor.core.spi.SpiTrigger;
import net.hasor.dataql.DataQL;
import net.hasor.web.WebController;

import javax.inject.Inject;

/**
 * 基础
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-03-24
 */
public abstract class BasicController extends WebController {
    @Inject
    protected DataQL     dataQL;
    @Inject
    protected SpiTrigger spiTrigger;
}