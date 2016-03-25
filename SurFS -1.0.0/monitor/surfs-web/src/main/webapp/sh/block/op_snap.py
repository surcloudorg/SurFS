#!/usr/bin/python
import os
import commands
import getopt
import sys
import json
import random

def splitHash(array, map):
	for item in array:
		tmp = item.split('\t')
		map[tmp[0]] = tmp[1]
	
try:
    	options,args = getopt.getopt(sys.argv[1:], "hfl", ["help", "force", "list", "source=", "remove=", "snap=", "new=", "new_size="])
except getopt.GetoptError:
	print "getopt exception"
    	sys.exit(1)
	
opt = " "
force = 0
vol = " "
snap = " "
dvol = " "
size = " "
	
for name,value in options:
	if name in ("-h","--help"):
		opt = "help"
	if name in ("-f","--force"):
		force = 1
	if name in ("-l","--list"):
		opt = "list"
	if name in ("--source"):
		vol = value
	if name in ("--remove"):
		opt = "remove"
		snap = value
	if name in ("--snap"):
		snap = value
	if name in ("--new"):
		dvol = value
	if name == "--new_size" :
		size = value
		
if opt == " " and vol != " " and snap != " ":
	opt = "snapshot"
elif opt == " " and vol != " " and dvol != " ":
	opt = "mkVolByVol"
elif opt == " " and snap != " " and dvol != " ":
	opt = "mkVolBySnap"

result = 0
		
if opt == "help":
		print "Usage:"
		print "		-h or --help"
		print "		-l or --list"
		print "				list all snapshot"
		print "		--source A --snap B"
		print "				use zfs to make one snapshot named B for vol A"
		print "		--source A --new B [-f] [--new_size XX]"
		print "				use zfs to make one snapshot first, then clone from the snapshot to one new vol B"
		print "		--snap A --new B [--new_size XX]"
		print "				use zfs to clone from the snapshot A to the new vol B"
		print "		--remove A"
		print "				remove one zfs snapshot"
		print "				it also can be used to remove one vol"

if opt == "remove":
	if snap == " ":
		print "miss device name"
		sys.exit(1)
	
	ret, output = commands.getstatusoutput('zfs destroy ' + snap)
	if ret != 0:
		print "zfs destroy get error: " + output
		sys.exit(1)
if opt == "list":
	result = []
	ret, output = commands.getstatusoutput('zfs list  -t snapshot -Hp -o name,used,creation')
	if ret != 0:
		print "zfs list snapshot: " + output
		sys.exit(1)

	aLine = output.split('\n')
	if aLine == ['']:
		print json.dumps(result)
		sys.exit(0)
	snap = {}
	for line in aLine:
		aItem = line.split()
		snap['name'] = aItem[0]
		snap['size'] = aItem[1]
		snap['ctime'] = aItem[2]
		result.append(snap)
		snap = {}
				
	print json.dumps(result)
		
		
if opt == "snapshot":
	ret, output = commands.getstatusoutput('zfs snapshot ' + vol + '@' + snap)
	if ret != 0:
		print "error to name snapshot"
		sys.exit(1)

if opt == "mkVolByVol":
    	num = random.randint(101, 10001)
	tmpSnap = vol + "@tmpsnap%d"%num
	ret, output = commands.getstatusoutput('zfs snapshot ' + tmpSnap)
	if ret != 0:
		print "error to make tmp snapshot"
		sys.exit(1)
		
	if force == 1 :
		ret, output = commands.getstatusoutput("zfs send -R " + tmpSnap + " | zfs recv -F " + dvol)
	else :
		ret, output = commands.getstatusoutput("zfs send -R " + tmpSnap + " | zfs recv " + dvol)
	if ret != 0:
		print "error to send data to new vol: " + output
		sys.exit(1)
		
	if size != " " :
		if ret != 0:
			print "error to grow size for new vol: " + output
			sys.exit(1)
		
	commands.getstatusoutput('zfs destroy ' + tmpSnap)
	if ret != 0:
		print "error to send data to new vol: " + output
		sys.exit(1)
	
	ret, output = commands.getstatusoutput("zfs list  -r " + dvol + " -t snapshot -H -o name")
	if ret != 0:
		print "error when clean old snapshot: " + output
		sys.exit(1)
	aSnap = output.split('\n')
	if aSnap == ['']:
		sys.exit(0)
		
	for item in aSnap:
		ret, output = commands.getstatusoutput("zfs destroy " + item)
		if ret != 0:
			print "error when clean old snapshot: " + output

if opt == "mkVolBySnap":

	ret, output = commands.getstatusoutput("zfs send -R " + snap + " | zfs recv -F " + dvol)
	if ret != 0:
		print "error to send data to new vol: " + output
		sys.exit(1)
	if size != " " :
		ret, output = commands.getstatusoutput("zfs set volsize=%s %s"%(size, dvol) )
		if ret != 0:
			print "error to grow size for new vol: " + output
			sys.exit(1)
			
	ret, output = commands.getstatusoutput("zfs list  -r " + dvol + " -t snapshot -H -o name")
	if ret != 0:
		print "error when clean old snapshot: " + output
		sys.exit(1)
	aSnap = output.split('\n')
	if aSnap == ['']:
		sys.exit(0)
		
	for item in aSnap:
		ret, output = commands.getstatusoutput("zfs destroy " + item)
		if ret != 0:
			print "error when clean old snapshot: " + output

sys.exit(0)

        
