package org.travis.shared.common.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.travis.shared.common.enums.MsgModuleEnum;
import org.travis.shared.common.enums.MsgStateEnum;

/**
 * @ClassName WsMessageVO
 * @Description WsMessageVO
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/30
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WebSocketMessage {
    @Schema(description = "消息模块")
    private MsgModuleEnum msgModule;
    @Schema(description = "消息状态")
    private MsgStateEnum msgState;
    @Schema(description = "消息内容")
    private String msgContent;
}
