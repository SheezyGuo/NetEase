package com.uestc.spider.www;

import java.util.LinkedList;
import java.util.Queue;

/*
 * ��ÿһ���Ż���վ��һ�����е���
 * ÿ���������ÿ�����������в�ͬ�ķ�����ȡ������
 * 
 * ��ȡÿ�����ŵ����ۣ��£�
 * ��������links������linksҲ����ϵ�ģ����۲���Ҫȫ��ץ������
 *
 * */
class NETEASENews implements FindLinks{
	
	//�����ȡ��������links
	public Queue<String> newsThemeLinks = new LinkedList<String>() ;
	
	//�����ȡ��������links
	public Queue<String> newsContentLinks = new LinkedList<String>() ;
	
	
	//�����Ѿ����ʵ�����links ���������ظ�
	public Queue<String> linksVisited = new LinkedList<String>() ;
	
	//��������link
	private String theme ;
	
	public NETEASENews(String theme){
		
		this.theme = theme ;
	}
	//��ȡ��������
	public void getGuoNeiNews(){
		
		//��������links��������ʽ
		String newsThemeLinksReg = "http://news.163.com/special/0001124J/guoneinews_[0-9]{1,2}.html#headList";
		
		//��������links��������ʽ
		String newsContentLinksReg = "http://news.163.com/[0-9]{2}/[0-9]{4}/[0-9]{2}/(.*?).html#f=dlist";
		
		//���������������links
		Queue<String> guoNeiTheme = new LinkedList<String>();
		guoNeiTheme.offer("http://news.163.com/domestic/");
		for(int i = 2 ; i < 11 ; i++){
			if(i < 10)
				guoNeiTheme.offer("http://news.163.com/special/0001124J/guoneinews_0"+i+".html#headList");
			else
				guoNeiTheme.offer("http://news.163.com/special/0001124J/guoneinews_"+i+".html#headList");
			
		}
//		System.out.println(guoNeiTheme);

	}
	@Override
	public Queue<String> findThemeLinks(String themeLink) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Queue<String> findContentLinks(String themeLink) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String findNewTitle(String html) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String findNewOriginalTite(String html) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String findNewContent(String html) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String findNewImages(String html) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String findNewTime(String html) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String findNewSource() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String findNewOriginalSource() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String findNewCategroy(String html) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String findNewOriginalCategroy(String html) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String findNewComment(String url) {
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
	public Queue<String> findThemeLinks(String themeLink) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Queue<String> findContentLinks(String themeLink) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String findNewTitle(String html) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String findNewOriginalTite(String html) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String findNewContent(String html) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String findNewImages(String html) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String findNewTime(String html) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String findNewSource() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String findNewOriginalSource() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String findNewCategroy(String html) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String findNewOriginalCategroy(String html) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String findNewComment(String url) {
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
		NETEASENews test = new NETEASENews("");
		test.getGuoNeiNews();
	}
	
}


