package com.github.zangxiaoqiang.dfc.locator;

import com.github.zangxiaoqiang.dfc.HashAlgorithm;

public abstract class CommonNodeLocator implements NodeLocator{
	
	public static long getPathHash(HashAlgorithm alg, final String path){
		byte[] digest = alg.computeMd5(path);
		return alg.hash(digest, 0);
	}
}
