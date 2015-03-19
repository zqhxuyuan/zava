package com.github.yuriyao.FLRMI.FLRMIAL;


import java.io.Serializable;

/**
 * FLRMI的服务查询的目标对象
 *
 * @author fengjing.yfj
 * @version $Id: FLRMITarget.java, v 0.1 2014年1月28日 上午10:31:05 fengjing.yfj Exp $
 */
public class FLRMITarget implements Serializable {

    /** 序列号 */
    private static final long  serialVersionUID = -5729614448033405490L;

    /** FLRMI 的协议前缀 */
    public static final String FL_RMI_PREFIX    = "FL_RMI";

    /** 服务名 */
    private String             serviceName;

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

}