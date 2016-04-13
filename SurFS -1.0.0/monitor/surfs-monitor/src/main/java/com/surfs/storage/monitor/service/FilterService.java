/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
 
package com.surfs.storage.monitor.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.surfs.storage.monitor.service.filter.DateFilter;
import com.surfs.storage.monitor.service.filter.Filter;
import com.surfs.storage.monitor.service.filter.StringFilter;

@Service("filterService")
public class FilterService {
	
	private List<Filter> listFilter = new ArrayList<>();

	
	public boolean filterLog(String line, String... filterArgs) {
		for (int i = 0; i < filterArgs.length; i++) {
			if (!listFilter.get(i).isFilter(line, filterArgs[i]))
				return false;
		}
		return true;
	}
	
	public FilterService() {
		this.addFilter(new DateFilter());
		this.addFilter(new StringFilter());
	}
	
	public void addFilter(Filter filter) {
		listFilter.add(filter);
	}
	
}
