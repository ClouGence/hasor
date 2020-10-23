package net.example.hasor.service;
import org.springframework.stereotype.Service;

@Service
public class MyServiceImpl implements MyService {
    @Override
    public String myName() {
        return "zyc";
    }
}