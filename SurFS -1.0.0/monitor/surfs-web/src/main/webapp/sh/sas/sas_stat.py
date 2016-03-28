/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
import os
import commands
import getopt
import sys
import json


ret, output = commands.getstatusoutput("ls -l /dev/bsg/ | grep expander")
if ret != 0:
    print "No expander found in /dev/bsg/"
    sys.exit(1)

ret, output = commands.getstatusoutput("ls -l /dev/bsg/expander*| awk '{print $NF}' | awk -F '/' '{print $4}'")
aExp = output.split('\n')
hSMP = {}
hA2EX = {}
hEX2A = {}
aSwitch = []
aFront = []
aRear = []
for exp in aExp :

	ret, output = commands.getstatusoutput("smp_discover -A /dev/bsg/%s"%exp)
	if ret != 0:
		print "SMP get expender %s info fail."%exp
		continue
	smp = output
	hSMP[exp] = smp
    	ret, output = commands.getstatusoutput("cat /sys/class/sas_device/%s/sas_address"%exp)
    	addr = output[:-2]
	hEX2A[exp]=addr[2:]
	hA2EX[addr[2:]] = exp
	ret, output = commands.getstatusoutput("echo -e '%s' | grep 'phy  29:D:disabled'"%smp)
	if ret == 0 :
		aRear.append(exp)
	else :
		aFront.append(exp)
	ret, output = commands.getstatusoutput("echo -e '%s' | grep 'SSP+STP+SMP'"%smp)
	if ret == 0 :
		aSwitch.append(exp)

ret, output = commands.getstatusoutput("zpool list | sed -n '2,$p' | awk '{print $1;}'")
aZpools = output.split('\n')
hZ2Disk = {}
aFree = []
for zpool in aZpools :
    	ret, output = commands.getstatusoutput("zpool status $ZPOOL | grep JBOD | awk '{print $1;}'")
    	aDisks = output.split('\n')
	for disk in aDisks :
        	hZ2Disk[disk] = zpool 
aExist = []     
for exp in aExp :
    smp = hSMP[exp]
    aLocal = []
    aLDisk = []
    ret, output = commands.getstatusoutput("echo -e '%s' | grep exp | awk '{print $2}' | awk -F '[:[]' '{print $5}' | sort | uniq"%smp )
    if output != "" :
        aLocal = output.split('\n')
    ret, output = commands.getstatusoutput("echo -e '%s' | grep -E '  t\(SSP\)]|SATA' | awk '{print $2}'"%smp)
    if output != "" :
        aLDisk = output.split('\n')
    fob = ""
    jbod = ""
    if exp in aFront :
        fob="f"
        jbod = hEX2A[exp]
    elif exp in aRear :
        fob="b"
	for unit in aLocal :
	    if unit not in aSwitch :
        	jbod = unit[:-2]
	
    for disk in aLDisk :
        ret, output = commands.getstatusoutput("echo '%s' | awk -F '[:[]' '{print $5}'"%disk )
        addr = output
        ret, output = commands.getstatusoutput("echo '%s' | awk -F '[:[]' '{print $1}'"%disk )
        phy = output
        stat = "-"
        name = "JBOD_%s_%s_phy%s"%(hEX2A[exp], fob, phy)
	if name in aExist :
		continue
	else :
		aExist.append(name)

        local = "remote"
        zpool = "none"
        ret, output = commands.getstatusoutput("cat /usr/local/sassw/conf/disk/localphy_manual | grep %s"%phy )
        if ret == 0 :
            local="local"
            mp = ""
            dev = ""
            ret, output = commands.getstatusoutput("multipath -ll %s | grep status=active"%name )
            if ret == 0 :
                mp = output
            ret, output = commands.getstatusoutput("multipath -ll %s | sed -n '$p;' | awk '{print $3;}'"%name )
            if ret == 0 :
                dev = output

            zpool = ""
            if hZ2Disk.has_key(name) :
                zpool = hZ2Disk[name]

            if mp == "" :
                stat = "FAIL"
                if zpool != "" :
                    print "%s %s %s %s %s %s --- %s"%(name, jbod, fob, phy, local, stat, zpool)
                else :
                    print "%s %s %s %s %s %s --- ---"%(name, jbod, fob, phy, local, stat)
            else :
                if zpool != "" : 
                    stat = "USED"
                    print "%s %s %s %s %s %s /dev/%s %s"%(name, jbod, fob, phy, local, stat, dev, zpool)
                else :
                    stat = "FREE"
                    print "%s %s %s %s %s %s --- ---"%(name, jbod, fob, phy, local, stat)
        else :
            ret, output = commands.getstatusoutput("cat /usr/local/sassw/conf/disk/remotephy | awk '{print $1;}'| grep %s"%phy )
            if ret == 0 :
                print "%s %s %s %s %s FREE"%(name, jbod, fob, phy, local)
            else :
                print "%s %s %s %s %s ----"%(name, jbod, fob, phy, local)
  
sys.exit(0)
