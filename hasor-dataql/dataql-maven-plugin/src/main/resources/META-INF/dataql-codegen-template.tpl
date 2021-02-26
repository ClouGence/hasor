%target_pacakge%
import net.hasor.dataql.*;
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
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

// Generated from '%source_resource%'
%inject_name%
public class %target_name% extends HintsSet implements Query {
    protected final String sourceCode = "%source_resource%";
    protected       Query  dataQuery;

    private %target_name%(HintsSet hintsSet) {
        this.setHints(hintsSet);
    }

    public %target_name%() throws IOException, QueryParseException {
        this(Finder.DEFAULT, Collections.emptyMap());
    }

    public %target_name%(DataQL dataQL) throws IOException, QueryParseException {
        this(dataQL.getFinder(), dataQL.getShareVarMap());
    }

    public %target_name%(Finder finder, Map<String, Supplier<?>> shareVarMap) throws IOException, QueryParseException {
        Set<String> keySet = shareVarMap.keySet();
        InputStream inputStream = Objects.requireNonNull(ResourcesUtils.getResourceAsStream(sourceCode), sourceCode);
        QueryModel queryModel = QueryHelper.queryParser(inputStream, Charset.forName("UTF-8"));
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
    public %target_name% clone() {
        %target_name% clone = new %target_name%(this);
        clone.dataQuery = this.dataQuery.clone();
        return clone;
    }
}
