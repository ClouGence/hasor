package net.hasor.db.mapping.resolve;
import net.hasor.utils.convert.ConverterUtils;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class MappingOptions {
    public static String  OPT_KEY_TO_CAMELCASE = "mapUnderscoreToCamelCase";
    public static String  OPT_KEY_AUTO_MAPPING = "autoMapping";
    public static String  OPT_KEY_OVERWRITE    = "overwrite";
    //
    private       boolean overwrite;
    private       boolean autoMapping;
    private       boolean mapUnderscoreToCamelCase;

    public MappingOptions() {
    }

    public MappingOptions(MappingOptions options) {
        if (options != null) {
            this.overwrite = options.overwrite;
            this.autoMapping = options.autoMapping;
            this.mapUnderscoreToCamelCase = options.mapUnderscoreToCamelCase;
        }
    }

    public boolean isOverwrite() {
        return this.overwrite;
    }

    public void setOverwrite(boolean overwrite) {
        this.overwrite = overwrite;
    }

    public boolean isAutoMapping() {
        return this.autoMapping;
    }

    public void setAutoMapping(boolean autoMapping) {
        this.autoMapping = autoMapping;
    }

    public boolean isMapUnderscoreToCamelCase() {
        return this.mapUnderscoreToCamelCase;
    }

    public void setMapUnderscoreToCamelCase(boolean mapUnderscoreToCamelCase) {
        this.mapUnderscoreToCamelCase = mapUnderscoreToCamelCase;
    }

    public static MappingOptions resolveOptions(Node refData) {
        NamedNodeMap nodeAttributes = refData.getAttributes();
        Node overwriteNode = nodeAttributes.getNamedItem(OPT_KEY_OVERWRITE);
        Node autoMappingNode = nodeAttributes.getNamedItem(OPT_KEY_AUTO_MAPPING);
        Node mapUnderscoreToCamelCaseNode = nodeAttributes.getNamedItem(OPT_KEY_TO_CAMELCASE);
        String overwrite = (overwriteNode != null) ? overwriteNode.getNodeValue() : null;
        String autoMapping = (autoMappingNode != null) ? autoMappingNode.getNodeValue() : null;
        String mapUnderscoreToCamelCase = (mapUnderscoreToCamelCaseNode != null) ? mapUnderscoreToCamelCaseNode.getNodeValue() : null;
        //
        MappingOptions options = new MappingOptions();
        options.overwrite = Boolean.TRUE.equals(ConverterUtils.convert(overwrite, Boolean.TYPE));
        options.autoMapping = Boolean.TRUE.equals(ConverterUtils.convert(autoMapping, Boolean.TYPE));
        options.mapUnderscoreToCamelCase = Boolean.TRUE.equals(ConverterUtils.convert(mapUnderscoreToCamelCase, Boolean.TYPE));
        return options;
    }
}
