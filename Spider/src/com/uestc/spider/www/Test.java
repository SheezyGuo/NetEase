package com.uestc.spider.www;

import java.io.File;
import java.net.UnknownHostException;
import java.util.Calendar;

/*
 * 1.先把华西都市报趴下来
 * 2.利用多线程爬取
 * 3.利用线程获取当天的新闻
 * 4.找到内存使用增加的原因！！
 * 
 * 2014.10.27
 * 1.实现多线程爬取：一个控制线程，多个爬取线程（每个线程负责一个报社新闻）
 * 
 * 2.尽可能的模板化
 * */
public class Test {
	
	
	 
	public static void main(String args[]) throws UnknownHostException{

//		String s = "        　　　　新华社北京10月23日电 中国共产党第十八届中央委员会第四次全体会议公报　　（2014年10月23日中国共产党第十八届中央委员会第四次全体会议通过）　　中国共产党第十八届中央委员会第四次全体会议，于2014年10月20日至23日在北京举行。　　出席这次全会的有，中央委员199人，候补中央委员164人。中央纪律检查委员会常务委员会委员和有关方面负责同志列席了会议。党的十八大代表中部分基层同志和专家学者也列席了会议。　　全会由中央政治局主持。中央委员会总书记习近平作了重要讲话。　　";
//		String s1 = "据交通部门预测，11月3日至12日单双号限行期间，每日自驾车出行的比例将减少";
////		s = s.replaceAll("        　　　　|　　|\\s*", "");
////		if(s.contains("　"))
//			s1 = s1.replaceAll("　+|\\s*", "");
//		System.out.println(s1);
//		File fileBuf = new File(".\\image","iloveu.jpg");
//		System.out.println(fileBuf.getAbsolutePath());
////		System.out.println("ss"+(1+1)+"www");
//		Calendar now = Calendar.getInstance();
//		System.out.println(now.get(Calendar.DATE));
		String[] s = null ;
		s[0] = "dd";
		System.out.print(s[0]);
	}
}
