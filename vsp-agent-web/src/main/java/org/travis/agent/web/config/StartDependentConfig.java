package org.travis.agent.web.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.util.Map;

/**
 * @ClassName StartDependentConfig
 * @Description StartDependentConfig
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/25
 */
@Setter
@Configuration
@ConfigurationProperties(prefix = "vsp.start.dependent")
public class StartDependentConfig {

    /**
     * 依赖文件路径前缀
     */
    private String filePrefix;

    /**
     * 依赖文件名称-Map
     */
    @Getter
    private Map<String, String> files;

    /**
     * 自定义-依赖文件路径前缀处理
     * eg: /opt/vsp/dependent
     *
     * @return filePrefix
     */
    public String getFilePrefix() {
        return filePrefix.endsWith(File.separator) ? filePrefix.substring(0, filePrefix.length() - File.separator.length()) : filePrefix;
    }
}
