package com.github.yuriyao.FLRMI.FLRMIALImpl;


import com.github.yuriyao.FLRMI.FLRMIAL.FLRMITarget;
import com.github.yuriyao.FLRMI.FLRMIAL.ServiceFinder;
import com.github.yuriyao.FLRMI.FLRMIAL.TargetFinderCenter;

/**
 * 用于从FLRMI协议目标获取服务对象
 *
 * @author fengjing.yfj
 * @version $Id: FLRMIServiceFinder.java, v 0.1 2014年1月28日 上午10:38:15 fengjing.yfj Exp $
 */
public class FLRMIServiceFinder implements ServiceFinder {

    /** 静态单例 */
    private static final FLRMIServiceFinder FLRMI_SERVICE_FINDER = new FLRMIServiceFinder();

    //注册到查找中心
    static {
        TargetFinderCenter.registerFinder(FLRMI_SERVICE_FINDER);
    }

    /** 默认构造器 */
    private FLRMIServiceFinder() {

    }

    /**
     * 创建单例
     *
     * @return
     */
    public static FLRMIServiceFinder create() {
        return FLRMI_SERVICE_FINDER;
    }

    @Override
    public boolean accept(Object target) {
        //只接受FLRMI对象
        if (target != null && target instanceof FLRMITarget) {
            return true;
        }
        return false;
    }

    @Override
    public Object getService(Object target) {
        FLRMITarget target2 = (FLRMITarget) target;

        /** 获取服务 */
        return ServiceCenterImpl.create().getService(FLRMITarget.FL_RMI_PREFIX,
                target2.getServiceName());
    }

}