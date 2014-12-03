package com.uestc.NETEASE.www;

import java.util.Queue;

public interface NETEASECOMMENT {

	public Queue<String> findThemeLinks(String themeLink ,String themeLinkReg) ; //获取主题链接
	
	public Queue<String> findContentLinks(Queue<String> themeLink ,String ContentLinkReg) ;  //获取内容链接
	
	public String findContentHtml(String url);    //获取新闻内容页的html
	
	public String HandleHtml(String html,String one);  //处理一个参数的标签的html
	
	public String HandleHtml(String html ,String one,String two);  //处理两个参数的标签
	
	public String findNewsComment(String url ,String html ,String[] label);
	
	public String handleComment(String commentUrl);
}
