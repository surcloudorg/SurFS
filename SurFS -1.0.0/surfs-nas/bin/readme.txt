1.部署服务端程序(surnas)
  
  配置存储服务的数据库地址端口
　%surfs-nas%/bin/surfs_pools.xml
　如：jdbc:mysql://localhost:3306/surnas?characterEncoding=utf8，
　localhost是mysql服务器地址，3306是mysql服务器端口
　surnas是mysql服务器中的一个数据库，需要手动创建，并将字符集编码设置为utf8
  user:访问帐号
　pwd:访问密码
 
  启动%surfs-nas%/bin/cmdline/surserver.sh
  一般数据库配置没问题,服务应该可以正常启动，否则从屏幕可以输出对应错误
  登陆控制台 http://ip:8080/login.jsp,帐号:surfs 密码:surfs
  设置参数,详情请见 %surfs-nas%/doc/书生NAS存储系统安装配置手册V1.0.doc

  服务调试完毕后,关闭上面命令行启动的服务,将服务端程序安装为系统服务
  %surfs-nas%/bin/service/surnas.sh install

  安装成功后,服务名surnas
  可通过service surnas start/stop/restart/status 启动,停止,重启,查看状态
   
  卸载surnas服务
  %surfs-nas%/bin/service/surnas.sh remove

  几个主要的服务运行参数
　编辑%surfs-nas%/bin/surserver.conf

  1)调整服务JVM内存占用
  wrapper.java.initmemory=1500
  wrapper.java.maxmemory=3000
  可根据本机内存情况分配JVM内存

　2)控制台http服务端口(http://ip:8080/login.jsp)
　wrapper.app.parameter.1=port=8080　
  默认8080端口

  3)文件传输服务端口地址
  wrapper.java.additional.4=-Dcom.surfs.nas.transport.TcpServer.Port=8020
  wrapper.java.additional.5=-Dcom.surfs.nas.transport.TcpServer.Host=
  默认8020端口
　如果不指定Host，客户端使用本服务器的机器名来访问本存储服务
　如果指定Host(一般是ip地址)，客户端使用指定的ip地址来访问本存储服务

　启动surnas服务
  service surnas start

2.linux平台部署客户端程序(surmount)

  关闭本机的nfs服务,释放端口2049
  关闭rpc服务,释放端口111
  关闭simba服务,释放端口445
 
  service nfs stop
  service rpcbind stop
  service smb stop
  最好禁用 

  配置几个主要的服务运行参数
　编辑%surfs-nas%/bin/surmount.conf

  1)调整服务JVM内存占用
  wrapper.java.initmemory=1500
  wrapper.java.maxmemory=3000
  可根据本机内存情况分配JVM内存

　2)指定存储节点的http服务地址端口
　wrapper.java.additional.1=-Dsurfs_server=ip1:8080;ip2:8080
　可指定多个服务节点的ip和端口，用";"号隔开

  启动%surfs-nas%/bin/cmdline/surmount.sh 
  一般存储节点的http服务地址端口配置没问题,服务应该可以正常启动，否则从屏幕可以输出对应错误

  启动成功后,用下面方法将nas卷挂载至本地目录
  nfs方式，可能需要安装nfs客户端工具
  yum install nfs-utils
  mount命令
  mount -t nfs -o nolock localhost:/surfs /mnt/testnfs
  /surfs：要挂载的卷，在nas存储服务终端创建的挂载点
  /mnt/testnfs：要挂在的本地目录，必须存在

  cifs方式，可能需要装smb客户端工具
  yum install cifs-utils
  mount命令
  mount -t cifs -o username=surfs,password=surfs //localhost/surfs /mnt/testsmb
  /surfs：要挂载的卷，在nas存储服务终端界面创建的挂载点
  /mnt/testnfs：要挂在的本地目录，必须存在
  账号密码：在nas存储服务终端界面创建的可以访问挂载点/surfs的用户

  安装surmount为linux服务 
  运行 %surfs-nas%/bin/service/surmount.sh install

  启动,停止,重启服务,查看状态
  service surmount start/stop/restart/status
   
  卸载surmount服务
  运行 %surfs-nas%/bin/service/surmount.sh remove
 
3.windows平台部署客户端程序(surmount)

  关闭windows文件共享服务（即关闭445端口）
  /控制面板/管理工具/服务...
  找到windows共享服务
  	显示名称为 Server
  	服务名称为 LanmanServer
  	服务描述为 支持此计算机通过网络的文件、打印、和命名管道共享。如果服务停止，这些功......
  将此服务禁用,重启windows(必需)释放445端口

  关闭windows的nfs服务（关闭111,2049端口）
  	显示名称为 Server for NFS
  	服务名称为 NfsService
  	服务描述为 使基于 Windows 的计算机可以用作 NFS 服务器
  同样需要重启windows

  配置几个主要的服务运行参数
　编辑%surfs-nas%/bin/surmount.conf

  1)调整服务JVM内存占用
  wrapper.java.initmemory=1500
  wrapper.java.maxmemory=3000
  可根据本机内存情况分配JVM内存

　2)指定存储节点的http服务地址端口
　wrapper.java.additional.1=-Dsurfs_server=ip1:8080;ip2:8080
　可指定多个服务节点的ip和端口，用";"号隔开

  启动%surfs-nas%/bin/cmdline/surmount.bat 
  一般存储节点的http服务地址端口配置没问题,服务应该可以正常启动，否则从屏幕可以输出对应错误

  成功后,用下面方法将nas卷映射为本地驱动器
  nfs方式，可能需要安装nfs客户端工具，win7以后操作系统，自带nfs客户端，
  win7以前操作系统,可从微软官网下载安装nfs客户端
  mount命令
  mount -o nolock \\localhost\surfs z:
  /surfs：要挂载的卷，在nas存储服务终端创建的挂载点
  z：要挂载到的本地磁盘驱动号
  
  cifs方式
  -〉映射网络驱动器
  -〉文件夹填 \\localhost\surfs
  -〉输入在nas存储服务终端界面创建的可以访问挂载点/surfs的账号密码

  对于有些版本windows挂载时验证错误问题
  运行"secpol.msc",点击"安全设置\本地安全策略\安全选项",找到右边的"网络安全:LAN管理器身份验证级别"选项
  将"没有定义"改为"发送LM和NTLM-如果已协商....."
  重试即可

  安装surmount为windows服务 
  运行 %surfs-nas%/bin/service/install-surmount.bat
 
  可通过services.msc工具启停surmount(Surfs Mount Server)服务
   
  卸载surmount服务
  运行 %surfs-nas%/bin/service/uninstall-surmount.bat

4.surmount服务,启动选项
  默认的surmount服务启动了 nfs/cifs服务端口 (445,111,2049)
  也可以仅启用nfs端口,或cifs服务端口
  编辑%surfs-nas%/bin/surmount.xml的servers节点
  如下：启动了nfs/cifs服务端口
    <servers>
        <SMB/>
        <NFS/>
    </servers>
  如下：仅启动了nfs服务端口
    <servers>
        <noSMB/>
        <NFS/>
    </servers>
  如下：仅启动了cifs服务端口
    <servers>
        <SMB/>
        <noNFS/>
    </servers>
  这样就可以选择禁用系统服务,比如不启动cifs服务,就可以不用禁用系统445端口服务

5.服务日志

  如果服务无法启动，或启动失败
  具体可查看 %surfs-nas%/log/surmount.log(surserver.log)查找原因

  服务启动后运行时日志
  服务端surnas,查看%surfs-nas%/log/server/sysytem/*
  客户端surmount,查看%surfs-nas%/log/client/sysytem/*