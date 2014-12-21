package testRouteAndLance;

import org.I0Itec.zkclient.ZkClient;

public class TestNode {
	public static void main(String[] args) {
		String serverList = "192.168.1.105:2181";
		String PATH = "/configcenter";//���ڵ�·��
		
		ZkClient zkClient = new ZkClient(serverList);
		boolean rootExists =  zkClient.exists(PATH);
		
		if(!rootExists) {
			zkClient.createPersistent(PATH);
			System.out.println("�ڷ�������"+serverList+" �����ɽڵ㣺"+PATH);
			
			zkClient.delete(PATH);
			System.out.println("�ڷ�������"+serverList+" ��ɾ���ڵ㣺"+PATH);
		}
	}
}
