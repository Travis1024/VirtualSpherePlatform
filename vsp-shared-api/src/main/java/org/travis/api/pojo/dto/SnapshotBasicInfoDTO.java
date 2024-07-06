package org.travis.api.pojo.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName SnapshotBasicInfoDTO
 * @Description SnapshotBasicInfoDTO
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/7/6
 */
@Data
public class SnapshotBasicInfoDTO implements Serializable {
    private String targetDev;
    private String subPath;
}
