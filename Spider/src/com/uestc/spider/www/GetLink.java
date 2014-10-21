package com.uestc.spider.www;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
/*
 * ����ò����ƿ�� �������ڴ治������
 * 2014.10.21��Ҫ�޸ĵĵط���1. �޷����ʵ����ӱ�������
 *                       2.���ʳ�ʱ�����ӱ������� �ȵ����ʵ�ʱ���������
 *                       3.
 * */
public class GetLink {
	
//	public String url;
	//��һ��
	public Queue<String> linkLast = new LinkedList<String>();
	//��һ��
	public Queue<String> linkNext = new LinkedList<String>();
	//������������
	public Queue<String> linkTheme = new LinkedList<String>();
	//pdf
	public Queue<String> linkPdf = new LinkedList<String>();
	//����ÿ���������ݵ�����
	public Queue<String> linkContent = new LinkedList<String>();
	//�����Ѿ����ʹ�������
	public Queue<String> linkVisit = new LinkedList<String>();
	
//	public GetLink(String url){
//		this.url = url;
//	}
	//��url����������ĳһ����themeurl ����ʹĳһ���������ŵ�themeurl
	public void getLink(String themeUrl){
		int state ;
		try{
			HttpURLConnection httpUrlConnection = (HttpURLConnection) new URL(themeUrl).openConnection(); //��������
			state = httpUrlConnection.getResponseCode();
			httpUrlConnection.disconnect();
		}catch (MalformedURLException e) {
//          e.printStackTrace();
			System.out.println("ssssss");
			return;
		} catch (IOException e) {
          // TODO Auto-generated catch block
//          e.printStackTrace();
			System.out.println("tttttttt");
			return ;
      }
		if(state != 200 && state != 201){
			return;
		}
		try{
			Parser parser = new Parser(themeUrl);
			parser.setEncoding("utf-8");
			NodeList nodeList = parser.extractAllNodesThatMatch(new NodeFilter(){
				public boolean accept(Node node)
				{
					if (node instanceof LinkTag)// ���
						return true;
					return false;
				}
			
			});
		
			//
			//���Ű���������ʽ
			Pattern newPage = Pattern.compile("http://e.chengdu.cn/html/[0-9]{4}-[0-9]{2}/[0-9]{2}/node_[0-9]{1,2}.htm");
			//�������ݵ�������ʽ
			Pattern newContent = Pattern.compile("http://e.chengdu.cn/html/[0-9]{4}-[0-9]{2}/[0-9]{2}/content_[0-9]{1,6}.htm");
			//PDF������ʽ
			Pattern newPdf = Pattern.compile("http://e.chengdu.cn/page/[0-9]{1}/[0-9]{4}-[0-9]{2}/[0-9]{2}/[0-9]{2}/[0-9]{10}_pdf.pdf");
		
		//��ȡһ����ҳ���е�����url ����url pdf url
			for (int i = 0; i < nodeList.size(); i++)
			{
			
				LinkTag n = (LinkTag) nodeList.elementAt(i);
//	        	System.out.print(n.getStringText() + "==>> ");
//	       	 	System.out.println(n.extractLink());
				//ĳһ��
				Matcher themeMatcher = newPage.matcher(n.extractLink());
				//���������
				Matcher contentMatcher = newContent.matcher(n.extractLink());
				//PDF
				Matcher pdfMatcher = newPdf.matcher(n.extractLink());
	        
				if(!linkVisit.contains(n.extractLink())){
	        			if(themeMatcher.find()){
	        				linkTheme.offer(n.extractLink());
	        				linkVisit.offer(n.extractLink());
	        			}
	        			if(contentMatcher.find()){
	        				linkContent.offer(n.extractLink());
	        				linkVisit.offer(n.extractLink());
	        			}
	        			if(pdfMatcher.find()){
	        				linkPdf.offer(n.extractLink());
	        				linkVisit.offer(n.extractLink());
	        			}
	        	
				}
			}
		}catch(ParserException e){
			return ;
		}catch(Exception e){
			return ;
		}
	}
	
	
	public void allWeWillDo(String themeUrl) throws Exception{
		
		int i = 0;
		linkTheme.offer(themeUrl);
//			linkVisit.offer(n.extractLink());
			while(!linkTheme.isEmpty()){
				getLink(linkTheme.poll());
				while(!linkContent.isEmpty()){
					StringBuffer s = new StringBuffer(linkContent.poll());
					i++;
//					System.out.println(s);
					CDSB cdsb = new CDSB(s.toString());
					cdsb.memory(s.toString());
				}
//				System.out.println("�����ڰѻ�ȡ�����Ŵ������ݿ�...");
			}
			System.out.println("���ֵ�����������"+ i);
			
		
	
	}
	
	
	public void result(int year,int month ,int day) throws Exception{
		String s1 = "http://e.chengdu.cn/html/2014-0";
		String s2 = "/node_2.htm";
		String s3 = "/";
		for(int j  = 1 ; j < 10 ;j ++){
			
			for(int i = 1 ; i < 32 ;i++){
				String url;
				if(i < 10)
					url = s1+j +s3+"0"+i+s2;
				else
					url = s1+j+s3+i+s2;
				
//			System.out.println(url);
				allWeWillDo(url);   
			//����Ѿ����ʵ�link�б���ÿ���������ȡ�洢��Ҫ�����з��ʹ������ӽ���������Լ�ڴ�
				linkVisit.clear();
				
			
			}
			System.gc();
		}
	}
	public static void main(String args[]) throws Exception{
		long start = System.currentTimeMillis();    
		GetLink test = new GetLink();
		test.result(0, 0, 0);
//		String url = "http://e.chengdu.cn/html/2014-10/16/node_2.htm";
//		System.out.println(" ������Ŭ����������...");
//		test.allWeWillDo(url);
//		System.out.println("����ִ�����...");
		long end = System.currentTimeMillis();
		System.out.println(end-start);
	}
	
}
