#This Source Code Form is subject to the terms of the Mozilla Public
#License, v. 2.0. If a copy of the MPL was not distributed with this
#file, You can obtain one at http://mozilla.org/MPL/2.0/.
#!/usr/bin/python
import os
import json
import commands
import sys
import re

# [
	# {
		# 'target': "iqn.52_1",
		# 'acl': [
		#	"iqn.aaa", "iqn.bb"
		# ],
		# 'account': [
		#	"userA", "userB"
		# ],
		# 'login': [
			# {
				# 'ip': "10.0.0.10",
				# 'initiator' : "iqn.1994-05.com.redhat:c6bf4aa4652b"   # not uesd for now
			# },
			# {
				# 'ip': "10.0.0.11",
				# 'initiator' : "iqn.1994-05.com.redhat:aasdawafdgs"
			# }
		# ],
		# 'device': [
			# {
				# 'vol': "/dev/zvol/zpool_1/vol1",
				# 'cap': "1T"
				# 'lun': "2"          # not uesd for now
			# },
			# {
				# 'vol': "/dev/zvol/zpool_1/vol2",
				# 'cap': "2T"
				# 'lun': "3"
			# }
		# ]
	# },
# ]

result = {}
info = []

output = commands.getoutput('hostname')
result['hostname'] = output

output = commands.getoutput("cat /usr/local/sassw/conf/function  | grep localhost | awk -F '=' '{print $2}'")
result['ip'] = output

ret, output = commands.getstatusoutput("tgtadm --lld iscsi --op show  --mode target")
if ret != 0:
	print "tgtd command fail."
	sys.exit(1)

aLine = output.split('\n')

stat = "none"
name = ""
mACL = {}
aACL = []
mAccount = {}
aAccount = []
pattern = re.compile('^Target ')
for line in aLine:
	if stat == "none" and pattern.match(line) :
		tmp = line.split()
		name = tmp[2]
	elif line == "    Account information:" :
		stat = "acconut"
		continue
	elif stat == "acconut" and line == "    ACL information:" :
		stat = "acl"
		continue
	elif stat == "acl" and pattern.match(line) :
		stat = "none"
		mACL[name] = aACL
		aACL = []
		mAccount[name] = aAccount
		aAccount = []
		tmp = line.split()
		name = tmp[2]
		continue
	
	if stat == "acconut" :
		aAccount.append(line.split()[0])
	elif stat == "acl":
		aACL.append(line.split()[0])
		
mACL[name] = aACL
aACL = []
mAccount[name] = aAccount
aAccount = []


ret, output = commands.getstatusoutput("tgtadm --lld iscsi --op show  --mode target | grep -E '(Target|Size|Backing store path|IP Address|Initiator|LUN:)'")

if ret != 0:
	result['info'] = []
	print json.dumps(result)
	sys.exit(0)

aLine = output.split('\n')

mTarget = {}
aLogin = []
mLogin = {}
aDevice = []
mDevice = {}
start = 0

for line in aLine:
	tmp = line.split()
	if tmp[0] == "Target":
		if start == 0:
			start = 1
		else:
			mTarget['login'] = aLogin
			mTarget['device'] = aDevice
			info.append(mTarget)
			
		mTarget = {}
		aLogin = []
		aDevice = []
		mTarget['target'] = tmp[2]
		mTarget['acl'] = mACL[tmp[2]]
		mTarget['account'] = mAccount[tmp[2]]
	elif tmp[0] == "IP":
		mLogin['ip'] = tmp[2] 
		aLogin.append(mLogin)
		mLogin = {}
	elif tmp[0] == "Initiator:":
		mLogin['initiator'] = tmp[1]
	elif tmp[0] == "Backing":
		if tmp[3] == 'None':
			continue
		mDevice['vol'] = tmp[3]
		aDevice.append(mDevice)
		mDevice = {}
	elif tmp[0] == "LUN:":
		if tmp[1] == '0':
			continue
		mDevice['lun'] = tmp[1]
		
	elif tmp[0] == "Size:":
		if tmp[1] == '0':
			continue
		mDevice['cap'] = tmp[1] + tmp[2][:-1]

if not start == 0:
		mTarget['login'] = aLogin
		mTarget['device'] = aDevice
		info.append(mTarget)		

result['info'] = info
print json.dumps(result)
		
		
	
