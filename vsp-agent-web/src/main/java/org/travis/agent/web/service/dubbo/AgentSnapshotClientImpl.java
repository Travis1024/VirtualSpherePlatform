package org.travis.agent.web.service.dubbo;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.travis.api.client.agent.AgentSnapshotClient;
import org.travis.api.pojo.bo.DiskBasicInfoBO;
import org.travis.api.pojo.dto.SnapshotBasicInfoDTO;
import org.travis.shared.common.domain.R;
import org.travis.shared.common.enums.BizCodeEnum;
import org.travis.shared.common.exceptions.DubboFunctionException;
import org.travis.shared.common.utils.VspRuntimeUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName AgentSnapshotClientImpl
 * @Description AgentSnapshotClientImpl
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/7/6
 */
@Slf4j
@DubboService
public class AgentSnapshotClientImpl implements AgentSnapshotClient {
    @Override
    public R<Void> mergeHistorySnapshot(String targetAgentIp, String vmwareUuid, String autoSnapshotName, String sharedStoragePath, List<SnapshotBasicInfoDTO> historySnapshotBasicInfoList) {
        try {
            // 1.循环合并所有磁盘历史版本
            for (SnapshotBasicInfoDTO snapshotBasicInfoDTO : historySnapshotBasicInfoList) {
                R<String> stringR = VspRuntimeUtil.execForStr(StrUtil.format("virsh blockcommit --domain {} --path {} --verbose --pivot --active", vmwareUuid, snapshotBasicInfoDTO.getTargetDev()));
                Assert.isTrue(stringR.checkSuccess(), () -> new DubboFunctionException(StrUtil.format("虚拟机-{} 历史版本-{} 合并失败:{}", vmwareUuid, autoSnapshotName, stringR.getMsg())));
                String execked = stringR.getData();
                Assert.isTrue(execked.contains("Successfully"), () -> new DubboFunctionException(StrUtil.format("虚拟机-{} 历史版本-{} 合并失败:{}", vmwareUuid, autoSnapshotName, execked)));
            }
            // 2.删除历史版本
            R<String> stringR = VspRuntimeUtil.execForStr(StrUtil.format("virsh snapshot-delete --domain {} --snapshotname {} --children --metadata", vmwareUuid, autoSnapshotName));
            Assert.isTrue(stringR.checkSuccess(), () -> new DubboFunctionException(StrUtil.format("虚拟机-{} 历史版本-{} 删除失败:{}", vmwareUuid, autoSnapshotName, stringR.getMsg())));
            String execked = stringR.getData();
            Assert.isTrue(execked.contains(autoSnapshotName) && execked.contains("deleted"), () -> new DubboFunctionException(StrUtil.format("虚拟机-{} 历史版本-{} 删除失败:{}", vmwareUuid, autoSnapshotName, execked)));

            // 3.手动删除快照文件
            for (SnapshotBasicInfoDTO snapshotBasicInfoDTO : historySnapshotBasicInfoList) {
                FileUtil.del(sharedStoragePath + snapshotBasicInfoDTO.getSubPath());
            }

            return R.ok();
        } catch (Exception e) {
            log.error("[AgentSnapshotClientImpl::mergeHistorySnapshot] Error -> {}", e.getMessage());
            return R.error(BizCodeEnum.DUBBO_FUNCTION_ERROR.getCode(), e.getMessage());
        }
    }

    @Override
    public R<Void> createSnapshot(String targetAgentIp, String vmwareUuid, String autoSnapshotName) {
        try {
            R<String> stringR = VspRuntimeUtil.execForStr(StrUtil.format("virsh snapshot-create-as --domain {} --name {} --atomic --disk-only --quiesce", vmwareUuid, autoSnapshotName));
            Assert.isTrue(stringR.checkSuccess(), () -> new DubboFunctionException(StrUtil.format("虚拟机-{} 快照创建失败:{}", vmwareUuid, stringR.getMsg())));
            String execked = stringR.getData();
            Assert.isTrue(execked.contains(autoSnapshotName) && execked.contains("created"), () -> new DubboFunctionException(StrUtil.format("虚拟机-{} 快照创建失败:{}", vmwareUuid, execked)));
            return R.ok();
        } catch (Exception e) {
            log.error("[AgentSnapshotClientImpl::createSnapshot] Error -> {}", e.getMessage());
            return R.error(BizCodeEnum.DUBBO_FUNCTION_ERROR.getCode(), e.getMessage());
        }
    }

    @Override
    public R<List<DiskBasicInfoBO>> queryDiskBasicInfo(String targetAgentIp, String vmwareUuid) {
        try {
            List<DiskBasicInfoBO> diskBasicInfoList = new ArrayList<>();

            // 1.查询磁盘信息列表
            R<List<String>> listR = VspRuntimeUtil.execForLines(StrUtil.format("virsh domblklist {} --details"));
            Assert.isTrue(listR.checkSuccess(), () -> new DubboFunctionException(StrUtil.format("虚拟机-{} 磁盘信息查询失败!")));
            List<String> data = listR.getData();

            // 2.循环处理
            for (String oneLine : data) {
                oneLine = oneLine.trim();
                if (oneLine.contains("file") && oneLine.contains("disk")) {
                    String[] split = oneLine.split("\\s+");
                    if (split.length == 4) {
                        DiskBasicInfoBO diskBasicInfoBO = new DiskBasicInfoBO();
                        diskBasicInfoBO.setType(split[0]);
                        diskBasicInfoBO.setDevice(split[1]);
                        diskBasicInfoBO.setTarget(split[2]);
                        diskBasicInfoBO.setSource(split[3]);
                        diskBasicInfoList.add(diskBasicInfoBO);
                    }
                }
            }

            return R.ok(diskBasicInfoList);
        } catch (Exception e) {
            log.error("[AgentSnapshotClientImpl::queryDiskBasicInfo] Error -> {}", e.getMessage());
            return R.error(BizCodeEnum.DUBBO_FUNCTION_ERROR.getCode(), e.getMessage());
        }
    }
}
