/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
#!/usr/bin/python
import os
import json
import commands
import sys
import getopt
import string

# {
	# 'local': "10.0.33.52"
	# 'hostname': "sur52"
	# 'info': [
				# {
					# 'zpool': "zp1",
					# 'total': "4T",
					# 'free': "1T",
					# 'ctime':  "Wed Jan 13  7:22 2016"
					# 'vols': [
						# {
							# 'vol': "vol1",
							# 'cap': "1T"
							# 'used': "no"
							# 'ctime':  "Wed Jan 13  7:22 2016"
						# },
						# {
							# 'vol': "vol2",
							# 'cap': "2T"
							# 'used': "yes"  # '--' means target command faild
							# 'ctime':  "Wed Jan 13  7:22 2016"
						# }
					# ]
				# },
				# {
					# 'zpool': "zp2",
					# 'total': "4T",
					# 'free': "4T",
					# 'vols': []
				# },
			# ]
# }

def splitHash(array, map):
	for item in array:
		tmp = item.split('\t')
		map[tmp[0]] = tmp[1]

pro = 0

try:
    	options,args = getopt.getopt(sys.argv[1:], "p", [])
except getopt.GetoptError:
		print "getopt exception"
		sys.exit(1)
		
for name,value in options:
	if name in ("-p"):
		pro = 1

result = {}
info = []

output = commands.getoutput('hostname')
result['hostname'] = output

output = commands.getoutput("cat /usr/local/sassw/conf/function  | grep localhost | awk -F '=' '{print $2}'")
result['ip'] = output

cmd = 'zpool list -H -o name,size'
ret, output = commands.getstatusoutput(cmd)
if ret != 0:
	sys.exit(1)

aNameAndSize = output.split('\n')
mNameAndSize = {}
if aNameAndSize == ['']:
	result['info'] = []
	print json.dumps(result)
	sys.exit(0)

splitHash(aNameAndSize, mNameAndSize)

if pro == 1 :
	cmd = 'zfs list -Hp -o name,available'
elif pro == 0 :
	cmd = 'zfs list -H -o name,available'
	
ret, output = commands.getstatusoutput(cmd)
if ret != 0:
	sys.exit(1)

aNameAndAvail = output.split('\n')
mNameAndAvail = {}
if aNameAndAvail == ['']:
	result['info'] = []
	print json.dumps(result)
	sys.exit(0)
splitHash(aNameAndAvail, mNameAndAvail)

if pro == 1 :
	cmd = 'zfs list -Hp -o name,used'
elif pro == 0 :
	cmd = 'zfs list -H -o name,used'
	
ret, output = commands.getstatusoutput(cmd)
if ret != 0:
	sys.exit(1)

aNameAndUsed = output.split('\n')
mNameAndUsed = {}
if aNameAndAvail == ['']:
	result['info'] = []
	print json.dumps(result)
	sys.exit(0)
splitHash(aNameAndUsed, mNameAndUsed)

if pro == 1 :
	cmd = 'zfs list -Hp -o name,creation'
elif pro == 0 :
	cmd = 'zfs list -H -o name,creation'

ret, output = commands.getstatusoutput(cmd)
if ret != 0:
	sys.exit(1)

aNameAndCtime = output.split('\n')
mNameAndCtime = {}
if aNameAndCtime == ['']:
	result['info'] = []
	print json.dumps(result)
	sys.exit(0)
for item in aNameAndCtime:
	tmp = item.split('\t', 2)
	mNameAndCtime[tmp[0]] = tmp[1]


mChildren = {}
if pro == 1 :
	for key in mNameAndAvail:
		if mNameAndSize.has_key(key):
			mNameAndSize[key] =  str(string.atoi(mNameAndAvail[key], 10) + string.atoi(mNameAndUsed[key], 10) )
		else :
			tmp = key.split('/')
			mChildren[tmp[0]] = []
elif pro == 0 :
	for key in mNameAndAvail:
		if not mNameAndSize.has_key(key):
			#mNameAndAvail[key] = mNameAndUsed[key]
			path = "/dev/zvol/" + key
			ret, output = commands.getstatusoutput("lsblk " + path + " -n -o size")
			if ret != 0:
				mNameAndAvail[key] = "UNKNOWN"
			else:
				mNameAndAvail[key] = output
			tmp = key.split('/')
			mChildren[tmp[0]] = []

for key in mNameAndAvail:
	if not mNameAndSize.has_key(key):
		tmp = key.split('/')
		vol = {}
		vol['vol'] = key
		vol['cap'] = mNameAndUsed[key].split()[0]
		vol['ctime'] = mNameAndCtime[key]
		ret, output = commands.getstatusoutput("tgtadm --lld iscsi --op show  --mode target")
		if ret != 0:
			vol['used'] = "no"
			ret, output = commands.getstatusoutput('cat /etc/tgt/targets.conf 2>&1 | grep -w ' + key + ' | wc -l')
			if not output == '0':
				vol['used'] = "yes"
		else:
			vol['used'] = "no"
			ret, output = commands.getstatusoutput('tgtadm --lld iscsi --op show  --mode target 2>&1 | grep -w ' + key + ' | wc -l')
			
			if not output == '0':
				vol['used'] = "yes"
		
		mChildren[tmp[0]].append(vol)

for key in mNameAndSize:
	
	tmp = {}
	if mNameAndSize.has_key(key) and mNameAndAvail.has_key(key):
		tmp['zpool'] = key
		tmp['total'] = mNameAndSize[key]
		tmp['free'] = mNameAndAvail[key]
		tmp['ctime'] = mNameAndCtime[key]
		if mChildren.has_key(key):
			tmp['vols'] = mChildren[key]
		else:
			tmp['vols'] = []
		info.append(tmp)
	
result['info'] = info

print json.dumps(result)

sys.exit(0)
		
	
