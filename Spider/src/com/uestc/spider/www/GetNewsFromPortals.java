package com.uestc.spider.www;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
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
 * ��ÿһ���Ż���վ��һ�����е���
 * ÿ���������ÿ�����������в�ͬ�ķ�����ȡ������
 * 
 * ��ȡÿ�����ŵ����ۣ��£�
 * ��������links������linksҲ����ϵ�ģ����۲���Ҫȫ��ץ������
 *
 * */
class NETEASENews implements FindLinks{
	
	private String ENCODE ;
	
	public NETEASENews(String encode){
		this.ENCODE = encode ;
	}
	
	//�����ȡ��������links
	public Queue<String> newsThemeLinks = new LinkedList<String>() ;
	
	//�����ȡ��������links
	public Queue<String> newsContentLinks = new LinkedList<String>() ;
	
	//�����Ѿ����ʵ�����links ���������ظ�
	public Queue<String> linksVisited = new LinkedList<String>() ;
	
	//��������links��������ʽ
	String newsThemeLinksReg ; //= "http://news.163.com/special/0001124J/guoneinews_[0-9]{1,2}.html#headList";
			
	//��������links��������ʽ
	String newsContentLinksReg ; //= "http://news.163.com/[0-9]{2}/[0-9]{4}/[0-9]{2}/(.*?).html#f=dlist";
	
	//��������link
	private String theme ;
	
//	public NETEASENews(String theme){
//		
//		this.theme = theme ;
//	}
	//��ȡ��������
	public void getGuoNeiNews(){
		
		//�������� ��ҳ����
		theme = "http://news.163.com/domestic/";
		
		int state ;
		try{
			HttpURLConnection httpUrlConnection = (HttpURLConnection) new URL(theme).openConnection(); //��������
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
		
		//��������links��������ʽ
		newsThemeLinksReg = "http://news.163.com/special/0001124J/guoneinews_[0-9]{1,2}.html#headList";
		
		//��������links��������ʽ
		newsContentLinksReg = "http://news.163.com/[0-9]{2}/[0-9]{4}/[0-9]{2}/(.*?).html#f=dlist";
		
		//���������������links
		Queue<String> guoNeiTheme = new LinkedList<String>();
		guoNeiTheme = findThemeLinks(theme,newsThemeLinksReg);
		System.out.println(guoNeiTheme);
		
		//��ȡ������������links
		Queue<String>guoNeiNews = new LinkedList<String>();
		guoNeiNews = findContentLinks(guoNeiTheme,newsContentLinksReg);
		

	}
	@Override
	public Queue<String> findThemeLinks(String themeLink ,String themeLinkReg) {
		
		// TODO Auto-generated method stub
		Queue<String> themelinks = new LinkedList<String>();
		Pattern newsThemeLink = Pattern.compile(themeLinkReg);
		themelinks.offer(themeLink);
		
		try {
				Parser parser = new Parser(themeLink);
				parser.setEncoding(ENCODE);
				NodeList nodeList = parser.extractAllNodesThatMatch(new NodeFilter(){
					public boolean accept(Node node)
					{
						if (node instanceof LinkTag)// ���
							return true;
						return false;
					}
			
				});
				
				for (int i = 0; i < nodeList.size(); i++)
				{
				
					LinkTag n = (LinkTag) nodeList.elementAt(i);
//		        	System.out.print(n.getStringText() + "==>> ");
//		       	 	System.out.println(n.extractLink());
					//��������
					Matcher themeMatcher = newsThemeLink.matcher(n.extractLink());
					if(themeMatcher.find()){
						
		        		themelinks.offer(n.extractLink());
		        	}
		        			
		        	

				}
			}catch(ParserException e){
				return null;
			}catch(Exception e){
				return null;
			}
		return themelinks ;
	}

	public Queue<String> findContentLinks(Queue<String> themeLink ,String contentLinkReg) {
		// TODO Auto-generated method stub
		Queue<String> contentlinks = new LinkedList<String>(); // ��ʱ����
		
		Pattern newsContent = Pattern.compile(contentLinkReg);
		while(!themeLink.isEmpty()){
			String buf = themeLink.poll();
			try {
				Parser parser = new Parser(buf);
				parser.setEncoding(ENCODE);
				NodeList nodeList = parser.extractAllNodesThatMatch(new NodeFilter(){
					public boolean accept(Node node)
					{
						if (node instanceof LinkTag)// ���
							return true;
						return false;
					}
		
				});
			
				for (int i = 0; i < nodeList.size(); i++)
				{
			
					LinkTag n = (LinkTag) nodeList.elementAt(i);
//	        	System.out.print(n.getStringText() + "==>> ");
//	       	 	System.out.println(n.extractLink());
					//��������
					Matcher themeMatcher = newsContent.matcher(n.extractLink());
					if(themeMatcher.find()){
					
						if(!contentlinks.contains(n.extractLink()))
							contentlinks.offer(n.extractLink());
					}
					
				}
			}catch(ParserException e){
				return null;
			}catch(Exception e){
				return null;
			}		
		}
		System.out.println(contentlinks);
		return contentlinks;
	}
	@Override
	public String findNewsTitle(String html) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String findNewsOriginalTite(String html) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String findNewsContent(String html) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String findNewsImages(String html) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String findNewsTime(String html) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String findNewsSource() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String findNewsOriginalSource() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String findNewsCategroy(String html) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String findNewsOriginalCategroy(String html) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String findNewsComment(String url) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void handle() {
		// TODO Auto-generated method stub
		
	}
	
	
}

class SINANews implements FindLinks{

	@Override
	public Queue<String> findThemeLinks(String themeLink , String themeLinkReg) {
		// TODO Auto-generated method stub
		return null;
	}

	public Queue<String> findContentLinks(Queue<String> themeLink,String contentLinkReg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String findNewsTitle(String html) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String findNewsOriginalTite(String html) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String findNewsContent(String html) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String findNewsImages(String html) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String findNewsTime(String html) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String findNewsSource() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String findNewsOriginalSource() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String findNewsCategroy(String html) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String findNewsOriginalCategroy(String html) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String findNewsComment(String url) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void handle() {
		// TODO Auto-generated method stub
		
	}
	
}

public class GetNewsFromPortals {
	

	public static void main(String[] args){
		NETEASENews test = new NETEASENews("utf-8");
		test.getGuoNeiNews();
	}
	
}


