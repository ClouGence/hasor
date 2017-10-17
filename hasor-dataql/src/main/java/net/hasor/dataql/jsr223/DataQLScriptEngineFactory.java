package net.hasor.dataql.jsr223;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/**
 *
 */
public class DataQLScriptEngineFactory implements ScriptEngineFactory {
    private static final String VERSION       = "1.0";
    private static final String SHORT_NAME    = "dataql";
    private static final String LANGUAGE_NAME = "DataQL";
    //
    //
    @Override
    public String getEngineName() {
        return "DataQL Engine";
    }
    @Override
    public String getEngineVersion() {
        return VERSION;
    }
    @Override
    public String getLanguageName() {
        return LANGUAGE_NAME;
    }
    @Override
    public String getLanguageVersion() {
        return VERSION;
    }
    //
    @Override
    public List<String> getExtensions() {
        return EXTENSIONS;
    }
    @Override
    public List<String> getMimeTypes() {
        return MIME_TYPES;
    }
    @Override
    public List<String> getNames() {
        return NAMES;
    }
    private static final List<String> NAMES;
    private static final List<String> EXTENSIONS;
    private static final List<String> MIME_TYPES;

    static {
        List<String> n = new ArrayList<String>(2);
        n.add(SHORT_NAME);
        n.add(LANGUAGE_NAME);
        NAMES = Collections.unmodifiableList(n);
        n = new ArrayList<String>(1);
        n.add("dql");
        EXTENSIONS = Collections.unmodifiableList(n);
        n = new ArrayList<String>(1);
        n.add("application/x-dataql");
        MIME_TYPES = Collections.unmodifiableList(n);
    }

    public String getMethodCallSyntax(String obj, String method, String... args) {
        String ret = obj + "." + method + "(";
        int len = args.length;
        if (len == 0) {
            ret += ")";
            return ret;
        }
        for (int i = 0; i < len; i++) {
            ret += args[i];
            if (i != len - 1) {
                ret += ",";
            } else {
                ret += ")";
            }
        }
        return ret;
    }
    public String getOutputStatement(String toDisplay) {
        StringBuilder buf = new StringBuilder();
        buf.append("println(\"");
        int len = toDisplay.length();
        for (int i = 0; i < len; i++) {
            char ch = toDisplay.charAt(i);
            switch (ch) {
            case '"':
                buf.append("\\\"");
                break;
            case '\\':
                buf.append("\\\\");
                break;
            default:
                buf.append(ch);
                break;
            }
        }
        buf.append("\")");
        return buf.toString();
    }
    public String getProgram(String... statements) {
        StringBuilder ret = new StringBuilder();
        int len = statements.length;
        for (int i = 0; i < len; i++) {
            ret.append(statements[i]);
            ret.append('\n');
        }
        return ret.toString();
    }
    //
    public Object getParameter(String key) {
        if (ScriptEngine.NAME.equals(key)) {
            return SHORT_NAME;
        } else if (ScriptEngine.ENGINE.equals(key)) {
            return getEngineName();
        } else if (ScriptEngine.ENGINE_VERSION.equals(key)) {
            return VERSION;
        } else if (ScriptEngine.LANGUAGE.equals(key)) {
            return LANGUAGE_NAME;
        } else if (ScriptEngine.LANGUAGE_VERSION.equals(key)) {
            return getEngineVersion();
        } else {
            throw new IllegalArgumentException("Invalid key");
        }
    }
    @Override
    public ScriptEngine getScriptEngine() {
        return new DataQLScriptEngine(this);
    }
}