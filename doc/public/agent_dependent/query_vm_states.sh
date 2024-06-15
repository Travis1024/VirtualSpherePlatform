#!/bin/bash

# 设置 LANG 环境变量为 en_US.UTF-8
export LANG=en_US.UTF-8

# 获取所有虚拟机的状态
vms=$(virsh list --all)

# 忽略前两行（标识行和分割线）
vms=$(echo "$vms" | tail -n +3)

# 逐行处理每个虚拟机
while IFS= read -r line; do
  # 使用正则表达式匹配虚拟机的 ID、名称和状态
  if [[ "$line" =~ ^[[:space:]]*([0-9-]+)[[:space:]]+([^[:space:]]+)[[:space:]]+(.+)$ ]]; then
    id="${BASH_REMATCH[1]}"
    name="${BASH_REMATCH[2]}"
    state="${BASH_REMATCH[3]}"

    # 获取虚拟机的 UUID
    uuid=$(virsh domuuid "$name" 2>/dev/null)

    # 检查是否成功获取 UUID
    if [ -n "$uuid" ]; then
      # 输出 UUID 和状态字段
      echo "$uuid|$state"
    fi
  fi
done <<< "$vms"
