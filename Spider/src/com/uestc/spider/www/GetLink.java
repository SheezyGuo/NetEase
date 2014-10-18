package com.uestc.spider.www;

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
	public void getLink(String themeUrl) throws ParserException{
		
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
		
		for (int i = 0; i < nodeList.size(); i++)
	      {
	        LinkTag n = (LinkTag) nodeList.elementAt(i);
//	        System.out.print(n.getStringText() + "==>> ");
//	        System.out.println(n.extractLink());
	        //ĳһ��
	        Matcher themeMatcher = newPage.matcher(n.extractLink());
	        //���������
	        Matcher contentMatcher = newContent.matcher(n.extractLink());
	        //PDF
	        Matcher pdfMatcher = newPdf.matcher(n.extractLink());
	        
	        if(!linkVisit.contains(n.extractLink())){
	        	
//	        	if(n.getStringText().equals("��һ��")){
//	        		System.out.println("zhe li zen me le ");
//	        		linkLast.offer(n.extractLink());
////	        		linkVisit.offer(n.extractLink());
//	        	}else if(n.getStringText().equals("��һ��")){
//	        		linkNext.offer(n.extractLink());
////	        		linkVisit.offer(n.extractLink());
//	        	}else{
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
	}
	
	
	public void allWeWillDo(String themeUrl) throws Exception{
		
		
		linkTheme.offer(themeUrl);
//			linkVisit.offer(n.extractLink());
			while(!linkTheme.isEmpty()){
				getLink(linkTheme.poll());
				while(!linkContent.isEmpty()){
					StringBuffer s = new StringBuffer(linkContent.poll());
					CDSB cdsb = new CDSB(s.toString());
					cdsb.memory(s.toString());
				}
				System.out.println("�����ڰѻ�ȡ�����Ŵ������ݿ�...");
			}
			
		
		
//		while(!linkNext.isEmpty()){
//			
//			getLink(linkNext.poll());
//			
//			while(!linkTheme.isEmpty()){
//				
//				getLink(linkTheme.poll());
//				
//				while(!linkContent.isEmpty()){
//					
//					
//					StringBuffer s = new StringBuffer(linkContent.poll());
//					
//					CDSB cdsb = new CDSB(s.toString());
//					
//					cdsb.memory(s.toString());
//					
//				}
//			}
//			
//		}
	
	}
	public void result(){
		String url = "http://e.chengdu.cn/html/2014-10/08/node_2.htm";
//		for(int i = 2014 ; i > 20)
	}
	public static void main(String args[]) throws Exception{
		long start = System.currentTimeMillis();    
		GetLink test = new GetLink();
		String url = "http://e.chengdu.cn/html/2014-10/16/node_2.htm";
		System.out.println(" ������Ŭ����������...");
		test.allWeWillDo(url);
		System.out.println("����ִ�����...");
		long end = System.currentTimeMillis();
		System.out.println(end-start);
	}
	
}
