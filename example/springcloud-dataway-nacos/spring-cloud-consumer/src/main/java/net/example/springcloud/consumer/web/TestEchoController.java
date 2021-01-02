package net.example.springcloud.consumer.web;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class TestEchoController {
    private final RestTemplate restTemplate;

    @Autowired
    public TestEchoController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @RequestMapping(value = "/consumer/echo/{str}", method = RequestMethod.GET)
    public String echo(@PathVariable String str) {
        String result = restTemplate.getForObject("http://service-provider/echo/" + str, String.class);
        return "action: consumer -> provider.\n result:" + result;
    }
}