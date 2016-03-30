1. Deploy server program(surnas)
  
  Configure database address port of storage service.
　%surfs-nas%/bin/surfs_pools.xml
　e.g. jdbc:mysql://localhost:3306/surnas?characterEncoding=utf8，
　localhost is mysql’s server address，3306 is mysql’s server port
　surnas is a database in mysql server，which needs to manually create, and set CCSID to utf8
  user: access account
　pwd: access password
 
  Start %surfs-nas%/bin/cmdline/surserver.sh
  There’s no problem for general database configuration, the service should be able to normally boot; Otherwise it can output the corresponding errors from the screen. 
  Log in the console http://ip:8080/login.jsp, account:surfs; password:surfs
  For setting parameters, specific to view %surfs-nas%/doc/SurDoc NAS Storage System Installation and Configuration Manual V1.0.doc

  After Service debugging, close above services launched from the command line, and change server program to system services.
  %surfs-nas%/bin/service/surnas.sh install

  After successful installation, the service name is: surnas
  It can through service surnas start/stop/restart/status to start, stop, restart, and check status.
   
  Uninstall surnas service
  %surfs-nas%/bin/service/surnas.sh remove

  Major service operation parameters
　Edit %surfs-nas%/bin/surserver.conf

  1) Adjust service JVM memory footprint
  wrapper.java.initmemory=1500
  wrapper.java.maxmemory=3000
  It can allocate JVM memory according to the situation of native memory 

　2) Console http port (http://ip:8080/login.jsp)
　wrapper.app.parameter.1=port=8080　
  The default is port 8080

  3) File transfer service port address
  wrapper.java.additional.4=-Dcom.surfs.nas.transport.TcpServer.Port=8020
  wrapper.java.additional.5=-Dcom.surfs.nas.transport.TcpServer.Host=
  The default is port 8020
　If you do not specify a host, the client will use the name of this server to access to the storage service
　If you specify a host (typically is IP address), the client will use the specified IP address to access to the storage service

　Start surnas service
  service surnas start

2.Deploy client program on Linux platform (surmount) 

  Shut down the native NFS service, and release port 2049
  Shut down rpc service, and release port 111
  Shut down simba service, and release port 445
 
  service nfs stop
  service rpcbind stop
  service smb stop
  It’s better to disable them. 

  Configure several major service operation parameters
　Edit %surfs-nas%/bin/surmount.conf

  1) Adjust service JVM memory footprint
  wrapper.java.initmemory=1500
  wrapper.java.maxmemory=3000
  It can allocate JVM memory according to the situation of native memory

　2)Specify the HTTP service address port of storage node
　wrapper.java.additional.1=-Dsurfs_server=ip1:8080;ip2:8080
　It can specify more than one service node IP and port, use ";”to separate

  Start %surfs-nas%/bin/cmdline/surmount.sh 
  There’s no problem for HTTP service address port configuration of general storage nodes, the service should be able to normally boot; otherwise it can output the corresponding errors from the screen.

  After start successfully, use the following method to mount nas volume to local directory
  Nfs method, may need to install the nfs client tools
  yum install nfs-utils
  mount command
  mount -t nfs -o nolock localhost:/surfs /mnt/testnfs
  /surfs： the volume needs to be mounted. It is a mount point created in nas storage service terminal
  /mnt/testnfs： The local directory that needs to mount to, which must exist

  cifs method，may need to install the smb client tools
  yum install cifs-utils
  mount command
  mount -t cifs -o username=surfs,password=surfs //localhost/surfs /mnt/testsmb
  /surfs： the volume needs to be mounted. It is a mount point created in nas storage service terminal interface
  /mnt/testnfs： The local directory that needs to mount to, which must exist
  account and password：the accessible mount point created in the nas storage service terminal interface/surfs’ users

  Install surmount for linux service
  Running %surfs-nas%/bin/service/surmount.sh install

  Start,stop,restart service,check status
  service surmount start/stop/restart/status
   
  Uninstall surmount service
  Running %surfs-nas%/bin/service/surmount.sh remove
 
3.Deploy client program on Windows platform(surmount) 

  Close Windows file sharing service（i.e. close port 445）
  /control panel/management tool/service...
  Find Windows sharing service
  	Display name: Server
  	Service name: LanmanServer
  	Service description is: Support the computer through the network’s file, print, and named pipe to share. If the service stops, these……
  Disable the service, restart Windows(must)release port 445

  Close Windows’ nfs service（close port 111,2049）
  	Display name: Server for NFS
  	Service name: NfsService
  	Service description is: to make the computer which is based on Windows can be used as a NFS server 
  Also need to restart Windows

  Configure several major service operation parameters
　Edit %surfs-nas%/bin/surmount.conf

  1)Adjust service JVM memory footprint
  wrapper.java.initmemory=1500
  wrapper.java.maxmemory=3000
  It can allocate JVM memory according to the situation of native memory

　2)Specify the HTTP service address port of storage node
　wrapper.java.additional.1=-Dsurfs_server=ip1:8080;ip2:8080
　It can specify more than one service node IP and port, use ";”to separate

  Start %surfs-nas%/bin/cmdline/surmount.bat 
  There’s no problem for HTTP service address port configuration of general storage nodes, the service should be able to normally boot; otherwise it can output the corresponding errors from the screen.

  After start successfully, use the following method to map nas volume to local drives
  nfs method，may need to install the nfs client tools. After Win 7, the operating system includes the NFS client
  Before Win7, nfs client can be downloaded from Microsoft's website
  mount command
  mount -o nolock \\localhost\surfs z:
  /surfs： the volume needs to be mounted. It is a mount point created in nas storage service terminal
  z：the local disk drive which needs to mount to
  
  cifs method
  -〉map network driver
  -〉Fill in the folder \\localhost\surfs
  -〉Input accessible mount point created in the nas storage service terminal interface/surfs’ account and password

  For some versions’ validation error problems when mounted Windows
  Running "secpol.msc", click" Security Settings\ Local security policy\ Security options",on the right to find “Network security: LAN manager authentication level"option
  Change “Not Defined" to “Send LM and NTLM-if has negotiated....."
  Retry is OK.

  Install surmount for Windows service
  Running %surfs-nas%/bin/service/install-surmount.bat
 
  Can use services.msc tool to start and stop surmount(Surfs Mount Server)service
   
  Uninstall surmount service
  Running %surfs-nas%/bin/service/uninstall-surmount.bat

4. surmount service, startup options
  The default surmount service launched nfs/cifs port (445,111,2049)
  It can also only enable nfs port, or cifs port
  Edit servers node in %surfs-nas%/bin/surmount.xml
  As follows：launched nfs/cifs port
    <servers>
        <SMB/>
        <NFS/>
    </servers>
  As follows：only launched nfs port
    <servers>
        <noSMB/>
        <NFS/>
    </servers>
  As follows：only launched cifs port
    <servers>
        <SMB/>
        <noNFS/>
    </servers>
  So you can choose to disable the system services, such as do not start cifs service, thus it doesn’t need to disable system port 445.

5. Service log

  If the service cannot be started, or launch failed
  Specific to view %surfs-nas%/log/surmount.log(surserver.log) to find the reason

  The runtime log after service starts
  Server-side surnas,to view %surfs-nas%/log/server/sysytem/*
  Client-side surmount,to view %surfs-nas%/log/client/sysytem/*

