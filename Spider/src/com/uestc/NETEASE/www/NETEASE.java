package com.uestc.NETEASE.www;

import java.util.Queue;


public interface NETEASE {

	public Queue<String> findThemeLinks(String themeLink ,String themeLinkReg) ; //获取主题链接
	
	public Queue<String> findContentLinks(Queue<String> themeLink ,String ContentLinkReg) ;  //获取内容链接
	
	public String findContentHtml(String url);    //获取新闻内容页的html
	
	public String HandleHtml(String html,String one);  //处理一个参数的标签的html
	
	public String HandleHtml(String html ,String one,String two);  //处理两个参数的标签
	
	public String findNewsTitle(String html ,String[] label,String buf) ; //获取新闻标题
	
	public String findNewsOriginalTitle(String html , String[] label,String buf) ; //获取新闻原始标题
	
	public String findNewsContent(String html , String[] label) ;    //获取新闻内容
	
	public String findNewsImages(String html , String[] label);     //获取新闻图片
	 
	public String findNewsTime(String html , String[] label) ;        //获取新闻发布时间
	
	public String findNewsSource(String html ,String[] label) ;           //新闻来源
	
	public String findNewsOriginalSource(String html ,String[] label) ;     //新闻具体来源
	 
	public String findNewsCategroy(String html , String[] label) ;  //新闻版面属性
	
	public String findNewsOriginalCategroy(String html , String[] label); //新闻具体版面属性
	
}
