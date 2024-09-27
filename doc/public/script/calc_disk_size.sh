#!/bin/bash
# 设置 LANG 环境变量为 en_US.UTF-8
export LANG=en_US.UTF-8

# 检查是否传入路径参数
if [ -z "$1" ]; then
    echo "Usage: $0 <path>"
    exit 1
fi

# 获取传入的路径参数
PATH_PARAM=$1

# 执行qemu-img info命令并提取virtual size值
VIRTUAL_SIZE=$(qemu-img info "$PATH_PARAM" | grep "virtual size" | awk '{print $3}')

# 输出结果
echo "$VIRTUAL_SIZE"
