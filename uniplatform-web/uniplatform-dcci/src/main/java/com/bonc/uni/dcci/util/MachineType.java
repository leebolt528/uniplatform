package com.bonc.uni.dcci.util;

/**机器类型  虚拟机  物理机
 * @author futao
 * 2017年9月5日
 */
public enum MachineType{
	
	PHYSICAL(1),VIRTUAL(2),ALL(3);
	
	private int value;
	
	private MachineType(int value) {
		this.value = value;
	}
	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}
	
}