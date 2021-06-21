package net.hasor.db.mapping.resolve;
import net.hasor.db.metadata.CaseSensitivityType;

public class AbstractResolveTableMapping {
    protected CaseSensitivityType caseSensitivity(CaseSensitivityType check, CaseSensitivityType defaultType) {
        return (check == null) ? defaultType : check;
    }

    protected String formatCaseSensitivity(String dataString, CaseSensitivityType sensitivityType) {
        if (sensitivityType == null || dataString == null) {
            return dataString;
        }
        switch (sensitivityType) {
            case Lower: {
                return dataString.toLowerCase();
            }
            case Upper: {
                return dataString.toUpperCase();
            }
            default: {
                return dataString;
            }
        }
    }
}
