package org.travis.shared.common.utils;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;

/**
 * @ClassName VspStrUtil
 * @Description VspStrUtil
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/13
 */
@Slf4j
public class VspStrUtil {

    private static final String PACKAGE_PREFIX = "org.travis";

    /**
     * @MethodName trimObjectStr
     * @Description 对实体类中的所有字符串字段进行 trim 操作
     * @Author travis-wei
     * @Data 2024/5/13
     * @param object	实体对象
     * @Return void
     **/
    public static void trimStr(Object object) {
        if (object == null) {
            return;
        }
        // 获取对象的类对象，用于反射字段
        Class<?> clazz = object.getClass();

        // 遍历所有字段
        for (Field field : clazz.getDeclaredFields()) {
            // 检查字段是否为 String 类型
            if (field.getType().equals(String.class)) {
                try {
                    // 确保私有字段也可访问
                    field.setAccessible(true);
                    // 获取当前字段的值
                    String value = (String) field.get(object);
                    // 如果值不为空，则进行 trim 操作
                    if (value != null) {
                        // 设置 trim 后的值回对象的字段中
                        field.set(object, value.trim());
                    }
                } catch (IllegalAccessException e) {
                    log.error("[VspStrUtil-Error] -> {}", e.getMessage());
                }
            }
        }
    }

    /**
     * @MethodName trimStringsRecursively
     * @Description 对实体类及其指定包下的嵌套实体中的所有字符串字段进行 trim 操作
     * @Author travis-wei
     * @Data 2024/5/13
     * @param object	要处理的实体对象
     * @Return void
     **/
    public static void trimStrRecursively(Object object) {
        trimStrRecursively(object, PACKAGE_PREFIX);
    }

    /**
     * @MethodName trimStringsRecursively
     * @Description 对实体类及其指定包下的嵌套实体中的所有字符串字段进行 trim 操作
     * @Author travis-wei
     * @Data 2024/5/13
     * @param object	要处理的实体对象
     * @param packageName	要递归处理的包名，例如 "org.travis"
     * @Return void
     **/
    public static void trimStrRecursively(Object object, String packageName) {
        if (object == null) {
            return;
        }

        // 获取对象的类对象，用于反射字段
        Class<?> clazz = object.getClass();

        // 遍历所有字段
        for (Field field : clazz.getDeclaredFields()) {
            // 确保私有字段也可访问
            field.setAccessible(true);

            try {
                Object value = field.get(object);
                // 如果字段是 String，进行 trim 操作
                if (value instanceof String) {
                    String stringValue = (String) value;
                    field.set(object, stringValue.trim());
                } else if (value != null && !field.getType().isPrimitive()) {
                    Class<?> fieldType = field.getType();
                    if (fieldType.getPackage() != null && fieldType.getPackage().getName().startsWith(packageName)) {
                        trimStrRecursively(value, packageName);
                    }
                }
            } catch (IllegalAccessException e) {
                log.error("[VspStrUtil-Error] -> {}", e.getMessage());
            }
        }
    }
}
