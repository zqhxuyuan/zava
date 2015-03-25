package com.github.wangxuehui.rpc.snrpc.server;

import java.util.List;

import com.github.wangxuehui.rpc.snrpc.conf.ConfigureParse;
import com.github.wangxuehui.rpc.snrpc.conf.RpcService;
import com.github.wangxuehui.rpc.snrpc.conf.SnRpcConfig;
import com.github.wangxuehui.rpc.snrpc.conf.XmlConfigureParse;

/**
 * @author skyim E-mail:wxh64788665@gmail.com
 * 类说明
 */
public class ParseXmlToService {

		public void parse(){
			String configFile = SnRpcConfig.getInstance().getPropertiesFile();
			ConfigureParse parse = new XmlConfigureParse(configFile);

			List<RpcService> serviceList = parse.parseService();
			for(RpcService service : serviceList){
				SnNettyRpcServerHandler.putService(service);
			}
		}
}
