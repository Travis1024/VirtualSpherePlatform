package org.travis.center.web.initializer;

import cn.hutool.core.util.ClassUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.travis.center.support.jobs.QuartzTestJob;
import org.travis.center.support.pojo.dto.QuartzCreateParamDTO;
import org.travis.center.support.service.QuartzService;

import javax.annotation.Resource;

/**
 * @ClassName QuartzJobsDatabaseInitializer
 * @Description QuartzJobsDatabaseInitializer
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/6/23
 */
@Slf4j
@Component
@Order(Ordered.LOWEST_PRECEDENCE)
public class QuartzJobsDatabaseInitializer implements CommandLineRunner {

    @Resource
    private QuartzService quartzService;

    @Override
    public void run(String... args) throws Exception {
        QuartzCreateParamDTO createParamDTO = new QuartzCreateParamDTO();
        createParamDTO.setJobClazz(ClassUtil.getClassName(QuartzTestJob.class, false));
        createParamDTO.setJobName("test");
        createParamDTO.setJobGroup("test");
        createParamDTO.setTriggerCrontab("0/30 * * * * ?");
        createParamDTO.setJobDescription("测试任务");

        quartzService.addJob(createParamDTO);
    }
}
