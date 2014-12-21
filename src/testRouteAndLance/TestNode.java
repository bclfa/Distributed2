package testRouteAndLance;

import org.I0Itec.zkclient.ZkClient;

public class TestNode {
	public static void main(String[] args) {
		String serverList = "192.168.1.105:2181";
		String PATH = "/configcenter";//根节点路径
		
		ZkClient zkClient = new ZkClient(serverList);
		boolean rootExists =  zkClient.exists(PATH);
		
		if(!rootExists) {
			zkClient.createPersistent(PATH);
			System.out.println("在服务器："+serverList+" 上生成节点："+PATH);
			
			zkClient.delete(PATH);
			System.out.println("在服务器："+serverList+" 上删除节点："+PATH);
		}
	}
}
