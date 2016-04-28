#This Source Code Form is subject to the terms of the Mozilla Public
#License, v. 2.0. If a copy of the MPL was not distributed with this
#file, You can obtain one at http://mozilla.org/MPL/2.0/.
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
	options,args = getopt.getopt(sys.argv[1:], "hs:j:", ["help", "status=", "json=", "import_lzpool", "import_rzpool", "current_import"])
except getopt.GetoptError:
	print "getopt exception"
	sys.exit(1)
	
opt = "help"
p1 = " "
	
for name,value in options:
	if name in ("-h","--help"):
		opt = "help"
	if name in ("-s","--status"):
		if value == 'network':
			opt = "network_status"
		elif value == 'zpool':
			opt = "zpool_status"
		elif value == 'import':
			opt = "import_status"
		else:
			print "error type for get status"
			sys.exit(1)
	if name in ("-j","--json"):
		if value == 'network':
			opt = "network_json"
		elif value == 'zpool':
			opt = "zpool_json"
		elif value == 'import':
			opt = "current_import"
		else:
			print "error type for get json"
			sys.exit(1)
	if name in ("--import_lzpool"):
			opt = "import_lzpool"
	if name in ("--import_rzpool"):
			opt = "import_rzpool"
	if name in ("--current_import"):
			opt = "current_import"
		
if opt == "help":
		print "Usage:"
		print "		-h or --help"
		print "		-s or --status / -j or --json"
		print "				get local status, use status to print result; use json to format result in json"
		print "				network will echo interface status; zpool echo zpool status; import echo if host manage the local/remote zpool"
		print "				python host_manage.py --status network/zpool/import"
		print "				python host_manage.py --json network/zpool/import"
		print "		--import_lzpool"
		print "				(!!!BE CAREFUL)import those zpools belong to local host"
		print "				zpools define in /usr/local/sassw/conf/function lzpool*=****"
		print "		--import_rzpool"
		print "				(!!!BE CAREFUL)import those zpools belong to remote host"
		print "				zpools define in /usr/local/sassw/conf/function rzpool*=****"
		print "		--current_import"
		print "				list current status of ipmort, define in /usr/local/sassw/conf/function, as same as --json import"
		print "				python host_manage.py --current_import, echo json string with zpool import status"
		print "				status:empty/none/have/all, empty means no zpool define in config file; none means no zpool import here;"
		print "				have means only some of those zpools are imported ; all means all of them imported here;"

if opt == "network_status":
	ret, output = commands.getstatusoutput("ls -l /etc/sysconfig/network-scripts/ifcfg-eth* 2>&1 | awk '{print $NF}'")
	if ret != 0:
		print "error when get network config file"
		sys.exit(ret)
		
	aFiles = output.split('\n')
	
	mStat = {}
	result = 0
	
	for file in aFiles:
		ret, output = commands.getstatusoutput("cat " + file + " | grep 'ONBOOT=yes' | wc -l")
		if not output == '0':
			ret, output = commands.getstatusoutput("cat " + file + " | grep 'DEVICE=' |  awk -F '=' '{print $2}'")
			name = output
			ret, output = commands.getstatusoutput("ip a | grep " + name + " | grep 'state UP' | wc -l")
			if output == '0':
				mStat[name] = "DOWN"
				result = 1
			else:
				mStat[name] = "UP"
	print result
	for key in mStat:
		print key + " : " + mStat[key]

if opt == "zpool_status":
	ret, output = commands.getstatusoutput("zpool list -o name,health -H")
	if ret != 0:
		print "error when get zpool status"
		sys.exit(ret)
		
	aNameAndHealth = output.split('\n')
	mNameAndHealth = {}
	if aNameAndHealth == ['']:
		sys.exit(0)
		
	splitHash(aNameAndHealth, mNameAndHealth)
	
	result = 0
	for key in mNameAndHealth:
		if mNameAndHealth[key] != "ONLINE":
			result = 1
	
	print result
	for key in mNameAndHealth:
		print key + " : " + mNameAndHealth[key]
		
	
if opt == "network_json":
	ret, output = commands.getstatusoutput("ls -l /etc/sysconfig/network-scripts/ifcfg-eth* 2>&1 | awk '{print $NF}'")
	if ret != 0:
		print "error when get network config file"
		sys.exit(ret)
		
	aFiles = output.split('\n')
	
	mStat = {}
	
	for file in aFiles:
		ret, output = commands.getstatusoutput("cat " + file + " | grep 'ONBOOT=yes' | wc -l")
		if not output == '0':
			ret, output = commands.getstatusoutput("cat " + file + " | grep 'DEVICE=' |  awk -F '=' '{print $2}'")
			name = output
			ret, output = commands.getstatusoutput("ip a | grep " + name + " | grep 'state UP' | wc -l")
			if output == '0':
				mStat[name] = "DOWN"
			else:
				mStat[name] = "UP"
	print json.dumps(mStat)

if opt == "zpool_json":
	ret, output = commands.getstatusoutput("zpool list -o name,health -H")
	if ret != 0:
		print "error when get zpool status"
		sys.exit(ret)
		
	aNameAndHealth = output.split('\n')
	mNameAndHealth = {}
	if aNameAndHealth == ['']:
		sys.exit(0)
		
	splitHash(aNameAndHealth, mNameAndHealth)
	print json.dumps(mNameAndHealth)

if opt == "import_lzpool":
	ret, output = commands.getstatusoutput('/usr/local/sassw/bin/addlocal.sh')
	if ret != 0:
		print "zfs import local zpools get error: " + output
		sys.exit(1)
	
if opt == "import_rzpool":
	ret, output = commands.getstatusoutput('/usr/local/sassw/bin/grabsata.sh')
	if ret != 0:
		print "zfs import remote zpools get error: " + output
		sys.exit(1)

if opt == "current_import" or opt == "import_status":
	result = {}
	jresult = {}
	ret, output = commands.getstatusoutput("grep -E '^lzpool[0-9]*=' /usr/local/sassw/conf/function 2>&1 | awk -F '=' '{print $2}'")
	if ret != 0:
		print "error get import config from function: " + output
		sys.exit(1)
	if output == "":
		result['local'] = "empty"
		
	aLZpools = output.split('\n')
	
	ret, output = commands.getstatusoutput("grep -E '^rzpool[0-9]*=' /usr/local/sassw/conf/function 2>&1 | awk -F '=' '{print $2}'")
	if ret != 0:
		print "error get import config from function: " + output
		sys.exit(1)
	if output == "":
		result['remote'] = "empty"
		
	aRZpools = output.split('\n')
	
	ret, output = commands.getstatusoutput("zpool list -o name -H")
	if ret != 0:
		print "error when get zpool status"
		sys.exit(ret)
		
	aZpoolName = output.split('\n')
	
	local = {}
	remote = {}
	result['local'] = "none"
	flag = 0
	for zpool in aLZpools:
		if zpool in aZpoolName:
			result['local'] = "not all"
			local[zpool] = 'online'
		else:
			flag = 1
			local[zpool] = 'offline'
	if flag == 0 and result['local'] == "not all":
		result['local'] = "all"
		
	result['remote'] = "none"
	flag = 0
	for zpool in aRZpools:
		if zpool in aZpoolName:
			result['remote'] = "not all"
			remote[zpool] = 'online'
		else:
			flag = 1
			remote[zpool] = 'offline'
	if flag == 0 and result['remote'] == "not all":
		result['remote'] = "all"
	
	jresult['local'] = local
	jresult['remote'] = remote
	
	if opt == "current_import":
		print json.dumps(jresult)
	else:
		if result['local'] == "all" and result['remote'] == "none":
			print 0
		elif result['local'] == "all" and result['remote'] == "all":
			print 2
		else:
			print 1
		print "local zpool  : " + result['local']
		print "remote zpool : " + result['remote']
	
	

sys.exit(0)
