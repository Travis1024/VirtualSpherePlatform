## 一、安装 kvm-qemu

### 1、安装 kvm

```shell
# 安装 kvm
yum -y install qemu-kvm libvirt virt-install bridge-utils
# 验证模块是否加载成功
lsmod | grep kvm
# 启动虚拟化和开机自启动
systemctl start libvirtd
systemctl enable libvirtd
systemctl list-unit-files |grep libvirtd.service
```

### 2、修改 libvirtd 权限

```shell
vim /etc/libvirt/qemu.conf
# 添加以下内容
user = "root"
group = "root"
# 重启
systemctl restart libvirtd.service
```



## 二、创建桥接网络流程

### 1、执行 shell 脚本

```shell
#!/bin/bash

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
```

确保将此脚本保存为文件，例如 `init_bridge.sh`，并赋予其执行权限：

```shell
chmod +x init_bridge.sh
```

执行脚本时，提供网桥名称和物理网络接口名称作为参数。例如：

```shell
./setup_bridge.sh br0-vsp-p4p1 p4p1
```

这样，脚本不仅会执行所需的命令，还会提供足够的输出信息，让用户能够清楚地看到每个步骤的进展和任何可能的问题。

### 2、virsh 创建新虚拟网络

- 创建 xml 文件 `bridged-network.xml`

  ```xml
  <network>
      <name>bridged-network</name>
      <forward mode="bridge" />
      <bridge name="br0-vsp-p4p1" />
  </network>
  ```

- 执行定义命令

  ```shell
  virsh net-define bridged-network.xml
  virsh net-start bridged-network
  virsh net-autostart bridged-network
  
  # 验证命令
  virsh net-list --all
  ```

- 全部替换为 shell 脚本执行

  ```shell
  #!/bin/bash
  
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
  ```

  ```shell
  chmod +x ./init_virsh_network.sh
  ./init_virsh_network.sh br0-vsp-p4p1
  ```



## 三、查询虚拟机 IP 地址

`virsh domifaddr kylin-1` 无输出结果

采用以下命令：`virsh domifaddr kylin-1 --source agent`，输出结果如下：

```shell
[root@kylin1 ~]# virsh domifaddr kylin-1 --source agent
 名称     MAC 地址           Protocol     Address
-------------------------------------------------------------------------------
 lo         00:00:00:00:00:00    ipv4         127.0.0.1/8
 -          -                    ipv6         ::1/128
 ens3       52:54:00:4c:ee:9c    ipv4         192.168.0.104/24
 -          -                    ipv6         fe80::dcab:7ce6:298e:16b/64
```



## 四、Dubbo 接口

- **所有 Dubbo 接口第一个参数必须为 `String targetAgentIp`，否则无法进行路由。**



## 五、网卡名称修改

- shell 脚本

  ```shell
  #!/bin/bash
  
  # 检查参数数量
  if [ "$#" -ne 2 ]; then
      echo "使用方式: $0 [新网卡名称] [网卡的 MAC 地址]"
      exit 1
  fi
  
  # 从命令行参数获取网卡名称和 MAC 地址
  NEW_NAME=$1
  MAC_ADDRESS=$2
  
  # 创建或修改 udev 规则文件
  echo "正在创建或修改 udev 规则..."
  RULE_FILE="/etc/udev/rules.d/77-persistent-net.rules"
  echo "SUBSYSTEM==\"net\", ACTION==\"add\", DRIVERS==\"?*\", ATTR{address}==\"$MAC_ADDRESS\", NAME=\"$NEW_NAME\"" > $RULE_FILE
  
  # 重新加载 udev 规则
  echo "重新加载 udev 规则..."
  sudo udevadm control --reload-rules
  sudo udevadm trigger
  
  # 提示用户可能需要重启计算机
  echo "请考虑重启计算机以使更改生效。"
  ```




## 六、获取宿主机虚拟 vCPU 使用数量

```shell
#!/bin/bash

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
    vcpus=$(virsh dominfo $vm | grep "CPU：" | awk '{print $2}')
    # 累加到总数
    total_vcpus=$((total_vcpus + vcpus))
done

# 遍历每个虚拟机
for vm in $vm_list_all; do
    # 获取当前虚拟机的 vCPU 数量
    vcpus=$(virsh dominfo $vm | grep "CPU：" | awk '{print $2}')
    # 累加到总数
    total_vcpus_all=$((total_vcpus_all + vcpus))
done

# 输出 vCPU 总数
echo $total_vcpus
echo $total_vcpus_all
echo $total_vcpus_max
```



## 七、查询 qemu-img 磁盘镜像的大小

```shell
#!/bin/bash
  
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
```



## 八、注意修改 zoo.cfg

```properties
dataDir=/data
clientPort=2181
dataLogDir=/datalog
tickTime=2000
minSessionTimeout=4000
maxSessionTimeout=20000
initLimit=10
syncLimit=5
autopurge.snapRetainCount=5
autopurge.purgeInterval=24
maxClientCnxns=200
skipACL=yes
standaloneEnabled=true
admin.enableServer=true
server.1=localhost:2888:3888;2181
```



## 九、外部快照创建及恢复

- 外部快照创建

  ```shell
  # 命令
  virsh snapshot-create-as --domain a5c655a8-589a-4524-b338-fa9b947a334d --name snap-1-manual --atomic --disk-only --quiesce
  # 结果
  Domain snapshot snap-2-manual created
  
  
  -rw------- 1 qemu qemu 21478375424 Jun 19 01:38 kylin2.qcow2
  -rw------- 1 qemu qemu     1310720 Jun 19 01:40 kylin2.snap-1-manual
  -rw------- 1 qemu qemu     1310720 Jun 19 01:43 kylin2.snap-2-manual
  ```

  - [参考-1](https://www.cnblogs.com/sammyliu/p/4468757.html)
  - [参考-2](https://notes.wadeism.net/post/kvm-external-snapshot/)
  - [参考-3](https://unix.stackexchange.com/questions/663372/error-creating-snapshot-operation-not-supported-internal-snapshots-of-a-vm-wit)
  - [参考-4](https://www.cyberciti.biz/faq/how-to-create-create-snapshot-in-linux-kvm-vmdomain/)

  

- 查看虚拟机磁盘挂载情况

  ```shell
  virsh domblklist vm1
  
   Target   Source
  --------------------------------------------------------
   vda      /var/lib/libvirt/images/kylin2.snap-2-manual
  ```

  



## 其他 virsh 命令

```shell
# 导出 xml 文件
virsh dumpxml kylin-1 > temp-vm.xml
# 查询虚拟机 vnc 端口
virsh vncdisplay [vm]
```



## ⚠️注意事项

- 镜像文件 + 磁盘文件 全部放在共享存储中

- 所有宿主机共享路径设置为相同路径

- 内存相关动态配置

  ```xml
  <!--引导时分配的最大内存-->
  <memory unit='KiB'>2097152</memory>
  <!--设置当前内存-->
  <currentMemory unit='KiB'>2097152</currentMemory>
  ```

  - memory （最大阈值）推荐设置为宿主机最大内存的（0.8），也是内存动态调整的最大阈值（**由用户手动配置**）
  - currentMemory 为宿主机的当前内存值
  - ==临时生效==`virsh setmem kylin-1 4194304`—动态修改虚拟机内存值（currentMemory），不得超过最大内存阈值（memory）
  - ==下次启动生效并持久化==`virsh setmem kylin-1 4194304 --config`—持久化虚拟机内存值
  - ==关机调整内存最大阈值==`virsh setmaxmem kylin-1 10485760`

- CPU相关动态配置

  ```xml
  <!--current：4为虚拟机当前 CPU 数量，8 是最大 CPU 数量-->
  <vcpu placement='static' current='4'>8</vcpu>
  ```

  - 查询宿主机最大 vcpu 数量：`virsh maxvcpus`
  - ==临时生效==`virsh setvcpus <虚拟机名称> 4 --live`
  - ==下次启动生效并持久化==`virsh setvcpus <虚拟机名称> 4 --config`—持久化 虚拟CPU 数量
  - ==关机调整 vCPU 最大阈值==`virsh setvcpus <虚拟机名称> 4 --maximum --config`


- 临时修改控制台语言：

  ```shell
  echo $LANG
  # 设置 LANG 环境变量为 en_US.UTF-8
  export LANG=en_US.UTF-8
  echo $LANG
  ```

  
