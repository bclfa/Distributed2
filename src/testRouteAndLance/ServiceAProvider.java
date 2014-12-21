package testRouteAndLance;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.I0Itec.zkclient.ZkClient;

public class ServiceAProvider {
	private String serviceName ="SERVICE-A";
	
	//向zookeeper注册服务
	public void init() throws UnknownHostException {
		String serverList = "192.168.1.105:2181";
		String PATH = "/configcenter";//根节点路径
		
		ZkClient zkClient = new ZkClient(serverList);
		boolean rootExists =  zkClient.exists(PATH);
		
		if(!rootExists) {
			zkClient.createPersistent(PATH);
		}
		
		boolean serviceExists =  zkClient.exists(PATH+"/"+serviceName);
		if(!serviceExists) {
			zkClient.createPersistent(PATH+"/"+serviceName);
		}
		
		//注册当前服务器
	    InetAddress addr = 	InetAddress.getLocalHost();
	    String ip = addr.getHostAddress().toString();
	    
	    //创建当前服务器节点
	    zkClient.createEphemeral(PATH+"/"+serviceName+"/"+ip);
	    //设置当前服务器权重为3
	    zkClient.writeData(PATH+"/"+serviceName+"/"+ip, 3);
	    
	    //测试数据
	    String ip2 = "192.168.1.2";
	    String ip3 = "192.168.1.3";
	    zkClient.createEphemeral(PATH+"/"+serviceName+"/"+ip2);
	    zkClient.writeData(PATH+"/"+serviceName+"/"+ip2, 2); 
	    
	    zkClient.createEphemeral(PATH+"/"+serviceName+"/"+ip3);
	    zkClient.writeData(PATH+"/"+serviceName+"/"+ip3, 1);
	    
	    System.out.println("注册到"+PATH+"/"+serviceName+" 节点下的子节点分别为：");
	    System.out.println(PATH+"/"+serviceName+"/"+ip);
	    System.out.println(PATH+"/"+serviceName+"/"+ip2);
	    System.out.println(PATH+"/"+serviceName+"/"+ip3);
	}
	
	//提供服务
	public void privider() {
		
	}
	
	public static void main(String[] args) throws UnknownHostException, InterruptedException {
		ServiceAProvider serviceA = new ServiceAProvider();
		serviceA.init();
		
		Thread.sleep(24*60*60*1000);
	}
}
