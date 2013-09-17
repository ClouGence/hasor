package org.hasor.test.web.plugin.safety;
import java.util.ArrayList;
import java.util.List;
/**
 * 
 * @version : 2013-7-25
 * @author ’‘”¿¥∫ (zyc@hasor.net)
 */
public class SafetyContext {
    private List<String> powers = new ArrayList<String>();
    public boolean checkPower(String value) {
        return powers.contains(value);
    }
    public void addPower(String power) {
        powers.add(power);
    };
    public void remotePower(String power) {
        powers.remove(power);
    }
    public List<String> getPowers() {
        return powers;
    }
}