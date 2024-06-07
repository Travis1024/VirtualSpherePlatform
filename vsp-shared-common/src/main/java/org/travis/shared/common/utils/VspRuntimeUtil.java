package org.travis.shared.common.utils;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.RuntimeUtil;

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
public class VspRuntimeUtil {

    public static String execForStr(String... commands) {
        Map<String, String> newEnvMap = new HashMap<>(System.getenv());
        newEnvMap.put("LANG", "en_US.UTF-8");
        Process process = RuntimeUtil.exec(mapToStringArray(newEnvMap), commands);
        return RuntimeUtil.getResult(process, CharsetUtil.CHARSET_UTF_8);
    }

    public static List<String> execForLines(String... commands) {
        Map<String, String> newEnvMap = new HashMap<>(System.getenv());
        newEnvMap.put("LANG", "en_US.UTF-8");
        Process process = RuntimeUtil.exec(mapToStringArray(newEnvMap), commands);
        return RuntimeUtil.getResultLines(process, CharsetUtil.CHARSET_UTF_8);
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
