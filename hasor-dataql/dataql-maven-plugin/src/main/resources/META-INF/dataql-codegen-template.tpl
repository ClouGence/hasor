%target_pacakge%
import net.hasor.dataql.*;
import net.hasor.dataql.compiler.ParseException;
import net.hasor.dataql.compiler.qil.QIL;
import net.hasor.dataql.runtime.*;
import net.hasor.utils.ResourcesUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

// Generated from '%source_resource%'
public class %target_name% extends HintsSet implements Query {
    protected final String sourceCode = "%source_resource%";
    protected       Query  dataQuery;

    private %target_name%(HintsSet hintsSet) {
        this.setHints(hintsSet);
    }

    public %target_name%() throws IOException, ParseException {
        this(Finder.DEFAULT, Collections.emptyMap());
    }

    public %target_name%(DataQL dataQL) throws IOException, ParseException {
        this(dataQL.getFinder(), dataQL.getShareVarMap());
    }

    public %target_name%(Finder finder, Map<String, VarSupplier> shareVarMap) throws IOException, ParseException {
        Set<String> keySet = shareVarMap.keySet();
        QueryModel queryModel = QueryHelper.queryParser(ResourcesUtils.getResourceAsStream(sourceCode), Charset.forName("UTF-8"));
        QIL queryQil = QueryHelper.queryCompiler(queryModel, keySet, finder);
        this.dataQuery = QueryHelper.createQuery(queryQil, finder);
        if (this.dataQuery instanceof CompilerVarQuery) {
            CompilerVarQuery varQuery = (CompilerVarQuery) this.dataQuery;
            shareVarMap.forEach(new BiConsumer<String, VarSupplier>() {
                @Override
                public void accept(String s, VarSupplier varSupplier) {
                    varQuery.setCompilerVar(s, varSupplier.get());
                }
            });
        }
    }

    @Override
    public QueryResult execute(CustomizeScope customizeScope) throws InstructRuntimeException {
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