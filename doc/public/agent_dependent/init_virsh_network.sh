#!/bin/bash
# 设置 LANG 环境变量为 en_US.UTF-8
export LANG=en_US.UTF-8
# 检查是否提供了足够的参数
if [ "$#" -ne 1 ]; then
    echo "使用方法: $0 [bridge-name]"
    exit 1
fi

# 从命令行参数获取网桥名称
BRIDGE_NAME=$1

# 创建一个临时 XML 文件
TMPFILE=$(mktemp)
cat > $TMPFILE <<EOF
<network>
    <name>bridged-network</name>
    <forward mode="bridge"/>
    <bridge name="$BRIDGE_NAME"/>
</network>
EOF

# 使用 virsh 定义网络
virsh net-define $TMPFILE

# 删除临时文件
rm $TMPFILE

# 可以在此处添加启动和自动启动网络的命令
echo "网络已定义-启动网络"
virsh net-start bridged-network
echo "网络启动成功!"
echo "设置网络自动启动"
virsh net-autostart bridged-network
echo "网络自启动成功"
