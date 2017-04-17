package net.hasor.graphql.dsl;
/**
 * Created by yongchun.zyc on 2017/3/21.
 */
class BindingBuilderWraper implements BindingBuilder {
    private BindingBuilder wraper;
    public BindingBuilderWraper(BindingBuilder wraper) {
        this.wraper = wraper;
    }
    //
    @Override
    public BindingBuilder addField(GraphField graphField) {
        return this.wraper.addField(graphField);
    }
    @Override
    public UDFBindingBuilder byUDF(String udfName) {
        return this.wraper.byUDF(udfName);
    }
    @Override
    public BindingBuilder asListObject() {
        return this.wraper.asListObject();
    }
    @Override
    public BindingBuilder asListValue() {
        return this.wraper.asListValue();
    }
    @Override
    public BindingBuilder asObject() {
        return this.wraper.asObject();
    }
    @Override
    public BindingBuilder asOriginal() {
        return this.wraper.asOriginal();
    }
    @Override
    public GraphField asField() {
        return this.wraper.asField();
    }
    @Override
    public GraphParam asParam() {
        return this.wraper.asParam();
    }
    @Override
    public QueryModel buildQuery() {
        return this.wraper.buildQuery();
    }
    @Override
    public String getName() {
        return this.wraper.getName();
    }
}