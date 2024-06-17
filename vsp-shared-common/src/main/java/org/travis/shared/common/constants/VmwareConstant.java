package org.travis.shared.common.constants;

/**
 * @ClassName VmwareConstant
 * @Description VmwareConstant
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/29
 */
public class VmwareConstant {

    public static final String TMP_XML_FOLDER = "/opt/vsp/xml";

    public static final String XML_PARAM_NAME = "name";
    public static final String XML_PARAM_UUID = "uuid";
    public static final String XML_PARAM_MAX_MEMORY = "maxMemory";
    public static final String XML_PARAM_CUR_MEMORY = "curMemory";
    public static final String XML_PARAM_MAX_VCPU = "maxVcpu";
    public static final String XML_PARAM_CUR_VCPU = "curVcpu";
    public static final String XML_PARAM_SYSTEM_DISK_PATH = "systemDiskPath";
    public static final String XML_PARAM_ISO_PATH = "isoPath";

    /**
     * 虚拟机关闭超时时间：毫秒
     */
    public static final Long VMWARE_SHUTDOWN_TIMEOUT = 90 * 1000L;

}
