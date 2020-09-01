package com.dubbo.router.loadbalance;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.extension.SPI;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.RpcContext;
import com.alibaba.dubbo.rpc.RpcException;
import com.alibaba.dubbo.rpc.cluster.LoadBalance;
import com.alibaba.dubbo.rpc.cluster.loadbalance.RandomLoadBalance;
import com.dubbo.router.DevDubboConstants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

public class DevLoadBalance extends RandomLoadBalance  {
    private static final Log logger = LogFactory.getLog(DevLoadBalance.class);
    public static final String NAME = "dev";

    public <T> Invoker<T> select(List<Invoker<T>> list, URL url, Invocation invocation) throws RpcException {
        logger.info("loadBalance select start");
        //获取起始ip信息
        Object fromIp = RpcContext.getContext().getAttachment(DevDubboConstants.START_IP);
        if(fromIp==null){
            try {
                //如果没有获取到起始ip，则优先选择自己的ip
                fromIp = InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
        logger.info("loadBalance select ip is:"+fromIp);
        //选择起始ip的服务，或者自己的服务进行调用
        for(Invoker<T> invoker:list){
            if(invoker.getUrl().getIp().equals(fromIp.toString())){
                return invoker;
            }
        }
        logger.info("select super loadBalance");
        //如果起始ip没有启动服务，自己也没有启动，则使用默认规则
        return super.select(list,url,invocation);
    }
}
