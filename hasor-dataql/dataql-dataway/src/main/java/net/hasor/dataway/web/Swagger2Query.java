package net.hasor.dataway.web;
import net.hasor.dataql.CustomizeScope;
import net.hasor.dataql.Finder;
import net.hasor.dataql.Query;
import net.hasor.dataql.QueryResult;
import net.hasor.dataql.compiler.qil.QIL;
import net.hasor.dataql.parser.QueryModel;
import net.hasor.dataql.parser.QueryParseException;
import net.hasor.dataql.runtime.CompilerArguments;
import net.hasor.dataql.runtime.HintsSet;
import net.hasor.dataql.runtime.QueryHelper;
import net.hasor.dataql.runtime.QueryRuntimeException;
import net.hasor.utils.ResourcesUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
// Generated from '/META-INF/hasor-framework/dataway-swagger2.ql'

public class Swagger2Query extends HintsSet implements Query {
    protected final String sourceCode = "/META-INF/hasor-framework/dataway-swagger2.ql";
    protected       Query  dataQuery;

    private Swagger2Query(HintsSet hintsSet) {
        this.setHints(hintsSet);
    }

    public Swagger2Query() throws IOException, QueryParseException {
        this(Finder.DEFAULT, Collections.emptyMap());
    }

    public Swagger2Query(Finder finder, Map<String, Supplier<?>> shareVarMap) throws IOException, QueryParseException {
        Set<String> keySet = shareVarMap.keySet();
        InputStream inputStream = Objects.requireNonNull(ResourcesUtils.getResourceAsStream(sourceCode), sourceCode);
        QueryModel queryModel = QueryHelper.queryParser(inputStream, StandardCharsets.UTF_8);
        QIL queryQil = QueryHelper.queryCompiler(queryModel, new CompilerArguments(keySet), finder);
        this.dataQuery = QueryHelper.createQuery(queryQil, finder);
        this.dataQuery.putShareVar(shareVarMap);
    }

    @Override
    public void addShareVar(String key, Object value) {
        this.dataQuery.addShareVar(key, value);
    }

    @Override
    public QueryResult execute(CustomizeScope customizeScope) throws QueryRuntimeException {
        this.dataQuery.setHints(this);
        return this.dataQuery.execute(customizeScope);
    }

    @Override
    public Swagger2Query clone() {
        Swagger2Query clone = new Swagger2Query(this);
        clone.dataQuery = this.dataQuery.clone();
        return clone;
    }
}
