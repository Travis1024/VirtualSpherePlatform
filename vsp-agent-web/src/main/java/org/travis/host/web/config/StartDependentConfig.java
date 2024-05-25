package org.travis.host.web.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.util.List;

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
     * 依赖文件名称列表
     */
    @Getter
    private List<String> files;

    /**
     * 自定义-依赖文件路径前缀处理
     * @return filePrefix
     */
    public String getFilePrefix() {
        return filePrefix.endsWith(File.separator) ? filePrefix.substring(0, filePrefix.length() - File.separator.length()) : filePrefix;
    }
}
