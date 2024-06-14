#!/bin/bash
# 设置 LANG 环境变量为 en_US.UTF-8
export LANG=en_US.UTF-8
# 初始化 vCPU 总数
# 活跃的 vCPU 数量
total_vcpus=0
# 所有已经定义的 vCPU 数量
total_vcpus_all=0
# 宿主机支持的最大 vCPU 数量
total_vcpus_max=$(virsh maxvcpus)

# 获取所有运行中的虚拟机列表
vm_list=$(virsh list --name)
vm_list_all=$(virsh list --name --all)

# 遍历每个虚拟机
for vm in $vm_list; do
    # 获取当前虚拟机的 vCPU 数量
    vcpus=$(virsh dominfo $vm | grep "CPU(s):" | awk '{print $2}')
    # 累加到总数
    total_vcpus=$((total_vcpus + vcpus))
done

# 遍历每个虚拟机
for vm in $vm_list_all; do
    # 获取当前虚拟机的 vCPU 数量
    vcpus=$(virsh dominfo $vm | grep "CPU(s):" | awk '{print $2}')
    # 累加到总数
    total_vcpus_all=$((total_vcpus_all + vcpus))
done

# 输出 vCPU 总数
echo $total_vcpus
echo $total_vcpus_all
echo $total_vcpus_max
