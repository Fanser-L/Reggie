CREATE TABLE `setmeal`
(
    `id`          bigint                                          NOT NULL COMMENT '主键',
    `category_id` bigint                                          NOT NULL COMMENT '菜品分类id',
    `name`        varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '套餐名称',
    `price`       decimal(10, 2)                                  NOT NULL COMMENT '套餐价格',
    `status`      int                                                      DEFAULT NULL COMMENT '状态 0:停用 1:启用',
    `code`        varchar(32) CHARACTER SET utf8 COLLATE utf8_bin          DEFAULT NULL COMMENT '编码',
    `description` varchar(512) CHARACTER SET utf8 COLLATE utf8_bin         DEFAULT NULL COMMENT '描述信息',
    `image`       varchar(255) CHARACTER SET utf8 COLLATE utf8_bin         DEFAULT NULL COMMENT '图片',
    `create_time` datetime                                        NOT NULL COMMENT '创建时间',
    `update_time` datetime                                        NOT NULL COMMENT '更新时间',
    `create_user` bigint                                          NOT NULL COMMENT '创建人',
    `update_user` bigint                                          NOT NULL COMMENT '修改人',
    `is_deleted`  int                                             NOT NULL DEFAULT '0' COMMENT '是否删除',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `idx_setmeal_name` (`name`)
);