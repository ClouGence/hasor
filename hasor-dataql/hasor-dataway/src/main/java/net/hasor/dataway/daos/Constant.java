package net.hasor.dataway.daos;
import java.util.function.Function;

public interface Constant {
    public final Function<String, String> ReqBodySchemaPrefix = s -> "ReqBodyType_" + s + "_";
    public final Function<String, String> ReqHeadSchemaPrefix = s -> "ReqHeadType_" + s + "_";
    public final Function<String, String> ResBodySchemaPrefix = s -> "ResBodyType_" + s + "_";
    public final Function<String, String> ResHeadSchemaPrefix = s -> "ResHeadType_" + s + "_";
}