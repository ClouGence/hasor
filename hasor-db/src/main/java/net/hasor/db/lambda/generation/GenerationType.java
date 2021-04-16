package net.hasor.db.lambda.generation;
public enum GenerationType {
    //    native: 对于 oracle 采用 Sequence 方式，对于MySQL 和 SQL Server 采用identity（自增主键生成机制），native就是将主键的生成工作交由数据库完成，hibernate不管（很常用）。
    //
    //    uuid: 采用128位的uuid算法生成主键，uuid被编码为一个32位16进制数字的字符串。占用空间大（字符串类型）。
    //
    //    hilo: 使用hilo生成策略，要在数据库中建立一张额外的表，默认表名为hibernate_unique_key,默认字段为integer类型，名称是next_hi（比较少用）。
    //
    //    assigned: 在插入数据的时候主键由程序处理（很常用），这是 <generator>元素没有指定时的默认生成策略。等同于JPA中的AUTO。
    //
    //    identity: 使用SQL Server 和 MySQL 的自增字段，这个方法不能放到 Oracle 中，Oracle 不支持自增字段，要设定sequence（MySQL 和 SQL Server 中很常用）。 等同于JPA中的INDENTITY。
    //
    //    select: 使用触发器生成主键（主要用于早期的数据库主键生成机制，少用）。
    //
    //    sequence: 调用底层数据库的序列来生成主键，要设定序列名，不然hibernate无法找到。
    //
    //    seqhilo: 通过hilo算法实现，但是主键历史保存在Sequence中，适用于支持 Sequence 的数据库，如 Oracle（比较少用）
    //
    //    increment: 插入数据的时候hibernate会给主键添加一个自增的主键，但是一个hibernate实例就维护一个计数器，所以在多个实例运行的时候不能使用这个方法。
    //
    //    foreign: 使用另外一个相关联的对象的主键。通常和<one-to-one>联合起来使用。
    //
    //    guid: 采用数据库底层的guid算法机制，对应MYSQL的uuid()函数，SQL Server的newid()函数，ORACLE的rawtohex(sys_guid())函数等。
    //
    //    uuid.hex: 看uuid，建议用uuid替换。
    //
    //    sequence-identity: sequence策略的扩展，采用立即检索策略来获取sequence值，需要JDBC3.0和JDK4以上（含1.4）版本
}