package com.uestc.spider.www;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;


public class JustForToday extends Thread {
	Calendar now;
	private int year ;
	private int month ;
	private int date ;
	public JustForToday(){
		now = Calendar.getInstance();
		year = now.get(Calendar.YEAR);
		month = now.get(Calendar.MONTH)+1; //月份0-11
		date = now.get(Calendar.DATE)+1;
		System.out.println(year+" "+month+" "+date );
	}
	public void cdsbToday(String newthemelink ,String newcontentlink,String newurl1,String newurl2 ,String newurl3 ,String newurl4 ,
			String[] bqtitle,String[] bqcontent,String[] bqdate,String[] bqnewsource ,
			String[] bqcategroy ,String bqbuf,String encode ,String newsource ,String newtable,String photourl,String imageurl,String imagescr,String imagebuf){
		
		StringBuffer s1 = new StringBuffer();
		s1 = s1.append(newurl1).append(year).append(newurl2). 
				append(month).append(newurl3).append(date).append(newurl4); //newurl1 = "http://e.chengdu.cn/html/"
		System.out.println(s1);                                               //newurl2 = "-" newurl3 = "/"
		boolean flag = true ;                                                  //newurl4 = "/node_2.htm"
		int state = 0;
		while(flag){
			HttpURLConnection httpUrlConnection;
			try {
				httpUrlConnection = (HttpURLConnection) new URL(s1.toString()).openConnection();
				state = httpUrlConnection.getResponseCode();
				httpUrlConnection.disconnect();
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
//				e.printStackTrace();
				System.out.println("无法访问该网页，正在努力尝试连接！");
				continue ;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("无法访问该网页，正在努力尝试连接！");
				continue ;
//				e.printStackTrace();
			} //创建连接
			if(state ==200 || state ==201){

				flag = false ;
				try {
					new GetLink(newthemelink,newcontentlink,newurl1,newurl2,newurl3,newurl4).
					allWeWillDo(s1.toString(),bqtitle,bqcontent,bqdate,bqnewsource ,bqcategroy ,bqbuf,encode ,newsource , newtable,photourl,imageurl,imagescr,imagebuf);
				} catch (Exception e) {
					// TODO Auto-generated catch block
//					e.printStackTrace();
					return ;
				}
				return ;
			}
			
			try{
				sleep(5000);    //休息一会
				System.out.println("休息一会，继续访问...");
			}catch(InterruptedException e){
				continue;
			}
			
			
		}
		
		

	}
	public void hxdsbToday(){
		
	}
	
	public static void main(String[] args){
		
//		JustForToday today = new JustForToday();
//		today.cdsbToday();
	}
}
