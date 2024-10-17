## 一、麒麟 v10 安装 kvm-qemu

### 1、检查是否支持虚拟化

Kvm是基于x86虚拟化拓展(Intel VT或者 AMD-V)技术的虚拟机软件,所以查看CPU是否支持VT技术,就可以判断是否支持kvm .有返回结果,如果结果中有vmx(inel)或svm(AMD)字样,就说明CPU的支持的.

```shell
#cat /proc/cpuinfo | egrep 'vmx|svm'
flags   : fpu vme de pse tsc msr pae mce cx8 apic sep mtrr pge mca cmov pat pse36 clflush dts acpi mmx fxsr sse

关闭selinux,将/etc/sysconfig/selinux中的selinux=enforcing修改为 selinux=disable

#vi /etc/sysconfig/selinux
```


### 2、安装 kvm

```shell
# 安装 kvm
yum -y install qemu-kvm libvirt virt-install bridge-utils
# 只下载 rpm 包到指定为止，但是不安装
yum install --downloadonly --downloaddir=/root/vsp/dependent qemu-kvm libvirt virt-install bridge-utils


# 验证模块是否加载成功
lsmod | grep kvm
# 启动虚拟化和开机自启动
systemctl start libvirtd
systemctl enable libvirtd
systemctl list-unit-files |grep libvirtd.service
```

### 3、修改 libvirtd 权限

```shell
vim /etc/libvirt/qemu.conf
# 添加以下内容
user = "root"
group = "root"
# 重启
systemctl restart libvirtd.service
```

### 4、修改 libvirtd 本机默认的 uuid

```shell
# 随机生成一个本机 uuid
cat /proc/sys/kernel/random/uuid
输出：2d0984ae-bedb-4888-bb84-bf3624416ef7

# 将 host_uuid 追加至文件末尾
echo -n "host_uuid = \"2d0984ae-bedb-4888-bb84-bf3624416ef7\"" >> /etc/libvirt/libvirtd.conf

# 重启
systemctl restart libvirtd.service
```



### 「离线」RPM安装

- 宿主机安装

  ```shell
  -rw-r--r-- 1 root root   12436  9月 29 15:34 libvirt-6.2.0-16.p26.ky10.x86_64.rpm
  -rw-r--r-- 1 root root   13192  9月 29 15:34 libvirt-bash-completion-6.2.0-16.p26.ky10.x86_64.rpm
  -rw-r--r-- 1 root root  318264  9月 29 15:34 libvirt-client-6.2.0-16.p26.ky10.x86_64.rpm
  -rw-r--r-- 1 root root  274480  9月 29 15:34 libvirt-daemon-6.2.0-16.p26.ky10.x86_64.rpm
  -rw-r--r-- 1 root root   14456  9月 29 15:34 libvirt-daemon-config-network-6.2.0-16.p26.ky10.x86_64.rpm
  -rw-r--r-- 1 root root   18572  9月 29 15:34 libvirt-daemon-config-nwfilter-6.2.0-16.p26.ky10.x86_64.rpm
  -rw-r--r-- 1 root root  142992  9月 29 15:34 libvirt-daemon-driver-interface-6.2.0-16.p26.ky10.x86_64.rpm
  -rw-r--r-- 1 root root  167732  9月 29 15:34 libvirt-daemon-driver-network-6.2.0-16.p26.ky10.x86_64.rpm
  -rw-r--r-- 1 root root  142516  9月 29 15:34 libvirt-daemon-driver-nodedev-6.2.0-16.p26.ky10.x86_64.rpm
  -rw-r--r-- 1 root root  166512  9月 29 15:34 libvirt-daemon-driver-nwfilter-6.2.0-16.p26.ky10.x86_64.rpm
  -rw-r--r-- 1 root root  777780  9月 29 15:34 libvirt-daemon-driver-qemu-6.2.0-16.p26.ky10.x86_64.rpm
  -rw-r--r-- 1 root root  132484  9月 29 15:34 libvirt-daemon-driver-secret-6.2.0-16.p26.ky10.x86_64.rpm
  -rw-r--r-- 1 root root   12300  9月 29 15:34 libvirt-daemon-driver-storage-6.2.0-16.p26.ky10.x86_64.rpm
  -rw-r--r-- 1 root root  183268  9月 29 15:34 libvirt-daemon-driver-storage-core-6.2.0-16.p26.ky10.x86_64.rpm
  -rw-r--r-- 1 root root   22148  9月 29 15:34 libvirt-daemon-driver-storage-disk-6.2.0-16.p26.ky10.x86_64.rpm
  -rw-r--r-- 1 root root   24120  9月 29 15:34 libvirt-daemon-driver-storage-gluster-6.2.0-16.p26.ky10.x86_64.rpm
  -rw-r--r-- 1 root root   19184  9月 29 15:34 libvirt-daemon-driver-storage-iscsi-6.2.0-16.p26.ky10.x86_64.rpm
  -rw-r--r-- 1 root root   21172  9月 29 15:34 libvirt-daemon-driver-storage-iscsi-direct-6.2.0-16.p26.ky10.x86_64.rpm
  -rw-r--r-- 1 root root   23120  9月 29 15:34 libvirt-daemon-driver-storage-logical-6.2.0-16.p26.ky10.x86_64.rpm
  -rw-r--r-- 1 root root   17172  9月 29 15:34 libvirt-daemon-driver-storage-mpath-6.2.0-16.p26.ky10.x86_64.rpm
  -rw-r--r-- 1 root root   27236  9月 29 15:34 libvirt-daemon-driver-storage-rbd-6.2.0-16.p26.ky10.x86_64.rpm
  -rw-r--r-- 1 root root   19288  9月 29 15:34 libvirt-daemon-driver-storage-scsi-6.2.0-16.p26.ky10.x86_64.rpm
  -rw-r--r-- 1 root root 4375516  9月 29 15:34 libvirt-libs-6.2.0-16.p26.ky10.x86_64.rpm
  -rw-r--r-- 1 root root 5771524  9月 29 15:34 qemu-4.1.0-63.p36.ky10.x86_64.rpm
  -rw-r--r-- 1 root root   25088 Oct  8 16:31 sshpass-1.06-8.ky10.x86_64.rpm
  
  # COMMAND
  rpm -i *.rpm
  # 启动虚拟化和开机自启动
  systemctl start libvirtd
  systemctl enable libvirtd
  # libvirt权限问题待测试
  ```

- 虚拟机安装

  **如果缺少qemu-guest-agent，virsh snapshot-create-as + virsh 查询 ip 会报错**
  
  ```shell
  -rw-r--r-- 1 root root  178556  9月 27 14:54 qemu-guest-agent-4.1.0-63.p35.ky10.x86_64.rpm
  
  # COMMAND
  rpm -i qemu-guest-agent-4.1.0-63.p35.ky10.x86_64.rpm
  systemctl start qemu-guest-agent
  
  # 不一定需要执行，待测试
  #systemctl enable qemu-guest-agent
  ```
  
  

### 「yum 依赖包下载地址」麒麟 v10
[yum 依赖包链接](https://update.cs2c.com.cn/NS/V10/V10SP3/os/adv/lic/base/aarch64/Packages/)



### 「JDK11」宿主机配置 JDK11

```shell
# amd64 环境
yum install java-11-openjdk.x86_64 java-11-openjdk-devel.x86_64

# 环境变量配置
vim /etc/profile

export JAVA_HOME=/usr/lib/jvm/jre-11-openjdk
export PATH=$JAVA_HOME/bin:$PATH
export CLASSPATH=.:$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar

source /etc/profile
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

### 方法一

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

注意：虚拟机需要安装qemu-guest-agent，否则会报错

```shell
# 报错
[root@kylin1 ~]# virsh domifaddr --domain 7613011f-d50a-4f91-b66f-1b6d1d8e61dd --source agent
error: Failed to query for interfaces addresses
error: 虚拟机代理不响应：QEMU guest agent is not connected

# 虚拟机内部安装
yum install qemu-guest-agent

# 启动 qemu-guest-agent 服务
systemctl start qemu-guest-agent.service
```

[参考-1](https://www.cnblogs.com/wang272/p/some_virsh_domxxx_commands.html)

### 方法二

- 使用 `nmap -sn 192.168.0.0/24`扫描网段内的 IP 地址
- 查询虚拟机的 mac 地址
- 根据虚拟机 mac 地址匹配虚拟机 IP 地址



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

- 外部快照创建 (开机关机状态均可)

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

- 外部快照合并（虚拟机必须处于`运行`状态）

  **在使用 `virsh blockcommit` 命令合并外部快照时，如果不指定 `--path` 参数，该命令不会自动合并所有磁盘。`--path` 参数是必须的，用于指定要合并的磁盘路径。如果你希望合并多个磁盘，需要分别对每个磁盘执行 `virsh blockcommit` 命令。**

  ```shell
  # 合并命令
  virsh blockcommit --domain a5c655a8-589a-4524-b338-fa9b947a334d --path vda --verbose --pivot --active
  # 结果
  Block commit: [100 %]
  Successfully pivoted
  ```

- 外部快照合并后历史快照删除

  ```shell
  # 合并后历史快照删除命令
  virsh snapshot-delete --domain a5c655a8-589a-4524-b338-fa9b947a334d --snapshotname snap-1-manual --children --metadata
  
  # 结果
  Domain snapshot snap-1-manual deleted
  
  # 手动删除快照文件
  TODO
  ```

- 查询虚拟机磁盘及快照状态

  ```shell
  # 查询虚拟机当前使用的磁盘
  virsh domblklist a5c655a8-589a-4524-b338-fa9b947a334d --details
  
  # 结果
   Type   Device   Target   Source
  ------------------------------------------------------------------------
   file   disk     vda      /var/lib/libvirt/images/kylin2.snap-4-manual
   
   # 查询虚拟机所有快照列表
  virsh snapshot-list --domain a5c655a8-589a-4524-b338-fa9b947a334d
   # 结果
    Name            Creation Time               State
  ------------------------------------------------------------
   snap-1-manual   2024-06-19 01:38:00 +0800   disk-snapshot
   snap-2-manual   2024-06-19 01:40:32 +0800   disk-snapshot
   snap-3-manual   2024-07-05 09:57:29 +0800   disk-snapshot
   snap-4-manual   2024-07-05 09:58:38 +0800   shutoff
  ```

- 快照恢复（新 -> 旧）

  - 初始虚拟机为运行状态：先关闭虚拟机再执行恢复操作
  - 需要遍历所有磁盘（系统盘 + 数据盘）

  ```shell
  # 初始虚拟机为关闭状态
  
  # 移除虚拟机的硬盘
  [root@kylin1 share_disk]# virt-xml 7613011f-d50a-4f91-b66f-1b6d1d8e61dd --remove-device --disk target=vda
  Domain '虚拟机-2' defined successfully.
  Changes will take effect after the domain is fully powered off.
  
  # 将原有硬盘挂载到虚拟机上
  [root@kylin1 share_disk]# virt-xml 7613011f-d50a-4f91-b66f-1b6d1d8e61dd --add-device --disk /root/vsp/share/share_disk/Root-Disk-1839223404004249600.qcow2,format=qcow2,bus=virtio
  Domain '虚拟机-2' defined successfully.
  Changes will take effect after the domain is fully powered off.
  
  # 启动虚拟机才可以生效
  virsh start xxx
  
  # 删除恢复前的快照信息
  virsh snapshot-delete --domain a5c655a8-589a-4524-b338-fa9b947a334d --snapshotname auto-snapshot --children --metadata
  # 结果
  Domain snapshot auto-snapshot deleted
  
  # 手动删除快照文件（for）
  TODO
  
  # 删除数据库中快照记录（for）
  ```



## 十、磁盘操作

```shell
# 创建磁盘
qemu-img create -f qcow2 -o preallocation=off /var/lib/libvirt/images/kylin2-attach-1.qcow2 20G
# response
Formatting '/var/lib/libvirt/images/kylin2-attach-1.qcow2', fmt=qcow2 size=21474836480 cluster_size=65536 preallocation=off lazy_refcounts=off refcount_bits=16

# 磁盘卸载
virsh detach-disk --domain kylin10.0 --target vdb --persistent
# response
Disk detached successfully

# 磁盘挂载 
virsh attach-disk --domain kylin10.0 --source /var/lib/libvirt/images/kylin10.0-attach-1.qcow2 --subdriver qcow2 --targetbus virtio --persistent --target vdb
# response
Disk attached successfully
```

- 查看虚拟机磁盘挂载情况

  ```shell
  virsh domblklist vm1
  
   Target   Source
  --------------------------------------------------------
   vda      /var/lib/libvirt/images/kylin2.snap-2-manual
  ```



## 十一、虚拟机迁移

`virsh migrate` 命令附加选项

- **--persistent** - 在目标主机物理机器上保留域
- **--undefinesource** - 取消定义源主机物理机器上的域
- **--suspend** - 使域暂停在目标主机物理机器上
- **--unsafe** - 强制迁移进行，忽略所有安全程序
- **--verbose** - 在发生迁移时显示迁移的进度
- **--live** - 在线迁移
- **--offline** - 离线迁移可以与不活动域一起使用，且必须与 **--persistent** 选项一起使用。
- [命令附加选项-参考链接](https://docs.redhat.com/zh_hans/documentation/red_hat_enterprise_linux/7/html/virtualization_deployment_and_administration_guide/sect-live_kvm_migration_with_virsh-additional_options_for_the_virsh_migrate_command)

```shell
# 「运行态」热迁移命令-1
virsh migrate a3b499b0-a417-4f75-8a76-70c0fb24eeb0 qemu+ssh://192.168.0.202/system tcp://192.168.0.202 --live --undefinesource --persistent --verbose --unsafe

# 「运行态」热迁移命令-2（省略密码键入）
sshpass -p 'xxx' virsh migrate a3b499b0-a417-4f75-8a76-70c0fb24eeb0 qemu+ssh://192.168.0.202/system tcp://192.168.0.202 --live --undefinesource --persistent --verbose --unsafe

# 「非运行态」冷迁移命令-1
virsh migrate a3b499b0-a417-4f75-8a76-70c0fb24eeb0 qemu+ssh://192.168.0.202/system tcp://192.168.0.202 --offline --undefinesource --persistent --verbose --unsafe

# 「非运行态」冷迁移命令-2（省略密码键入）
sshpass -p 'xxx' virsh migrate a3b499b0-a417-4f75-8a76-70c0fb24eeb0 qemu+ssh://192.168.0.202/system tcp://192.168.0.202 --offline --undefinesource --persistent --verbose --unsafe


Migration: [100 %]
```



### 【error: 内部错误：尝试将虚拟机迁移到同一主机】

**报错原因：**

很多厂商都是OEM服务器，导致UUID一样。

使用`virsh sysinfo | grep uuid`或者`dmidecode -s system-uuid`都可以查询服务器的UUID，结果查询到计算节点的UUID都是一样的，所以导致迁移的时候源主机认为目的主机就是自己。

KVM并不是直接查找这个硬件的UUID而是先到`/etc/libvirt/libvirtd.conf`内找`host_uuid`字段，但是此字段是被默认注释掉的，所以找到对方硬件的UUID。

**解决方案：**

先随机生成一个UUID，如下：

```shell
[root@kylin1 libvirt]# cat /proc/sys/kernel/random/uuid
2d0984ae-bedb-4888-bb84-bf3624416ef7
```

然后使用上面的uuid替换`/etc/libvirt/libvirtd.conf`中的`host_uuid`字段

```shell
#host_uuid = "00000000-0000-0000-0000-000000000000"  改为
host_uuid = "2d0984ae-bedb-4888-bb84-bf3624416ef7" 
```

[解决方案参考链接](https://blog.csdn.net/bai0324lin/article/details/85337649)

```shell
# 将 host_uuid 追加至文件末尾
echo -n "host_uuid = \"2d0984ae-bedb-4888-bb84-bf3624416ef7\"" >> /etc/libvirt/libvirtd.conf

# 查询文件最后 20 行
tail -n 20 /etc/libvirt/libvirtd.conf
```



## 十二、noVNC & websockify 连接虚拟机

[通过 noVNC 和 websockify 连接到 Qemu 虚拟机](https://www.cnblogs.com/zqyanywn/p/11417028.html)

- 中间服务器上部署websockify代理



## 常用 virsh 命令

```shell
# 导出 xml 文件
virsh dumpxml kylin-1 > temp-vm.xml
# 查询虚拟机 vnc 端口
virsh vncdisplay [vm]
```



## ⚠️注意事项

- ```shell
  # 设置 LANG 环境变量为 en_US.UTF-8
  export LANG=en_US.UTF-8
  ```

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



