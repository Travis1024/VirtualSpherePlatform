package org.travis.center.auth.pojo.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.travis.center.common.enums.UserRoleEnum;
import org.travis.center.support.aspect.RequestLockKey;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @ClassName UserModifyRoleDTO
 * @Description UserModifyRoleDTO
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/13
 */
@Data
public class UserModifyRoleDTO implements Serializable {
    /**
     * ID
     */
    @RequestLockKey
    @Schema(description="ID")
    @NotNull(message = "用户ID不能为空!")
    private Long id;

    /**
     * 用户角色类型（1-管理员、2-普通用户）
     */
    @Schema(description="用户角色类型（1-管理员、2-普通用户）")
    @NotNull(message = "用户角色类型不能为空!")
    private UserRoleEnum roleType;
}
