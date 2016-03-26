# Table of Contents

1. [install SurFS command line tools](#install)
2. [CLI help](#help)
3. [configuration instruction](#config_file)
3. [pool management](#pool)
4. [volume management(volume)](#volume)
4. [snap management(snapshot)](#snapshot)
5. [export management](#export)

<a name="install"></a>
## install SurFS command line tools

download SurFS CLI tools package from webset,that will include an executable file and a configuration instruction. Follow the steps below:

```bash
# extract the CLI tools package
tar xvf surfs-cli.tar.gz

# copy "surfs" to /usr/bin
cp surfs-cli/surfs  /usr/bin

# the configuration file path is "/etc/surfs/config.json"，copy the example file to this path
mkdir –pv /etc/surfs/
cp surfs-cli/config.json /etc/surfs/
```

## output format discription
Either the command is successfully excute or not it will always return 0,the users need to analysis the "success field" to judge a command is success or not.

all the output is Json format,true in the "success field" means the command is success and false means the command is failed.

#### example1
command success,
```json
{
    "data": {
        "version": "1.0.0"
    },
    "success": true
}
```
#### example2
command failed
```json
{
    "message": "error: volume not found",
    "success": false
}
```

<a name="help"></a>
## CLI help

#### show all the commands
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
#### search for the specific command's user guide
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
## configuration manual
SurFS CLI uses two main configuration files:
- config.json: main configuration file
- affiliation.json: this file is using for setting up the priority of the compute node and the storage node,and also the hostip

#### main configuration file(config.json)
default path "/etc/surfs/config.json", use "-config=/path/to/config.json" to set up the configuration file path.

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

keypoint configuration：
- entryPoint: mandatory configuration，SurFS entry server
- workDir: optional configuration,working directory,default path is "/etc/surfs/";CLI will get other configuration files and save data over here
- timeout: request time out,0 by default means never timeout.

#### priority set up(affiliation.json)
the path is "workDir/affiliation.json",the json format is as below:
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
- hostip: compute node ip，
- affiliationIps: create volume over those storage node first

<a name="pool"></a>
## pool management

####	get SurFS version information

version:get the SurFS version information
- cliVersion: SurFS CLI version
- version: SurFS file system version

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

####	List all the pools in SurFS
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
#### connect to a pool
surfs connect:connect to all the pools in SurFS or specific ones.
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

#### get all the pools information
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

#### get the specific pool information
pool must be connected or it will return an error.
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

#### diconnect from a pool
surfs disconnect:diconnect from a specific pool

```bash
$ surfs disconnect test
{
    "success": true
}
```
<a name="volume"></a>
## volume management(volume)


#### get the volumes in a pool
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

#### get the specific volume information

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
#### creat volume
create:create volume with parameters：
- -V 10G: volume size
- -P hostip: optional parmeter,pick the nearest pool from hostip
- pool/volname | volname: volume name

create volume by the following orders:
- If the user specify a pool，create on the specific pool;if there is not enough space,return an error
- specific hostip：
	- If the hostip is the same as a pool，create on that pool
	- If the hostip gets a pool priority,follow the rules
- select a biggest pool from all the pools
- Through those steps,if there is not enough space at the current pool ,then jump to the next step until it get one


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

#### delete volume

```bash
$ surfs delete vol2
{
    "success": true
}
```

#### copy volume
copy:
- -f: if the target volume is exist,cover it

Notes:
- SurFS only allow to create the target volume in one pool
- copy volume is an asynchronous operation，you can use "copy_progress" to check the status

```bash
$ surfs copy vol1 vol1_copy
{
    "data": {
        "ip": "10.0.76.10"
    }, 
    "success": true
}
```

#### check the rate of copy progress
copy_progress target_volume_name

get a status when query successfully
- "running"
- "completed" 
- "failed"
- "notExist" 

else success field is false,

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
## snapshot management(snapshot)

#### get the snapshot list in the specific pool

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

#### create snapshot

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

#### delete snapshot

```bash
$ surfs delete vol1_backup2
{
    "success": true
}
```

#### create volume by snapshot

```bash
$ surfs snap_to_volume vol1_backup1 vol1_new1 104G
{
    "success": true
}
```

<a name="export"></a>
## export management

#### export the specific volume
```bash
$ surfs export iqn.2016-01.com.sursen:storage:vol1 10.0.0.1 user:password vol1
{
    "data": {
        "ip": "10.0.76.10"
    },
    "success": true
}
```

####	delete the volume exported
```bash
$ surfs disexport vol1
{
    "success": true
}
```

####	check and repair the volume exported
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
