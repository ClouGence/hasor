package net.hasor.dataway.service;
import java.util.Map;

public interface QueryScriptBuild {
    public String buildScript(Map<String, Object> jsonParam);
}
