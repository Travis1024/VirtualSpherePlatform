package org.travis.shared.common.utils;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.RuntimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.travis.shared.common.domain.R;
import org.travis.shared.common.enums.BizCodeEnum;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName VspRuntimeUtil
 * @Description VspRuntimeUtil
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/6/7
 */
@Slf4j
public class VspRuntimeUtil {

    public static R<String> execForStr(String... commands) {
        Map<String, String> newEnvMap = new HashMap<>(System.getenv());
        newEnvMap.put("LANG", "en_US.UTF-8");
        Process process = RuntimeUtil.exec(mapToStringArray(newEnvMap), commands);
        try {
            process.waitFor();
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
            return R.error(BizCodeEnum.INTERNAL_MESSAGE.getCode(), e.getMessage());
        }
        int exitValue = process.exitValue();
        log.warn("exitValue: {}", exitValue);
        return exitValue == 0
                ? R.ok(RuntimeUtil.getResult(process, CharsetUtil.CHARSET_UTF_8))
                : R.error(BizCodeEnum.INTERNAL_MESSAGE.getCode(), RuntimeUtil.getErrorResult(process, CharsetUtil.CHARSET_UTF_8));
    }

    public static R<List<String>> execForLines(String... commands) {
        Map<String, String> newEnvMap = new HashMap<>(System.getenv());
        newEnvMap.put("LANG", "en_US.UTF-8");
        Process process = RuntimeUtil.exec(mapToStringArray(newEnvMap), commands);
        try {
            process.waitFor();
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
            return R.error(0, e.getMessage());
        }
        int exitValue = process.exitValue();
        log.warn("exitValue: {}", exitValue);
        return exitValue == 0
                ? R.ok(RuntimeUtil.getResultLines(process, CharsetUtil.CHARSET_UTF_8))
                : R.error(BizCodeEnum.INTERNAL_MESSAGE.getCode(), RuntimeUtil.getErrorResult(process, CharsetUtil.CHARSET_UTF_8));
    }

    public static String[] mapToStringArray(Map<String, String> map) {
        // 使用Java的stream API将Map转换为String数组
        return map.entrySet().stream()
                // 将每个Entry转换为 "key=value" 格式的字符串
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                // 将流收集到String数组
                .toArray(String[]::new);
    }
}
