package com.dubbo.router.filter;

import com.alibaba.dubbo.rpc.*;
import com.dubbo.router.DevDubboConstants;
import com.dubbo.router.loadbalance.DevLoadBalance;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class DevFilter implements Filter {
    private static final Log logger = LogFactory.getLog(DevLoadBalance.class);
    /**
     * 每次进行远程条用都经过此方法，加入开始ip的标识
     */
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        logger.info("dev filter start");
        //判断是否存在开始ip
        Object fromIp = RpcContext.getContext().getAttachment(DevDubboConstants.START_IP);
        if(fromIp==null){
            try {
                //第一次调用时，设置开始ip
                fromIp = InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
        logger.info("dev filter set startip is "+fromIp);
        //设置ip信息
        RpcContext.getContext().setAttachment(DevDubboConstants.START_IP,fromIp.toString());
        return invoker.invoke(invocation);
    }
}
