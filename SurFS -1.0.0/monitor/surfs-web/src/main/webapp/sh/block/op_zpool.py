#This Source Code Form is subject to the terms of the Mozilla Public
#License, v. 2.0. If a copy of the MPL was not distributed with this
#file, You can obtain one at http://mozilla.org/MPL/2.0/.
#!/usr/bin/python
import os
import commands
import getopt
import sys
import json

def splitHash(array, map):
	for item in array:
		tmp = item.split('\t')
		map[tmp[0]] = tmp[1]
	
try:
    	options,args = getopt.getopt(sys.argv[1:], "hsvl", ["help", "status", "vols", "list", "create=", "remove=", "size="])
except getopt.GetoptError:
	print "getopt exception"
    	sys.exit(1)
	
opt = "help"
p1 = " "
p2 = " "
	
for name,value in options:
	if name in ("-h","--help"):
		opt = "help"
	if name in ("-s","--status"):
		opt = "status"
	if name in ("-v","--vols"):
		opt = "vols"
	if name in ("-l","--list"):
		opt = "list"
	if name in ("--create"):
		opt = "create"
		p1 = value
	if name in ("--size"):
		p2 = value
	if name in ("--remove"):
		opt = "remove"
		p1 = value

result = 0
		
if opt == "help":
		print "Usage:"
		print "		-h or --help"
		print "		-s or --status"
		print "				get zfs module status by lsmod"
		print "		-v or --vols"
		print "				get all zfs block device in local host, with its lsblk size"
		print "		-l or --list"
		print "				get all zfs block device in local host, list by zpool style"
		print "		--create"
		print "				create one zfs volume from zpool, you should use --size together, and the zpool name is necessary"
		print "				python op_zpool.py --create zpool_name/vol_name --size 50G "
		print "		--remove"
		print "				remove one zfs volume from zpool"
		print "				python op_zpool.py --remove zpool_name/vol_name"
if opt == "status":
	ret, output = commands.getstatusoutput('lsmod | grep zfs | wc -l')
	if ret != 0:
		print "error when get zfs module status"
		sys.exit(ret)
	else:
		if not output == "6":
			print "zfs module not full loaded"
			sys.exit(1)
		else:
			print "zfs module [OK]"
			sys.exit(0)

if opt == "vols":
	result = []
	ret, output = commands.getstatusoutput('zpool list -H -o name,size')
	if ret != 0:
		sys.exit(1)

	aNameAndSize = output.split('\n')
	mNameAndSize = {}
	if aNameAndSize == ['']:
		sys.exit(0)

	splitHash(aNameAndSize, mNameAndSize)

	ret, output = commands.getstatusoutput('zfs list -H -o name,used')
	if ret != 0:
			sys.exit(1)

	aNameAndAvail = output.split('\n')
	mNameAndAvail = {}
	if aNameAndAvail == ['']:
		print json.dumps(result)
		sys.exit(0)
	splitHash(aNameAndAvail, mNameAndAvail)

	mVol = {}
	for key in mNameAndAvail:
			if not mNameAndSize.has_key(key):
				mVol['name'] = '/dev/zvol/' + key
				mVol['cap'] = mNameAndAvail[key]
				result.append(mVol)
				mVol = {}
				
	print json.dumps(result)

if opt == "list":
	result = []
	ret, output = commands.getstatusoutput('zpool list -H -o name')
	if ret != 0:
		sys.exit(1)

	aZpoolName = output.split('\n')
	if aZpoolName == ['']:
		print json.dumps(result)
		sys.exit(0)

	ret, output = commands.getstatusoutput('zfs list -H -o name')
	if ret != 0:
		sys.exit(1)

	aName = output.split('\n')
	if aName == ['']:
		sys.exit(0)

	for item in aName:
			if not item in aZpoolName:
				result.append(item)
				
	print json.dumps(result)


if opt == "create":
	if p2 == " ":
		print "miss param, use --size"
		sys.exit(1)
	if p1 == " ":
		print "miss device name"
		sys.exit(1)
	
	ret, output = commands.getstatusoutput('zfs create -V ' + p2 + ' ' + p1)
	if ret != 0:
		print "zfs create get error: " + output
		sys.exit(1)
	
if opt == "remove":
	if p1 == " ":
		print "miss device name"
		sys.exit(1)
	
	ret, output = commands.getstatusoutput('zfs destroy ' + p1)
	if ret != 0:
		print "zfs destroy get error: " + output
		sys.exit(1)


sys.exit(0)

        
