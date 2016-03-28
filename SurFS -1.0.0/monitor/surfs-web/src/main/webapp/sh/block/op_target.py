
/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
#!/usr/bin/python
import os
import commands
import getopt
import sys
import string
import json
import re

target_conf = "/dev/null"

def getLunByTid(tid, luns):
	ret, output = commands.getstatusoutput("tgtadm --lld iscsi --op show  --mode target | grep -E '(Target|Backing store path|LUN:)'")
	if ret != 0:
		sys.exit(1)
		
	selected = 0
	lun = {}
	lines = output.split('\n')
	for line in lines:
		tmp = line.split()
		if tmp[0] == "Target":
			if selected == 1:
				break
			if tmp[1].split(':')[0] == tid:
				selected = 1
		elif tmp[0] == "Backing":
			if tmp[3] == 'None':
				continue
			if selected == 1:
				lun['device'] = tmp[3]
				luns.append(lun)
				lun = {}
		elif tmp[0] == "LUN:":
			if tmp[1] == '0':
				continue
			if selected == 1:
				lun['id'] = tmp[1]
	
	return 
	
def getTid(target):
	ret, output = commands.getstatusoutput('tgtadm --lld iscsi --op show  --mode target | grep -w Target')
	if ret != 0:
		print "get tid fail: " + output
		sys.exit(1)
		
	lines = output.split('\n')
	
	for line in lines:
		tmp = line.split()
		if tmp[2] == target:
			return tmp[1][:-1]

	return '0'

def getLun(tid, device):
	luns = []
	getLunByTid(tid, luns)
	
	for lun in luns:
		if lun['device'] == device:
			return lun['id']
	return '0'

def getFreeTid():
	ret, output = commands.getstatusoutput("tgtadm --lld iscsi --mode target --op show | grep -E '^Target'")
	if output == '':
                return '1'
	if ret != 0:
		print "get tid fail: " + output
		sys.exit(1)
	
	index = 1
	lines = output.split('\n')
		
	for line in lines:
		tmp = line.split(':')
		id = string.atoi(tmp[0].split()[1])
		if not id == index:
			break
		index += 1
		
	return str(index)
		
	

def getFreeLun(tid):
	luns = []
	getLunByTid(tid, luns)
	ids = []
	for lun in luns:
		id = string.atoi(lun['id'])
		ids.append(id)

	index = 1	
	for id in ids:
		if not id == index:
			break
		index +=1

	return str(index)
	

try:
    	options,args = getopt.getopt(sys.argv[1:], "hs", ["help", "status", "new", "delete", "target=", "add=", "remove=", "acl=", "user=", "pw=", "unbind_user="])
except getopt.GetoptError:
		print "getopt exception"
		sys.exit(1)
	
opt = "help"
p1 = " "
p2 = " "

acl = " "
user = " "
pw = " "
	
for name,value in options:
	if name in ("-h","--help"):
		opt = "help"
	if name in ("-s","--status"):
		opt = "status"
	if name in ("--new"):
		opt = "new_target"
	if name in ("--delete"):
		opt = "delete_target"
	if name in ("--add"):
		opt = "add"
		p1 = value
	if name in ("--remove"):
		opt = "remove"
		p1 = value
	if name in ("--target"):
		p2 = value
	if name in ("--acl"):
		opt = "acl"
		acl = value
	if name in ("--user"):
		opt = "account"
		user = value
	if name in ("--unbind_user"):
		opt = "unbind_account"
		user = value
	if name in ("--pw"):
		opt = "account"
		pw = value
result = 0
		
if opt == "help":
		print "Usage:"
		print "		-h or --help"
		print "		-s or --status"
		print "				get tgtd service status"
		print "		--new"
		print "				create one target, you should also use --target to point the new target name"
		print "				python op_target.py --new --target iqn.123"
		print "		--delete"
		print "				delete one target, you should also use --target to point the target name"
		print "				python oop_target.py --delete --target iqn.123"
		print "		--add"
		print "				add one device to one target, use --target to point which target will be edited"
		print "				python op_target.py --add /dev/zvol/test/vol1 --target iqn.123"
		print "		--remove"
		print "				remove one device from one target, use --target to point which target will be edited"
		print "				python op_target.py --remove /dev/zvol/test/vol1 --target iqn.123"
		print "		--acl"
		print "				add acl info to one target, use ALL to allow all client login"
		print "				python op_target.py --acl iqn.client.13468 --target iqn.localhost"
		print "		--user and --pw"
		print "				add one account and bind it to one target, so that other clinet can login with this account"
		print "				python op_target.py --target iqn.localhost --user me --pw 123456"
		print "		--unbind_user"
		print "				unbind one account from one target. Then if no target bind this account, the account will be removed"
		print "				python op_target.py --target iqn.localhost --unbind_user me"
#
if opt == "status":
		ret, output = commands.getstatusoutput('service tgtd status')
		if ret != 0:
			print "error when get tgtd service status"
			sys.exit(ret)
		else:
			print "tgtd service [OK]"
			sys.exit(0)
#
if opt == "new_target":
	if p2 == " ":
		print "miss target name"
		sys.exit(1)
		
	tid = getFreeTid()
	ret, output = commands.getstatusoutput('tgtadm --lld iscsi --op new --mode target --tid ' + tid + ' -T ' + p2)
	if ret != 0:
		print "add new target error: " + output
		sys.exit(1)
		
	ret, output = commands.getstatusoutput('tgtadm --lld iscsi --op bind --mode target --tid ' + tid + ' -I ALL')
	if ret != 0:
		print "add target authorization for all fail: " + output
		sys.exit(1)
	
	ret, output = commands.getstatusoutput('tgt-admin --dump > ' + target_conf)
	if ret != 0:
		print "tgt flush config file error:" + output
		sys.exit(1)
#
if opt == "delete_target":
	if p2 == " ":
		print "miss target name"
		sys.exit(1)
	
	tid = getTid(p2)
	if tid == '0':
		print "Error get tid"
		sys.exit(1)
	ret, output = commands.getstatusoutput('tgtadm --lld iscsi --op delete --mode target --tid ' + tid)
	if ret != 0:
		print "delete target error: " + output
		sys.exit(1)
		
	ret, output = commands.getstatusoutput('tgt-admin --dump > ' + target_conf)
	if ret != 0:
		print "tgt flush config file error:" + output
		sys.exit(1)
#
if opt == "add":
	if p1 == " ":
		print "miss device name"
		sys.exit(1)
	if p2 == " ":
		print "miss target name"
		sys.exit(1)
	
	tid = getTid(p2)
	if tid == '0':
		print "Error get tid"
		sys.exit(1)
	lun = getFreeLun(tid)
	ret, output = commands.getstatusoutput('tgtadm --lld iscsi --op new --mode logicalunit --tid ' + tid + ' --lun ' + lun + ' -b ' + p1)
	if ret != 0:
		print "tgt add lun " + p1 + " to " + p2 + " get error: " + output
		sys.exit(1)
		
	ret, output = commands.getstatusoutput('tgt-admin --dump > ' + target_conf)
	if ret != 0:
		print "tgt flush config file error:" + output
		sys.exit(1)
#		
if opt == "remove":
	if p1 == " ":
		print "miss device name"
		sys.exit(1)
	if p2 == " ":
		print "miss target name"
		sys.exit(1)
	
	tid = getTid(p2)
	if tid == '0':
		print "Error get tid"
		sys.exit(1)
	lun = getLun(tid, p1)
	if lun == '0':
		print "Error get lun"
		sys.exit(1)
	ret, output = commands.getstatusoutput('tgtadm --lld iscsi --op delete --mode logicalunit --tid ' + tid + ' --lun ' + lun)
	if ret != 0:
		print "tgt add lun " + p1 + " to " + p2 + " get error: " + output
		sys.exit(1)
	
	ret, output = commands.getstatusoutput('tgt-admin --dump > ' + target_conf)
	if ret != 0:
		print "tgt flush config file error:" + output
		sys.exit(1)
#
if opt == "acl":
	if acl == " ":
		print "miss acl info"
		sys.exit(1)
	if p2 == " ":
		print "miss target name"
		sys.exit(1)
	
	tid = getTid(p2)
	if tid == '0':
		print "Error get tid"
                sys.exit(1)
	#unbind ALL because when create target, wo add default acl: ALL
	commands.getstatusoutput("tgtadm --lld iscsi --op unbind --mode target --tid " + tid + " -I ALL")
	
	ret, output = commands.getstatusoutput("tgtadm --lld iscsi --op bind --mode target --tid " + tid + " -I " + acl)
	if ret != 0:
		print "tgt bind acl " + acl + " to " + p2 + " get error: " + output
		sys.exit(1)
	
	ret, output = commands.getstatusoutput('tgt-admin --dump > ' + target_conf)
	if ret != 0:
		print "tgt flush config file error:" + output
		sys.exit(1)
#		
if opt == "account":
	if user == " ":
		print "miss user name"
		sys.exit(1)
	if pw == " ":
		print "miss password"
		sys.exit(1)
	if p2 == " ":
		print "miss target name"
		sys.exit(1)
	
	tid = getTid(p2)
	if tid == '0':
		print "Error get tid"
                sys.exit(1)
	
	ret, output = commands.getstatusoutput('tgtadm --lld iscsi --op show --mode account | grep ' + user + ' | wc -l')
	if ret != 0:
		print "tgt grep account get error: " + output
		sys.exit(1)
	# we can not delete exist account because every target will lost the account info we deleted
	if output == "0" :
		ret, output = commands.getstatusoutput('tgtadm --lld iscsi --op new --mode account --user ' + user + ' --password ' + pw)
		if ret != 0:
			print "tgt new account get error: " + output
			sys.exit(1)
			
	ret, output = commands.getstatusoutput('tgtadm --lld iscsi --op bind --mode account --tid ' + tid + ' --user ' + user)
	if ret != 0:
		print "tgt bind account get error: " + output
		commands.getstatusoutput('tgtadm --lld iscsi --op delete --mode account --user ' + user)
		sys.exit(1)
	
	ret, output = commands.getstatusoutput('tgt-admin --dump > ' + target_conf)
	if ret != 0:
		print "tgt flush config file error:" + output
		sys.exit(1)
#
if opt == "unbind_account":
	if user == " ":
		print "miss user name"
		sys.exit(1)
	if p2 == " ":
		print "miss target name"
		sys.exit(1)
	
	tid = getTid(p2)
	if tid == '0':
		print "Error get tid"
		sys.exit(1)		
	
	ret, output = commands.getstatusoutput('tgtadm --lld iscsi --op unbind --mode account --tid ' + tid + ' --user ' + user)
	if ret != 0:
		print "tgt unbind account get error: " + output
		sys.exit(1)

	ret, output = commands.getstatusoutput("tgtadm --lld iscsi --op show  --mode target")
	if ret != 0:
		print "tgtd show fail."
		sys.exit(1)

	aLine = output.split('\n')

	stat = "none"
	aAccount = []
	pattern = re.compile('^Target ')
	for line in aLine:
		if line == "    Account information:" :
			stat = "acconut"
			continue
		elif stat == "acconut" and pattern.match(line) :
			stat = "none"
			continue
		if stat == "acconut" :
			aAccount.append(line.split()[0])
			
	if not user in aAccount :
		ret, output = commands.getstatusoutput('tgtadm --lld iscsi --op delete --mode account --user ' + user)
		if ret != 0:
			print "delete account fail: " + output
			sys.exit(1)
	
	ret, output = commands.getstatusoutput('tgt-admin --dump > ' + target_conf)
	if ret != 0:
		print "tgt flush config file error:" + output
		sys.exit(1)

sys.exit(0)

