package com.uestc.spider.www;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Vector;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.filters.OrFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.nodes.TextNode;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.tags.TableTag;
import org.htmlparser.tags.TitleTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.htmlparser.visitors.HtmlPage;

/*
 * ��Գɶ��̱�����������
 * ��ȡ�ɶ��̱�һ�����������
 * ���Ű��� ������Ŀ������ʱ�䣬�������ݣ����������Լ���PDF��ʽ�������ļ�
 * �����ã���
 * */
public class CDSB implements Runnable {

	HttpURLConnection httpUrlConnection;
    InputStream inputStream;
    BufferedReader bufferedReader;
    String url;              		//Ҫ�����url
    String text = "";       		 //�洢url��html����
    String nameSource = "cdsb";       //������Դ

    public String title;  			//���ű���
    public String titleContent;     //�������ݱ���
    public String originalTitle;    //δ����ԭʼ����
    
    public String content;			//��������
    
    public String time;             //���ŷ���ʱ��
    
    public String newSource;       //������Դ
    public String originalSource ;       //δ����ԭʼ������Դ
    
    public String categroy ;            //�������
    public String originalCategroy ; //����ԭʼ����
    
    private String bqTitle[] = {"class","bt_title"};   //���ű�����ҳ��ǩ
    private String[] bqContent = {"class","bt_con"} ; // ����������ҳ��ǩ
    private String[] bqDate = {"class","riq"} ;     //ʱ���ǩ"class","header-today"
    private String[] bqNewSource ={"name","author"} ; //������Դ��ǩ
    private String[] bqCategroy = {"class","s_left"};
    private String bqBuf = "�������б�" ;              //�������ݣ���- �ɶ��̱�|�ɶ��̱����Ӱ�|�ɶ��̱��ٷ���վ �Լ�
    
    private String ENCODE = "gb2312";
    
    public int state = 0;
  
    public CDSB(String url) {
  
        try {
        	this.url = url;
//        	this.baseUrl = ;
        
        } catch (Exception e) {
        	
//        	e.printStackTrace();
        }
  
        try {
            httpUrlConnection = (HttpURLConnection) new URL(url).openConnection(); //��������
            state = httpUrlConnection.getResponseCode();
            httpUrlConnection.disconnect();
        } catch (MalformedURLException e) {
//            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
//            e.printStackTrace();
        }
  
//        System.out.println("---------start-----------");
        if(state == 200 ||state == 201){
        	try {
				httpUrlConnection = (HttpURLConnection) new URL(url).openConnection();
			} catch (MalformedURLException e1) {
				// TODO Auto-generated catch block
//				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
//				e1.printStackTrace();
			} //��������
        	Thread thread = new Thread(this);
        	thread.start();
        	try {
        		thread.join();
        	} catch (InterruptedException e) {
//        		e.printStackTrace();
        	}
        }else{
        	System.out.println("�޷�������������");
        }
  
//        System.out.println("----------end------------");
    }
  
    public void run() {
        // TODO Auto-generated method stub
        try {
            httpUrlConnection.setRequestMethod("GET");
        } catch (ProtocolException e) {
//            e.printStackTrace();
        }
  
        try {
            httpUrlConnection.setUseCaches(true); //ʹ�û���
            httpUrlConnection.connect();           //��������  ���ӳ�ʱ����
        } catch (IOException e) {
//            e.printStackTrace();
//        	continue;
        	System.out.println("�����ӷ��ʳ�ʱ...");
        }
  
        try {
            inputStream = httpUrlConnection.getInputStream(); //��ȡ������
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream, ENCODE)); 
            String string;
            StringBuffer sb = new StringBuffer();
            while ((string = bufferedReader.readLine()) != null) {
//            	System.out.println("xxxxxxx");
            	sb.append(string);
            	sb.append("\n");
            }
            text = sb.toString();
        } catch (IOException e) {
//            e.printStackTrace();
        } finally {
            try {
                bufferedReader.close();
                inputStream.close();
                httpUrlConnection.disconnect();
            } catch (IOException e) {
//                e.printStackTrace();
            	System.out.println("���ӹرճ�������...");
            }
  
        }
  
    }

 
   /*
     	ֻ��Ҫһ�������Ϳ����жϵı�ǩ������title 
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
   * ��Ҫ�������������ж�׼ȷ�����ݣ����� content-title ��ozoom��
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
  * ���ű���
  * */ 
 String handleOriginalTitle(String html){
	   originalTitle = handle(html,bqTitle[0],bqTitle[1]);
//	   title += handle(html,"class","content-title");
	   System.out.println(originalTitle);
	   return originalTitle;
   }
 /*
  * �������ݱ���
  * 
  * */
 String handleTitleContent(String html){
	 titleContent = handle(html,"class","content-title");
	 return titleContent;
 }
 String handleTitle(String html){
	 title = handle(html,bqTitle[0],bqTitle[1]);
	 if(title != null && title != "")
		 title = title.replace(bqBuf, "");
	 System.out.println(title);
	 return title;
 }
 String hanleUrl(){
	 return url;
 }
/*
 * ��������
 * */
   String handleContent(String html){
	   
	   content = handle(html,bqContent[0],bqContent[1]);
//	   content = content.replaceAll("\\n", "");
	   System.out.println(content);
	   return content;
   }
 /*
  * ����ͼƬ ͼƬ��Ϊ��ʱ��+��׺�����磺20140910.jpg��
  * ����������Ľ�
  * */
   public String handleImage(String html){
	   
	   StringBuffer buf = new StringBuffer("");
	   StringBuffer load = new StringBuffer("C:\\Users\\Administrator\\git\\spider\\Spider\\image\\");
	   StringBuffer symbol = new StringBuffer(";");
	   GetImage image = new GetImage();
	   image.fileName = handleTime(html).replaceAll("[^0-9]", "")+" "+ nameSource;
	   Vector<String> dateSourceNumNum = image.getImage(html); 
	   for(String s: dateSourceNumNum){
		   buf = buf.append(load).append(new StringBuffer(s)).append(symbol);
	   }
	   if(buf.toString() == ""||buf.toString() == null)
		   buf = new StringBuffer("No Images");
	   return buf.toString();
	   
   }
   
  /*
  * ����pdf
  * */
   void handlePDF(String url){
	   
	   new GetPdf(url);
   }
   /*
    * ���ŷ���ʱ��
    * */
   String handleTime(String html){
	
	   time = handle(html,bqDate[0],bqDate[1]);
//	   time = time.substring(0,12);  //ֻ��ȡʱ��
	   time = time.replaceAll("[^0-9]", "");
	   System.out.println(time);
	   return time;
   }
  /*
   * ��ȡԭʼ��������
   * �д��Ľ���Ŀǰ�޷��Ľ���������ò���е��鷳
   * */
   String handleNewSource(String html){
	   
	   newSource = handle(html,bqNewSource[0],bqNewSource[1]);
//	   if(newSource.length() >= 4)
//		   newSource = newSource.substring(0, 4);
	   System.out.println(newSource);
	   return bqBuf;
   }
   /*
    * ������Դ
    * */
   public String handleOriginalSource(String html) {
	// TODO Auto-generated method stub
	originalSource = handle(html,bqNewSource[0],bqNewSource[1]);
//	System.out.println(cgSource);
	return bqBuf;
}
   /*
    * ��������
    * */
   String handleCategroy(String html){
	   categroy = handle(html ,bqCategroy[0],bqCategroy[1]);
//	   if(categroy.length() >= 10){
//		   categroy = categroy.substring(9, 10);
//		   categroy = categroy.replaceAll("\\s*", "");
//		   categroy = categroy.substring(5,categroy.length());
//	   }

	   System.out.println(categroy);
	   return categroy.substring(categroy.lastIndexOf("��")+1,categroy.length());
	   
   }
 /*
  * ����ԭʼ���
  * */
   String handleOriginalCategroy(String html){
	   originalCategroy = handle(html ,bqCategroy[0],bqCategroy[1]);
//	   if(originalCategroy.length() >= 19){
//		   originalCategroy = originalCategroy.substring(10, 19);
//		   originalCategroy = originalCategroy.replaceAll("\\s*", "");
//	   }
	   return originalCategroy;
   }
   /*
    * ����������ݵĴ洢
    * ���� ʱ��  ���� ��������
    * */
   public static void memory(String url){
	   
	   CRUT crut = new CRUT();
	   CDSB cdsb = new CDSB(url);
//	   if(cdsb.text != null){
//		System.out.println(cdsb.text);   
	   System.out.println(url);
	   crut.add(cdsb.handleTitle(cdsb.text),cdsb.handleOriginalTitle(cdsb.text), cdsb.handleTitleContent(cdsb.text),
			   cdsb.handleTime(cdsb.text),cdsb.handleContent(cdsb.text),
			   cdsb.handleNewSource(cdsb.text), cdsb.handleOriginalSource(cdsb.text),
			   cdsb.handleCategroy(cdsb.text), cdsb.handleOriginalCategroy(cdsb.text),
			   cdsb.hanleUrl(),cdsb.handleImage(cdsb.text));
	   crut =null;
	   cdsb =null;
//	   }
   }
   
   

	public static void main(String[] args) throws Exception {
    	
//    	String url3 = "http://e.chengdu.cn/html/2014-09/10/content_487767.htm";
//    	String url2 = "http://paper.people.com.cn/rmrb/html/2014-09/05/nw.D110000renmrb_20140905_1-01.htm";
    	String url1 = "http://e.chengdu.cn/html/2014-10/16/content_493041.htm";
    	String url2 = "http://www.wccdaily.com.cn/shtml/hxdsb/20141021/251241.shtml";
    	String url3 = "http://www.wccdaily.com.cn/shtml/hxdsb/20141023/251684.shtml";
    	CDSB T = new CDSB(url3);
//    	System.out.println(T.text);
//    	T.handleOriginalTitle(T.text);
    	memory(url3);
    	
    	String s = "sfsafsa98u8swf8i98wufwe";
//    	System.out.println(s.replaceAll("[^0-9]", ""));
    	
//    	memory(url1);
//    	CDSB test = new CDSB(url1);	
//    	test.handleOfficeName(test.text);
//    	CRUT crut = new CRUT();
//    	crut.add("2014",test.handleTitle(test.text),test.handleContent(test.text),"cdsb");

    }
}
