package org.travis.center.manage.template.vmware.destroy;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.travis.center.common.entity.support.DynamicConfigInfo;
import org.travis.center.common.enums.DynamicConfigAffiliationTypeEnum;
import org.travis.center.common.mapper.support.DynamicConfigInfoMapper;
import org.travis.center.manage.pojo.pipeline.VmwareDestroyPipe;
import org.travis.center.support.processor.AbstractDynamicConfigService;
import org.travis.shared.common.domain.R;
import org.travis.shared.common.enums.BizCodeEnum;
import org.travis.shared.common.exceptions.PipelineProcessException;
import org.travis.shared.common.pipeline.BusinessExecutor;
import org.travis.shared.common.pipeline.ProcessContext;

import javax.annotation.Resource;
import java.util.List;

/**
 * @ClassName VmConfigCleanAction
 * @Description 删除监测任务 + 删除动态配置记录
 * @Author Travis
 * @Data 2024/09
 */
@Slf4j
@Service
public class VmConfigCleanAction implements BusinessExecutor<VmwareDestroyPipe> {
    @Resource
    private DynamicConfigInfoMapper dynamicConfigInfoMapper;

    @Override
    public void execute(ProcessContext<VmwareDestroyPipe> context) {
        VmwareDestroyPipe dataModel = context.getDataModel();

        try {
            log.info("虚拟机动态配置清理「开始」");
            // 1.查询当前虚拟机的所有动态配置
            List<DynamicConfigInfo> dynamicConfigInfos = dynamicConfigInfoMapper.selectList(
                    Wrappers.<DynamicConfigInfo>lambdaQuery()
                            .eq(DynamicConfigInfo::getAffiliationMachineId, dataModel.getVmwareId())
                            .eq(DynamicConfigInfo::getAffiliationType, DynamicConfigAffiliationTypeEnum.VMWARE)
            );

            if (dynamicConfigInfos == null || dynamicConfigInfos.isEmpty()) {
                log.info("当前虚拟机没有动态配置，无需清理");
                return;
            }

            // 2.循环处理
            for (DynamicConfigInfo dynamicConfigInfo : dynamicConfigInfos) {
                AbstractDynamicConfigService dynamicConfigHandler = AbstractDynamicConfigService.getMatchedService(dynamicConfigInfo.getConfigType());
                if (dynamicConfigHandler != null) {
                    dynamicConfigHandler.executeDeleteValue(dynamicConfigInfo.getId());
                } else {
                    log.warn("动态配置类型：{}，不支持清理", dynamicConfigInfo.getConfigType());
                }
            }

        } catch (PipelineProcessException pipelineProcessException) {
            log.error("虚拟机动态配置清理失败：{}", pipelineProcessException.getMessage());
            context.setResponse(R.error(BizCodeEnum.PIPELINE_ERROR.getCode(), "虚拟机动态配置清理失败：" + pipelineProcessException.getMessage()));
            context.setNeedBreak(true);
        } catch (Exception e) {
            log.error("「未知异常」虚拟机动态配置清理失败：{}", e.toString());
            context.setResponse(R.error(BizCodeEnum.PIPELINE_ERROR.getCode(), "「未知异常」虚拟机动态配置清理失败：" + e.getMessage()));
            context.setNeedBreak(true);
        }
    }
}
