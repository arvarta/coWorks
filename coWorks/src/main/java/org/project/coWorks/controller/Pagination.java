package org.project.coWorks.controller;

import java.util.ArrayList;
import java.util.List;

public class Pagination {
	public static final int PER_PAGE = 10;

	public static <T> List<T> paging(List<T> pagingList, int page) throws Exception {
		if(pagingList.size() <= PER_PAGE) {
			return pagingList;
		}
		
		List<T> result = new ArrayList<>();
		int perPage = totalPage(pagingList);
		if(perPage < page) {
			throw new Exception("Requested page (" + page + ") exceeds max page (" + perPage + ")");
		}
		
		int firstPage = (page - 1)*PER_PAGE;
		for(int i=0; (firstPage+i)<pagingList.size() && i < PER_PAGE; i++) {
			result.add(pagingList.get(firstPage+i));
		}
		return result;
	}
	
	public static <T> List<T> paging(List<T> pagingList, int perPage, int page) throws Exception {
		if(pagingList.size() <= page) {
			return pagingList;
		}
		
		List<T> result = new ArrayList<>();
		int totalPage = totalPage(pagingList, perPage);
		if(totalPage < page) {
			throw new Exception("Requested page (" + page + ") exceeds max page (" + totalPage + ")");
		}
		
		int firstPage = (page - 1)*perPage;
		for(int i=0; (firstPage+i)<pagingList.size() && i < perPage; i++) {
			result.add(pagingList.get(firstPage+i));
		}
		return result;
	}
	
	public static int totalPage(List<?> list) {
		int totalPage = list.size()/PER_PAGE;
		if(list.size()%PER_PAGE != 0) {
			++totalPage;
		}
		return totalPage;
	}
	
	public static int totalPage(List<?> list, int perPage) {
		int totalPage = list.size()/perPage;
		if(list.size()%perPage != 0) {
			++totalPage;
		}
		return totalPage;
	}
}
