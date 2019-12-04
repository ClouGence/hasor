package net.hasor.dataql.compiler;
import java.io.IOException;

public class ParseException extends IOException {
    public ParseException(String s) {
        super(s);
    }
}