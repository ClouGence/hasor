package net.hasor.graphql.dsl;
import net.hasor.graphql.domain.ValueType;
/**
 * Created by yongchun.zyc on 2017/3/23.
 */
class FieldBindingBuilderImpl implements FieldBindingBuilder {
    private BindingBuilder inQuery;
    public FieldBindingBuilderImpl(BindingBuilder inQuery) {
        this.inQuery = inQuery;
    }
    @Override
    public UDFBindingBuilder withUDF(String udfName) {
        return this.inQuery.byUDF(udfName);
    }
    @Override
    public GraphField withMapping(String fieldName) {
        return new RouteField(this.inQuery.getName(), fieldName);
    }
    @Override
    public GraphField withFragment(GraphQuery graphQuery) {
        return new QueryField(this.inQuery.getName(), graphQuery.getDomain());
    }
    @Override
    public GraphField withNull() {
        return new ValueField(this.inQuery.getName(), null, ValueType.Null);
    }
    @Override
    public GraphField withBoolean(boolean value) {
        return new ValueField(this.inQuery.getName(), value, ValueType.Boolean);
    }
    @Override
    public GraphField withNumber(Number value) {
        return new ValueField(this.inQuery.getName(), value, ValueType.Number);
    }
    @Override
    public GraphField withString(String value) {
        return new ValueField(this.inQuery.getName(), value, ValueType.String);
    }
    @Override
    public BindingBuilder asObject() {
        return this.inQuery.asObject();
    }
    @Override
    public BindingBuilder asListObject() {
        return this.inQuery.asListObject();
    }
    @Override
    public BindingBuilder asListValue() {
        return this.inQuery.asListValue();
    }
}