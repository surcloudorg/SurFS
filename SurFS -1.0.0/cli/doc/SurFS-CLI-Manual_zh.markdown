# Table of Contents

1. [安装SurFS命令行工具](#install)
2. [命令行帮助手册说明](#help)
3. [配置文件说明](#config_file)
3. [pool管理](#pool)
4. [卷管理(volume)](#volume)
4. [快照管理(snapshot)](#snapshot)
5. [导出管理](#export)

<a name="install"></a>
## 安装SurFS命令行工具

从《网站》下载命令工具压缩包，包括surfs命令行可执行文件和一个示例配置文件。按照如下命令操作：

```bash
# 将压缩包解压
tar xvf surfs-cli.tar.gz

# 将surfs复制到需要/usr/bin
cp surfs-cli/surfs  /usr/bin

# 默认配置文件路径为/etc/surfs/config.json，将示例配置文件复制到该路径
mkdir –pv /etc/surfs/
cp surfs-cli/config.json /etc/surfs/
```

## 输出格式说明
不管命令成功还是失败都返回0，用户需要通过解析返回的json中success字段来判断执行成功还是失败。

所有的输出为Json格式，如果success字段值为true则说明命令执行成功，如果失败则success字段为false。

#### 内容示例1
如果成功，则输出如下格式的json内容：
```json
{
    "data": {
        "version": "1.0.0"
    },
    "success": true
}
```
#### 内容示例2
如果出现错误，则输出如下格式的json内容：
```
{
    "message": "error: volume not found",
    "success": false
}
```

<a name="help"></a>
## 命令行帮助手册说明

#### 列出所有命令
```
$ surfs  help

NAME:
   surfs - SurFS command line tool

USAGE:
   surfs [global options] command [command options] [arguments...]

VERSION:
   1.0.0 (Git: 8e320cf)

AUTHOR(S):
   SurDoc

COMMANDS:
   dumpconfig		Dump configuration
   info			Get information of surfs cluster
   connect		Connect to specific pools or all pools if no pool specified
   disconnect		Disconnect a pool from surfs
   disk_cab_list	List all pools
   disk_cab_status	Get status of a pool
   version		Print version information
   list			List all volumes in a pool
   volume		Get volume information
   create		Create a volume
   copy			Copy a volume
   delete		Delete volume or snapshot
   export		Create iSCSI target for a volume
   check_export		Check the iSCSI target status of a volume
   disexport		Delete the iSCSI target of volume
   snap			Create a snapshot
   snaplist		List snapshots in a pool
   snap_to_volume	Create volume from snapshot
   help, h		Shows a list of commands or help for one command

GLOBAL OPTIONS:
   --debug		debug [$SURFS_DEBUG]
   --proxy 		proxy [$SURFS_PROXY]
   --timeout "0"	timeout in second
   --config 		config file
   --log 		log file
   --pretty		pretty json output
   --help, -h		show help
   --version, -v	print the version

```
#### 查看指定命令的帮助信息
```
$ surfs help export
NAME:
   surfs export - Create iSCSI target for a volume

USAGE:
   surfs export <iqn_name> <initiator_name> <username:password> <volume_name>

DESCRIPTION:
   export volume
```
<a name="config_file"></a>
## 配置文件手册
SurFS命令行工具主要使用两个配置文件：
- config.json: 主配置文件
- affiliation.json: 用于配置计算节点和存储节点的优选关系，用于处理hostip参数

#### 主配置文件(config.json)
默认配置文件路径为/etc/surfs/config.json, 可通过-config=/path/to/config.json参数来覆盖默认选项。

```json
{
    "entryPoint": "http://10.0.76.10:8080",
    "debug": false,
    "logfile": "/var/log/surfs.log",
    "proxy": "",
    "timeout": 0,
    "workDir": "/etc/surfs/"
}
```

关键配置有：
- entryPoint: 必选配置，SurFS入口服务器地址
- workDir: 可选配置，工作目录，默认为/etc/surfs/；命令行工具会从该目录读取其他配置以及保存其他数据
- timeout: 请求超时时间，默认为0，表示永远不超时

#### 优选关系配置文件(affiliation.json)
路径为workDir/affiliation.json，该文件格式如下：
```json
[
    {
        "hostip": "10.0.0.1",
        "affiliationIps": ["10.0.76.10", "10.0.76.20"]
    },
    {
        "hostip": "10.0.0.2",
        "affiliationIps": ["10.0.76.10", "10.0.76.20"]
    }
]

```
- hostip: 为计算节点ip，
- affiliationIps: 表示为该hostip优先选择在这些列表中的存储节点上创建卷

<a name="pool"></a>
## pool管理

####	获取SurFS版本信息

通过version命令获取SurFS版本信息，该命令返回如下信息：
- cliVersion: SurFS命令行工具版本
- version: SurFS文件系统版本

```bash
$ surfs version
{
    "data": {
        "cliVersion": "1.0.0",
        "version": "1.0.0"
    },
    "success": true
}
```

####	获取SurFS中的pool列表
```bash
$ surfs info
{
    "data": {
        "pools": [
            {
                "connected": false,
                "pool": "test"
            },
            {
                "connected": false,
                "pool": "test2"
            }
        ],
		"version": "1.0.0"
    },
    "success": true
}
```
#### 连接pool
使用surfs connect命令连接到SurFS中的所有pool或者指定pool。
```bash
$ surfs connect test
{
    "data": [
        {
            "free": 3912375387136,
            "ip": "10.0.76.10",
            "pool": "test",
            "success": true,
            "total": 3923452624896,
            "used": 11077237760
        }
    ],
    "success": true
}
```

#### 获取所有pool的信息
```bash
$ surfs disk_cab_list
{
    "data": [
        {
            "free": 3571245271552,
            "ip": "10.0.76.10",
            "pool": "test",
            "success": true,
            "total": 3923452624896,
            "used": 352207353344
        }
    ],
    "success": true
}
```

#### 查询指定pool的信息
pool必须处于已连接状态，否则返回错误。
```bash
$ surfs disk_cat_status test
{
    "data": {
        "free": 3912375387136,
        "ip": "10.0.76.10",
        "pool": "test",
        "success": true,
        "total": 3923452624896,
        "used": 11077237760
    },
    "success": true
}

```

#### 断开连接pool
使用surfs disconnect命令断开和指定pool的连接。

```bash
$ surfs disconnect test
{
    "success": true
}
```
<a name="volume"></a>
## 卷管理(volume)


#### 获取Pool中的卷列表
```bash
$ surfs list test
{
    "data": [
        {
            "name": "vol1",
            "pool": "test",
            "size": 11075846144
        }
    ],
    "success": true
}
```

#### 查询指定卷信息

```bash
$ surfs volume vol1
{
    "data": {
        "name": "vol1",
        "pool": "test",
        "size": 110756544512
    },
    "success": true
}
```
#### 创建卷
使用create命令创建卷，包含如下参数：
- -V 10G: 卷大小
- -P hostip: 可选参数，用于选择距离该ip最近的pool
- pool/volname | volname: 卷名

SurFS将按照如下顺序顺序选择pool来创建:
- 如果指定pool，那么则在该pool上创建；如果该pool空间不足则返回错误
- 如果指定hostip：
	- 如果该hostip和某个pool的ip一样，那么则在该pool上创建
	- 如果该hostip配置了优选pool列表，那么从改优选列表中选择pool
- 从所有pool中选择一个空间最大的pool
- 在此过程中，如果pool空间不足则按照顺序依次选择次优的pool直到找到一个有足够剩余空间的pool


```bash
$ surfs create -V 10G vol2
{
    "data": {
        "name": "vol2",
        "pool": "test",
        "size": 10737549312
    },
    "success": true
}
```

#### 删除卷

```bash
$ surfs delete vol2
{
    "success": true
}
```

#### 复制卷
使用copy命令复制卷，包含如下参数：
- -f: 如果目标卷已经存在，则强制覆盖

注意事项：
- 在SurFS中，只允许在同一个pool中创建目标卷
- 卷复制是一个异步操作，之后需要通过copy_progress命令来查询复制进度

```bash
$ surfs copy vol1 vol1_copy
{
    "data": {
        "ip": "10.0.76.10"
    }, 
    "success": true
}
```

#### 查询复制卷进度
使用copy_progress命令来查询复制卷进度，以目标卷名为标识。

如果目标卷存在且查询成功，返回如下进度信息：
- "running": 正在复制
- "completed": 复制完成
- "failed": 复制失败
- "notExist": 不存在以目标卷为标识复制卷命令

否则success字段为false，并给出错误信息。

```bash
$ surfs copy_progress vol1_copy
{
    "data": {
        "progress": "completed"
    }, 
    "success": true
}

```

<a name="snapshot"></a>
## 快照管理(snapshot)

#### 获取指定pool中的快照列表

```bash
$ surfs snaplist test
{
    "data": [
        {
            "ctime": 1455712376,
            "ip": "10.0.76.10",
            "pool": "test",
            "size": 0,
            "snapshot": "vol1_backup1",
            "srcSize": 110756544512,
            "volume": "vol1"
        },
        {
            "ctime": 1455712382,
            "ip": "10.0.76.10",
            "pool": "test",
            "size": 0,
            "snapshot": "vol1_backup2",
            "srcSize": 110756544512,
            "volume": "vol1"
        }
    ],
    "success": true
}
```

#### 创建快照

```bash
$ surfs snap vol1 vol1_backup2
```
```json
{
    "data": {
        "ctime": 1455712382,
        "ip": "10.0.76.10",
        "pool": "test",
        "size": 0,
        "snapshot": "vol1_backup2",
        "srcSize": 110756544512,
        "volume": "vol1"
    },
    "success": true
}
```

#### 删除快照

```bash
$ surfs delete vol1_backup2
{
    "success": true
}
```

#### 使用快照恢复卷

```bash
$ surfs snap_to_volume vol1_backup1 vol1_new1 104G
{
    "success": true
}
```

<a name="export"></a>
## 导出管理

#### 导出指定卷
```bash
$ surfs export iqn.2016-01.com.sursen:storage:vol1 10.0.0.1 user:password vol1
{
    "data": {
        "ip": "10.0.76.10"
    },
    "success": true
}
```

####	删除指定卷的导出
```bash
$ surfs disexport vol1
{
    "success": true
}
```

####	检查并修复指定卷的导出
```bash
$ surfs check_export iqn.2016-01.com.sursen:storage:vol1 10.0.0.1 foo:bar vol1
{
    "data": {
		"ip": "10.0.76.10",
        "repaired": "target"
    },
    "success": true
}
```
