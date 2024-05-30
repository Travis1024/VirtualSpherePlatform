package org.travis.center.message.pojo.vo;

import cn.dev33.satoken.error.SaErrorCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.travis.center.common.enums.MsgModuleEnum;
import org.travis.center.common.enums.MsgStateEnum;

/**
 * @ClassName WsMessageVO
 * @Description WsMessageVO
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/30
 */
@Data
public class WsMessageVO implements SaErrorCode {
    @Schema(description = "消息模块")
    private MsgModuleEnum msgModule;
    @Schema(description = "消息状态")
    private MsgStateEnum msgState;
    @Schema(description = "消息内容")
    private String msgContent;
}
