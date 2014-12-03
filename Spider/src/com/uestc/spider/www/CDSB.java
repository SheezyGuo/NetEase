package com.uestc.spider.www;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Vector;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.util.NodeList;


/*
 * 针对成都商报的新闻爬虫
 * 获取成都商报一天的所有新闻
 * 新闻包括 新闻题目，发布时间，新闻内容，报道记者以及其PDF格式的新闻文件
 * 可配置！！
 * */
public class CDSB implements Runnable {

	HttpURLConnection httpUrlConnection;
    InputStream inputStream;
    BufferedReader bufferedReader;
    String url;              		//要处理的url
    String text = "";       		 //存储url的html内容
    String nameSource = "cdsb";       //新闻来源

    public String title;  			//新闻标题
    public String titleContent;     //新闻内容标题
    public String originalTitle;    //未处理原始标题
    
    public String content;			//新闻内容
    
    public String time;             //新闻发布时间
    
    public String newSource;       //新闻来源
    public String originalSource ;       //未处理原始新闻来源
    
    public String categroy ;            //新闻类别
    public String originalCategroy ; //新闻原始分类
    
    private String bqTitle[] ;     //= {"title"};   //新闻标题网页标签"class","bt_title"
    private String[] bqContent ;    //= {"id","ozoom"} ; // 新闻内容网页标签"bt_con" id="ozoom
    private String[] bqDate    ;   //=  {"class","header-today"} ;     //时间标签"class","header-today" "riq"
    private String[] bqNewSource ;    //={"class","info"} ; //新闻来源标签 name"author"
    private String[] bqCategroy ;     //= {"width","57%"};  //新闻分类width="57%" "class","s_left"
    private String bqBuf     ;        // = "- 成都商报|成都商报电子版|成都商报官方网站" ;// "华西都市报" ;              //过滤内容，如- 成都商报|成都商报电子版|成都商报官方网站 以及
    
    //图片配置
    private String photoUrl ; // 替换相对路径
    private String imageUrl ; //= "IMG src=\"(.*?)res(.*?)attpic_brief.jpg\"";     //"img src=\"(.*?)res(.*?)attpic_brief.jpg\""
	private String imageScr ; //= "http:\"?(.*?)(\"|>|\\s+)";     //"http:\"?(.*?)(\"|>|\\s+)"
	private String imageBuf ; //= "../../../"; 
    
    private String ENCODE   ; // = "gb2312"; //gb2312 utf-8
    
    public int state = 0;
  
    public CDSB(String url, String[] bqtitle,String[] bqcontent,
    		String[] bqdate,String[] bqnewsource ,String[] bqcategroy ,String bqbuf,String encode,
    		String photourl,String imageurl,String imagescr,String imagebuf) {
  
        try {
        	this.url = url;
        	this.bqTitle = bqtitle;
        	this.bqContent = bqcontent ;
        	this.bqDate = bqdate;
        	this.bqNewSource = bqnewsource;
        	this.bqCategroy = bqcategroy;
        	this.bqBuf = bqbuf;
        	this.ENCODE = encode;
//        	this.baseUrl = ;
        	this.photoUrl = photourl;
        	this.imageUrl = imageurl;
        	this.imageScr = imagescr ;
        	this.imageBuf = imagebuf;
        
        } catch (Exception e) {
        	
//        	e.printStackTrace();
        }
  
        try {
            httpUrlConnection = (HttpURLConnection) new URL(url).openConnection(); //创建连接
            state = httpUrlConnection.getResponseCode();
            httpUrlConnection.disconnect();
        } catch (MalformedURLException e) {
//            e.printStackTrace();
        } catch (IOException e) {
           
//            e.printStackTrace();
        }
  
//        System.out.println("---------start-----------");
        if(state == 200 ||state == 201){
        	try {
				httpUrlConnection = (HttpURLConnection) new URL(url).openConnection();
			} catch (MalformedURLException e1) {
				
//				e1.printStackTrace();
			} catch (IOException e1) {
				
//				e1.printStackTrace();
			} //创建连接
        	Thread thread = new Thread(this);
        	thread.start();
        	try {
        		thread.join();
        	} catch (InterruptedException e) {
//        		e.printStackTrace();
        	}
        }
  
//        System.out.println("----------end------------");
    }
  
    public void run() {
        try {
            httpUrlConnection.setRequestMethod("GET");
        } catch (ProtocolException e) {
//            e.printStackTrace();
        }
  
        try {
            httpUrlConnection.setUseCaches(true); //使用缓存
            httpUrlConnection.connect();           //建立连接  链接超时处理
        } catch (IOException e) {
//            e.printStackTrace();
//        	continue;
        	System.out.println("该链接访问超时...");
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
            text = sb.toString();
        } catch (IOException e) {
//            e.printStackTrace();
        } //finally {
//            try {
//                bufferedReader.close();
//                inputStream.close();
//                httpUrlConnection.disconnect();
//            } catch (IOException e) {
//                e.printStackTrace();
//            	System.out.println("链接关闭出现问题...");
//            }
  
//        }
  
    }

 
   /*
     	只需要一个参数就可以判断的标签，比如title 
    * */
   String handle(String html ,String one){
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
   
  /*
   * 需要两个参数才能判断准确的内容：比如 content-title ，ozoom等
   */
   String handle(String html ,String one ,String two){
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
 /*
  * 新闻标题
  * */ 
 String handleOriginalTitle(String html){
	 if(bqTitle[1].equals(""))
	   originalTitle = handle(html,bqTitle[0]); //,bqTitle[1]
	 else
	   originalTitle = handle(html,bqTitle[0],bqTitle[1]);
//	   System.out.println(originalTitle);
	   return originalTitle;
   }
 /*
  * 新闻内容标题
  * 
  * */
 String handleTitleContent(String html){
	 if(bqTitle[1].equals(""))
		 titleContent = handle(html,bqTitle[0]);
	 else
		 titleContent = handle(html,bqTitle[0],bqTitle[1]);
	 return titleContent;
 }
 
 String handleTitle(String html){
	 if(bqTitle[1].equals(""))
		 title = handle(html,bqTitle[0]); //,bqTitle[1]
	 else
		 title = handle(html,bqTitle[0],bqTitle[1]);
	 if(title != null && title != "")
		 title = title.replace(bqBuf, "");
	 if(url.contains("newspaper.jfdaily.com/xwcb"))  //新闻晨报
		 title = title.substring(0, title.lastIndexOf(" "));
//	 System.out.println(title);
	 return title;
 }
 
 String handleUrl(){
	 return url;
 }
/*
 * 新闻内容
 * */
   String handleContent(String html){
	   
	   if(bqContent[1].equals(""))
		   content = handle(html,bqContent[0]);
	   else
		   content = handle(html,bqContent[0],bqContent[1]);
	   if(url.contains("gzdaily.dayoo.com/html")){
		   content = content.substring(content.indexOf("来源: 广州日报")+8,content.length());
	   }else if(url.contains("bjwb.bjd.com.cn/html")){ //北京晚报
		   content = html.substring(html.indexOf("<!--enpcontent-->")+17, html.indexOf("<!--/enpcontent-->"));
		   content = content.replaceAll("<P>|<p>|</p>|</P>", "");
		   content = content.replaceAll("&nbsp;","\n");
	   }else if(url.contains("http://epaper.jinghua.cn/html")){ //京华时报
		   content = html.substring(html.indexOf("<!--enpcontent-->")+17, html.indexOf("<!--/enpcontent-->"));
		   content = content.replaceAll("<P>|<p>|</p>|</P>", "");
		   content = content.replaceAll("&nbsp;","\n");
	   }else if(url.contains("kb.dsqq.cn/html")){  //现代快报
		   content = html.substring(html.indexOf("<founder-content>")+17,html.indexOf("</founder-content>"));
		   content = content.replaceAll("<P>|<p>|</p>|</P>", "");
		   content = content.replaceAll("&nbsp;","\n");
	   };
//	   content = content.replaceAll("\\n", "");
//	   System.out.println(content);
	   return content;
   }
 /*
  * 新闻图片 图片名为：时间+后缀（比如：20140910.jpg）
  * 命名处理待改进
  * */
   public String handleImage(String html){
	 //图片配置：http://www.chinamil.com.cn/jfjbmap/ 
	   //正则表达式："IMG src=\"(.*?)res(.*?)attpic_brief.jpg\"" 路径表达式："http:\"?(.*?)(\"|>|\\s+)" 辅助："../../../"
//	   String url = "http://www.chinamil.com.cn/jfjbmap/";
//	   String imageurl = "IMG src=\"(.*?)res(.*?)attpic_brief.jpg\"";     //"img src=\"(.*?)res(.*?)attpic_brief.jpg\""
//	   String imagescr = "http:\"?(.*?)(\"|>|\\s+)";     //"http:\"?(.*?)(\"|>|\\s+)"
//	   String imageBuf = "../../../";
	   StringBuffer buf = new StringBuffer("");
	   StringBuffer load = new StringBuffer("C:\\Users\\Administrator\\git\\Spider\\Spider\\image\\");
	   StringBuffer symbol = new StringBuffer(";");
	   GetImage image = new GetImage(photoUrl,imageUrl,imageScr,imageBuf);    //图片命名正则表达式
	   image.fileName = handleTime(html).replaceAll("[^0-9]", "")+ nameSource;
//	   System.out.println(image.fileName);
	   Vector<String> dateSourceNumNum = image.getImage(html); 
	   for(String s: dateSourceNumNum){
		   buf = buf.append(load).append(new StringBuffer(s)).append(symbol);
	   }
	   if(buf.toString() == ""||buf.toString() == null)
		   buf = new StringBuffer("No Images");
	   return buf.toString();
	   
   }
   
  /*
  * 新闻pdf
  * */
   void handlePDF(String url){
	   
	   new GetPdf(url);
   }
   /*
    * 新闻发布时间
    * */
   String handleTime(String html){
	
	   if(bqDate[1].equals(""))
		   time = handle(html,bqDate[0]);
	   else
		   time = handle(html,bqDate[0],bqDate[1]);
//	   time = time.substring(0,12);  //只获取时间
	   time = time.replaceAll("[^0-9]", "");  //新闻晨报
	   if(url.contains("newspaper.jfdaily.com/xwcb"))
		   time = time.substring(0, 8);
	   else if(url.contains("http://gzdaily.dayoo.com/html")){
		   time = time.substring(0, 8);
		}else;
//	   System.out.println(time);
	   return time;
   }
  /*
   * 获取原始报社名称
   * 有待改进，目前无法改进啊。。。貌似有点麻烦
   * */
   String handleNewSource(String html){
	   
//	   newSource = handle(html,bqNewSource[0],bqNewSource[1]);
//	   if(newSource.length() >= 4)
//		   newSource = newSource.substring(0, 4);
//	   System.out.println(newSource);
//	   newSource = html ;
	   return bqNewSource[0];
   }
   /*
    * 新闻来源
    * */
   public String handleOriginalSource(String html) {
//	originalSource = handle(html,bqNewSource[0],bqNewSource[1]);
//	originalSource = html;
//	System.out.println(cgSource);
	return bqNewSource[0]+" ，"+bqNewSource[1];
}
   /*
    * 版面属性 "："之后 即为所需
    * */
   String handleCategroy(String html){
	   if(bqCategroy[1].equals(""))
		   categroy = handle(html ,bqCategroy[0]);
	   else
		   categroy = handle(html ,bqCategroy[0],bqCategroy[1]);
	   if(url.contains("http://e.chengdu.cn/")){ //商报
		   categroy = categroy.substring(categroy.lastIndexOf("：")+1, categroy.lastIndexOf("»")-1);
	   }
	   else if(url.contains("/www.chinamil.com.cn")){  //解放军报
		   categroy = categroy.substring(categroy.lastIndexOf("：")+1,categroy.lastIndexOf("P")) ;// categroy.lastIndexOf(" "));
	   }else if(url.contains("newspaper.jfdaily.com/xwcb")){  //新闻晨报
		   categroy = categroy.replaceAll("\\s*", "");
		   categroy = categroy.substring(categroy.indexOf("：")+1,categroy.indexOf("稿"));
	   }else if(url.contains("gzdaily.dayoo.com/html")){     //广州日报
		   
		   categroy = categroy.replaceAll("本版新闻", "");
	   }else if(url.contains("www.ycwb.com/ePaper/ycwb/html")){ //羊城晚报 
		   if(categroy.contains("："))
			   categroy = categroy.substring(categroy.indexOf("：")+1, categroy.indexOf("按日期检索"));
		   
	   }else if(url.contains("http://epaper.nandu.com/")){
		   
		   categroy = categroy.substring(categroy.indexOf("版名：")+3, categroy.indexOf("字号："));
	   }else if(url.contains("epaper.jinghua.cn/html")){
		   categroy = categroy.replaceAll("&gt;", "");
		   categroy = categroy.substring(18,categroy.length());
		  
	   };
	   
	   if(categroy.contains("："))
		   categroy = categroy.substring(categroy.indexOf("：")+1, categroy.length());
	   categroy = categroy.replaceAll("\\s*", "");
//	   System.out.println(categroy);
	   return categroy;        //.substring(categroy.lastIndexOf("：")+1,categroy.length());
	   
   }
 /*
  * 新闻原始类别
  * */
   String handleOriginalCategroy(String html){
	   
	   if(bqCategroy[1].equals(""))
		   originalCategroy = handle(html ,bqCategroy[0]);
	   else
		   originalCategroy = handle(html ,bqCategroy[0],bqCategroy[1]);
	   
	   if(url.contains("www.ycwb.com/ePaper/ycwb/html")){  //羊城晚报
		   originalCategroy = originalCategroy.replaceAll("\\s*", "");
		   originalCategroy = originalCategroy.substring(6, originalCategroy.indexOf("按日期检索"));
		   
	   }else  if(url.contains("epaper.jinghua.cn/html")){   //京华时报
		   categroy = categroy.replaceAll("&gt;", "");
		   
	   };
//	   if(originalCategroy.length() >= 19){
//		   originalCategroy = originalCategroy.substring(10, 19);
//		   originalCategroy = originalCategroy.replaceAll("\\s*", "");
//	   }
	   
	   originalCategroy = originalCategroy.replaceAll("\\s*", "");
//	   System.out.println(originalCategroy);
	   
	   return originalCategroy;
   }
   
   //获取新闻评论
   public String HandleComment(String html){
	   return null;
   }
   
   /*
    * 新闻相关内容的存储
    * 标题 时间  内容 报社名称
    * newsource 为创建的数据库名称 ；newtable 为创建的数据库表名
    * */
   public static void memory(String url,String[] bqtitle,String[] bqcontent,
   		String[] bqdate,String[] bqnewsource ,String[] bqcategroy ,String bqbuf ,String encode,String DBName ,String DBTable,
   		String photourl,String imageurl,String imagescr,String imagebuf){
	   
	   CRUT crut = new CRUT(DBName ,DBTable);
	   CDSB cdsb = new CDSB(url,bqtitle,bqcontent,bqdate,bqnewsource,bqcategroy,bqbuf,encode, photourl, imageurl, imagescr, imagebuf);
//	   if(cdsb.text != null){
//		System.out.println(cdsb.text);   
//	   System.out.println(url);
	   crut.add(cdsb.handleTitle(cdsb.text),cdsb.handleOriginalTitle(cdsb.text), cdsb.handleTitleContent(cdsb.text),
			   cdsb.handleTime(cdsb.text),cdsb.handleContent(cdsb.text),
			   cdsb.handleNewSource(cdsb.text), cdsb.handleOriginalSource(cdsb.text),
			   cdsb.handleCategroy(cdsb.text), cdsb.handleOriginalCategroy(cdsb.text),
			   cdsb.handleUrl(),cdsb.handleImage(cdsb.text));
	   crut =null;
	   cdsb =null;
//	   }
   }
  /*
   * 重载函数，增加新闻评论
   * */
//   public static void memory(String url,String[] bqtitle,String[] bqcontent,
//	   		String[] bqdate,String[] bqnewsource ,String[] bqcategroy ,String bqbuf ,String encode,String DBName ,String DBTable,
//	   		String photourl,String imageurl,String imagescr,String imagebuf){
//		   
//		   CRUT crut = new CRUT(DBName ,DBTable);
//		   CDSB cdsb = new CDSB(url,bqtitle,bqcontent,bqdate,bqnewsource,bqcategroy,bqbuf,encode, photourl, imageurl, imagescr, imagebuf);
////		   if(cdsb.text != null){
////			System.out.println(cdsb.text);   
////		   System.out.println(url);
//		   crut.add(cdsb.handleTitle(cdsb.text),cdsb.handleOriginalTitle(cdsb.text), cdsb.handleTitleContent(cdsb.text),
//				   cdsb.handleTime(cdsb.text),cdsb.handleContent(cdsb.text),
//				   cdsb.handleNewSource(cdsb.text), cdsb.handleOriginalSource(cdsb.text),
//				   cdsb.handleCategroy(cdsb.text), cdsb.handleOriginalCategroy(cdsb.text),
//				   cdsb.handleUrl(),cdsb.handleImage(cdsb.text));
//		   crut =null;
//		   cdsb =null;
////		   }
//	   }

	public static void main(String[] args) throws Exception {
    	/*
    	 * String url,String[] bqtitle,String[] bqcontent,
   		String[] bqdate,String[] bqnewsource ,String[] bqcategroy ,String bqbuf ,String newsource ,String newtable
    	 * */
//    	String url3 = "http://e.chengdu.cn/html/2014-09/10/content_487767.htm";
//    	String url2 = "http://paper.people.com.cn/rmrb/html/2014-09/05/nw.D110000renmrb_20140905_1-01.htm";
//    	String url1 = "http://e.chengdu.cn/html/2014-10/20/content_493712.htm";
//    	String url2 = "http://www.wccdaily.com.cn/shtml/hxdsb/20141021/251241.shtml";
//    	
//    	String url3 = "http://epaper.ynet.com/html/2014-11/05/content_94803.htm?div=-1";
//    	String url4 = "http://bjwb.bjd.com.cn/html/2014-11/03/content_229916.htm";
//    	String url5 = "http://bjwb.bjd.com.cn/html/2014-11/06/content_231000.htm";
//    	String url6 = "http://www.cdwb.com.cn/html/2014-11/04/content_2140919.htm";
//    	String url7 = "http://www.cdrb.com.cn/html/2014-11/06/content_2141933.htm";
    	String url8 = "http://www.chinamil.com.cn/jfjbmap/content/2014-11/08/content_92414.htm";
//    	String url9 = "http://xmwb.xinmin.cn/html/2014-11/06/content_5_2.htm";
//    	String url10 = "http://newspaper.jfdaily.com/xwcb/html/2014-11/06/content_33941.htm";
//    	String url11 = "http://newspaper.jfdaily.com/xwcb/html/2014-11/06/content_33936.htm";
//    	String url12 = "http://gzdaily.dayoo.com/html/2014-11/06/content_2790788.htm";
//    	String url13 = "http://www.ycwb.com/ePaper/ycwb/html/2014-11/06/content_574483.htm?div=-1";
//    	String url14 = "http://www.ycwb.com/ePaper/ycwb/html/2014-09/29/content_550830.htm?div=-1";
//    	String url15 = "http://epaper.nandu.com/epaper/A/html/2014-11/06/content_3339541.htm?div=-1";
//    	String url16 = "http://epaper.jinghua.cn/html/2014-11/07/content_142969.htm";
//    	String url17 = "http://kb.dsqq.cn/html/2014-11/07/content_369572.htm";
//    	
//    	String[] s1 = {"",""};
//    	String[] s2 = {"id","ozoom"};
//    	String s3 = "utf-8";  //= "gb2312"; //gb2312 utf-8
    	String[] bqtitle = {"style","line-height:140%;"};
		String[] bqcontent = {"id","ozoom"};
		String[] bqdate = {"height","25"};
//		String[] bqnewsource = {"解放军报","....."};
		String[] bqcategroy ={"class","info"};
		String bqbuf ="";
		String encode = "utf-8";
//		String DBName = "jfjb";
//		String DBTable = "cg";
		String photourl = "http://www.chinamil.com.cn/jfjbmap/";
		String imageurl = "IMG src=\"(.*?)res(.*?)attpic_brief.jpg\"";     //"img src=\"(.*?)res(.*?)attpic_brief.jpg\""
		String imagescr = "http:\"?(.*?)(\"|>|\\s+)";     //"http:\"?(.*?)(\"|>|\\s+)"
		String imageBuf = "../../../";
    	CDSB T = new CDSB(url8,bqtitle,bqcontent,bqdate,bqcategroy,bqcategroy,bqbuf,encode,photourl,imageurl,imagescr,imageBuf);
//    	T.memory(url8, bqtitle, bqcontent, bqdate, bqnewsource, bqcategroy, bqbuf, DBName, DBTable, encode,photourl,imageurl,imagescr,imageBuf);
//    	System.out.println(T.text);
    	T.handleCategroy(T.text);
//    	System.out.println(T.text);
//    	T.handleOriginalTitle(T.text);
//    	memory(url1);
    	
//    	String s = "sfsafsa98u8swf8i98wufwe";
//    	System.out.println(s.replaceAll("[^0-9]", ""));
    	
//    	memory(url1);
//    	CDSB test = new CDSB(url1);	
//    	test.handleOfficeName(test.text);
//    	CRUT crut = new CRUT();
//    	crut.add("2014",test.handleTitle(test.text),test.handleContent(test.text),"cdsb");

    }
}
