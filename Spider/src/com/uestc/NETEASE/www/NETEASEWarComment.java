package com.uestc.NETEASE.www;
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
import com.uestc.spider.www.CRUT;
public class NETEASEWarComment {

	private String url;
	private String html;
	private String[] label ;
	private String ENCODE;
	//获取新闻评论
	public String findNewsComment(String url ,String html ,String[] label) {
		
		/*
		 * 先判断新闻类型 再做决定
		 * */
		NETEASEFocus focus = new NETEASEFocus();
		String categroyBuf ="";
		if(label[1].equals("")){
			categroyBuf = focus.HandleHtml(html , label[0]);
		}else{
			categroyBuf = focus.HandleHtml(html , label[0],label[1]);
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
//			//如果网页失效 直接返回null
//			if(categroyBuf == ""){ 
//				System.out.println("纳尼！！！");
//				return null;
//			}
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
//				e1.printStackTrace();
			System.out.println("what is the fuck!!!");
			return null;
		}
					
        WebClient wc=new WebClient();
        WebRequest request=new WebRequest(link); 
        request.setCharset(ENCODE);
//	        其他报文头字段可以根据需要添加
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
//				e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
//				e.printStackTrace();
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
//	        System.out.println(ss);
        result = new StringBuffer(ss);
        ss = null; 
        content = content.substring(0, content.indexOf("文明社会，从理性发贴开始。谢绝地域攻击。"));
        content = content.replaceAll("\\s+", "");
        String commentReg = "发表(.*?)顶";
//	        String source = "发表哈哈哈啊哈顶顶顶顶发表家具啊姐姐顶发表哈哈哈顶发表。。。。顶发表；；；；顶发表【【】。；；；顶发表。。、；匹配顶发表(.*?)顶";
        Pattern newPage = Pattern.compile(commentReg);
        
        Matcher themeMatcher = newPage.matcher(content);
        while(themeMatcher.find()){
        	String mm = themeMatcher.group();
        	mm = mm.replaceAll("发表", "");
        	mm = mm.replaceAll("顶", "");
//	        	System.out.println(mm);
        	result = result.append(mm).append("☆");
        	mm = null;
        }
		commentReg = null ;
		content = null;
		return result.toString();
	}
}
