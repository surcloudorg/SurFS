#This Source Code Form is subject to the terms of the Mozilla Public
#License, v. 2.0. If a copy of the MPL was not distributed with this
#file, You can obtain one at http://mozilla.org/MPL/2.0/.
#!/bin/bash

EC=`ls -l /dev/bsg/ | grep expander| wc -l`
if [ $EC == 0 ] 
then
	echo "No expander found in /dev/bsg/"
	exit 1
fi

AEX_NAME=$(ls -l /dev/bsg/expander*| awk '{print $NF}' | awk -F '/' '{print $4}')
declare -A AEX_ADDR
declare -A AEX_A2EX
declare -A AEX_FLAG
for EX in ${AEX_NAME}
do
	smp_discover -A /dev/bsg/$EX > /dev/null
	if [ $? != 0 ] 
	then
		continue
	fi
	ME2=`cat /sys/class/sas_device/$EX/sas_address`
	ME=${ME2:2}
	AEX_ADDR[$EX]=$ME
	AEX_A2EX[$ME]=$EX
	SWITCH=`smp_discover -A /dev/bsg/$EX | grep 'SSP+STP+SMP' | wc -l`
	if [ $SWITCH != 0 ] 
	then
		AEX_FLAG[$EX]=1
	else
		AEX_FLAG[$EX]=0
	fi
done

ZPOOLS=$(zpool list | sed -n '2,$p' | awk '{print $1;}')
declare -A ZPOOL_DISK
declare -A DISK_FREE
for ZPOOL in ${ZPOOLS}
do
	DISKS=$(zpool status $ZPOOL | grep JBOD | awk '{print $1;}')
	for DISK in ${DISKS}
	do
		ZPOOL_DISK[$DISK]=$ZPOOL
	done
done

declare -A EX_PAIR
for EX in ${AEX_NAME}
do
	smp_discover -A /dev/bsg/$EX > /dev/null
	if [ $? != 0 ]
	then
			continue
	fi
	LEX=$(smp_discover /dev/bsg/$EX | grep exp | awk '{print $2}' | awk -F '[:[]' '{print $5}' | sort | uniq)
	LSATA=$(smp_discover /dev/bsg/$EX | grep -E '  t\(SSP\)]|SATA' | awk '{print $2}')
	
	FOB="b"
	JBOD=${AEX_ADDR[$EX]:0:14}
	for UNIT in ${LEX}
	do
		if [ ${AEX_FLAG[${AEX_A2EX[$UNIT]}]} == 1 ] 
		then
			FOB="f";
		else
			JBOD=${UNIT:0:14}
		fi
	done
	if [ $FOB == "f" ]
	then
		JBOD=${AEX_ADDR[$EX]:0:14}
	fi
	
	for UNIT in ${LSATA}
	do
		ADDR=`echo $UNIT | awk -F '[:[]' '{print $5}'`
		PHY=`echo $UNIT | awk -F '[:[]' '{print $1}'`
		STAT="-"
		NAME="JBOD_${AEX_ADDR[$EX]}_${FOB}_phy${PHY}"
		
		LOCAL="remote"
		ZPOOL="none"
		IN=`cat /usr/local/sassw/conf/disk/localphy_manual | grep $PHY | wc -l`
		if [ $IN != 0 ] 
		then
			LOCAL="local"
			MP=` multipath -ll $NAME | grep status=active | wc -l`
			DEV=`multipath -ll $NAME | sed -n '$p;' | awk '{print $3;}'`
			ZP=`echo "${!ZPOOL_DISK[@]}" | grep "$NAME" | wc -l`
			if [ $MP == 0 ] 
			then
				STAT="FAIL"
				if [ $ZP != 0 ]
                                then
                                        ZPOOL=${ZPOOL_DISK[$NAME]}
                                        echo "$NAME $JBOD $FOB $PHY $LOCAL $STAT --- $ZPOOL"
                                else
                                        echo "$NAME $JBOD $FOB $PHY $LOCAL $STAT --- ---"
                                fi
			else 
				if [ $ZP != 0 ] 
				then
					STAT="USED"
					ZPOOL=${ZPOOL_DISK[$NAME]}
					echo "$NAME $JBOD $FOB $PHY $LOCAL $STAT /dev/$DEV $ZPOOL"
				else
					STAT="FREE"
					echo "$NAME $JBOD $FOB $PHY $LOCAL $STAT --- ---"
				fi
			fi
		else
			FREE=`cat /usr/local/sassw/conf/disk/remotephy | awk '{print $1;}'| grep $PHY | wc -l`
			if [ $FREE == 0 ]
			then
				echo "$NAME $JBOD $FOB $PHY $LOCAL FREE"
			else
				echo "$NAME $JBOD $FOB $PHY $LOCAL ----"
			fi
		fi

	done
done	
exit 0


