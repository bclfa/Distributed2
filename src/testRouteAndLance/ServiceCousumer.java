package testRouteAndLance;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;

public class ServiceCousumer {
	private List<String> serverIpList = new ArrayList<String>();
	private Map<String,Integer> serverListMap = new HashMap<String,Integer>();
	
	public static Integer pos = 0;
	
	//初始化服务器地址信息 
	public void init() throws Exception {
		String serviceName = "SERVICE-A";
		String serverList = "192.168.1.105:2181";
		
		String SERVICE_PATH = "/configcenter/"+serviceName;//服务节点路径
		
		ZkClient zkClient = new ZkClient(serverList) ;
		boolean serviceExists =  zkClient.exists(SERVICE_PATH);
		
		if(serviceExists) {
			List<String> ips =  zkClient.getChildren(SERVICE_PATH);
			
			serverIpList = ips;
			
			for(int i=0;i<ips.size();i++) {
				Integer weight = zkClient.readData(SERVICE_PATH+"/"+ips.get(i));
				serverListMap.put(ips.get(i), weight);
			}
		}else {
			throw new Exception("service not exist!");
		}
		
		//注册事件监听
		zkClient.subscribeChildChanges(SERVICE_PATH, new IZkChildListener() {
			
			@Override
			public void handleChildChange(String parentPath, List<String> currentChilds)
					throws Exception {
				serverIpList = 	currentChilds;	
			}
		});
		
		serverListMap.clear();//重新获取权重信息
		for(int i=0;i<serverIpList.size();i++) {
			Integer weight = zkClient.readData(SERVICE_PATH+"/"+serverIpList.get(i));
			serverListMap.put(serverIpList.get(i), weight);
		}
	}
	
	
	//消费服务
	public void consume() {
		System.out.println("服务路由到的IP地址列表为：");
		Set<String> set = serverListMap.keySet();
		
		for(String ip:set) {
			System.out.println(ip);
		}
		
		String server = roundRobin();
		System.out.println("轮询法分配到的IP为："+server);
		
	    server = random();
		System.out.println("随机法分配到的IP为："+server);
		
		server = consumerHash();
		System.out.println("源地址哈希法分配到的IP为："+server);
		
		server = weightRoundRobin();
		System.out.println("加权轮询法分配到的IP为："+server);
		
		server = weightRandom();
		System.out.println("加权随机法分配到的IP为："+server);
	}
	
	//轮询法
	public String roundRobin() {
		Map<String,Integer> serverMap = new HashMap<String,Integer>();
		serverMap.putAll(serverListMap);
		
		Set<String> set = serverMap.keySet();
		List<String> ipList = new ArrayList<String>();
		ipList.addAll(set);
		
		String server = null;
		
		synchronized(pos) {
			if(pos >= set.size()) {
				pos = 0;
			}
			
			server = ipList.get(pos);
			pos ++;
		}
		return server;
	}
	
	//随机法
	public String random() {
		Map<String, Integer> serverMap = new HashMap<String, Integer>();
		serverMap.putAll(serverListMap);

		Set<String> set = serverMap.keySet();
		List<String> ipList = new ArrayList<String>();
		ipList.addAll(set);

		String server = null;

		Random random = new Random();
		int randomPos = random.nextInt(ipList.size());
		
		server = ipList.get(randomPos);
		
		return server;
	}
	
	//源地址哈希法
	public String consumerHash(){
		Map<String, Integer> serverMap = new HashMap<String, Integer>();
		serverMap.putAll(serverListMap);

		Set<String> set = serverMap.keySet();
		List<String> ipList = new ArrayList<String>();
		ipList.addAll(set);

		String server = null;
		
		InetAddress addr = null;
		try {
			addr = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		int hashCode = addr.getHostAddress().toString().hashCode();
		
		int serverPos = hashCode % ipList.size();
		server = ipList.get(serverPos);

		return server;
	}
	
	//加权轮询法
	public String weightRoundRobin() {
		Map<String, Integer> serverMap = new HashMap<String, Integer>();
		serverMap.putAll(serverListMap);

		Set<String> set = serverMap.keySet();
		List<String> ipList = new ArrayList<String>();
		
		for(String ip:set) {
			Integer weight = serverMap.get(ip);
			
			for(int i=0;i<weight;i++) {
				ipList.add(ip);
			}
		}

		String server = null;

		synchronized (pos) {
			if (pos >= set.size()) {
				pos = 0;
			}

			server = ipList.get(pos);
			pos++;
		}
		return server;
	}
	
	//加权随机法
	public String weightRandom() {
		Map<String, Integer> serverMap = new HashMap<String, Integer>();
		serverMap.putAll(serverListMap);

		Set<String> set = serverMap.keySet();
		List<String> ipList = new ArrayList<String>();

		for(String ip:set) {
			Integer weight = serverMap.get(ip);
			
			for(int i=0;i<weight;i++) {
				ipList.add(ip);
			}
		}
		
		String server = null;

		Random random = new Random();
		int randomPos = random.nextInt(ipList.size());

		server = ipList.get(randomPos);

		return server;
	}
	
	public static void main(String[] args) throws Exception {
		ServiceCousumer consumer = new ServiceCousumer();
		consumer.init();
		consumer.consume();
	}
}
