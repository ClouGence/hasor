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
package net.example.springcloud.consumer.web;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

/**
 * http://localhost:8081/api/abc?message=HelloWord
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2021-01-02
 */
@RestController
public class TestDatawayController {
    private final RestTemplate restTemplate;

    @Autowired
    public TestDatawayController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @RequestMapping(value = "/api/{str}", method = RequestMethod.GET)
    public Object echo(@PathVariable String str, @RequestParam() String message) {
        String result = restTemplate.getForObject("http://dataway-provider/api/" + str + "?message=" + message, String.class);
        return "action: consumer -> dataway.\n result:" + result;
    }
}
