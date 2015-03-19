package com.github.zangxiaoqiang.common.mail;

public interface Postman {
    /**
     * Send mail notice to admin
     * @param message: reprocess jobs commands such as hadoop jar a.jar 2011-11-11
     */
   public void mailNotcie(String message);	

}
