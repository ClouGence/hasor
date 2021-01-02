package net.example.springcloud.consumer.web;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * try visit http://127.0.0.1:8081/api/abc?message=HelloWord
 */
@RestController
public class TestDatawayController {
    private final RestTemplate restTemplate;

    @Autowired
    public TestDatawayController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @RequestMapping(value = "/consumer/api/{str}", method = RequestMethod.GET)
    public Object echo(@PathVariable String str, @RequestParam() String message) {
        String result = restTemplate.getForObject("http://dataway-provider/api/" + str + "?message=" + message, String.class);
        return "action: consumer -> dataway.\n result:" + result;
    }
}
