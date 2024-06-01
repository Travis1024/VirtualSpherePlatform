package org.travis.center.message.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.travis.shared.common.domain.PageQuery;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @ClassName OperationLogQueryDTO
 * @Description OperationLogQueryDTO
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/6/1
 */
@Data
public class OperationLogQueryDTO implements Serializable {
    @Valid
    @NotNull(message = "分页查询对象不能为空!")
    @Schema(description = "分页查询对象")
    public PageQuery pageQuery;

    @Length(min = 6, max = 6, message = "月份信息有误!")
    @NotBlank(message = "查询月份不能为空!")
    @Schema(description = "查询月份", example = "202406")
    public String yyyyMm;
}
