package com.uestc.spider.www;

import java.util.Queue;
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
		ThreadPoolExecutor cg = new ThreadPoolExecutor(10,50,30,TimeUnit.SECONDS,new ArrayBlockingQueue<Runnable>(queueDeep),  
                new ThreadPoolExecutor.DiscardOldestPolicy());
		/*
		 * �ֶ�һ�����ļ��뱨�����ã��ȽϷ���
		 * */
	/*	for(int i = 0 ; i < 50 ; i++){
			
			try{
				Thread.sleep(2);
			}catch(InterruptedException e){
				System.out.println("��ʧ���ˡ�");
			}
			while(getQueueSize(cg.getQueue()) >= queueDeep){
				
				System.out.println("�����������ȴ�3�����������");
				try{
					Thread.sleep(3000);
				}catch(InterruptedException e){
					System.out.println("��ʧ���ˡ�");
				}
			}
			
		}
		*/
		//�������걨 ���ű��⣺h1  ���ݣ�class="contnt" ���ڣ�class="fst" ������Դ���������걨  ���ŷ��ࣺid=PageLink���������ַ���Ϊ""
		//ͼƬ����src="../../../images/2013-10/30/B02/gly3a1262_b.jpg"��image�����"IMG src=\"(.*?)iamges(.*?)_b.jpg\"" ·�����ʽ��"http:\"?(.*?)(\"|>|\\s+)"�����ַ���"http://epaper.ynet.com"
		//�������ӣ�http://epaper.ynet.com/html/[0-9]{4}-[0-9]{2}/[0-9]{2}/node_[0-9]{4,5}.htm �������ӣ�http://epaper.ynet.com/html/[0-9]{4}-[0-9]{2}/[0-9]{2}/content_[0-9]{5,6}.htm?div=-1
		//s1 = http://epaper.ynet.com/html/ s2 = - s3 = / s4 = /node_1331.htm
		TaskThreadPool ynet = new TaskThreadPool("ynet","cg",new String[]{"h1",""},new String[]{"class","contnt"},new String[]{"class","fst"},new String[]{"�������걨","�������걨�������ߣ�65902020; ������ߣ�400-188-8610;С��ñ���������ߣ�6775-6666;�������������ߣ�65901660"},new String[]{"id","PageLink"},"",
				"http://epaper.ynet.com/","IMG src=\"(.*?)iamges(.*?)_b.jpg\"","http:\"?(.*?)(\"|>|\\s+)","../../../",
				"http://epaper.ynet.com/html/[0-9]{4}-[0-9]{2}/[0-9]{2}/node_[0-9]{4,5}.htm","http://epaper.ynet.com/html/[0-9]{4}-[0-9]{2}/[0-9]{2}/content_[0-9]{5,6}.htm?div=-1","http://epaper.ynet.com/","-","/","/node_1331.htm");        //0,"",new String[]{"",""}
		
		cg.execute(ynet); //���걨�����̳߳�
		
		//�����������ű��⣺title ����(������ԣ�����Ϊʲô����)��id=ozoom ���ڣ�width="316" ��Դ ������ ���ŷ��ࣺwidth="145" �������ַ���""
		//ͼƬ���ã�(http://bjwb.bjd.com.cn/)!images/2014-11/03/10/wjh4b24_b.jpg image������ʽ��"IMG src=\"(.*?)iamges(.*?)_b.jpg\""·�����ʽ��"http:\"?(.*?)(\"|>|\\s+)" �����ַ���"../../../"
		//�������ӣ�http://bjwb.bjd.com.cn/html/[0-9]{4}-[0-9]{2}/[0-9]{2}/node_[0-9]{4,5}.htm �������ӣ�http://bjwb.bjd.com.cn/html/[0-9]{4}-[0-9]{2}/[0-9]{2}/content_[0-9]{5,7}.htm
		//s1 = http://bjwb.bjd.com.cn/ s2  = - s3 = / s4 = /node_82.htm
		TaskThreadPool bjwb = new TaskThreadPool("bjwb","cg",new String[]{"title",""},new String[]{"id","ozoom"},new String[]{"width","316"},new String[]{"������","����������������������վ���������������ձ���ҵ�������У�δ����ɲ���ת��"},new String[]{"width","145"},"",
				"http://bjwb.bjd.com.cn/","IMG src=\"(.*?)iamges(.*?)_b.jpg\"","http:\"?(.*?)(\"|>|\\s+)","../../../",
				"http://bjwb.bjd.com.cn/html/[0-9]{4}-[0-9]{2}/[0-9]{2}/node_[0-9]{4,5}.htm","http://bjwb.bjd.com.cn/html/[0-9]{4}-[0-9]{2}/[0-9]{2}/content_[0-9]{5,7}.htm","http://bjwb.bjd.com.cn/","-","/","/node_82.htm");
		cg.execute(bjwb);
		
		//�ɶ��̱������ű��⣺title ������ id = ozoom ,���ڣ�class="header-today" ��Դ���ɶ��̱� ���ŷ��ࣺ"width","57%";�������ַ�: " - �ɶ��̱�|�ɶ��̱����Ӱ�|�ɶ��̱��ٷ���վ"
		//ͼƬ���ã�http://e.chengdu.cn/html/ ������ʽ��"img src=\"(.*?)res(.*?)attpic_brief.jpg\"" ·�����ʽ��"http:\"?(.*?)(\"|>|\\s+)" �����ַ�����"../../../"
		//��������"http://e.chengdu.cn/html/[0-9]{4}-[0-9]{2}/[0-9]{2}/node_[0-9]{1,2}.htm" �������ӣ�"http://e.chengdu.cn/html/[0-9]{4}-[0-9]{2}/[0-9]{2}/content_[0-9]{1,6}.htm"
		//s1 = http://e.chengdu.cn/html/ s2 - s3 / s4 /node_2.htm
		TaskThreadPool cdsb = new TaskThreadPool("cdsb","cg",new String[]{"title",""},new String[]{"id","ozoom"},new String[]{"class","header-today"},new String[]{"�ɶ��̱�","�ɶ��̱���վ: http://e.chengdu.cn | �ɶ��̱�����: CN51-0073�ɶ��̱���������: 86612222  "},new String[]{"width","57%"},"- �ɶ��̱�|�ɶ��̱����Ӱ�|�ɶ��̱��ٷ���վ",
				"http://e.chengdu.cn/html/","img src=\"(.*?)res(.*?)attpic_brief.jpg\"","http:\"?(.*?)(\"|>|\\s+)","../../../",
				"http://e.chengdu.cn/html/[0-9]{4}-[0-9]{2}/[0-9]{2}/node_[0-9]{1,2}.htm","http://e.chengdu.cn/html/[0-9]{4}-[0-9]{2}/[0-9]{2}/content_[0-9]{1,6}.htm","http://e.chengdu.cn/html/","-","/","/node_2.htm");
		cg.execute(cdsb);
		
		//�ɶ���:���ű���class="bt1" ���ݣ�"class","M_m_cont",���ڣ�"style","display:none" ��Դ���ɶ��� ���ŷ���(��һ���ſ������������)��class="info" ���������ַ���"" 
		//ͼƬ���ã�http://www.cdwb.com.cn/html/ ������ʽ��"IMG src=\"(.*?)res(.*?)attpic_brief.jpg\"" ·����"http:\"?(.*?)(\"|>|\\s+)" ������"../../../"
		//�������ӣ�http://www.cdwb.com.cn/html/[0-9]{4}-[0-9]{2}/[0-9]{2}/node_[0-9]{2,4}.htm ������http://www.cdwb.com.cn/html/[0-9]{4}-[0-9]{2}/[0-9]{2}/content_[0-9]{6,8}.htm
		//s1 = http://www.cdwb.com.cn/html/ s2 - s3 / s4 /node_282.htm
		TaskThreadPool cdwb = new TaskThreadPool("cdwb","cg",new String[]{"class","bt1"},new String[]{"class","M_m_cont"},new String[]{"style","display:none"},new String[]{"�ɶ���","�������ߣ�962111  \\n �ٷ�΢����http://weibo.com/cdwbwb \\n �������ߣ�028-86741226 ������ߣ�028-86746906 \\n �����ַ���ɶ�����·����159��(�ʱ�:610017);�ɶ����Ӱ��Ȩ���У��ɶ����� ��澭Ӫ���֤�ţ�5101034000060"},new String[]{"class","info"},"",
				"http://www.cdwb.com.cn/html/","IMG src=\"(.*?)res(.*?)attpic_brief.jpg\"","http:\"?(.*?)(\"|>|\\s+)","../../../",
				"http://www.cdwb.com.cn/html/[0-9]{4}-[0-9]{2}/[0-9]{2}/node_[0-9]{2,4}.htm","http://www.cdwb.com.cn/html/[0-9]{4}-[0-9]{2}/[0-9]{2}/content_[0-9]{6,8}.htm","http://www.cdwb.com.cn/html/","-","/","/node_282.htm");
		cg.execute(cdwb);
		
		
	
	
	
	}
	private synchronized int getQueueSize(Queue queue){
		
		return queue.size();
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
//	public int flag ;           //�ڼ����߳�
	
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
		
//		this.flag = flag ;
		
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
