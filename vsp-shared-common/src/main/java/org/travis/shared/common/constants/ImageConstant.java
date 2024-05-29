package org.travis.shared.common.constants;

/**
 * @ClassName ImageConstant
 * @Description ImageConstant
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/14
 */
public class ImageConstant {
    public static final String TMP_IMAGE_PATH_PREFIX = "/tmp/vsp/image";
    public static final String SUB_IMAGE_PATH_PREFIX = "/share_image";

    public static final String SLICE_UPLOAD = "/sliceUpload";
    public static final String SLICE_MERGE = "/sliceMerge";
    public static final String HOST_SLICE_UPLOAD_URI = SystemConstant.AGENT_REQUEST_PREFIX + SystemConstant.AGENT_FILE_CONTROLLER + SLICE_UPLOAD;
    public static final String HOST_SLICE_MERGE_URI = SystemConstant.AGENT_REQUEST_PREFIX + SystemConstant.AGENT_FILE_CONTROLLER + SLICE_MERGE;
}
