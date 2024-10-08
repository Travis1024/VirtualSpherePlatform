package org.travis.center.manage.template.vmware.destroy;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.travis.center.common.entity.auth.AuthVmwareRelation;
import org.travis.center.common.entity.manage.VmwareInfo;
import org.travis.center.common.entity.manage.VmwareXmlDetails;
import org.travis.center.common.mapper.auth.AuthVmwareRelationMapper;
import org.travis.center.common.mapper.manage.VmwareInfoMapper;
import org.travis.center.common.mapper.manage.VmwareXmlDetailsMapper;
import org.travis.center.manage.pojo.pipeline.VmwareDestroyPipe;
import org.travis.shared.common.domain.R;
import org.travis.shared.common.enums.BizCodeEnum;
import org.travis.shared.common.exceptions.PipelineProcessException;
import org.travis.shared.common.pipeline.BusinessExecutor;
import org.travis.shared.common.pipeline.ProcessContext;

import javax.annotation.Resource;

/**
 * @ClassName VmDeleteRecordAction
 * @Description 删除虚拟机数据库记录（vm + vmXml + vm权限关联）
 * @Author Travis
 * @Data 2024/09
 */
@Slf4j
@Service
public class VmDeleteRecordAction implements BusinessExecutor<VmwareDestroyPipe> {
    @Resource
    private VmwareXmlDetailsMapper vmwareXmlDetailsMapper;
    @Resource
    private AuthVmwareRelationMapper authVmwareRelationMapper;
    @Resource
    private VmwareInfoMapper vmwareInfoMapper;

    @Transactional
    @Override
    public void execute(ProcessContext<VmwareDestroyPipe> context) {
        VmwareDestroyPipe dataModel = context.getDataModel();
        VmwareInfo vmwareInfo = dataModel.getVmwareInfo();

        try {
            // 删除虚拟机xml记录
            vmwareXmlDetailsMapper.delete(Wrappers.<VmwareXmlDetails>lambdaQuery().eq(VmwareXmlDetails::getId, vmwareInfo.getId()));
            // 删除虚拟机权限关联记录
            authVmwareRelationMapper.delete(Wrappers.<AuthVmwareRelation>lambdaQuery().eq(AuthVmwareRelation::getVmwareId, vmwareInfo.getId()));
            // 删除虚拟机记录
            vmwareInfoMapper.delete(Wrappers.<VmwareInfo>lambdaQuery().eq(VmwareInfo::getId, vmwareInfo.getId()));

            log.info("虚拟机数据库记录删除成功！");

        } catch (PipelineProcessException pipelineProcessException) {
            log.error("虚拟机数据库记录删除失败：{}", pipelineProcessException.getMessage());
            context.setResponse(R.error(BizCodeEnum.PIPELINE_ERROR.getCode(), "虚拟机数据库记录删除失败：" + pipelineProcessException.getMessage()));
            context.setNeedBreak(true);
        } catch (Exception e) {
            log.error("「未知异常」虚拟机数据库记录删除失败：{}", e.toString());
            context.setResponse(R.error(BizCodeEnum.PIPELINE_ERROR.getCode(), "「未知异常」虚拟机数据库记录删除失败：" + e.getMessage()));
            context.setNeedBreak(true);
        }
    }
}
