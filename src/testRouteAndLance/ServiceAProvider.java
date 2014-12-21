package testRouteAndLance;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.I0Itec.zkclient.ZkClient;

public class ServiceAProvider {
	private String serviceName ="SERVICE-A";
	
	//��zookeeperע�����
	public void init() throws UnknownHostException {
		String serverList = "192.168.1.105:2181";
		String PATH = "/configcenter";//���ڵ�·��
		
		ZkClient zkClient = new ZkClient(serverList);
		boolean rootExists =  zkClient.exists(PATH);
		
		if(!rootExists) {
			zkClient.createPersistent(PATH);
		}
		
		boolean serviceExists =  zkClient.exists(PATH+"/"+serviceName);
		if(!serviceExists) {
			zkClient.createPersistent(PATH+"/"+serviceName);
		}
		
		//ע�ᵱǰ������
	    InetAddress addr = 	InetAddress.getLocalHost();
	    String ip = addr.getHostAddress().toString();
	    
	    //������ǰ�������ڵ�
	    zkClient.createEphemeral(PATH+"/"+serviceName+"/"+ip);
	    //���õ�ǰ������Ȩ��Ϊ3
	    zkClient.writeData(PATH+"/"+serviceName+"/"+ip, 3);
	    
	    //��������
	    String ip2 = "192.168.1.2";
	    String ip3 = "192.168.1.3";
	    zkClient.createEphemeral(PATH+"/"+serviceName+"/"+ip2);
	    zkClient.writeData(PATH+"/"+serviceName+"/"+ip2, 2); 
	    
	    zkClient.createEphemeral(PATH+"/"+serviceName+"/"+ip3);
	    zkClient.writeData(PATH+"/"+serviceName+"/"+ip3, 1);
	    
	    System.out.println("ע�ᵽ"+PATH+"/"+serviceName+" �ڵ��µ��ӽڵ�ֱ�Ϊ��");
	    System.out.println(PATH+"/"+serviceName+"/"+ip);
	    System.out.println(PATH+"/"+serviceName+"/"+ip2);
	    System.out.println(PATH+"/"+serviceName+"/"+ip3);
	}
	
	//�ṩ����
	public void privider() {
		
	}
	
	public static void main(String[] args) throws UnknownHostException, InterruptedException {
		ServiceAProvider serviceA = new ServiceAProvider();
		serviceA.init();
		
		Thread.sleep(24*60*60*1000);
	}
}
