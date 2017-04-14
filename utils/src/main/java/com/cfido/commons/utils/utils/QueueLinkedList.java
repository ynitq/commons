package com.cfido.commons.utils.utils;

import java.util.LinkedList;

/**
 * <pre>
 * 限制大小的LinkedList队列--非线程安全，
 * 线程安全的请使用：
 * ConcurrentLinkedQueue（并发队列）
 * LinkedBlockingQueue（阻塞队列）
 * </pre>
 * 
 * @author 黄云
 * 2015-9-25
 */
public class QueueLinkedList<E> extends LinkedList<E>{

	private static final long serialVersionUID = 1L;

	private final int maxSize;
	
	public QueueLinkedList(int maxSize) {
		this.maxSize = maxSize;
	}
	
	@Override
	public boolean add(E element) {
		if(this.size()>=maxSize){
			super.removeFirst();
		}
        return super.add(element);
    }
	
}
