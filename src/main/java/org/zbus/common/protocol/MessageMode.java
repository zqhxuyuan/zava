package org.zbus.common.protocol;


public enum MessageMode {
	MQ,     //消息队列
	PubSub, //发布订阅 
	Temp;   //是否临时
	
	private MessageMode(){
        mask = (1 << ordinal());
    }
	
    private final int mask;

    public final int getMask() {
        return mask;
    }
    
    public int intValue(){
    	return this.mask;
    }
    
    public static boolean isEnabled(int features, MessageMode feature) {
        return (features & feature.getMask()) != 0;
    }
    
    public static int intValue(MessageMode... features){
    	int value = 0;
    	for(MessageMode feature : features){
    		value |= feature.mask;
    	}
    	return value;
    }
}