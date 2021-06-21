package net.hasor.db.mapping.resolve;
import net.hasor.db.mapping.TableMapping;
import net.hasor.db.metadata.MetaDataService;
import net.hasor.db.types.TypeHandlerRegistry;

import java.sql.SQLException;

public interface ResolveTableMapping<T> {
    public TableMapping resolveTableMapping(T refData, TypeHandlerRegistry typeRegistry, MetaDataService metaDataService) throws SQLException;
}
