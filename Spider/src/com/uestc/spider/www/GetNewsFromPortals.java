package com.uestc.spider.www;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
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
import com.gargoylesoftware.htmlunit.util.Cookie;

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
	
	String DBName  ;           			//���ݿ�����
	String DBTable  ;       			//����
	private String[] newsTitleLabel;     //���ű����ǩ t
	private String[] newsContentLabel ;  //�������ݱ�ǩ 
	private String[] newsTimeLabel ;   //����ʱ��
	private String[] newsSourceLabel ; //��3��������������Դ ͬ����ʱ��
	private String[] newsCategroyLabel ; // "����" "��������-��������-http://news.163.com/domestic/"
//	private String[] news
	
	public NETEASENews(String encode, String[] newsTitle ,String[] newsContent ,String[] newsTime,String[] newsSource,String[] newsCategroy ){
		this.ENCODE = encode ;
		this.newsTitleLabel = newsTitle ;
		this.newsContentLabel = newsContent ;
		this.newsTimeLabel = newsTime ;
		this.newsSourceLabel = newsSource ;
		this.newsCategroyLabel = newsCategroy ;
		
		
		
	}
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
		DBName = "NETEASE";   //���ݿ�����
		DBTable = "GUONEI";   //����
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
		DBName = "NETEASE";   //���ݿ�����
		DBTable = "SHEHUI";   //����
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
			
//			System.out.println(findNewsComment(url));
//			System.out.println("\n");
			crut.add(findNewsTitle(html,newsTitleLabel,"_������������"), findNewsOriginalTitle(html,newsTitleLabel,"_������������"),findNewsOriginalTitle(html,newsTitleLabel,"_������������"), findNewsTime(html,newsTimeLabel),findNewsContent(html,newsContentLabel),findNewsComment(url,html,newsCategroyLabel), findNewsSource(html,newsSourceLabel),
					findNewsOriginalSource(html,newsSourceLabel), findNewsCategroy(html,newsCategroyLabel), findNewsOriginalCategroy(html,newsCategroyLabel), url, "");
		}
		System.out.println(i);
		
		

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
			titleBuf = titleBuf.substring(0, titleBuf.indexOf(buf)+7)	;
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
			categroyBuf = categroyBuf.substring(categroyBuf.indexOf("��������")+5, categroyBuf.indexOf("����")-1);
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
			categroyBuf = categroyBuf.substring(categroyBuf.indexOf("��������")+5, categroyBuf.indexOf("����")-1);
		}
		//�����ҳʧЧ ֱ�ӷ���null
		if(categroyBuf == ""){ 
			System.out.println("���ᣡ����");
			return null;
		}
		//���۱�����
		StringBuffer result = new StringBuffer("");
		
		//����url
		String commentUrl;
		// TODO Auto-generated method stub
		String[] s1 = {"http://comment.news.163.com/news_shehui7_bbs/","http://comment.news.163.com/news_guonei8_bbs/","http://comment.news.163.com/news3_bbs/"};
		String s2 = ".html";
		String s3 = url.substring(url.lastIndexOf("/")+1, url.lastIndexOf("."))+s2;
		if(categroyBuf.equals("�������")){
			commentUrl = s1[0] + s3;
		}else if(categroyBuf.equals("������")){
			commentUrl = s1[0] + s3;
		}else if(categroyBuf.equals("��������")){
			commentUrl = s1[1] + s3 ;
		}else
			commentUrl = s1[2] + s3 ;
		
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
        	System.out.println("��Ȼû�� ȥ�����㳡����");
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

	public String findNewsTitle(String html , String[] label , String buf) {
		// TODO Auto-generated method stub
		return null;
	}

	public String findNewsOriginalTitle(String html , String[] label , String buf) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String findNewsContent(String html , String[] label) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String findNewsImages(String html , String[] label) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String findNewsTime(String html , String[] label) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String findNewsSource(String html , String[] label) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String findNewsOriginalSource(String html,String[] label) {
		// TODO Auto-generated method stub
		if(label.length < 1)
			return null;
		else
			return label[0];
	}

	@Override
	public String findNewsCategroy(String html , String[] label) {
		// TODO Auto-generated method stub
		
		return null;
	}

	@Override
	public String findNewsOriginalCategroy(String html , String[] label) {
		// TODO Auto-generated method stub
		if(label.length < 1)
			return null;
		if(label.length == 2)
			return label[0]+"-" +label[0];
		else
			return label[0];
	}

	@Override
	public String findNewsComment(String url,String html ,String[] label) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void handle(String DBName,String DBTable,String html ,String url) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String findContentHtml(String url) {
		// TODO Auto-generated method stub
		//url = "http://news.163.com/14/1114/15/AB18IT2H00014JB6.html#f=wlist";
		String s1 = "http://comment.news.163.com/news3_bbs/";
		String s2 = ".html"; 
		String commentUrl;   //http://comment.news.163.com/news3_bbs/AB0V4LPH00014JB6.html
		commentUrl = s1+url.substring(url.lastIndexOf("/")+1, url.lastIndexOf("."))+s2;
		return commentUrl;
	}

	@Override
	public String HandleHtml(String html, String one) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String HandleHtml(String html, String one, String two) {
		// TODO Auto-generated method stub
		return null;
	}
	
}

public class GetNewsFromPortals {
	
	public static void main(String[] args){
		/*private String[] newsTitleLabel;     //���ű����ǩ title or id=h1title
		private String[] newsContentLabel ;  //�������ݱ�ǩ "id","endText"
		private String[] newsTimeLabel ;   //����ʱ��"class","ep-time-soure cDGray"
		private String[] newsSourceLabel ; //��3��������������Դ ͬ����ʱ��"class","ep-time-soure cDGray" �ټ���һ��"��������-��������"
		private String[] newsCategroyLabel ; // "����" "��������-��������-http://news.163.com/domestic/"
		 * 
		 * */
		long start = System.currentTimeMillis();
		
		NETEASENews test = new NETEASENews("gb2312");  //����gb2312
		test.getSheHuiNews();
		long end = System.currentTimeMillis();
		System.out.println(end-start);
	}
	
}


