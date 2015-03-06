package com.ctriposs.bigcache.sample;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class SampleValue  implements Serializable {
	
	private static final long serialVersionUID = 1L;
	public String aa = "aaaaaaaaaa";
    public String bb = "bbbbbbbbbb";
    public BuySell cc = BuySell.Buy;
    public BuySell dd = BuySell.Sell;
    public int ee = 123456;
    public int ff = 654321;
    public double gg = 1.23456789;
    public double hh = 9.87654321;
    public long ii = 987654321;
    public long jj = 123456789;
    
    public byte[] toBytes() throws IOException {
    	ByteArrayOutputStream bos = new ByteArrayOutputStream();
    	ObjectOutput out = null;
    	try {
    	  out = new ObjectOutputStream(bos);   
    	  out.writeObject(this);
    	  byte[] yourBytes = bos.toByteArray();
    	  return yourBytes;
    	} finally {
    	  try {
    	    if (out != null) {
    	      out.close();
    	    }
    	  } catch (IOException ex) {
    	    // ignore close exception
    	  }
    	  try {
    	    bos.close();
    	  } catch (IOException ex) {
    	    // ignore close exception
    	  }
    	}
    }
    
    public static SampleValue fromBytes(byte[] bytes) throws ClassNotFoundException, IOException {
    	ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
    	ObjectInput in = null;
    	try {
    	  in = new ObjectInputStream(bis);
    	  Object o = in.readObject(); 
    	  return (SampleValue)o;
    	} finally {
    	  try {
    	    bis.close();
    	  } catch (IOException ex) {
    	    // ignore close exception
    	  }
    	  try {
    	    if (in != null) {
    	      in.close();
    	    }
    	  } catch (IOException ex) {
    	    // ignore close exception
    	  }
    	}
    }
    
	enum BuySell {
	    Buy, Sell
	}
	

}
