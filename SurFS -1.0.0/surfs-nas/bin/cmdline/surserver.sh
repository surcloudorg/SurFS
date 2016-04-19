/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
# ! /bin/sh
# ---------------------NAS-server-----------------------
while  IFS='=' read var val
do
  if [[ $var == 'wrapper.java.command' ]]
  then
	java_cmd=${val:0:${#val}-1}
  elif [[ $var == 'wrapper.java.initmemory' ]]
  then
    java_opts=-Xms${val:0:${#val}-1}M
  elif [[ $var == 'wrapper.java.maxmemory' ]]
  then
    java_opts="$java_opts -Xmx${val:0:${#val}-1}M"
  elif [[ $var == 'wrapper.java.additional.1' ]]
  then
    java_opts="$java_opts ${val:0:${#val}-1}"
  elif [[ $var == 'wrapper.java.additional.2' ]]
  then
    java_opts="$java_opts ${val:0:${#val}-1}"
  elif [[ $var == 'wrapper.java.additional.3' ]]
  then
    java_opts="$java_opts ${val:0:${#val}-1}"
  elif [[ $var == 'wrapper.java.additional.4' ]]
  then
    java_opts="$java_opts ${val:0:${#val}-1}"
  elif [[ $var == 'wrapper.java.additional.5' ]]
  then
    java_opts="$java_opts ${val:0:${#val}-1}"
  elif [[ $var == 'wrapper.java.classpath.1' ]]
  then
    classpath=${val:0:${#val}-1}
  elif [[ $var == 'wrapper.java.classpath.2' ]]
  then
    classpath="$classpath:${val:0:${#val}-1}"
  elif [[ $var == 'wrapper.java.classpath.3' ]]
  then
    classpath="$classpath:${val:0:${#val}-1}"
  elif [[ $var == 'wrapper.java.classpath.4' ]]
  then
    classpath="$classpath:${val:0:${#val}-1}"
  elif [[ $var == 'wrapper.app.parameter.1' ]]
  then
    java_args=${val:0:${#val}-1}
  elif [[ $var == 'wrapper.app.parameter.2' ]]
  then
    java_args="$java_args ${val:0:${#val}-1}"
  elif [[ $var == 'wrapper.java.mainclass' ]]
  then
    mainclass=${val:0:${#val}-1}
  fi 
done < ../surserver.conf
 
cmd="$java_cmd $java_opts -classpath $classpath $mainclass $java_args"
echo "cmd: $cmd"
$cmd
