package net.hasor.dataway.dal.nacos;
import net.hasor.dataway.dal.FieldDef;

import java.util.Map;

public class DataEnt extends ApiJson {
    private Map<FieldDef, String> dataEnt;

    public Map<FieldDef, String> getDataEnt() {
        return dataEnt;
    }

    public void setDataEnt(Map<FieldDef, String> dataEnt) {
        this.dataEnt = dataEnt;
    }
}
