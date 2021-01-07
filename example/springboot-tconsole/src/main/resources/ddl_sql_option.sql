CREATE TABLE my_option
(
    `id`          varchar(50)  NOT NULL,
    `key`         varchar(100) NOT NULL,
    `value`       text,
    `desc`        text,
    `create_time` datetime     NOT NULL,
    `modify_time` datetime     NOT NULL,
    PRIMARY KEY (`id`)
);