package org.travis.host.web.handler;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Assert;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.travis.host.web.config.StartDependentConfig;

import javax.annotation.Resource;
import java.io.File;
import java.util.List;

/**
 * @ClassName ApplicationStartPreCheck
 * @Description 启动预检查
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/25
 */
@Slf4j
@Component
public class ApplicationStartPreCheck implements CommandLineRunner {

    @Resource
    private StartDependentConfig startDependentConfig;

    @Override
    public void run(String... args) throws Exception {
        log.info("[开始检查依赖文件列表]");
        String filePrefix = startDependentConfig.getFilePrefix();
        List<String> files = startDependentConfig.getFiles();

        if (files != null && !files.isEmpty()) {
            // 失败标识
            boolean successFlag = true;
            for (String fileName : files) {
                String absolutePath = filePrefix + File.separator + fileName;
                boolean checked = FileUtil.exist(absolutePath) && FileUtil.isFile(absolutePath);
                successFlag = checked && successFlag;
                log.info("{} -> {}", absolutePath, checked);
            }
            Assert.isTrue(successFlag, () -> {
                log.error("[依赖文件列表检查-Error]");
                return new Exception("依赖文件列表检查失败!");
            });
        }

        log.info("[依赖文件列表检查-Success]");
    }
}
