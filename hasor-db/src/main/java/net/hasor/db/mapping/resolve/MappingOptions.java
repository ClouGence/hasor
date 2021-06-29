package net.hasor.db.mapping.resolve;
import net.hasor.utils.convert.ConverterUtils;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class MappingOptions {
    public static String  OPT_KEY_TO_CAMELCASE = "mapUnderscoreToCamelCase";
    public static String  OPT_KEY_AUTO_MAPPING = "autoMapping";
    public static String  OPT_KEY_OVERWRITE    = "overwrite";
    //
    private       Boolean overwrite;
    private       Boolean autoMapping;
    private       Boolean mapUnderscoreToCamelCase;

    public MappingOptions() {
    }

    public MappingOptions(MappingOptions options) {
        if (options != null) {
            this.overwrite = options.overwrite;
            this.autoMapping = options.autoMapping;
            this.mapUnderscoreToCamelCase = options.mapUnderscoreToCamelCase;
        }
    }

    public Boolean getOverwrite() {
        return this.overwrite;
    }

    public void setOverwrite(Boolean overwrite) {
        this.overwrite = overwrite;
    }

    public Boolean getAutoMapping() {
        return this.autoMapping;
    }

    public void setAutoMapping(Boolean autoMapping) {
        this.autoMapping = autoMapping;
    }

    public Boolean getMapUnderscoreToCamelCase() {
        return this.mapUnderscoreToCamelCase;
    }

    public void setMapUnderscoreToCamelCase(Boolean mapUnderscoreToCamelCase) {
        this.mapUnderscoreToCamelCase = mapUnderscoreToCamelCase;
    }

    public static MappingOptions resolveOptions(Node refData) {
        if (refData == null) {
            return new MappingOptions();
        }
        NamedNodeMap nodeAttributes = refData.getAttributes();
        Node overwriteNode = nodeAttributes.getNamedItem(OPT_KEY_OVERWRITE);
        Node autoMappingNode = nodeAttributes.getNamedItem(OPT_KEY_AUTO_MAPPING);
        Node mapUnderscoreToCamelCaseNode = nodeAttributes.getNamedItem(OPT_KEY_TO_CAMELCASE);
        String overwrite = (overwriteNode != null) ? overwriteNode.getNodeValue() : null;
        String autoMapping = (autoMappingNode != null) ? autoMappingNode.getNodeValue() : null;
        String mapUnderscoreToCamelCase = (mapUnderscoreToCamelCaseNode != null) ? mapUnderscoreToCamelCaseNode.getNodeValue() : null;
        //
        MappingOptions options = new MappingOptions();
        options.overwrite = (overwrite == null) ? null : Boolean.TRUE.equals(ConverterUtils.convert(overwrite, Boolean.TYPE));
        options.autoMapping = (autoMapping == null) ? null : Boolean.TRUE.equals(ConverterUtils.convert(autoMapping, Boolean.TYPE));
        options.mapUnderscoreToCamelCase = (mapUnderscoreToCamelCase == null) ? null : Boolean.TRUE.equals(ConverterUtils.convert(mapUnderscoreToCamelCase, Boolean.TYPE));
        return options;
    }

    public static MappingOptions resolveOptions(Node refData, MappingOptions defaultOptions) {
        MappingOptions options = resolveOptions(refData);
        if (options.overwrite == null) {
            options.overwrite = defaultOptions.overwrite;
        }
        if (options.autoMapping == null) {
            options.autoMapping = defaultOptions.autoMapping;
        }
        if (options.mapUnderscoreToCamelCase == null) {
            options.mapUnderscoreToCamelCase = defaultOptions.mapUnderscoreToCamelCase;
        }
        return options;
    }
}
