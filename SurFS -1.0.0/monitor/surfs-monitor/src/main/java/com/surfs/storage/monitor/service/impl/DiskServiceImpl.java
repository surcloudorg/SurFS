/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.surfs.storage.monitor.service.impl;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.autumn.core.log.LogFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.surfs.storage.common.util.CmdUtils;
import com.surfs.storage.common.util.Constant;
import com.surfs.storage.common.util.HttpUtils;
import com.surfs.storage.monitor.model.Disk;
import com.surfs.storage.monitor.service.DiskService;
import com.surfs.storage.monitor.service.FilterService;
import com.surfs.storage.monitor.service.MonitorConstant;
import com.surfs.storage.monitor.utils.CollectionUtil;
import com.surfs.storage.monitor.utils.DiskComparator;

@Service("diskService")
public class DiskServiceImpl implements DiskService {
	
	@Autowired
	private FilterService filterService;
	
	/*private String port;
	
	private String service_diskInfos;
	
	private String service_path;
	
	private String shell_path;
	
	private String remote_host_cmd;
	
	private String log_location;
	
	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getService_diskInfos() {
		return service_diskInfos;
	}

	public void setService_diskInfos(String service_diskInfos) {
		this.service_diskInfos = service_diskInfos;
	}

	public String getService_path() {
		return service_path;
	}

	public void setService_path(String service_path) {
		this.service_path = service_path;
	}

	public String getShell_path() {
		return shell_path;
	}

	public void setShell_path(String shell_path) {
		this.shell_path = shell_path;
	}

	public String getLog_location() {
		return log_location;
	}

	public void setLog_location(String log_location) {
		this.log_location = log_location;
	}

	public String getRemote_host_cmd() {
		return remote_host_cmd;
	}

	public void setRemote_host_cmd(String remote_host_cmd) {
		this.remote_host_cmd = remote_host_cmd;
	}*/
	
	@Override
	public Map<String, Map<String, List<Disk>>> getPoolUsedDisks() {
		Map<String, Map<String, List<Disk>>> mmList = new HashMap<>();
		Map<String, List<Disk>> mapList = new HashMap<>();
		
		for (String pool : MonitorConstant.SURFS_LOCAL_POOL.split(",")) {
			List<Disk> disks = new ArrayList<>();
			mapList.put(pool, disks);
		}
		
		for (Disk disk : getLocalDiskInfos()) {
			if (mapList.containsKey(disk.getZpoolName())) {
				List<Disk> list = mapList.get(disk.getZpoolName());
				list.add(disk);
			}
		}
		
		mmList.put(CmdUtils.getLocalhostIp(), mapList);
		
		return mmList;
	}
	
	private Disk getInstance(String[] lines) {
		Disk disk = new Disk();
		for (int i = 0; i < lines.length; i++) {
			if (i == 0)
				disk.setId(lines[i]);
			else if (i == 1)
				disk.setJbodId(lines[i]);
			else if (i == 2)
				disk.setPanel(lines[i]);
			else if (i == 3)
				disk.setDisk(lines[i]);
			else if (i == 4)
				disk.setLocation(lines[i]);
			else if (i == 6)
				disk.setDevName(lines[i]);
			else if (i == 7)
				disk.setZpoolName(lines[i]);
			else if (i == 5) {
				disk.setStatus(lines[i]);
				disk.setStatusCss(CollectionUtil.getStatusCssClass(lines[i]));
			}
		}
		return disk;
	}
	
	private void fillDiskToMap(Map<String, Map<String, Map<String, Disk>>> mapDisks,
			Disk disk) {
		if (mapDisks.containsKey(disk.getJbodId())) {
			Map<String, Map<String, Disk>> frontBack = mapDisks.get(disk
					.getJbodId());
			if (frontBack.containsKey(disk.getPanel())) {
				Map<String, Disk> disks = frontBack.get(disk.getPanel());
				if (disks.containsKey(disk.getDisk())) {
					Disk diskMap = disks.get(disk.getDisk());
					// 新加入的disk是local
					if (disk.getLocation().equals("local")) {
						// 新加入的disk和map中的disk的localtion相等，并池名称不相等
						if (disk.getLocation().equals(diskMap.getLocation())
								&& !disk.getZpoolName().equals(diskMap.getZpoolName())) {
							diskMap.setZpoolName(diskMap.getZpoolName() + "," + disk.getZpoolName());
						} else {
							disks.put(disk.getDisk(), disk);
						}
					}
				} else {
					disks.put(disk.getDisk(), disk);
				}
			} else {
				Map<String, Disk> disks = new TreeMap<>(new DiskComparator());
				disks.put(disk.getDisk(), disk);
				frontBack.put(disk.getPanel(), disks);
			}
		} else {
			Map<String, Disk> disks = new TreeMap<>(new DiskComparator());
			disks.put(disk.getDisk(), disk);
			Map<String, Map<String, Disk>> frontBack = new HashMap<>();
			frontBack.put(disk.getPanel(), disks);
			mapDisks.put(disk.getJbodId(), frontBack);
		}
	}

	@Override
	public Map<String, Map<String, Map<String, Disk>>> getDiskInfos() {
		// 合并本地远程disk
		Map<String, Map<String, Map<String, Disk>>> mergeDisks = mergeDisks(
				getLocalDiskInfos(), getRemoteDiskInfos());
		// 填充没有盘位的disk信息
		fillNotConnectedDisks(mergeDisks);

		return mergeDisks;
	}
	
	@Override
	public List<Disk> getLocalDiskInfos() {
		// <jbodid, <面板(f/b), <盘位(8-28/12-35), disk对象>>>
		List<Disk> disks = new ArrayList<>();
		BufferedReader bufReader = null;
		try {
			bufReader = CmdUtils.executeCmdForReader(MonitorConstant.DISKINFOS_SHELL_PATH);
			while(true) {
				String line = bufReader.readLine();
				if (StringUtils.isBlank(line))
					break;
				String[] lines = line.split(" ");
				disks.add(getInstance(lines));
			}
		} catch (Exception e) {
			LogFactory.error(e.getMessage());
		} finally {
			if (bufReader != null)
				try {
					bufReader.close();
				} catch (Exception e) {
					LogFactory.error(e.getMessage());
				}
		}
		return disks;
	}
	
	
	@Override
	public List<Disk> getRemoteDiskInfos() {
		List<Disk> list = new ArrayList<>();
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			list = objectMapper.readValue(getRemoteDiskInfosForJson(),  new TypeReference<List<Disk>>(){});
		} catch (Exception e) {
			LogFactory.error(e.getMessage());
		}
		return list;
	}
	
	private String getRemoteDiskInfosForJson() throws Exception {
		String ip = CmdUtils.getRemoteIp();
		//String ip = "10.0.33.52";
		String url = HttpUtils.getUrl(ip, Constant.REST_SERVICE_PORT,
				MonitorConstant.DISK_SERVICE_PATH,
				MonitorConstant.DISK_SERVICE_DISKINFOS_NAME);
		return HttpUtils.invokeHttpForGet(url);
	}
	
	private Map<String, Map<String, Map<String, Disk>>> mergeDisks(List<Disk> locals, List<Disk> remotes) {
		// <jbodid, <面板(f/b), <盘位(8-28/12-35), disk对象>>>
		Map<String, Map<String, Map<String, Disk>>> mergeDisks = new HashMap<>();

		addDisk(mergeDisks, locals);
		
		addDisk(mergeDisks, remotes);

		return mergeDisks;
	}
	
	private void addDisk(
			Map<String, Map<String, Map<String, Disk>>> mergeDisks,
			List<Disk> disks) {
		for (Disk disk : disks) {
			fillDiskToMap(mergeDisks, disk);
		}
	}
	
	private void fillNotConnectedDisks(Map<String, Map<String, Map<String, Disk>>> mapDisks) {
		for (Entry<String, Map<String, Map<String, Disk>>> jbodDisks : mapDisks.entrySet()) {
			String jbod = jbodDisks.getKey();
			Map<String, Map<String, Disk>> panelDisksMap = jbodDisks.getValue();
			// 前面板没有可用的盘
			if (!panelDisksMap.containsKey("f")) {
				Map<String, Disk> front = new TreeMap<>(new DiskComparator());
				panelDisksMap.put("f", front);
			}
			// 后面板没有可用的盘
			if (!panelDisksMap.containsKey("b")) {
				Map<String, Disk> back = new TreeMap<>(new DiskComparator());
				panelDisksMap.put("b", back);
			}
			for (Entry<String, Map<String, Disk>> panelDisks : panelDisksMap.entrySet()) {
				String panel = panelDisks.getKey();
				Map<String, Disk> disks = panelDisks.getValue();
				if ("f".equals(panel)) {
					addNotConnectedDisks(jbod, panel, disks,
							CollectionUtil.FRONT_START,
							CollectionUtil.FRONT_END);
				} else {
					addNotConnectedDisks(jbod, panel, disks,
							CollectionUtil.BACK_START, CollectionUtil.BACK_END);
				}
			}
		}
	}
	
	/**
	 * 填充没有的盘位
	 * 
	 * @param jbod
	 * @param panel
	 * @param disks
	 * @param start
	 * @param end
	 */
	private void addNotConnectedDisks(String jbod, String panel,
			Map<String, Disk> disks, int start, int end) {
		for (int i = start; i <= end; i++) {
			if (!disks.containsKey(String.valueOf(i))) {
				Disk disk = new Disk();
				disk.setId(UUID.randomUUID().toString());
				disk.setJbodId(jbod);
				disk.setPanel(panel);
				disk.setDisk(String.valueOf(i));
				disk.setStatus("NOT CONN");
				// css style
				disk.setStatusCss(CollectionUtil.getStatusCssClass(disk
						.getStatus()));
				disks.put(disk.getDisk(), disk);
			}
		}
	}
	
	@Override
	public String readLocationLog(String date) {
		StringBuilder sb = new StringBuilder();
		BufferedReader read = null;
		try {
			FileInputStream fis = new FileInputStream(MonitorConstant.DISK_LOGFILE_PATH);
			read = new BufferedReader(new InputStreamReader(fis));
			while(true) {
				String line = read.readLine();
				if (StringUtils.isBlank(line))
					break;
				if (filterService.filterLog(line, date, "Error")) {
					sb.append(line);
					sb.append("<br>");
				}
			}
		} catch (Exception e) {
			LogFactory.error(e.getMessage());
		} finally {
			try {
				if (read != null)
				read.close();
			} catch (Exception e) {
				LogFactory.error(e.getMessage());
			}
		}
		
		return sb.toString();
	}
	
}
