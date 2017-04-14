package com.cfido.commons.utils.db;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import org.springframework.util.Assert;

import com.cfido.commons.beans.form.IPageForm;
import com.cfido.commons.beans.others.IConverter;
import com.cfido.commons.utils.utils.ConverterUtil;

/**
 * 用于存储分页查询的结果
 * @author liangwj
 *
 * @param <T>
 */
public class PageQueryResult<T> implements Serializable{

	private static final long serialVersionUID = 2886257609486632909L;

	// 翻页栏上面页码的数量
	private int pageBarLinkCount = 5;

	private final int pageSize;

	private int pageNo = 1;
	private int pageTotal;// 总页数
	private final int itemTotal;// 总记录数
	private final List<T> list; // 数据

	private final PageNumBean prev = new PageNumBean();
	private final PageNumBean next = new PageNumBean();

	private String actionUrl;// 用于页面上生成翻页栏

	public String getActionUrl() {
		return actionUrl;
	}

	public void setActionUrl(String actionUrl) {
		this.actionUrl = actionUrl;
	}

	public PageQueryResult(int itemTotal, List<T> list, IPageForm form) {
		this(itemTotal, list, form.getPageNo(), form.getPageSize());
	}

	public PageQueryResult(int itemTotal, List<T> list, int pageno, int pageSize) {
		Assert.notNull(list,"数据list不能为空");

		this.list = list;
		this.itemTotal = itemTotal;
		this.pageNo = pageno;
		this.pageSize = pageSize;

		calc();

		// 上一页
		if (this.pageNo > 1) {
			this.prev.setPageNo(this.pageNo - 1);
			this.prev.setBol(true);
		}

		// 下一页
		if (this.itemTotal > 0 && pageTotal > pageNo) {
			this.next.setPageNo(this.pageNo + 1);
			this.next.setBol(true);
		}
	}

	/**
	 * 根据参数计算总页数，当前页数等
	 */
	private void calc() {
		if (itemTotal > 0) {
			pageTotal = itemTotal / pageSize;
			if (itemTotal % pageSize != 0)
				pageTotal++;
			if (pageNo < 1) {
				this.pageNo = 1;
			}
		} else {
			this.pageTotal = 0;
		}
		if (pageNo > pageTotal) {
			pageNo = pageTotal;
		}

	}

	public int getItemTotal() {
		return itemTotal;
	}

	public int getPageNo() {
		return pageNo;
	}

	public int getPageTotal() {
		return pageTotal;
	}

	public List<T> getList() {
		return list;
	}

	public List<PageNumBean> getPageList() {
		List<PageNumBean> res = new LinkedList<PageNumBean>();
		if (this.itemTotal > 0) {
			int begin = this.pageNo - this.pageBarLinkCount;
			if (begin < 1) {
				begin = 1;
			}

			int end = begin + 2 * this.pageBarLinkCount;
			if (end > this.pageTotal) {
				end = this.pageTotal;
			}
			for (int i = begin; i <= end; i++) {
				PageNumBean bean = new PageNumBean();
				bean.setPageNo(i);
				bean.setBol(i == this.pageNo);
				res.add(bean);
			}
		}
		return res;
	}
	
	public int getPageSize(){
		return pageSize;
	}

	public void setPageBarLinkCount(int pageBarLinkCount) {
		this.pageBarLinkCount = pageBarLinkCount;
	}

	public PageNumBean getPrev() {
		return prev;
	}

	public PageNumBean getNext() {
		return next;
	}
	
	public int getPageBarLinkCount() {
		return pageBarLinkCount;
	}

	/**
	 * 将分页的结果转换成为另外一个
	 * 
	 * @param converter
	 *            转换器
	 * @return
	 */
	public <TARGET> PageQueryResult<TARGET> convert(IConverter<T, TARGET> converter) {

		// 将list转化一下
		List<TARGET> targetList = ConverterUtil.convertList(this.list, converter);

		// 重新包装 PageQueryResult
		PageQueryResult<TARGET> res = new PageQueryResult<>(this.itemTotal, targetList, this.pageNo, this.pageSize);

		// 设置其他属性
		res.setActionUrl(this.actionUrl);
		res.setPageBarLinkCount(this.pageBarLinkCount);

		return res;

	}
	
}
