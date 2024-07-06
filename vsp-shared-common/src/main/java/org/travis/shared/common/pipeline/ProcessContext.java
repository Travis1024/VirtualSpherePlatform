package org.travis.shared.common.pipeline;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.travis.shared.common.domain.R;

/**
 * @ClassName ProcessContext
 * @Description 责任链上下文
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/7/6
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class ProcessContext<T extends ProcessModel> {
    /**
     * 标识责任链的 businessCode
     */
    private String businessCode;
    /**
     * 存储责任链上下文数据的模型
     */
    private T dataModel;
    /**
     * 责任链中断的标识
     */
    private Boolean needBreak;
    /**
     * 流程处理的结果
     */
    private R<?> response;

    public boolean checkSuccess() {
        if (needBreak == null && response == null) {
            return true;
        }
        if (needBreak == null) {
            return response.checkSuccess();
        }
        if (response == null) {
            return !needBreak;
        }
        return !needBreak && response.checkSuccess();
    }

    public boolean checkFail() {
        return !checkSuccess();
    }
}
