#!/bin/bash
# 设置 LANG 环境变量为 en_US.UTF-8
export LANG=en_US.UTF-8

echo "Starting the bridge setup script..."

# 检查参数数量
if [ "$#" -ne 2 ]; then
    echo "Incorrect usage! Expected exactly 2 parameters."
    echo "Usage: $0 <bridgeName> <networkInterface>"
    exit 1
fi

echo "Parameters received correctly."

# 提取参数
BRIDGE_NAME="$1"
NETWORK_INTERFACE="$2"

echo "Creating bridge with name: $BRIDGE_NAME..."
nmcli connection add ifname "$BRIDGE_NAME" type bridge con-name "$BRIDGE_NAME" autoconnect yes
echo "Bridge $BRIDGE_NAME created successfully."

echo "Retrieving IPv4 configuration for interface $NETWORK_INTERFACE..."
IPV4_METHOD=$(nmcli connection show "$NETWORK_INTERFACE" | grep -i "ipv4.method:" | awk '{print $2}')
IPV4_ADDRESSES=$(nmcli connection show "$NETWORK_INTERFACE" | grep -i "ipv4.addresses:" | awk '{print $2}')
IPV4_GATEWAY=$(nmcli connection show "$NETWORK_INTERFACE" | grep -i "ipv4.gateway:" | awk '{print $2}')
IPV4_DNS=$(nmcli connection show "$NETWORK_INTERFACE" | grep -i "ipv4.dns:" | awk '{print $2}')
echo "IPv4 configuration retrieved."

# 将 IP 配置加入网桥中
if [ "$IPV4_ADDRESSES" != "--" ]; then
    echo "Setting IPv4 addresses..."
    nmcli connection modify "$BRIDGE_NAME" ipv4.addresses "$IPV4_ADDRESSES"
    echo "IPv4 addresses set to $IPV4_ADDRESSES."
else
    echo "No IPv4 addresses set, parameter was '--'."
fi

if [ "$IPV4_GATEWAY" != "--" ]; then
    echo "Setting IPv4 gateway..."
    nmcli connection modify "$BRIDGE_NAME" ipv4.gateway "$IPV4_GATEWAY"
    echo "IPv4 gateway set to $IPV4_GATEWAY."
else
    echo "No IPv4 gateway set, parameter was '--'."
fi

if [ "$IPV4_DNS" != "--" ]; then
    echo "Setting IPv4 DNS..."
    nmcli connection modify "$BRIDGE_NAME" ipv4.dns "$IPV4_DNS"
    echo "IPv4 DNS set to $IPV4_DNS."
else
    echo "No IPv4 DNS set, parameter was '--'."
fi

if [ "$IPV4_METHOD" != "--" ]; then
    echo "Setting IPv4 method..."
    nmcli connection modify "$BRIDGE_NAME" ipv4.method "$IPV4_METHOD"
    echo "IPv4 method set to $IPV4_METHOD."
else
    echo "No IPv4 method set, parameter was '--'."
fi

echo "Creating slave connection for $NETWORK_INTERFACE with $BRIDGE_NAME..."
nmcli connection add type bridge-slave ifname "$NETWORK_INTERFACE" master "$BRIDGE_NAME" autoconnect yes
echo "Slave connection created successfully."

echo "Disabling autoconnect for $NETWORK_INTERFACE..."
nmcli connection modify "$NETWORK_INTERFACE" autoconnect no
echo "Autoconnect disabled."

echo "Restarting network connections..."
nmcli connection down "$NETWORK_INTERFACE" && nmcli connection up "$BRIDGE_NAME"
echo "Network connections have been restarted."

echo "Bridge setup script completed successfully."
