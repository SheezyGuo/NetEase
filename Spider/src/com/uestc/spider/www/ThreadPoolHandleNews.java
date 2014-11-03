package com.uestc.spider.www;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolHandleNews{

	private static int queueDeep = 20 ;
	
	public void createThreadPool(){
		/*   
         * �����̳߳أ���С�߳���Ϊ10������߳���Ϊ50���̳߳�ά���̵߳Ŀ���ʱ��Ϊ30�룬   
         * ʹ�ö������Ϊ20���н���У����ִ�г�����δ�رգ���λ�ڹ�������ͷ�������񽫱�ɾ����   
         * Ȼ������ִ�г�������ٴ�ʧ�ܣ����ظ��˹��̣��������Ѿ����ݶ�����ȶ�������ؽ����˿��ơ�   
         */ 
		ThreadPoolExecutor cg = new ThreadPoolExecutor(10,100,30,TimeUnit.SECONDS,new ArrayBlockingQueue<Runnable>(queueDeep),  
                new ThreadPoolExecutor.DiscardOldestPolicy());
	}
	
	public static void main(String[] args){
		
		TaskThreadPool testPool = new TaskThreadPool();
		Thread t = new Thread(testPool);
		t.start();
//		testPool.run();
	}
	
	
}

class TaskThreadPool implements Runnable{

	/**
	 * �ܶ���Ҫ��ʼ���Ķ�����
	 * �ú�����
	 * 1.���ݿ� ���� ����
	 * 2.���ţ����ű����ǩTitle[] ���ݱ�ǩContent[] ���ڱ�ǩdate[]  ������Դ newSource[] ���ŷ��� categroy[] ���Ŵ������ַ��� bufString
	 * 3.ͼƬ��ͼƬ����url ��image������ʽ��IMURL_REG ·�����ʽ��IMSRC_REG ���Ӹ����ַ��� ��imageBuf
	 * 4.�����������ӣ�ThemeLink ,�������ӣ�contentLink �Լ������ַ���
	 * 
	 * */
	public String DBName ;      //���ݿ�����
	public String DBTable ;     //���ݿ����
	
	public String title[];      // ���ű���
	public String content[];    //��������
	public String date[] ;      //��������
	public String newSource[] ; //������Դ    //�ɹ̶�
	public String categroy[] ;  //���ŷ���
	public String bufString ;   //���Ŵ������ַ���
	
	public String imageUrl ;        //ͼƬ����url
	public String imurl_reg ;      //image������ʽ
	public String imscr_reg ;      //ͼƬ·��
	public String imageBuf ;       //ͼƬ���Ӹ����ַ���
	
	public String themeLink ;       //��������������ʽ
	public String contentLink;     //��������������ʽ
	public String newurl1 ;        //����������� 1,2,3,4
	public String newurl2 ;
	public String newurl3 ;
	public String newurl4 ;
	
	
	public TaskThreadPool(){
		
	}
	
	//�о�������캯�����۰�
	
	public TaskThreadPool(String DBName ,String DBTable ,
			String title[],String content[],String date[],String newSource[] ,String categroy[] ,String bufString,
			String imageurl ,String imurl_reg ,String imsrc_reg,String imageBuf,
			String themeLink ,String contentLink,String newurl1,String newurl2,String newurl3 ,String newurl4){
		
		this.DBName = DBName ;
		this.DBTable = DBTable ;
		
		this.title = title ;
		this.content = content ;
		this.date = date ;
		this.newSource = newSource ;
		this.categroy = categroy ;
		this.bufString = bufString ;
		
		this.imageUrl = imageurl ;
		this.imurl_reg = imurl_reg ;
		this.imscr_reg = imsrc_reg ;
		this.imageBuf = imageBuf ;
		
		this.themeLink = themeLink ;
		this.contentLink = contentLink ;
		this.newurl1 = newurl1 ;
		this.newurl2 = newurl2 ;
		this.newurl3 = newurl3 ;
		this.newurl4 = newurl4 ;
		
		
		
		
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		System.out.println(Thread.currentThread()+"xixi");
	}
	
	
}
