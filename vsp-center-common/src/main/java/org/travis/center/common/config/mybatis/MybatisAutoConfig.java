package org.travis.center.common.config.mybatis;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.DynamicTableNameInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.travis.shared.common.constants.DatabaseConstant;

import java.util.List;

/**
 * @ClassName MybatisAutoConfig
 * @Description MybatisPlus 配置类
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/4/21
 */
@Configuration
@ConditionalOnClass({MybatisPlusInterceptor.class, BaseMapper.class})
public class MybatisAutoConfig {

    /**
     * 目标月份分表表名
     */
    public static final List<String> TABLE_NAMES = List.of(
            DatabaseConstant.OPERATION_LOG_TABLE_NAME_PREFIX
    );

    @Bean
    @ConditionalOnMissingBean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        // 1.定义 MybatisPlus 插件主体类
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        // 2.增加「动态表名」插件
        DynamicTableNameInnerInterceptor dynamicTableNameInnerInterceptor = new DynamicTableNameInnerInterceptor();
        dynamicTableNameInnerInterceptor.setTableNameHandler(new MybatisMonthTableNameHandler(TABLE_NAMES));
        interceptor.addInnerInterceptor(dynamicTableNameInnerInterceptor);

        // 3.添加「分页」插件
        PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor();
        paginationInnerInterceptor.setDbType(DbType.DM);
        // 设置溢出总页数时默认跳到第一页
        paginationInnerInterceptor.setOverflow(true);
        // 设置分页查询单页最大记录数限制（单页 200 条）
        paginationInnerInterceptor.setMaxLimit(200L);
        interceptor.addInnerInterceptor(paginationInnerInterceptor);

        // 4.添加「字段填充」插件
        interceptor.addInnerInterceptor(new MybatisAutoCompleteInterceptor());

        return interceptor;
    }
}
