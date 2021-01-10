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
package net.example.dal.web;
import net.example.dal.dto.OptionInfoDO;
import net.example.dal.ql.DelOptionQuery;
import net.example.dal.ql.GetOptionQuery;
import net.example.dal.ql.ListOptionQuery;
import net.example.dal.ql.SetOptionQuery;
import net.hasor.dataql.DataQL;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;

/**
 *
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2021-01-02
 */
@RestController
public class TestController {
    @Resource
    private DataQL dataQL;

    @PostMapping("/api/dalGet")
    public Object dalGet(@RequestBody() String key) throws IOException {
        return new GetOptionQuery(this.dataQL).execute(new Object[] { key }).getData().unwrap();
    }

    @PostMapping("/api/dalSet")
    public Object dalSet(@RequestBody() OptionInfoDO optionInfo) throws IOException {
        return new SetOptionQuery(this.dataQL).execute(new Object[] { optionInfo }).getData().unwrap();
    }

    @PostMapping("/api/dalList")
    public Object dalList() throws IOException {
        return new ListOptionQuery(this.dataQL).execute().getData().unwrap();
    }

    @PostMapping("/api/dalDelete")
    public Object dalDelete(@RequestBody() String key) throws IOException {
        return new DelOptionQuery(this.dataQL).execute(new Object[] { key }).getData().unwrap();
    }
}
