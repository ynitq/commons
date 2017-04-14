/**
 * <pre>
 * 用aop解决死锁的方案，原理是:
 * 1. 进入方法前先将锁都找出来，
 * 2. 先按ID排序，
 * 3. 然后统一加锁，
 * 4. 出方法后，统一解锁
 * </pre>
 * 
 * @author 梁韦江
 *  2016年7月8日
 */

package com.cfido.commons.utils.sortedLock;
