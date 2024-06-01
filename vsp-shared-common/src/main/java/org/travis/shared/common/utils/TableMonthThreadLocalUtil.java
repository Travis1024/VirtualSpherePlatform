package org.travis.shared.common.utils;

import com.alibaba.ttl.TransmittableThreadLocal;

/**
 * @ClassName TableMonthThreadLocalUtil
 * @Description TableMonthThreadLocalUtil
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/6/1
 */
public class TableMonthThreadLocalUtil {
    /**
     * 每个请求线程维护一个 year、month 数据，避免多线程数据冲突。所以使用 ThreadLocal
     */
    private static final TransmittableThreadLocal<String> MONTH_DATA = new TransmittableThreadLocal<>();

    /**
     * 设置请求线程的 year、month 数据
     * @param yearMonth year、month
     */
    public static void setData(String yearMonth) {
        MONTH_DATA.set(yearMonth);
    }

    /**
     * 获取请求线程的 yearMonth 数据
     */
    public static String getData() {
        return MONTH_DATA.get();
    }

    /**
     * 删除当前请求线程的 yearMonth 数据
     */
    public static void removeData() {
        MONTH_DATA.remove();
    }
}
