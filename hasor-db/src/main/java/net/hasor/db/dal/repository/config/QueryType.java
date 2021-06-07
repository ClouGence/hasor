package net.hasor.db.dal.repository.config;
public enum QueryType {
    /** Insert 类型 */
    Insert,
    /** Delete 类型 */
    Delete,
    /** Update 类型 */
    Update,
    /** 查询类型 类型 */
    Query,
    /** 调用存储过程 */
    Callable,
    /** Sql 片段，可以被 include */
    Segment
}
