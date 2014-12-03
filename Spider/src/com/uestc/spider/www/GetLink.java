package com.uestc.spider.www;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.Queue;
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
	//��һ�ڣ�Ϊ����չ
	public Queue<String> linkLast = new LinkedList<String>();
	//��һ�� ��չ
	public Queue<String> linkNext = new LinkedList<String>();
	//������������
	public Queue<String> linkTheme = new LinkedList<String>();
	//pdf ��չ
	public Queue<String> linkPdf = new LinkedList<String>();
	//����ÿ���������ݵ�����
	public Queue<String> linkContent = new LinkedList<String>();
	//�����Ѿ����ʹ�������
	public Queue<String> linkVisit = new LinkedList<String>();
	
	//ƥ������link theme
	private String newThemeLink ;  // = "http://www.chinamil.com.cn/jfjbmap/content/[0-9]{4}-[0-9]{2}/[0-9]{2}/node_[0-9]{1,2}.htm";
	//ƥ������link 
	private String newContentLink ; // ="http://www.chinamil.com.cn/jfjbmap/content/[0-9]{4}-[0-9]{2}/[0-9]{2}/content_[0-9]{5,6}.htm";
	//ƥ��PDF link Ԥ��
//	private String newPdfLink;
	
	//��ȡ��������
	private String newurl1 ;         //"http://e.chengdu.cn/html/"
	private String newurl2 ;         // "-"
	private String newurl3 ;         // "/"
	private String newurl4 ;         //"/node_2.htm"
	public GetLink(String newthemelink ,String newcontentlink,String s1,String s2 ,String s3,String s4){
		this.newThemeLink = newthemelink ;
		this.newContentLink = newcontentlink ;
		this.newurl1 = s1 ;
		this.newurl2 = s2 ;
		this.newurl3 = s3 ;
		this.newurl4 = s4 ;
	}
	//��url����������ĳһ����themeurl ����ʹĳһ���������ŵ�themeurl
	public void getLink(String themeUrl){
		int state ;
		try{
			HttpURLConnection httpUrlConnection = (HttpURLConnection) new URL(themeUrl).openConnection(); //��������
			state = httpUrlConnection.getResponseCode();
			httpUrlConnection.disconnect();
		}catch (MalformedURLException e) {
//          e.printStackTrace();
			System.out.println("���������Ѿ��޷��������ӣ��޷���ȡ����");
			return;
		} catch (IOException e) {
          // TODO Auto-generated catch block
//          e.printStackTrace();
			System.out.println("���糬�������Ѿ��޷��������ӣ��޷���ȡ����");
			return ;
      }
		if(state != 200 && state != 201){
			return;
		}
		try{
			Parser parser = new Parser(themeUrl);
			parser.setEncoding("utf-8");
			@SuppressWarnings("serial")
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
			Pattern newPage = Pattern.compile(newThemeLink); //"http://e.chengdu.cn/html/[0-9]{4}-[0-9]{2}/[0-9]{2}/node_[0-9]{1,2}.htm"
			//�������ݵ�������ʽ
			Pattern newContent = Pattern.compile(newContentLink); //"http://e.chengdu.cn/html/[0-9]{4}-[0-9]{2}/[0-9]{2}/content_[0-9]{1,6}.htm"
			//PDF������ʽ
//			Pattern newPdf = Pattern.compile("http://e.chengdu.cn/page/[0-9]{1}/[0-9]{4}-[0-9]{2}/[0-9]{2}/[0-9]{2}/[0-9]{10}_pdf.pdf");
		
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
//				Matcher pdfMatcher = newPdf.matcher(n.extractLink());
	        
				if(!linkVisit.contains(n.extractLink())){
	        			if(themeMatcher.find()){
	        				linkTheme.offer(n.extractLink());
	        				linkVisit.offer(n.extractLink());
	        			}
	        			if(contentMatcher.find()){
	        				linkContent.offer(n.extractLink());
	        				linkVisit.offer(n.extractLink());
	        			}
//	        			if(pdfMatcher.find()){
//	        				linkPdf.offer(n.extractLink());
//	        				linkVisit.offer(n.extractLink());
//	        			}
	        	
				}
				
				themeMatcher = null;
				contentMatcher = null;
//				pdfMatcher = null;
			}
		}catch(ParserException e){
			return ;
		}catch(Exception e){
			return ;
		}
	}
	
	
	@SuppressWarnings("static-access")
	public void allWeWillDo(String themeUrl,String[] bqtitle,String[] bqcontent,
    		String[] bqdate,String[] bqnewsource ,String[] bqcategroy ,String bqbuf,String encode ,String DBName ,String DBTable,
    		String photourl,String imageurl,String imagescr,String imagebuf) throws Exception{
		
		int i = 0;
		linkTheme.offer(themeUrl);
//			linkVisit.offer(n.extractLink());
			while(!linkTheme.isEmpty()){
				getLink(linkTheme.poll());
				while(!linkContent.isEmpty()){
					StringBuffer s = new StringBuffer(linkContent.poll());
					i++;
//					System.out.println(s);
					CDSB cdsb = new CDSB(s.toString() , bqtitle ,bqcontent ,bqdate ,bqnewsource ,bqcategroy ,bqbuf,encode,photourl,imageurl,imagescr,imagebuf);
					cdsb.memory(s.toString(), bqtitle ,bqcontent ,bqdate ,bqnewsource ,bqcategroy ,bqbuf,encode,DBName,DBTable,photourl,imageurl,imagescr,imagebuf);
//					s = null;
//					cdsb = null;
				}
//				System.out.println("�����ڰѻ�ȡ�����Ŵ������ݿ�...");
			}
			System.out.println("���ֵ�����������"+ i);
			
		
	
	}
	
	//��ȡһ�������
	public void result(int year,int month ,int day,String[] bqtitle,String[] bqcontent,
    		String[] bqdate,String[] bqnewsource ,String[] bqcategroy ,String bqbuf,String encode,String DBName ,String DBTable,
    		String photourl,String imageurl,String imagescr,String imagebuf) throws Exception{
		Calendar now = Calendar.getInstance();
		int year1 = now.get(Calendar.YEAR);
		if(year > year1)
			return;
		if(month > 12 && month < 1)
			return;
		if(day > 31 && day < 1)
			return;
		
		StringBuffer s1 = new StringBuffer(newurl1);  //newurl1 = "http://e.chengdu.cn/html/" newurl2 = -
		StringBuffer s2 = new StringBuffer(newurl3);          //newurl3 = "/"
		StringBuffer s3 = new StringBuffer(newurl4);    //newurl4 = "/node_2.htm"
		for(int j  = 1 ; j < 13 ;j ++){
			
			if(j < 10)
				s1 = s1.append(year).append(newurl2).append("0");  //http://e.chengdu.cn/html/2014-0
			else
				s1 = s1.append(year).append(newurl2);
			StringBuffer url = new StringBuffer();
			for(int i = 1 ; i < 32 ;i++){
//				String url;
				if(i < 10)
					url = url.append(s1).append(j).append(s2).append("0").append(i).append(s3);
				else
					url = url.append(s1).append(s2).append(i).append(s3); //url.append(s1).append(j).append(s3).append(i).append(s2);
				
//			System.out.println(url);
				allWeWillDo(url.toString(),bqtitle,bqcontent,
			    		bqdate,bqnewsource ,bqcategroy ,bqbuf,encode,DBName , DBTable,photourl,imageurl,imagescr,imagebuf);   
			//����Ѿ����ʵ�link�б���ÿ���������ȡ�洢��Ҫ�����з��ʹ������ӽ���������Լ�ڴ�
				linkVisit.clear();
				
			
			}
			url = null;
			System.gc();
		}
	}
	
	//��ȡһ�������
	public void resultForOneDay(int year,int month ,int day,String[] bqtitle,String[] bqcontent,
    		String[] bqdate,String[] bqnewsource ,String[] bqcategroy ,String bqbuf,String encode,String DBName ,String DBTable,
    		String photourl,String imageurl,String imagescr,String imagebuf){
		
		StringBuffer s1 = new StringBuffer(newurl1);  //newurl1 = "http://e.chengdu.cn/html/" newurl2 = -
		StringBuffer s2 = new StringBuffer(newurl3);          //newurl3 = "/"
		StringBuffer s3 = new StringBuffer(newurl4);    //newurl4 = "/node_2.htm"
//		for(int j  = 1 ; j < 13 ;j ++){
			
			if(month < 10)
				s1 = s1.append(year).append(newurl2).append("0");  //http://e.chengdu.cn/html/2014-0
			else
				s1 = s1.append(year).append(newurl2);
			
			for(int i = 14 ; i < 30 ;i++){
				StringBuffer url = new StringBuffer("");
				try {
//					if(i < 10)
//						url = url.append(s1).append(month).append(s2).append("0").append(i).append(s3);
//					else
						url = url.append(s1).append(month).append(s2).append(i).append(s3);
						
						System.out.println(url);
						allWeWillDo(url.toString(),bqtitle,bqcontent,
							bqdate,bqnewsource ,bqcategroy ,bqbuf,encode,DBName , DBTable,photourl,imageurl,imagescr,imagebuf);
				} catch (Exception e) {
					// TODO Auto-generated catch block
//					e.printStackTrace();
				}   
			//����Ѿ����ʵ�link�б���ÿ���������ȡ�洢��Ҫ�����з��ʹ������ӽ���������Լ�ڴ�
//				linkVisit.clear();
				
			
//			}
			System.gc();
			System.out.println(i);
			}
//		}
//		}
	}
	
	public void hxdsb(String[] bqtitle,String[] bqcontent,
    		String[] bqdate,String[] bqnewsource ,String[] bqcategroy ,String bqbuf,String encode ,String newsource ,String newtable){
		
		StringBuffer s1 = new StringBuffer("http://www.wccdaily.com.cn/shtml/hxdsb/20141024/va");
		StringBuffer s2 = new StringBuffer(".shtml");
		
		for(int i = 1 ; i < 37 ; i++){
			StringBuffer theme = new StringBuffer();
			if(i < 10)
				theme = theme.append(s1).append(0).append(i).append(s2);
			else
				theme = theme.append(s1).append(i).append(s2);
			System.out.println(theme);
			getLink(theme.toString());
			while(!linkContent.isEmpty()){
				StringBuffer s = new StringBuffer(linkContent.poll());
//				i++;
				System.out.println(s);
//				CDSB cdsb = new CDSB(s.toString(),bqtitle ,bqcontent ,bqdate ,bqnewsource ,bqcategroy ,bqbuf,encode);
//				cdsb.memory(s.toString(),bqtitle ,bqcontent ,bqdate ,bqnewsource ,bqcategroy ,bqbuf,encode ,newsource,newtable);
//				s = null;
//				cdsb = null;
			}
//			theme = null;
		}
		
	}
	
	public static void main(String args[]) throws Exception{
		long start = System.currentTimeMillis();
//		String url ="http://www.wccdaily.com.cn/shtml/hxdsb/20141029/va01.shtml" ;
//		String url1 = "http://www.chinamil.com.cn/jfjbmap/content/2014-10/27/node_2.htm";
//		String test1 = "http://zqb.cyol.com/html/2014-10/29/nbs.D110000zgqnb_01.htm";
//		GetLink test = new GetLink();
//		test.allWeWillDo(url1);
//		test.hxdsb();
//		test.getLink(url);
//		test.result(0, 0, 0);
//		String url = "http://e.chengdu.cn/html/2014-10/16/node_2.htm";
//		System.out.println(" ������Ŭ����������...");
//		test.allWeWillDo(url);
//		System.out.println("����ִ�����...");
		String theme = "http://www.chinamil.com.cn/jfjbmap/content/[0-9]{4}-[0-9]{2}/[0-9]{2}/node_[0-9]{1,2}.htm";
		String content ="http://www.chinamil.com.cn/jfjbmap/content/[0-9]{4}-[0-9]{2}/[0-9]{2}/content_[0-9]{5,6}.htm";
		String s1 = "http://www.chinamil.com.cn/jfjbmap/content/";
		String s2 = "-";
		String s3 = "/";
		String s4 = "/node_2.htm";
		String[] bqtitle = {"style","line-height:140%;"};
		String[] bqcontent = {"id","ozoom"};
		String[] bqdate = {"height","25"};
		String[] bqnewsource = {"��ž���","....."};
		String[] bqcategroy ={"class","info"};
		String bqbuf ="";
		String encode = "utf-8";
		String DBName = "jfjb1";
		String DBTable = "cg";
		String photourl = "http://www.chinamil.com.cn/jfjbmap/";
		String imageurl = "IMG src=\"(.*?)res(.*?)attpic_brief.jpg\"";     //"img src=\"(.*?)res(.*?)attpic_brief.jpg\""
		String imagescr = "http:\"?(.*?)(\"|>|\\s+)";     //"http:\"?(.*?)(\"|>|\\s+)"
		String imagebuf = "../../../";
		String thEMEuRL = "http://bjwb.bjd.com.cn/html/2014-11/11/node_82.htm";
		GetLink test = new GetLink(theme,content,s1,s2,s3,s4);
		test.getLink(thEMEuRL);
		//��ȡ����ĳһ������������  url�̶�
//		test.allWeWillDo(url1,bqtitle,bqcontent,
//	    		bqdate,bqnewsource ,bqcategroy ,bqbuf,encode,DBName , DBTable,photourl,imageurl,imagescr,imagebuf);
		//��ȡ�ƶ�ĳ�����������
//		test.resultForOneDay(2014, 11, 8, bqtitle, bqcontent, bqdate, bqnewsource, bqcategroy, bqbuf, encode, DBName, DBTable,photourl,imageurl,imagescr,imagebuf);
		
		long end = System.currentTimeMillis();
		System.out.println(end-start);
	}
	
}
