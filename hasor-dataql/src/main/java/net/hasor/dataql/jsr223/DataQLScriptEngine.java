package net.hasor.dataql.jsr223;
import net.hasor.dataql.Query;
import net.hasor.dataql.UdfManager;
import net.hasor.dataql.UdfSource;
import net.hasor.dataql.domain.compiler.QIL;
import net.hasor.dataql.domain.compiler.QueryCompiler;
import net.hasor.dataql.domain.parser.ParseException;
import net.hasor.dataql.udf.SimpleUdfManager;
import net.hasor.utils.IOUtils;

import javax.script.*;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.util.List;
/**
 *
 */
public class DataQLScriptEngine extends AbstractScriptEngine implements ScriptEngine, Compilable, UdfManager {
    private final DataQLScriptEngineFactory engineFactory;
    private       ClassLoader               loader;
    private final UdfManager                udfManager;
    public DataQLScriptEngine(DataQLScriptEngineFactory engineFactory) {
        this.engineFactory = engineFactory;
        this.loader = getParentLoader();
        this.udfManager = new SimpleUdfManager();
    }
    //
    public ClassLoader getLoader() {
        return loader;
    }
    public void setLoader(ClassLoader loader) {
        this.loader = loader;
    }
    @Override
    public UdfSource getSourceByName(String sourceName) {
        return this.udfManager.getSourceByName(sourceName);
    }
    @Override
    public List<String> getSourceNames() {
        return this.udfManager.getSourceNames();
    }
    @Override
    public void addSource(String sourceName, UdfSource udfSource) {
        this.udfManager.addSource(sourceName, udfSource);
    }
    @Override
    public void addDefaultSource(UdfSource udfSource) {
        this.udfManager.addDefaultSource(udfSource);
    }
    //
    @Override
    public Object eval(Reader queryString, ScriptContext context) throws ScriptException {
        try {
            StringWriter outWriter = new StringWriter();
            IOUtils.copy(queryString, outWriter);
            return this.eval(outWriter.toString(), context);
        } catch (IOException e) {
            throw new ScriptException(e);
        }
    }
    @Override
    public CompiledScript compile(Reader queryString) throws ScriptException {
        try {
            StringWriter outWriter = new StringWriter();
            IOUtils.copy(queryString, outWriter);
            return this.compile(outWriter.toString());
        } catch (IOException e) {
            throw new ScriptException(e);
        }
    }
    @Override
    public ScriptEngineFactory getFactory() {
        return this.engineFactory;
    }
    private static ClassLoader getParentLoader() {
        // check whether thread context loader can "see" Groovy Script class
        ClassLoader ctxtLoader = Thread.currentThread().getContextClassLoader();
        try {
            Class c = ctxtLoader.loadClass(Query.class.getName());
            if (c == Query.class) {
                return ctxtLoader;
            }
        } catch (ClassNotFoundException cnfe) {
            /* ignore */
        }
        // exception was thrown or we get wrong class
        return Query.class.getClassLoader();
    }
    //
    //
    @Override
    public Object eval(String queryString, ScriptContext context) throws ScriptException {
        return compile(queryString).eval(context);
    }
    @Override
    public CompiledScript compile(String queryString) throws ScriptException {
        try {
            QIL compilerQIL = QueryCompiler.compilerQuery(queryString);
            return new DataQLCompiledScript(compilerQIL, this);
        } catch (ParseException e) {
            throw new ScriptException(e);
        }
    }
    @Override
    public Bindings createBindings() {
        return new SimpleBindings();
    }
}