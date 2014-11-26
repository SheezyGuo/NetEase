package com.uestc.spider.www;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class NETEASE implements FindLinks{

private String ENCODE ;
	
	String DBName  ;           			//数据库名称
	String DBTable  ;       			//表名
	private String[] newsTitleLabel;     //新闻标题标签 t
	private String[] newsContentLabel ;  //新闻内容标签 
	private String[] newsTimeLabel ;   //新闻时间
	private String[] newsSourceLabel ; //（3个参数）新闻来源 同新闻时间
	private String[] newsCategroyLabel ; // "国内" "网易新闻-国内新闻-http://news.163.com/domestic/"
//	private String[] news
	public String DBTableTest;
	public NETEASE(String encode, String[] newsTitle ,String[] newsContent ,String[] newsTime,String[] newsSource,String[] newsCategroy ){
		this.ENCODE = encode ;
		this.newsTitleLabel = newsTitle ;
		this.newsContentLabel = newsContent ;
		this.newsTimeLabel = newsTime ;
		this.newsSourceLabel = newsSource ;
		this.newsCategroyLabel = newsCategroy ;
		
		
		
	}
	public NETEASE(String dbtable){
		this.DBTableTest = dbtable;
	}
	public NETEASE(){
		System.out.println("程序正在启动...");
		System.out.println("准备获取网易新闻...");
	}
	//保存获取到的主题links
	public Queue<String> newsThemeLinks = new LinkedList<String>() ;
	
	//保存获取到的新闻links
	public Queue<String> newsContentLinks = new LinkedList<String>() ;
	
	//保存已经访问的新闻links 以免新闻重复
	public Queue<String> linksVisited = new LinkedList<String>() ;
	
	//新闻主题links的正则表达式
	public String newsThemeLinksReg ; //= "http://news.163.com/special/0001124J/guoneinews_[0-9]{1,2}.html#headList";
			
	//新闻内容links的正则表达式
	public String newsContentLinksReg ; //= "http://news.163.com/[0-9]{2}/[0-9]{4}/[0-9]{2}/(.*?).html#f=dlist";
	
	//新闻主题link
	public String theme ;
	
	//评论正则
	public String commentReg ;  //= "http://comment.news.163.com/news3_bbs/";
	
//	public NETEASENews(String theme){
//		
//		this.theme = theme ;
//	}
	//获取国内新闻
	public void getGuoNeiNews(){
		/*初始化各个标签等
		 * 
		 * */
		ENCODE = "GB2312";
		DBName = "NET";   //数据库名称
		DBTable = "guonei";   //表名
		newsTitleLabel = new String[]{"title",""};     //新闻标题标签 title or id=h1title
		newsContentLabel = new String[]{"id" ,"endText"};  //新闻内容标签 "id","endText"
		newsTimeLabel = new String[]{"class","ep-time-soure cDGray"};   //新闻时间"class","ep-time-soure cDGray"  
		newsSourceLabel =new String[]{"class","ep-time-soure cDGray","网易新闻-国内新闻"}; //（3个参数）新闻来源 同新闻时间"class","ep-time-soure cDGray" 再加上一个"网易新闻-国内新闻"
		newsCategroyLabel = new String[]{"class","ep-crumb JS_NTES_LOG_FE"} ; // "国内" "网易新闻-国内新闻-http://news.163.com/domestic/"
		commentReg = "http://comment.news.163.com/news3_bbs/";
		
		CRUT crut = new CRUT(DBName ,DBTable);
		//国内新闻 首页链接
		theme = "http://news.163.com/domestic/";
		
		//新闻主题links的正则表达式
		newsThemeLinksReg = "http://news.163.com/special/0001124J/guoneinews_[0-9]{1,2}.html#headList";
		
		//新闻内容links的正则表达式 (http://view.163.com/14/1119/10/ABDHAKC500012Q9L.html#f=dlist)
		newsContentLinksReg = "http://news.163.com/[0-9]{2}/[0-9]{4}/[0-9]{2}/(.*?).html#f=dlist";
		
		int state ;
		try{
			HttpURLConnection httpUrlConnection = (HttpURLConnection) new URL(theme).openConnection(); //创建连接
			state = httpUrlConnection.getResponseCode();
			httpUrlConnection.disconnect();
		}catch (MalformedURLException e) {
//          e.printStackTrace();
			System.out.println("网络慢，已经无法正常链接，无法获取新闻");
			return;
		} catch (IOException e) {
          // TODO Auto-generated catch block
//          e.printStackTrace();
			System.out.println("网络超级慢，已经无法正常链接，无法获取新闻");
			return ;
      }
		if(state != 200 && state != 201){
			return;
		}
		//保存国内新闻主题links
		Queue<String> guoNeiNewsTheme = new LinkedList<String>();
		guoNeiNewsTheme = findThemeLinks(theme,newsThemeLinksReg);
//		System.out.println(guoNeiNewsTheme);
		
		//获取国内新闻内容links
		Queue<String>guoNeiNewsContent = new LinkedList<String>();
		guoNeiNewsContent = findContentLinks(guoNeiNewsTheme,newsContentLinksReg);
//		System.out.println(guoNeiNewsContent);
		//获取每个新闻网页的html
		int i = 0;
		while(!guoNeiNewsContent.isEmpty()){
			String url = guoNeiNewsContent.poll();
			String html = findContentHtml(url);  //获取新闻的html
			System.out.println(url);
//			System.out.println(html);
			i++;
//			System.out.println(findNewsComment(url));
//			System.out.println("\n");
			crut.add(findNewsTitle(html,newsTitleLabel,"_网易新闻中心"), findNewsOriginalTitle(html,newsTitleLabel,"_网易新闻中心"),findNewsOriginalTitle(html,newsTitleLabel,"_网易新闻中心"), findNewsTime(html,newsTimeLabel),findNewsContent(html,newsContentLabel),findNewsComment(url,html,newsCategroyLabel), findNewsSource(html,newsSourceLabel),
					findNewsOriginalSource(html,newsSourceLabel), findNewsCategroy(html,newsCategroyLabel), findNewsOriginalCategroy(html,newsCategroyLabel), url, "");
		}
		System.out.println(i);
		
		

	}
	
	//获取社会新闻
	
	public void getSheHuiNews(){
		
		/*初始化各个标签等
		 * 
		 * */
		ENCODE = "GB2312";
		DBName = "NET";   //数据库名称
		DBTable = "shehui";   //表名
		newsTitleLabel = new String[]{"title",""};     //新闻标题标签 title or id=h1title
		newsContentLabel = new String[]{"id" ,"endText"};  //新闻内容标签 "id","endText"
		newsTimeLabel = new String[]{"class","ep-time-soure cDGray"};   //新闻时间"class","ep-time-soure cDGray"  
		newsSourceLabel =new String[]{"class","ep-time-soure cDGray","网易新闻-社会新闻"}; //（3个参数）新闻来源 同新闻时间"class","ep-time-soure cDGray" 再加上一个"网易新闻-国内新闻"
		newsCategroyLabel = new String[]{"class","ep-crumb JS_NTES_LOG_FE"} ; // "国内" "网易新闻-国内新闻-http://news.163.com/domestic/"
//		commentReg = "http://comment.news.163.com/news_shehui7_bbs/";
		
		CRUT crut = new CRUT(DBName ,DBTable);
		//国内新闻 首页链接
		theme = "http://news.163.com/shehui/";
		
		//新闻主题links的正则表达式
		newsThemeLinksReg = "http://news.163.com/special/00011229/shehuinews_[0-9]{1,2}.html#headList";
		
		//新闻内容links的正则表达式http://focus.news.163.com/  
		newsContentLinksReg = "http://news.163.com/[0-9]{2}/[0-9]{4}/[0-9]{2}/(.*?).html#f=s((list)|(focus))";
		
		int state ;
		try{
			HttpURLConnection httpUrlConnection = (HttpURLConnection) new URL(theme).openConnection(); //创建连接
			state = httpUrlConnection.getResponseCode();
			httpUrlConnection.disconnect();
		}catch (MalformedURLException e) {
//          e.printStackTrace();
			System.out.println("网络慢，已经无法正常链接，无法获取新闻");
			return;
		} catch (IOException e) {
          // TODO Auto-generated catch block
//          e.printStackTrace();
			System.out.println("网络超级慢，已经无法正常链接，无法获取新闻");
			return ;
      }
		if(state != 200 && state != 201){
			return;
		}
		//保存社会新闻主题links
		Queue<String> sheHuiNewsTheme = new LinkedList<String>();
		sheHuiNewsTheme = findThemeLinks(theme,newsThemeLinksReg);
//		System.out.println(sheHuiNewsTheme);
		
		//获取社会新闻内容links
		Queue<String>sheHuiNewsContent = new LinkedList<String>();
		sheHuiNewsContent = findContentLinks(sheHuiNewsTheme,newsContentLinksReg);
//		System.out.println(sheHuiNewsContent);
		//获取每个新闻网页的html
		int i = 0;
		while(!sheHuiNewsContent.isEmpty()){
			String url = sheHuiNewsContent.poll();
			String html = findContentHtml(url);  //获取新闻的html
			System.out.println(url);
//			System.out.println(findNewsTitle(html,new String[]{"title",""},"_网易新闻中心"));
//			System.out.println(findNewsContent(html,new String[]{"id" ,"endText"}));
//			System.out.println(findNewsTime(html,new String[]{"class","ep-time-soure cDGray"}));
//			System.out.println(findNewsCategroy(html,new String[]{"class","ep-crumb JS_NTES_LOG_FE"}));
//			System.out.println(findNewsComment(url,html,newsCategroyLabel));
			i++;
			
//			System.out.println(findNewsComment(url,html,newsCategroyLabel));
//			System.out.println("\n");
			crut.add(findNewsTitle(html,newsTitleLabel,"_网易新闻中心"), findNewsOriginalTitle(html,newsTitleLabel,"_网易新闻中心"),findNewsOriginalTitle(html,newsTitleLabel,"_网易新闻中心"), findNewsTime(html,newsTimeLabel),findNewsContent(html,newsContentLabel),findNewsComment(url,html,newsCategroyLabel), findNewsSource(html,newsSourceLabel),
					findNewsOriginalSource(html,newsSourceLabel), findNewsCategroy(html,newsCategroyLabel), findNewsOriginalCategroy(html,newsCategroyLabel), url, "");
		}
		System.out.println(i);
		
		

	}
	
	//国际新闻
	public void getGuoJiNews(){
		
		/*初始化各个标签等 编码 gb2312
		 * 
		 * */
		ENCODE = "GB2312";
		DBName = "NET";   //数据库名称
		DBTable = "guoji";   //表名
		newsTitleLabel = new String[]{"title",""};     //新闻标题标签 title or id=h1title
		newsContentLabel = new String[]{"id" ,"endText"};  //新闻内容标签 "id","endText"
		newsTimeLabel = new String[]{"class","ep-time-soure cDGray"};   //新闻时间"class","ep-time-soure cDGray"  
		newsSourceLabel =new String[]{"class","ep-time-soure cDGray","网易新闻-国际新闻"}; //（3个参数）新闻来源 同新闻时间"class","ep-time-soure cDGray" 再加上一个"网易新闻-国内新闻"
		newsCategroyLabel = new String[]{"class","ep-crumb JS_NTES_LOG_FE"} ; // "国内" "网易新闻-国内新闻-http://news.163.com/domestic/"
		CRUT crut = new CRUT(DBName,DBTable);
		//从rss获取
		theme = "http://news.163.com/special/00011K6L/rss_gj.xml";
		
		//新闻内容的正则表达式（国际板块比较特殊）
		newsContentLinksReg = "http://news.163.com/[0-9]{2}/[0-9]{4}/[0-9]{2}/(.*?).html";
		
		String guoJiHtml = findContentHtml(theme);
		//保存已经访问的links
		Queue<String> visitedLinks = new LinkedList<String>();
		//匹配获得内容的links
		Pattern newPage = Pattern.compile(newsContentLinksReg);
        
        Matcher themeMatcher = newPage.matcher(guoJiHtml);
        int i = 0;
        while(themeMatcher.find()){
        	i++;
        	String url = themeMatcher.group();
        	if(!visitedLinks.contains(url)){
        		String html = findContentHtml(url);
        		System.out.println(url);
//        		System.out.println(findNewsTitle(html,newsTitleLabel,"_网易新闻中心"));
//        		System.out.println(findNewsContent(html,newsContentLabel));
        		crut.add(findNewsTitle(html,newsTitleLabel,"_网易新闻中心"), findNewsOriginalTitle(html,newsTitleLabel,"_网易新闻中心"),findNewsOriginalTitle(html,newsTitleLabel,"_网易新闻中心"), findNewsTime(html,newsTimeLabel),findNewsContent(html,newsContentLabel),findNewsComment(url,html,newsCategroyLabel), findNewsSource(html,newsSourceLabel),
        				findNewsOriginalSource(html,newsSourceLabel), findNewsCategroy(html,newsCategroyLabel), findNewsOriginalCategroy(html,newsCategroyLabel), url, "");
        		visitedLinks.add(url);
        	}
        	
        }
        System.out.println(i);
		
		
	}
	
	//网易军事 var
	public void getWarNews(){
		
		/*初始化各个标签等 编码 gb2312
		 * 
		 * */
		ENCODE = "GB2312";
		DBName = "NET";   //数据库名称
		DBTable = "war";   //表名
		newsTitleLabel = new String[]{"title",""};     //新闻标题标签 title or id=h1title
		newsContentLabel = new String[]{"id" ,"endText"};  //新闻内容标签 "id","endText"
		newsTimeLabel = new String[]{"class","ep-time-soure cDGray"};   //新闻时间"class","ep-time-soure cDGray"  
		newsSourceLabel =new String[]{"class","ep-time-soure cDGray","网易新闻-军事新闻"}; //（3个参数）新闻来源 同新闻时间"class","ep-time-soure cDGray" 再加上一个"网易新闻-国内新闻"
		newsCategroyLabel = new String[]{"class","ep-crumb JS_NTES_LOG_FE"} ; // "国内" "网易新闻-国内新闻-http://news.163.com/domestic/"
		CRUT crut = new CRUT(DBName,DBTable);
		
		/*这个模块分四部分抓取
		 * 1、主页"http://war.163.com/index.html"
		 * 2.详细分类一共10叶：http://war.163.com/special/millatestnews/ http://war.163.com/special/millatestnews_06/
		 * 3.军事历史：http://war.163.com/special/historyread/
		 * */
		//主题
		Queue<String> warNewsThemeLinks = new LinkedList<String>();
		//内容
		Queue<String> warNewsContentLinks = new LinkedList<String>();
		
		//处理主页
		theme = "http://war.163.com/index.html";
		//新闻内容的正则表达式http://war.163.com/14/1124/09/ABQAT7EM00011MTO.html
		newsContentLinksReg = "http://war.163.com/[0-9]{2}/[0-9]{4}/[0-9]{2}/(.*?).html";
		warNewsThemeLinks.offer(theme);
		warNewsContentLinks = findContentLinks(warNewsThemeLinks,newsContentLinksReg);
//		
		while(!warNewsContentLinks.isEmpty()){
			String url = warNewsContentLinks.poll();
			String html = findContentHtml(url);  //获取新闻的html
//			System.out.println(url);
			crut.add(findNewsTitle(html,newsTitleLabel,"_网易新闻中心"), findNewsOriginalTitle(html,newsTitleLabel,"_网易新闻中心"),findNewsOriginalTitle(html,newsTitleLabel,"_网易新闻中心"), findNewsTime(html,newsTimeLabel),findNewsContent(html,newsContentLabel),findNewsComment(url,html,newsCategroyLabel), findNewsSource(html,newsSourceLabel),
					findNewsOriginalSource(html,newsSourceLabel), findNewsCategroy(html,newsCategroyLabel), findNewsOriginalCategroy(html,newsCategroyLabel), url, "");
		}
		
		//详细分类模块
		theme  = "http://war.163.com/special/millatestnews/";
		newsThemeLinksReg = "http://war.163.com/special/millatestnews(_[0-9]{2})*/";  //主题正则表达式
		newsContentLinksReg = "http://war.163.com/[0-9]{2}/[0-9]{4}/[0-9]{2}/(.*?).html"; //内容正则表达式
		warNewsThemeLinks = findThemeLinks(theme,newsThemeLinksReg);
		warNewsContentLinks = findContentLinks(warNewsThemeLinks,newsContentLinksReg);
//		int k = 1;
		while(!warNewsContentLinks.isEmpty()){
			String url = warNewsContentLinks.poll();
			String html = findContentHtml(url);  //获取新闻的html
//			System.out.println(url);
//			System.out.println(findNewsTitle(html,new String[]{"title",""},"_网易军事"));
//			System.out.println(findNewsContent(html,new String[]{"id" ,"endText"}));
//			System.out.println(findNewsTime(html,new String[]{"class","ep-time-soure cDGray"}));
//			System.out.println(findNewsCategroy(html,new String[]{"class","ep-crumb JS_NTES_LOG_FE"}));
//			System.out.println(findNewsComment(url,html,newsCategroyLabel));
//			k++;
			
//			System.out.println(findNewsComment(url,html,newsCategroyLabel));
//			System.out.println("\n");
			crut.add(findNewsTitle(html,newsTitleLabel,"_网易军事"), findNewsOriginalTitle(html,newsTitleLabel,"_网易军事"),findNewsOriginalTitle(html,newsTitleLabel,"_网易军事"), findNewsTime(html,newsTimeLabel),findNewsContent(html,newsContentLabel),findNewsComment(url,html,newsCategroyLabel), findNewsSource(html,newsSourceLabel),
					findNewsOriginalSource(html,newsSourceLabel), findNewsCategroy(html,newsCategroyLabel), findNewsOriginalCategroy(html,newsCategroyLabel), url, "");
		}
//		System.out.println(k);
		
		//军事历史
		theme = "http://war.163.com/special/historyread/";
		newsThemeLinksReg = "http://war.163.com/special/historyread(_[0-9]{2})*/";  //主题正则表达式
		newsContentLinksReg = "http://war.163.com/[0-9]{2}/[0-9]{4}/[0-9]{2}/(.*?).html"; //内容正则表达式
		warNewsThemeLinks = findThemeLinks(theme,newsThemeLinksReg);
		warNewsContentLinks = findContentLinks(warNewsThemeLinks,newsContentLinksReg);
//		int j = 1 ;
		while(!warNewsContentLinks.isEmpty()){
			String url = warNewsContentLinks.poll();
			String html = findContentHtml(url);  //获取新闻的html
//			System.out.println(url);
//			System.out.println(findNewsTitle(html,new String[]{"title",""},"_网易军事"));
//			System.out.println(findNewsContent(html,new String[]{"id" ,"endText"}));
//			System.out.println(findNewsTime(html,new String[]{"class","ep-time-soure cDGray"}));
//			System.out.println(findNewsCategroy(html,new String[]{"class","ep-crumb JS_NTES_LOG_FE"}));
//			System.out.println(findNewsComment(url,html,newsCategroyLabel));
//			j++;
			
//			System.out.println(findNewsComment(url,html,newsCategroyLabel));
//			System.out.println("\n");
			crut.add(findNewsTitle(html,newsTitleLabel,"_网易军事"), findNewsOriginalTitle(html,newsTitleLabel,"_网易军事"),findNewsOriginalTitle(html,newsTitleLabel,"_网易军事"), findNewsTime(html,newsTimeLabel),findNewsContent(html,newsContentLabel),findNewsComment(url,html,newsCategroyLabel), findNewsSource(html,newsSourceLabel),
					findNewsOriginalSource(html,newsSourceLabel), findNewsCategroy(html,newsCategroyLabel), findNewsOriginalCategroy(html,newsCategroyLabel), url, "");
		}
//		System.out.println(j);
	}
	//深度新闻
	public void getFocusNews(){
		/*初始化各个标签等 编码 gb2312
		 * 
		 * */
		ENCODE = "GB2312";
		DBName = "NET";   //数据库名称
		DBTable = "focus";   //表名
		newsTitleLabel = new String[]{"title",""};     //新闻标题标签 title or id=h1title
		newsContentLabel = new String[]{"id" ,"endText"};  //新闻内容标签 "id","endText"
		newsTimeLabel = new String[]{"style","float:left;"};   //新闻时间"class","info"  
		newsSourceLabel =new String[]{"class","path","网易新闻-深度报道"}; //（3个参数）新闻来源 同新闻时间"class","ep-time-soure cDGray" 再加上一个"网易新闻-国内新闻"
		newsCategroyLabel = new String[]{"class","path"} ; // "国内" "网易新闻-国内新闻-http://news.163.com/domestic/"
		CRUT crut = new CRUT(DBName,DBTable);
		
		theme = "http://focus.news.163.com/";
		
		//内容正则表达式http://focus.news.163.com/13/0325/18/8QR65LJS00011SM9.html
		newsContentLinksReg = "http://focus.news.163.com/[0-9]{2}/[0-9]{4}/[0-9]{2}/(.*?).html"; //内容正则表达式
		
		String focusHtml = findContentHtml(theme);
		Queue<String> visitedLinks = new LinkedList<String>();
		//匹配获得内容的links
		Pattern newPage = Pattern.compile(newsContentLinksReg);
        
        Matcher themeMatcher = newPage.matcher(focusHtml);
//        int i = 0;
        while(themeMatcher.find()){
//        	i++;
        	String url = themeMatcher.group();
        	if(!visitedLinks.contains(url)){
        		String html = findContentHtml(url);
//        		System.out.println(url);
//        		System.out.println(findNewsTitle(html,newsTitleLabel,"_网易新闻中心"));
//        		System.out.println(findNewsTime(html,newsTimeLabel));
        		crut.add(findNewsTitle(html,newsTitleLabel,"_网易新闻中心"), findNewsOriginalTitle(html,newsTitleLabel,"_网易新闻中心"),findNewsOriginalTitle(html,newsTitleLabel,"_网易新闻中心"), findNewsTime(html,newsTimeLabel),findNewsContent(html,newsContentLabel),findNewsComment(url,html,newsCategroyLabel), findNewsSource(html,newsSourceLabel),
        				findNewsOriginalSource(html,newsSourceLabel), findNewsCategroy(html,newsCategroyLabel), findNewsOriginalCategroy(html,newsCategroyLabel), url, "");
        		visitedLinks.add(url);
        	}
        	
        }
//        System.out.println(i);
	}
	
	//评论新闻
	public void getViewNews(){
		/*初始化各个标签等 编码 gb2312
		 * 
		 * */
		ENCODE = "GB2312";
		DBName = "NET";   //数据库名称
		DBTable = "view";   //表名
		newsTitleLabel = new String[]{"title",""};     //新闻标题标签 title or id=h1title
		newsContentLabel = new String[]{"class" ,"feed-text"};  //新闻内容标签 class="feed-text"
		newsTimeLabel = new String[]{"style","float:left;"};   //新闻时间"class","info"  
		newsSourceLabel =new String[]{"class","path","网易新闻-深度报道"}; //（3个参数）新闻来源 同新闻时间"class","ep-time-soure cDGray" 再加上一个"网易新闻-国内新闻"
		newsCategroyLabel = new String[]{"class","path"} ; // "国内" "网易新闻-国内新闻-http://news.163.com/domestic/"
		CRUT crut = new CRUT(DBName,DBTable);
		
		theme = "http://view.163.com/";
		
		//内容正则表达式http://view.163.com/14/1125/11/ABT5R5GF00012Q9L.html
		newsContentLinksReg = "http://view.163.com/[0-9]{2}/[0-9]{4}/[0-9]{2}/(.*?).html"; //内容正则表达式
		
		String focusHtml = findContentHtml(theme);
		Queue<String> visitedLinks = new LinkedList<String>();
		//匹配获得内容的links
		Pattern newPage = Pattern.compile(newsContentLinksReg);
        
        Matcher themeMatcher = newPage.matcher(focusHtml);
//        int i = 0;
        while(themeMatcher.find()){
//        	i++;
        	String url = themeMatcher.group();
        	if(!visitedLinks.contains(url)){
        		String html = findContentHtml(url);
//        		System.out.println(url);
//        		System.out.println(findNewsTitle(html,newsTitleLabel,"_网易新闻中心"));
//        		System.out.println(findNewsTime(html,newsTimeLabel));
        		crut.add(findNewsTitle(html,newsTitleLabel,"_网易新闻中心"), findNewsOriginalTitle(html,newsTitleLabel,"_网易新闻中心"),findNewsOriginalTitle(html,newsTitleLabel,"_网易新闻中心"), findNewsTime(html,newsTimeLabel),findNewsContent(html,newsContentLabel),findNewsComment(url,html,newsCategroyLabel), findNewsSource(html,newsSourceLabel),
        				findNewsOriginalSource(html,newsSourceLabel), findNewsCategroy(html,newsCategroyLabel), findNewsOriginalCategroy(html,newsCategroyLabel), url, "");
        		visitedLinks.add(url);
        	}
        	
        }
//        System.out.println(i);
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
						if (node instanceof LinkTag)// 标记
							return true;
						return false;
					}});
				
				for (int i = 0; i < nodeList.size(); i++)
				{
				
					LinkTag n = (LinkTag) nodeList.elementAt(i);
//		        	System.out.print(n.getStringText() + "==>> ");
//		       	 	System.out.println(n.extractLink());
					//新闻主题
					Matcher themeMatcher = newsThemeLink.matcher(n.extractLink());
					if(themeMatcher.find()){
						if(!themelinks.contains(n.extractLink()))
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
		Queue<String> contentlinks = new LinkedList<String>(); // 临时征用
		
		Pattern newsContent = Pattern.compile(contentLinkReg);
		while(!themeLink.isEmpty()){
			
			String buf = themeLink.poll();
		
			try {
				Parser parser = new Parser(buf);
				parser.setEncoding(ENCODE);
				NodeList nodeList = parser.extractAllNodesThatMatch(new NodeFilter(){
					public boolean accept(Node node)
					{
						if (node instanceof LinkTag)// 标记
							return true;
						return false;
					}
		
				});
			
				for (int i = 0; i < nodeList.size(); i++)
				{
			
					LinkTag n = (LinkTag) nodeList.elementAt(i);
//	        	System.out.print(n.getStringText() + "==>> ");
//	       	 	System.out.println(n.extractLink());
					//新闻主题
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
//		System.out.println(contentlinks);
		return contentlinks;
	}
	
	@Override
	public String findContentHtml(String url) {
		// TODO Auto-generated method stub
		String html = null;                 //网页html
		
		HttpURLConnection httpUrlConnection;
	    InputStream inputStream;
	    BufferedReader bufferedReader;
	    
		int state;
		//判断url是否为有效连接
		try{
			httpUrlConnection = (HttpURLConnection) new URL(url).openConnection(); //创建连接
			state = httpUrlConnection.getResponseCode();
			httpUrlConnection.disconnect();
		}catch (MalformedURLException e) {
//          e.printStackTrace();
			System.out.println("该连接"+url+"网络有故障，已经无法正常链接，无法获取新闻");
			return null ;
		} catch (IOException e) {
          // TODO Auto-generated catch block
//          e.printStackTrace();
			System.out.println("该连接"+url+"网络超级慢，已经无法正常链接，无法获取新闻");
			return null ;
      }
		if(state != 200 && state != 201){
			return null;
		}
  
        try {
        	httpUrlConnection = (HttpURLConnection) new URL(url).openConnection(); //创建连接
        	httpUrlConnection.setRequestMethod("GET");
            httpUrlConnection.setUseCaches(true); //使用缓存
            httpUrlConnection.connect();           //建立连接  链接超时处理
        } catch (IOException e) {
        	System.out.println("该链接访问超时...");
        	return null;
        }
  
        try {
            inputStream = httpUrlConnection.getInputStream(); //读取输入流
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream, ENCODE)); 
            String string;
            StringBuffer sb = new StringBuffer();
            while ((string = bufferedReader.readLine()) != null) {
            	sb.append(string);
            	sb.append("\n");
            }
            html = sb.toString();
        } catch (IOException e) {
//            e.printStackTrace();
        }
//        System.out.println(html);
		return html;
	}
	
	@Override
	public String HandleHtml(String html, String one) {
		// TODO Auto-generated method stub
		NodeFilter filter = new HasAttributeFilter(one);
		String buf = "";
		try{
			Parser parser = Parser.createParser(html, ENCODE);
			NodeList nodes = parser.extractAllNodesThatMatch(filter);
   		
			if(nodes!=null) {
				for (int i = 0; i < nodes.size(); i++) {
					Node textnode1 = (Node) nodes.elementAt(i);
					buf += textnode1.toPlainTextString();
					if(buf.contains("&nbsp;"))
						buf = buf.replaceAll("&nbsp;", "\n");
				}
			}
		}catch(Exception e){
		   
		   
		}
		return buf ;
	}
	
	@Override
	public String HandleHtml(String html, String one, String two) {
		// TODO Auto-generated method stub
		NodeFilter filter = new HasAttributeFilter(one,two);
		String buf = "";
		try{
			Parser parser = Parser.createParser(html, ENCODE);
			NodeList nodes = parser.extractAllNodesThatMatch(filter);
   		
			if(nodes!=null) {
				for (int i = 0; i < nodes.size(); i++) {
					Node textnode1 = (Node) nodes.elementAt(i);
					buf += textnode1.toPlainTextString();
					if(buf.contains("&nbsp;"))
						buf = buf.replaceAll("&nbsp;", "\n");
				}
			}
		}catch(Exception e){
 
		}
		return buf ;
	}
	//news title
	public String findNewsTitle(String html , String[] label,String buf) {
		String titleBuf ;
		if(label[1].equals("")){
			titleBuf = HandleHtml(html,label[0]);
		}else{
			titleBuf = HandleHtml(html,label[0],label[1]);
		}
		if(titleBuf.contains(buf))
			titleBuf = titleBuf.substring(0, titleBuf.indexOf(buf))	;
		return titleBuf;
	}
	//news 未处理标题
	public String findNewsOriginalTitle(String html , String[] label,String buf) {
		// TODO Auto-generated method stub
		String titleBuf ;
		if(label[1].equals("")){
			titleBuf = HandleHtml(html,label[0]);
		}else{
			titleBuf = HandleHtml(html,label[0],label[1]);
		}
		if(titleBuf.contains(buf))
			titleBuf = titleBuf.substring(0, titleBuf.indexOf(buf)+buf.length())	;
		return titleBuf;
	}
	@Override
	public String findNewsContent(String html , String[] label) {
		// TODO Auto-generated method stub
		String contentBuf;
		if(label[1].equals("")){
			contentBuf = HandleHtml(html,label[0]);
		}else{
			contentBuf = HandleHtml(html,label[0],label[1]);
		}
		if(contentBuf == ""){
			contentBuf = HandleHtml(html ,"class","feed-text");
			System.out.println(contentBuf);
		}
		if(contentBuf.contains("(NTES);")){
			contentBuf = contentBuf.substring(contentBuf.indexOf("(NTES);")+7, contentBuf.length());
		}
		return contentBuf;
	}
	@Override
	public String findNewsImages(String html , String[] label) {
		// TODO Auto-generated method stub
		return null;
	}
	//新闻时间
	@Override
	public String findNewsTime(String html , String[] label) {
		// TODO Auto-generated method stub
		String timeBuf ="";
		if(label[1].equals("")){
			timeBuf = HandleHtml(html , label[0]);
		}else{
			timeBuf = HandleHtml(html , label[0],label[1]);
		}
		if(timeBuf == ""){
			timeBuf = HandleHtml(html,"id","ptime");
//			return timeBuf;
		}else if(label[0].equals("style")&&label[1].equals("float:left;")){
			timeBuf = timeBuf.substring(0,19);
		}else
			timeBuf = timeBuf.substring(9, 28);  //根据不同新闻 不同处理
		return timeBuf;
	}
	@Override
	public String findNewsSource(String html ,String[] label) {
		// TODO Auto-generated method stub
		if(label.length == 3 && (!label[2].equals("")))
			return label[2];
		else
			return null;
	}
	@Override
	public String findNewsOriginalSource(String html ,String[] label) {
		// TODO Auto-generated method stub
		String sourceBuf;
		if(label[1].equals("")){
			sourceBuf = HandleHtml(html , label[0]);
		}else{
			sourceBuf = HandleHtml(html , label[0],label[1]);
		}
		
		if(sourceBuf.length() >29)
			sourceBuf = sourceBuf.substring(29, sourceBuf.length());  //根据不同新闻 不同处理
		if(label.length == 3 && (!label[2].equals("")))
			return label[2]+"-"+sourceBuf;
		else
			return sourceBuf;
	}
	@Override
	public String findNewsCategroy(String html , String[] label) {
		// TODO Auto-generated method stub
		String categroyBuf ="";
		if(label[1].equals("")){
			categroyBuf = HandleHtml(html , label[0]);
		}else{
			categroyBuf = HandleHtml(html , label[0],label[1]);
		}
		if(categroyBuf.contains("&gt;")){
			categroyBuf = categroyBuf.replaceAll("&gt;", "");
			if(categroyBuf.contains("新闻中心")){
				categroyBuf = categroyBuf.substring(categroyBuf.indexOf("新闻中心")+5, categroyBuf.indexOf("正文")-1);
			}else if(categroyBuf.contains("新闻频道")){
				categroyBuf = categroyBuf.substring(categroyBuf.indexOf("新闻频道")+5, categroyBuf.length());
			}else if(categroyBuf.contains("新闻首页")){
				categroyBuf = categroyBuf.substring(categroyBuf.indexOf("新闻首页")+5, categroyBuf.indexOf("正文")-1);
			}else;
			
			categroyBuf = categroyBuf.replaceAll("\\s+", "");
		}
		return categroyBuf;
	}
	@Override
	public String findNewsOriginalCategroy(String html , String[] label) {
		// TODO Auto-generated method stub
		String categroyBuf ="";
		if(label[1].equals("")){
			categroyBuf = HandleHtml(html , label[0]);
		}else{
			categroyBuf = HandleHtml(html , label[0],label[1]);
		}
		if(categroyBuf.contains("&gt;")){
			categroyBuf = categroyBuf.replaceAll("&gt;", "");
		}
		return categroyBuf;
	}
	//获取新闻评论
	@Override
	public String findNewsComment(String url ,String html ,String[] label) {
		
		/*
		 * 先判断新闻类型 再做决定
		 * */
		String categroyBuf ="";
		if(label[1].equals("")){
			categroyBuf = HandleHtml(html , label[0]);
		}else{
			categroyBuf = HandleHtml(html , label[0],label[1]);
		}
		if(categroyBuf.contains("&gt;")){
			categroyBuf = categroyBuf.replaceAll("&gt;", "");
			if(categroyBuf.contains("新闻中心")){
				categroyBuf = categroyBuf.substring(categroyBuf.indexOf("新闻中心")+5, categroyBuf.indexOf("正文")-1);
			}else if(categroyBuf.contains("新闻频道")){
				categroyBuf = categroyBuf.substring(categroyBuf.indexOf("新闻频道")+5, categroyBuf.length());
			}
			
			categroyBuf = categroyBuf.replaceAll("\\s+", "");
		}
//		//如果网页失效 直接返回null
//		if(categroyBuf == ""){ 
//			System.out.println("纳尼！！！");
//			return null;
//		}
		//评论保存结果
		String result = null;
		
		//评论url
		String commentUrl = null;
		// TODO Auto-generated method stubhttp://comment.news.163.com/news_guoji2_bbs/ABQ1KHA20001121M.html
		String[] s1 = {"http://comment.news.163.com/news_shehui7_bbs/","http://comment.news.163.com/news_guonei8_bbs/","http://comment.news.163.com/news3_bbs/","http://comment.news.163.com/news_guoji2_bbs/","http://comment.news.163.com/news_junshi_bbs/"};
		String s2 = ".html";
		String s3 = url.substring(url.lastIndexOf("/")+1, url.lastIndexOf("."))+s2;
		if(categroyBuf.equals("社会新闻")){
			commentUrl = s1[0] + s3;
		}else if(categroyBuf.equals("易奇闻")){
			commentUrl = s1[0] + s3;
		}else if(categroyBuf.equals("国内新闻")){
			commentUrl = s1[1] + s3 ;
		}else if(categroyBuf.equals("国际新闻")){
			commentUrl = s1[3] + s3 ;
		}else if(categroyBuf.equals("军事")){
			commentUrl = s1[4] + s3 ;
		}else if(categroyBuf.equals("深度报道")){
			commentUrl = s1[0] + s3 ;
		}else if(categroyBuf.equals("评论频道")){
			commentUrl = s1[2] + s3 ;
		}else
			commentUrl = s1[2] + s3 ;
		result = handleComment(commentUrl);
		if(result == null && categroyBuf.equals("军事")){
			commentUrl = null;
			commentUrl = s1[2] +s3 ;
			result = handleComment(commentUrl);
		}
		
		if(result == null && url.contains("war.163.com")){
			commentUrl = null;
			commentUrl = s1[4] + s3 ;
			result = handleComment(commentUrl);
		}
		
		return result;
	}
	
	//烦人的评论处理
	public String handleComment(String commentUrl){
		
		StringBuffer result = new StringBuffer();
		
		URL link = null;
		
		try {
			link = new URL(commentUrl);
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
//			e1.printStackTrace();
			System.out.println("what is the fuck!!!");
			return null;
		}
					
        WebClient wc=new WebClient();
        WebRequest request=new WebRequest(link); 
        request.setCharset(ENCODE);
//        其他报文头字段可以根据需要添加
        wc.getCookieManager().setCookiesEnabled(true);//开启cookie管理
        wc.getOptions().setJavaScriptEnabled(true);//开启js解析。对于变态网页，这个是必须的
        wc.getOptions().setCssEnabled(true);//开启css解析。对于变态网页，这个是必须的。
        wc.getOptions().setThrowExceptionOnFailingStatusCode(false);
        wc.getOptions().setThrowExceptionOnScriptError(false);
        wc.getOptions().setTimeout(10000);
        //准备工作已经做好了
        HtmlPage page= null;
        try {
			page = wc.getPage(request);
		} catch (FailingHttpStatusCodeException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		}
        if(page==null)
        {
            System.out.println("采集 "+commentUrl+" 失败!!!");
            return null;
        }
        String content=page.asText();//网页内容保存在content里
        if(content==null)
        {
            System.out.println("采集 "+commentUrl+" 失败!!!");
            return null;
        }else;
        	//System.out.println(content);
        if(!content.contains("去跟贴广场看看")){
        	System.out.println("居然没有 去跟帖广场看看"+ commentUrl);
        	return null;
        	
        }
        String ss = content.substring(content.indexOf("去跟贴广场看看")+7, content.indexOf("跟贴用户自律公约"));
//        System.out.println(ss);
        result = new StringBuffer(ss);
        ss = null; 
        content = content.substring(0, content.indexOf("文明社会，从理性发贴开始。谢绝地域攻击。"));
        content = content.replaceAll("\\s+", "");
        String commentReg = "发表(.*?)顶";
//        String source = "发表哈哈哈啊哈顶顶顶顶发表家具啊姐姐顶发表哈哈哈顶发表。。。。顶发表；；；；顶发表【【】。；；；顶发表。。、；匹配顶发表(.*?)顶";
        Pattern newPage = Pattern.compile(commentReg);
        
        Matcher themeMatcher = newPage.matcher(content);
        while(themeMatcher.find()){
        	String mm = themeMatcher.group();
        	mm = mm.replaceAll("发表", "");
        	mm = mm.replaceAll("顶", "");
//        	System.out.println(mm);
        	result = result.append(mm).append("☆");
        	mm = null;
        }
		commentReg = null ;
		content = null;
		return result.toString();
	}
	@Override
	public void handle(String DBName ,String DBTable,String html ,String url) {
		// TODO Auto-generated method stub
//		CRUT crut = new CRUT(DBName ,DBTable);
//		crut.add(title, originalTitle, titleContent, time, content, comment, newSource, originalSource, category, originalCategroy, url, image);
		
	}
	
	//启动所有程序
	public void getNETEASENews(){
		getGuoNeiNews();
		getSheHuiNews();
		getWarNews();
		getFocusNews();
		getViewNews();
		getGuoJiNews();
	}
	//线程池
	public void createThreadPool(){
		/*   
         * 创建线程池，最小线程数为6，最大线程数为12，线程池维护线程的空闲时间为10秒，   
         * 使用队列深度为20的有界队列，如果执行程序尚未关闭，则位于工作队列头部的任务将被删除，   
         * 然后重试执行程序（如果再次失败，则重复此过程），里面已经根据队列深度对任务加载进行了控制。   
         */ 
		ThreadPoolExecutor cg = new ThreadPoolExecutor(6,12,10,TimeUnit.SECONDS,new ArrayBlockingQueue<Runnable>(20),  
                new ThreadPoolExecutor.DiscardOldestPolicy());
		TaskThreadPoolForNETEASE test1 = new TaskThreadPoolForNETEASE("guonei");
		TaskThreadPoolForNETEASE test2 = new TaskThreadPoolForNETEASE("guoji");
		TaskThreadPoolForNETEASE test3 = new TaskThreadPoolForNETEASE("war");
		TaskThreadPoolForNETEASE test4 = new TaskThreadPoolForNETEASE("view");
		TaskThreadPoolForNETEASE test5 = new TaskThreadPoolForNETEASE("focus");
		TaskThreadPoolForNETEASE test6 = new TaskThreadPoolForNETEASE("shehui");
		cg.execute(test1);
		cg.execute(test2);
		cg.execute(test3);
		cg.execute(test4);
		cg.execute(test5);
		cg.execute(test6);
		cg.shutdown();
		
		
	}
	public static void main(String[] args){
		long start = System.currentTimeMillis();
		NETEASE netease = new NETEASE();
//		netease.getNETEASENews();
		netease.createThreadPool();
		long end = System.currentTimeMillis();
		System.out.println(end-start);
	}
	
}

class TaskThreadPoolForNETEASE implements Runnable{
	
	public String dbtable1;
	public TaskThreadPoolForNETEASE(String dbtable1){
		this.dbtable1 = dbtable1;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		NETEASE threadNETEASE = new NETEASE(dbtable1);
		if(dbtable1.equals("guonei"))
			threadNETEASE.getGuoNeiNews();
		else if(dbtable1.equals("guoji"))
			threadNETEASE.getGuoJiNews();
		else if(dbtable1.equals("shehui"))
			threadNETEASE.getSheHuiNews();
		else if(dbtable1.equals("focus"))
			threadNETEASE.getFocusNews();
		else if(dbtable1.equals("war"))
			threadNETEASE.getWarNews();
		else if(dbtable1.equals("view"))
			threadNETEASE.getViewNews();
		else;
		threadNETEASE = null;
	}
	
}
