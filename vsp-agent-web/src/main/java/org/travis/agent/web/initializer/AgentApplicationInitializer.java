package org.travis.agent.web.initializer;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Assert;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.travis.agent.web.config.StartDependentConfig;

import javax.annotation.Resource;
import java.io.File;
import java.util.Map;

/**
 * @ClassName ApplicationStartPreCheck
 * @Description 启动预检查
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/25
 */
@Slf4j
@Component
public class AgentApplicationInitializer implements CommandLineRunner {

    @Resource
    public StartDependentConfig startDependentConfig;

    @Override
    public void run(String... args) throws Exception {
        log.info("[开始检查依赖文件列表]");
        String filePrefix = startDependentConfig.getFilePrefix();
        Map<String, String> filesMap = startDependentConfig.getFiles();

        if (filesMap != null && !filesMap.isEmpty()) {
            // 失败标识
            boolean successFlag = true;

            for (Map.Entry<String, String> entry : filesMap.entrySet()) {
                String absolutePath = filePrefix + File.separator + entry.getValue();
                boolean checked = FileUtil.exist(absolutePath) && FileUtil.isFile(absolutePath);
                successFlag = checked && successFlag;
                log.info("{} : {} -> {}", entry.getKey(), absolutePath, checked);
            }
            Assert.isTrue(successFlag, () -> {
                log.error("[依赖文件列表检查-Error]");
                return new Exception("依赖文件列表检查失败!");
            });
        }

        log.info("[依赖文件列表检查-Success]");
    }
}
