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
	
	String DBName  ;           			//���ݿ�����
	String DBTable  ;       			//����
	private String[] newsTitleLabel;     //���ű����ǩ t
	private String[] newsContentLabel ;  //�������ݱ�ǩ 
	private String[] newsTimeLabel ;   //����ʱ��
	private String[] newsSourceLabel ; //��3��������������Դ ͬ����ʱ��
	private String[] newsCategroyLabel ; // "����" "��������-��������-http://news.163.com/domestic/"
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
		System.out.println("������������...");
		System.out.println("׼����ȡ��������...");
	}
	//�����ȡ��������links
	public Queue<String> newsThemeLinks = new LinkedList<String>() ;
	
	//�����ȡ��������links
	public Queue<String> newsContentLinks = new LinkedList<String>() ;
	
	//�����Ѿ����ʵ�����links ���������ظ�
	public Queue<String> linksVisited = new LinkedList<String>() ;
	
	//��������links��������ʽ
	public String newsThemeLinksReg ; //= "http://news.163.com/special/0001124J/guoneinews_[0-9]{1,2}.html#headList";
			
	//��������links��������ʽ
	public String newsContentLinksReg ; //= "http://news.163.com/[0-9]{2}/[0-9]{4}/[0-9]{2}/(.*?).html#f=dlist";
	
	//��������link
	public String theme ;
	
	//��������
	public String commentReg ;  //= "http://comment.news.163.com/news3_bbs/";
	
//	public NETEASENews(String theme){
//		
//		this.theme = theme ;
//	}
	//��ȡ��������
	public void getGuoNeiNews(){
		/*��ʼ��������ǩ��
		 * 
		 * */
		ENCODE = "GB2312";
		DBName = "NET";   //���ݿ�����
		DBTable = "guonei";   //����
		newsTitleLabel = new String[]{"title",""};     //���ű����ǩ title or id=h1title
		newsContentLabel = new String[]{"id" ,"endText"};  //�������ݱ�ǩ "id","endText"
		newsTimeLabel = new String[]{"class","ep-time-soure cDGray"};   //����ʱ��"class","ep-time-soure cDGray"  
		newsSourceLabel =new String[]{"class","ep-time-soure cDGray","��������-��������"}; //��3��������������Դ ͬ����ʱ��"class","ep-time-soure cDGray" �ټ���һ��"��������-��������"
		newsCategroyLabel = new String[]{"class","ep-crumb JS_NTES_LOG_FE"} ; // "����" "��������-��������-http://news.163.com/domestic/"
		commentReg = "http://comment.news.163.com/news3_bbs/";
		
		CRUT crut = new CRUT(DBName ,DBTable);
		//�������� ��ҳ����
		theme = "http://news.163.com/domestic/";
		
		//��������links��������ʽ
		newsThemeLinksReg = "http://news.163.com/special/0001124J/guoneinews_[0-9]{1,2}.html#headList";
		
		//��������links��������ʽ (http://view.163.com/14/1119/10/ABDHAKC500012Q9L.html#f=dlist)
		newsContentLinksReg = "http://news.163.com/[0-9]{2}/[0-9]{4}/[0-9]{2}/(.*?).html#f=dlist";
		
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
		//���������������links
		Queue<String> guoNeiNewsTheme = new LinkedList<String>();
		guoNeiNewsTheme = findThemeLinks(theme,newsThemeLinksReg);
//		System.out.println(guoNeiNewsTheme);
		
		//��ȡ������������links
		Queue<String>guoNeiNewsContent = new LinkedList<String>();
		guoNeiNewsContent = findContentLinks(guoNeiNewsTheme,newsContentLinksReg);
//		System.out.println(guoNeiNewsContent);
		//��ȡÿ��������ҳ��html
		int i = 0;
		while(!guoNeiNewsContent.isEmpty()){
			String url = guoNeiNewsContent.poll();
			String html = findContentHtml(url);  //��ȡ���ŵ�html
			System.out.println(url);
//			System.out.println(html);
			i++;
//			System.out.println(findNewsComment(url));
//			System.out.println("\n");
			crut.add(findNewsTitle(html,newsTitleLabel,"_������������"), findNewsOriginalTitle(html,newsTitleLabel,"_������������"),findNewsOriginalTitle(html,newsTitleLabel,"_������������"), findNewsTime(html,newsTimeLabel),findNewsContent(html,newsContentLabel),findNewsComment(url,html,newsCategroyLabel), findNewsSource(html,newsSourceLabel),
					findNewsOriginalSource(html,newsSourceLabel), findNewsCategroy(html,newsCategroyLabel), findNewsOriginalCategroy(html,newsCategroyLabel), url, "");
		}
		System.out.println(i);
		
		

	}
	
	//��ȡ�������
	
	public void getSheHuiNews(){
		
		/*��ʼ��������ǩ��
		 * 
		 * */
		ENCODE = "GB2312";
		DBName = "NET";   //���ݿ�����
		DBTable = "shehui";   //����
		newsTitleLabel = new String[]{"title",""};     //���ű����ǩ title or id=h1title
		newsContentLabel = new String[]{"id" ,"endText"};  //�������ݱ�ǩ "id","endText"
		newsTimeLabel = new String[]{"class","ep-time-soure cDGray"};   //����ʱ��"class","ep-time-soure cDGray"  
		newsSourceLabel =new String[]{"class","ep-time-soure cDGray","��������-�������"}; //��3��������������Դ ͬ����ʱ��"class","ep-time-soure cDGray" �ټ���һ��"��������-��������"
		newsCategroyLabel = new String[]{"class","ep-crumb JS_NTES_LOG_FE"} ; // "����" "��������-��������-http://news.163.com/domestic/"
//		commentReg = "http://comment.news.163.com/news_shehui7_bbs/";
		
		CRUT crut = new CRUT(DBName ,DBTable);
		//�������� ��ҳ����
		theme = "http://news.163.com/shehui/";
		
		//��������links��������ʽ
		newsThemeLinksReg = "http://news.163.com/special/00011229/shehuinews_[0-9]{1,2}.html#headList";
		
		//��������links��������ʽhttp://focus.news.163.com/  
		newsContentLinksReg = "http://news.163.com/[0-9]{2}/[0-9]{4}/[0-9]{2}/(.*?).html#f=s((list)|(focus))";
		
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
		//���������������links
		Queue<String> sheHuiNewsTheme = new LinkedList<String>();
		sheHuiNewsTheme = findThemeLinks(theme,newsThemeLinksReg);
//		System.out.println(sheHuiNewsTheme);
		
		//��ȡ�����������links
		Queue<String>sheHuiNewsContent = new LinkedList<String>();
		sheHuiNewsContent = findContentLinks(sheHuiNewsTheme,newsContentLinksReg);
//		System.out.println(sheHuiNewsContent);
		//��ȡÿ��������ҳ��html
		int i = 0;
		while(!sheHuiNewsContent.isEmpty()){
			String url = sheHuiNewsContent.poll();
			String html = findContentHtml(url);  //��ȡ���ŵ�html
			System.out.println(url);
//			System.out.println(findNewsTitle(html,new String[]{"title",""},"_������������"));
//			System.out.println(findNewsContent(html,new String[]{"id" ,"endText"}));
//			System.out.println(findNewsTime(html,new String[]{"class","ep-time-soure cDGray"}));
//			System.out.println(findNewsCategroy(html,new String[]{"class","ep-crumb JS_NTES_LOG_FE"}));
//			System.out.println(findNewsComment(url,html,newsCategroyLabel));
			i++;
			
//			System.out.println(findNewsComment(url,html,newsCategroyLabel));
//			System.out.println("\n");
			crut.add(findNewsTitle(html,newsTitleLabel,"_������������"), findNewsOriginalTitle(html,newsTitleLabel,"_������������"),findNewsOriginalTitle(html,newsTitleLabel,"_������������"), findNewsTime(html,newsTimeLabel),findNewsContent(html,newsContentLabel),findNewsComment(url,html,newsCategroyLabel), findNewsSource(html,newsSourceLabel),
					findNewsOriginalSource(html,newsSourceLabel), findNewsCategroy(html,newsCategroyLabel), findNewsOriginalCategroy(html,newsCategroyLabel), url, "");
		}
		System.out.println(i);
		
		

	}
	
	//��������
	public void getGuoJiNews(){
		
		/*��ʼ��������ǩ�� ���� gb2312
		 * 
		 * */
		ENCODE = "GB2312";
		DBName = "NET";   //���ݿ�����
		DBTable = "guoji";   //����
		newsTitleLabel = new String[]{"title",""};     //���ű����ǩ title or id=h1title
		newsContentLabel = new String[]{"id" ,"endText"};  //�������ݱ�ǩ "id","endText"
		newsTimeLabel = new String[]{"class","ep-time-soure cDGray"};   //����ʱ��"class","ep-time-soure cDGray"  
		newsSourceLabel =new String[]{"class","ep-time-soure cDGray","��������-��������"}; //��3��������������Դ ͬ����ʱ��"class","ep-time-soure cDGray" �ټ���һ��"��������-��������"
		newsCategroyLabel = new String[]{"class","ep-crumb JS_NTES_LOG_FE"} ; // "����" "��������-��������-http://news.163.com/domestic/"
		CRUT crut = new CRUT(DBName,DBTable);
		//��rss��ȡ
		theme = "http://news.163.com/special/00011K6L/rss_gj.xml";
		
		//�������ݵ�������ʽ�����ʰ��Ƚ����⣩
		newsContentLinksReg = "http://news.163.com/[0-9]{2}/[0-9]{4}/[0-9]{2}/(.*?).html";
		
		String guoJiHtml = findContentHtml(theme);
		//�����Ѿ����ʵ�links
		Queue<String> visitedLinks = new LinkedList<String>();
		//ƥ�������ݵ�links
		Pattern newPage = Pattern.compile(newsContentLinksReg);
        
        Matcher themeMatcher = newPage.matcher(guoJiHtml);
        int i = 0;
        while(themeMatcher.find()){
        	i++;
        	String url = themeMatcher.group();
        	if(!visitedLinks.contains(url)){
        		String html = findContentHtml(url);
        		System.out.println(url);
//        		System.out.println(findNewsTitle(html,newsTitleLabel,"_������������"));
//        		System.out.println(findNewsContent(html,newsContentLabel));
        		crut.add(findNewsTitle(html,newsTitleLabel,"_������������"), findNewsOriginalTitle(html,newsTitleLabel,"_������������"),findNewsOriginalTitle(html,newsTitleLabel,"_������������"), findNewsTime(html,newsTimeLabel),findNewsContent(html,newsContentLabel),findNewsComment(url,html,newsCategroyLabel), findNewsSource(html,newsSourceLabel),
        				findNewsOriginalSource(html,newsSourceLabel), findNewsCategroy(html,newsCategroyLabel), findNewsOriginalCategroy(html,newsCategroyLabel), url, "");
        		visitedLinks.add(url);
        	}
        	
        }
        System.out.println(i);
		
		
	}
	
	//���׾��� var
	public void getWarNews(){
		
		/*��ʼ��������ǩ�� ���� gb2312
		 * 
		 * */
		ENCODE = "GB2312";
		DBName = "NET";   //���ݿ�����
		DBTable = "war";   //����
		newsTitleLabel = new String[]{"title",""};     //���ű����ǩ title or id=h1title
		newsContentLabel = new String[]{"id" ,"endText"};  //�������ݱ�ǩ "id","endText"
		newsTimeLabel = new String[]{"class","ep-time-soure cDGray"};   //����ʱ��"class","ep-time-soure cDGray"  
		newsSourceLabel =new String[]{"class","ep-time-soure cDGray","��������-��������"}; //��3��������������Դ ͬ����ʱ��"class","ep-time-soure cDGray" �ټ���һ��"��������-��������"
		newsCategroyLabel = new String[]{"class","ep-crumb JS_NTES_LOG_FE"} ; // "����" "��������-��������-http://news.163.com/domestic/"
		CRUT crut = new CRUT(DBName,DBTable);
		
		/*���ģ����Ĳ���ץȡ
		 * 1����ҳ"http://war.163.com/index.html"
		 * 2.��ϸ����һ��10Ҷ��http://war.163.com/special/millatestnews/ http://war.163.com/special/millatestnews_06/
		 * 3.������ʷ��http://war.163.com/special/historyread/
		 * */
		//����
		Queue<String> warNewsThemeLinks = new LinkedList<String>();
		//����
		Queue<String> warNewsContentLinks = new LinkedList<String>();
		
		//������ҳ
		theme = "http://war.163.com/index.html";
		//�������ݵ�������ʽhttp://war.163.com/14/1124/09/ABQAT7EM00011MTO.html
		newsContentLinksReg = "http://war.163.com/[0-9]{2}/[0-9]{4}/[0-9]{2}/(.*?).html";
		warNewsThemeLinks.offer(theme);
		warNewsContentLinks = findContentLinks(warNewsThemeLinks,newsContentLinksReg);
//		
		while(!warNewsContentLinks.isEmpty()){
			String url = warNewsContentLinks.poll();
			String html = findContentHtml(url);  //��ȡ���ŵ�html
//			System.out.println(url);
			crut.add(findNewsTitle(html,newsTitleLabel,"_������������"), findNewsOriginalTitle(html,newsTitleLabel,"_������������"),findNewsOriginalTitle(html,newsTitleLabel,"_������������"), findNewsTime(html,newsTimeLabel),findNewsContent(html,newsContentLabel),findNewsComment(url,html,newsCategroyLabel), findNewsSource(html,newsSourceLabel),
					findNewsOriginalSource(html,newsSourceLabel), findNewsCategroy(html,newsCategroyLabel), findNewsOriginalCategroy(html,newsCategroyLabel), url, "");
		}
		
		//��ϸ����ģ��
		theme  = "http://war.163.com/special/millatestnews/";
		newsThemeLinksReg = "http://war.163.com/special/millatestnews(_[0-9]{2})*/";  //����������ʽ
		newsContentLinksReg = "http://war.163.com/[0-9]{2}/[0-9]{4}/[0-9]{2}/(.*?).html"; //����������ʽ
		warNewsThemeLinks = findThemeLinks(theme,newsThemeLinksReg);
		warNewsContentLinks = findContentLinks(warNewsThemeLinks,newsContentLinksReg);
//		int k = 1;
		while(!warNewsContentLinks.isEmpty()){
			String url = warNewsContentLinks.poll();
			String html = findContentHtml(url);  //��ȡ���ŵ�html
//			System.out.println(url);
//			System.out.println(findNewsTitle(html,new String[]{"title",""},"_���׾���"));
//			System.out.println(findNewsContent(html,new String[]{"id" ,"endText"}));
//			System.out.println(findNewsTime(html,new String[]{"class","ep-time-soure cDGray"}));
//			System.out.println(findNewsCategroy(html,new String[]{"class","ep-crumb JS_NTES_LOG_FE"}));
//			System.out.println(findNewsComment(url,html,newsCategroyLabel));
//			k++;
			
//			System.out.println(findNewsComment(url,html,newsCategroyLabel));
//			System.out.println("\n");
			crut.add(findNewsTitle(html,newsTitleLabel,"_���׾���"), findNewsOriginalTitle(html,newsTitleLabel,"_���׾���"),findNewsOriginalTitle(html,newsTitleLabel,"_���׾���"), findNewsTime(html,newsTimeLabel),findNewsContent(html,newsContentLabel),findNewsComment(url,html,newsCategroyLabel), findNewsSource(html,newsSourceLabel),
					findNewsOriginalSource(html,newsSourceLabel), findNewsCategroy(html,newsCategroyLabel), findNewsOriginalCategroy(html,newsCategroyLabel), url, "");
		}
//		System.out.println(k);
		
		//������ʷ
		theme = "http://war.163.com/special/historyread/";
		newsThemeLinksReg = "http://war.163.com/special/historyread(_[0-9]{2})*/";  //����������ʽ
		newsContentLinksReg = "http://war.163.com/[0-9]{2}/[0-9]{4}/[0-9]{2}/(.*?).html"; //����������ʽ
		warNewsThemeLinks = findThemeLinks(theme,newsThemeLinksReg);
		warNewsContentLinks = findContentLinks(warNewsThemeLinks,newsContentLinksReg);
//		int j = 1 ;
		while(!warNewsContentLinks.isEmpty()){
			String url = warNewsContentLinks.poll();
			String html = findContentHtml(url);  //��ȡ���ŵ�html
//			System.out.println(url);
//			System.out.println(findNewsTitle(html,new String[]{"title",""},"_���׾���"));
//			System.out.println(findNewsContent(html,new String[]{"id" ,"endText"}));
//			System.out.println(findNewsTime(html,new String[]{"class","ep-time-soure cDGray"}));
//			System.out.println(findNewsCategroy(html,new String[]{"class","ep-crumb JS_NTES_LOG_FE"}));
//			System.out.println(findNewsComment(url,html,newsCategroyLabel));
//			j++;
			
//			System.out.println(findNewsComment(url,html,newsCategroyLabel));
//			System.out.println("\n");
			crut.add(findNewsTitle(html,newsTitleLabel,"_���׾���"), findNewsOriginalTitle(html,newsTitleLabel,"_���׾���"),findNewsOriginalTitle(html,newsTitleLabel,"_���׾���"), findNewsTime(html,newsTimeLabel),findNewsContent(html,newsContentLabel),findNewsComment(url,html,newsCategroyLabel), findNewsSource(html,newsSourceLabel),
					findNewsOriginalSource(html,newsSourceLabel), findNewsCategroy(html,newsCategroyLabel), findNewsOriginalCategroy(html,newsCategroyLabel), url, "");
		}
//		System.out.println(j);
	}
	//�������
	public void getFocusNews(){
		/*��ʼ��������ǩ�� ���� gb2312
		 * 
		 * */
		ENCODE = "GB2312";
		DBName = "NET";   //���ݿ�����
		DBTable = "focus";   //����
		newsTitleLabel = new String[]{"title",""};     //���ű����ǩ title or id=h1title
		newsContentLabel = new String[]{"id" ,"endText"};  //�������ݱ�ǩ "id","endText"
		newsTimeLabel = new String[]{"style","float:left;"};   //����ʱ��"class","info"  
		newsSourceLabel =new String[]{"class","path","��������-��ȱ���"}; //��3��������������Դ ͬ����ʱ��"class","ep-time-soure cDGray" �ټ���һ��"��������-��������"
		newsCategroyLabel = new String[]{"class","path"} ; // "����" "��������-��������-http://news.163.com/domestic/"
		CRUT crut = new CRUT(DBName,DBTable);
		
		theme = "http://focus.news.163.com/";
		
		//����������ʽhttp://focus.news.163.com/13/0325/18/8QR65LJS00011SM9.html
		newsContentLinksReg = "http://focus.news.163.com/[0-9]{2}/[0-9]{4}/[0-9]{2}/(.*?).html"; //����������ʽ
		
		String focusHtml = findContentHtml(theme);
		Queue<String> visitedLinks = new LinkedList<String>();
		//ƥ�������ݵ�links
		Pattern newPage = Pattern.compile(newsContentLinksReg);
        
        Matcher themeMatcher = newPage.matcher(focusHtml);
//        int i = 0;
        while(themeMatcher.find()){
//        	i++;
        	String url = themeMatcher.group();
        	if(!visitedLinks.contains(url)){
        		String html = findContentHtml(url);
//        		System.out.println(url);
//        		System.out.println(findNewsTitle(html,newsTitleLabel,"_������������"));
//        		System.out.println(findNewsTime(html,newsTimeLabel));
        		crut.add(findNewsTitle(html,newsTitleLabel,"_������������"), findNewsOriginalTitle(html,newsTitleLabel,"_������������"),findNewsOriginalTitle(html,newsTitleLabel,"_������������"), findNewsTime(html,newsTimeLabel),findNewsContent(html,newsContentLabel),findNewsComment(url,html,newsCategroyLabel), findNewsSource(html,newsSourceLabel),
        				findNewsOriginalSource(html,newsSourceLabel), findNewsCategroy(html,newsCategroyLabel), findNewsOriginalCategroy(html,newsCategroyLabel), url, "");
        		visitedLinks.add(url);
        	}
        	
        }
//        System.out.println(i);
	}
	
	//��������
	public void getViewNews(){
		/*��ʼ��������ǩ�� ���� gb2312
		 * 
		 * */
		ENCODE = "GB2312";
		DBName = "NET";   //���ݿ�����
		DBTable = "view";   //����
		newsTitleLabel = new String[]{"title",""};     //���ű����ǩ title or id=h1title
		newsContentLabel = new String[]{"class" ,"feed-text"};  //�������ݱ�ǩ class="feed-text"
		newsTimeLabel = new String[]{"style","float:left;"};   //����ʱ��"class","info"  
		newsSourceLabel =new String[]{"class","path","��������-��ȱ���"}; //��3��������������Դ ͬ����ʱ��"class","ep-time-soure cDGray" �ټ���һ��"��������-��������"
		newsCategroyLabel = new String[]{"class","path"} ; // "����" "��������-��������-http://news.163.com/domestic/"
		CRUT crut = new CRUT(DBName,DBTable);
		
		theme = "http://view.163.com/";
		
		//����������ʽhttp://view.163.com/14/1125/11/ABT5R5GF00012Q9L.html
		newsContentLinksReg = "http://view.163.com/[0-9]{2}/[0-9]{4}/[0-9]{2}/(.*?).html"; //����������ʽ
		
		String focusHtml = findContentHtml(theme);
		Queue<String> visitedLinks = new LinkedList<String>();
		//ƥ�������ݵ�links
		Pattern newPage = Pattern.compile(newsContentLinksReg);
        
        Matcher themeMatcher = newPage.matcher(focusHtml);
//        int i = 0;
        while(themeMatcher.find()){
//        	i++;
        	String url = themeMatcher.group();
        	if(!visitedLinks.contains(url)){
        		String html = findContentHtml(url);
//        		System.out.println(url);
//        		System.out.println(findNewsTitle(html,newsTitleLabel,"_������������"));
//        		System.out.println(findNewsTime(html,newsTimeLabel));
        		crut.add(findNewsTitle(html,newsTitleLabel,"_������������"), findNewsOriginalTitle(html,newsTitleLabel,"_������������"),findNewsOriginalTitle(html,newsTitleLabel,"_������������"), findNewsTime(html,newsTimeLabel),findNewsContent(html,newsContentLabel),findNewsComment(url,html,newsCategroyLabel), findNewsSource(html,newsSourceLabel),
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
						if (node instanceof LinkTag)// ���
							return true;
						return false;
					}});
				
				for (int i = 0; i < nodeList.size(); i++)
				{
				
					LinkTag n = (LinkTag) nodeList.elementAt(i);
//		        	System.out.print(n.getStringText() + "==>> ");
//		       	 	System.out.println(n.extractLink());
					//��������
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
//		System.out.println(contentlinks);
		return contentlinks;
	}
	
	@Override
	public String findContentHtml(String url) {
		// TODO Auto-generated method stub
		String html = null;                 //��ҳhtml
		
		HttpURLConnection httpUrlConnection;
	    InputStream inputStream;
	    BufferedReader bufferedReader;
	    
		int state;
		//�ж�url�Ƿ�Ϊ��Ч����
		try{
			httpUrlConnection = (HttpURLConnection) new URL(url).openConnection(); //��������
			state = httpUrlConnection.getResponseCode();
			httpUrlConnection.disconnect();
		}catch (MalformedURLException e) {
//          e.printStackTrace();
			System.out.println("������"+url+"�����й��ϣ��Ѿ��޷��������ӣ��޷���ȡ����");
			return null ;
		} catch (IOException e) {
          // TODO Auto-generated catch block
//          e.printStackTrace();
			System.out.println("������"+url+"���糬�������Ѿ��޷��������ӣ��޷���ȡ����");
			return null ;
      }
		if(state != 200 && state != 201){
			return null;
		}
  
        try {
        	httpUrlConnection = (HttpURLConnection) new URL(url).openConnection(); //��������
        	httpUrlConnection.setRequestMethod("GET");
            httpUrlConnection.setUseCaches(true); //ʹ�û���
            httpUrlConnection.connect();           //��������  ���ӳ�ʱ����
        } catch (IOException e) {
        	System.out.println("�����ӷ��ʳ�ʱ...");
        	return null;
        }
  
        try {
            inputStream = httpUrlConnection.getInputStream(); //��ȡ������
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
	//news δ�������
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
	//����ʱ��
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
			timeBuf = timeBuf.substring(9, 28);  //���ݲ�ͬ���� ��ͬ����
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
			sourceBuf = sourceBuf.substring(29, sourceBuf.length());  //���ݲ�ͬ���� ��ͬ����
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
			if(categroyBuf.contains("��������")){
				categroyBuf = categroyBuf.substring(categroyBuf.indexOf("��������")+5, categroyBuf.indexOf("����")-1);
			}else if(categroyBuf.contains("����Ƶ��")){
				categroyBuf = categroyBuf.substring(categroyBuf.indexOf("����Ƶ��")+5, categroyBuf.length());
			}else if(categroyBuf.contains("������ҳ")){
				categroyBuf = categroyBuf.substring(categroyBuf.indexOf("������ҳ")+5, categroyBuf.indexOf("����")-1);
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
	//��ȡ��������
	@Override
	public String findNewsComment(String url ,String html ,String[] label) {
		
		/*
		 * ���ж��������� ��������
		 * */
		String categroyBuf ="";
		if(label[1].equals("")){
			categroyBuf = HandleHtml(html , label[0]);
		}else{
			categroyBuf = HandleHtml(html , label[0],label[1]);
		}
		if(categroyBuf.contains("&gt;")){
			categroyBuf = categroyBuf.replaceAll("&gt;", "");
			if(categroyBuf.contains("��������")){
				categroyBuf = categroyBuf.substring(categroyBuf.indexOf("��������")+5, categroyBuf.indexOf("����")-1);
			}else if(categroyBuf.contains("����Ƶ��")){
				categroyBuf = categroyBuf.substring(categroyBuf.indexOf("����Ƶ��")+5, categroyBuf.length());
			}
			
			categroyBuf = categroyBuf.replaceAll("\\s+", "");
		}
//		//�����ҳʧЧ ֱ�ӷ���null
//		if(categroyBuf == ""){ 
//			System.out.println("���ᣡ����");
//			return null;
//		}
		//���۱�����
		String result = null;
		
		//����url
		String commentUrl = null;
		// TODO Auto-generated method stubhttp://comment.news.163.com/news_guoji2_bbs/ABQ1KHA20001121M.html
		String[] s1 = {"http://comment.news.163.com/news_shehui7_bbs/","http://comment.news.163.com/news_guonei8_bbs/","http://comment.news.163.com/news3_bbs/","http://comment.news.163.com/news_guoji2_bbs/","http://comment.news.163.com/news_junshi_bbs/"};
		String s2 = ".html";
		String s3 = url.substring(url.lastIndexOf("/")+1, url.lastIndexOf("."))+s2;
		if(categroyBuf.equals("�������")){
			commentUrl = s1[0] + s3;
		}else if(categroyBuf.equals("������")){
			commentUrl = s1[0] + s3;
		}else if(categroyBuf.equals("��������")){
			commentUrl = s1[1] + s3 ;
		}else if(categroyBuf.equals("��������")){
			commentUrl = s1[3] + s3 ;
		}else if(categroyBuf.equals("����")){
			commentUrl = s1[4] + s3 ;
		}else if(categroyBuf.equals("��ȱ���")){
			commentUrl = s1[0] + s3 ;
		}else if(categroyBuf.equals("����Ƶ��")){
			commentUrl = s1[2] + s3 ;
		}else
			commentUrl = s1[2] + s3 ;
		result = handleComment(commentUrl);
		if(result == null && categroyBuf.equals("����")){
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
	
	//���˵����۴���
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
//        ��������ͷ�ֶο��Ը�����Ҫ���
        wc.getCookieManager().setCookiesEnabled(true);//����cookie����
        wc.getOptions().setJavaScriptEnabled(true);//����js���������ڱ�̬��ҳ������Ǳ����
        wc.getOptions().setCssEnabled(true);//����css���������ڱ�̬��ҳ������Ǳ���ġ�
        wc.getOptions().setThrowExceptionOnFailingStatusCode(false);
        wc.getOptions().setThrowExceptionOnScriptError(false);
        wc.getOptions().setTimeout(10000);
        //׼�������Ѿ�������
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
            System.out.println("�ɼ� "+commentUrl+" ʧ��!!!");
            return null;
        }
        String content=page.asText();//��ҳ���ݱ�����content��
        if(content==null)
        {
            System.out.println("�ɼ� "+commentUrl+" ʧ��!!!");
            return null;
        }else;
        	//System.out.println(content);
        if(!content.contains("ȥ�����㳡����")){
        	System.out.println("��Ȼû�� ȥ�����㳡����"+ commentUrl);
        	return null;
        	
        }
        String ss = content.substring(content.indexOf("ȥ�����㳡����")+7, content.indexOf("�����û����ɹ�Լ"));
//        System.out.println(ss);
        result = new StringBuffer(ss);
        ss = null; 
        content = content.substring(0, content.indexOf("������ᣬ�����Է�����ʼ��л�����򹥻���"));
        content = content.replaceAll("\\s+", "");
        String commentReg = "����(.*?)��";
//        String source = "�������������������������Ҿ߰���㶥���������������������������������������������������������������ƥ�䶥����(.*?)��";
        Pattern newPage = Pattern.compile(commentReg);
        
        Matcher themeMatcher = newPage.matcher(content);
        while(themeMatcher.find()){
        	String mm = themeMatcher.group();
        	mm = mm.replaceAll("����", "");
        	mm = mm.replaceAll("��", "");
//        	System.out.println(mm);
        	result = result.append(mm).append("��");
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
	
	//�������г���
	public void getNETEASENews(){
		getGuoNeiNews();
		getSheHuiNews();
		getWarNews();
		getFocusNews();
		getViewNews();
		getGuoJiNews();
	}
	//�̳߳�
	public void createThreadPool(){
		/*   
         * �����̳߳أ���С�߳���Ϊ6������߳���Ϊ12���̳߳�ά���̵߳Ŀ���ʱ��Ϊ10�룬   
         * ʹ�ö������Ϊ20���н���У����ִ�г�����δ�رգ���λ�ڹ�������ͷ�������񽫱�ɾ����   
         * Ȼ������ִ�г�������ٴ�ʧ�ܣ����ظ��˹��̣��������Ѿ����ݶ�����ȶ�������ؽ����˿��ơ�   
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
