package com.uestc.spider.www;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolHandleNews{

	private static int queueDeep = 20 ;
	
	public void createThreadPool(){
		/*   
         * 创建线程池，最小线程数为10，最大线程数为50，线程池维护线程的空闲时间为30秒，   
         * 使用队列深度为20的有界队列，如果执行程序尚未关闭，则位于工作队列头部的任务将被删除，   
         * 然后重试执行程序（如果再次失败，则重复此过程），里面已经根据队列深度对任务加载进行了控制。   
         */ 
		ThreadPoolExecutor cg = new ThreadPoolExecutor(6,12,3,TimeUnit.SECONDS,new ArrayBlockingQueue<Runnable>(queueDeep),  
                new ThreadPoolExecutor.DiscardOldestPolicy());
		/*
		 * 手动一个个的加入报社配置，比较繁琐
		 * */
	/*	for(int i = 0 ; i < 50 ; i++){
			
			try{
				Thread.sleep(2);
			}catch(InterruptedException e){
				System.out.println("我失眠了。");
			}
			while(getQueueSize(cg.getQueue()) >= queueDeep){
				
				System.out.println("队列已满，等待3秒再添加任务");
				try{
					Thread.sleep(3000);
				}catch(InterruptedException e){
					System.out.println("我失眠了。");
				}
			}
			
		}
		*/
		//北京青年报 新闻标题：h1  内容：class="contnt" 日期：class="fst" 新闻来源：北京青年报  新闻分类：id=PageLink，待处理字符串为""
		//图片配置src="../../../images/2013-10/30/B02/gly3a1262_b.jpg"，image正则表达："IMG src=\"(.*?)iamges(.*?)_b.jpg\"" 路径表达式："http:\"?(.*?)(\"|>|\\s+)"辅助字符串"http://epaper.ynet.com"
		//主题链接：http://epaper.ynet.com/html/[0-9]{4}-[0-9]{2}/[0-9]{2}/node_[0-9]{4,5}.htm 内容链接：http://epaper.ynet.com/html/[0-9]{4}-[0-9]{2}/[0-9]{2}/content_[0-9]{5,6}.htm?div=-1
		//s1 = http://epaper.ynet.com/html/ s2 = - s3 = / s4 = /node_1331.htm
		//链接需要重新配置！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！
//		TaskThreadPool ynet = new TaskThreadPool("A","cg",new String[]{"h1",""},new String[]{"class","contnt"},new String[]{"class","fst"},new String[]{"北京青年报","北京青年报新闻热线：65902020; 广告热线：400-188-8610;小红帽”发行热线：6775-6666;北青网新闻热线：65901660"},new String[]{"id","PageLink"},"","utf-8",
//				"http://epaper.ynet.com/","IMG src=\"(.*?)iamges(.*?)_b.jpg\"","http:\"?(.*?)(\"|>|\\s+)","../../../",
//				"http://epaper.ynet.com/html/[0-9]{4}-[0-9]{2}/[0-9]{2}/node_[0-9]{4,5}.htm","http://epaper.ynet.com/html/[0-9]{4}-[0-9]{2}/[0-9]{2}/content_[0-9]{5,6}.htm?div=-1","http://epaper.ynet.com/","-","/","/node_1331.htm");        //0,"",new String[]{"",""}
//		
//		cg.execute(ynet); //青年报加入线程池
		
		//北京晚报：新闻标题：title 内容(已经搞定)：id=ozoom 日期：width="316" 来源 北京晚报 新闻分类：width="145" 待处理字符：""
		//图片配置：(http://bjwb.bjd.com.cn/)!images/2014-11/03/10/wjh4b24_b.jpg image正则表达式："IMG src=\"(.*?)iamges(.*?)_b.jpg\""路径表达式："http:\"?(.*?)(\"|>|\\s+)" 辅助字符串"../../../"
		//主题链接：http://bjwb.bjd.com.cn/html/[0-9]{4}-[0-9]{2}/[0-9]{2}/node_[0-9]{4,5}.htm 内容链接：http://bjwb.bjd.com.cn/html/[0-9]{4}-[0-9]{2}/[0-9]{2}/content_[0-9]{5,7}.htm
		//s1 = http://bjwb.bjd.com.cn/ s2  = - s3 = / s4 = /node_82.htm
//		TaskThreadPool bjwb = new TaskThreadPool("B","cg",new String[]{"title",""},new String[]{"id","ozoom"},new String[]{"width","316"},new String[]{"北京晚报","京报集团所属报刊；本网站所有内容属北京日报报业集团所有，未经许可不得转载"},new String[]{"width","145"},"","utf-8",
//				"http://bjwb.bjd.com.cn/","IMG src=\"(.*?)images(.*?)_b.jpg\"","http:\"?(.*?)(\"|>|\\s+)","../../../",
//				"http://bjwb.bjd.com.cn/html/[0-9]{4}-[0-9]{2}/[0-9]{2}/node_[0-9]{1,5}.htm","http://bjwb.bjd.com.cn/html/[0-9]{4}-[0-9]{2}/[0-9]{2}/content_[0-9]{5,7}.htm","http://bjwb.bjd.com.cn/","-","/","/node_82.htm");
//		cg.execute(bjwb);
//		
//		//成都商报：新闻标题：title ，内容 id = ozoom ,日期：class="header-today" 来源：成都商报 新闻分类："width","57%";待处理字符: " - 成都商报|成都商报电子版|成都商报官方网站"
//		//图片配置：http://e.chengdu.cn/html/ 正则表达式："img src=\"(.*?)res(.*?)attpic_brief.jpg\"" 路径表达式："http:\"?(.*?)(\"|>|\\s+)" 辅助字符串："../../../"
//		//主题链接"http://e.chengdu.cn/html/[0-9]{4}-[0-9]{2}/[0-9]{2}/node_[0-9]{1,2}.htm" 内容链接："http://e.chengdu.cn/html/[0-9]{4}-[0-9]{2}/[0-9]{2}/content_[0-9]{1,6}.htm"
//		//s1 = http://e.chengdu.cn/html/ s2 - s3 / s4 /node_2.htm
		TaskThreadPool cdsb = new TaskThreadPool("C","cg",new String[]{"title",""},new String[]{"id","ozoom"},new String[]{"class","header-today"},new String[]{"成都商报","成都商报网站: http://e.chengdu.cn | 成都商报刊号: CN51-0073成都商报新闻热线: 86612222  "},new String[]{"width","57%"},"- 成都商报|成都商报电子版|成都商报官方网站","utf-8",
				"http://e.chengdu.cn/html/","img src=\"(.*?)res(.*?)attpic_brief.jpg\"","http:\"?(.*?)(\"|>|\\s+)","../../../",
				"http://e.chengdu.cn/html/[0-9]{4}-[0-9]{2}/[0-9]{2}/node_[0-9]{1,2}.htm","http://e.chengdu.cn/html/[0-9]{4}-[0-9]{2}/[0-9]{2}/content_[0-9]{1,6}.htm","http://e.chengdu.cn/html/","-","/","/node_2.htm");
		cg.execute(cdsb);
//		
//		//成都晚报:新闻标题class="bt1" 内容："class","M_m_cont",日期："style","display:none" 来源：成都晚报 新闻分类(单一新闻看不出具体分类)：class="info" ，待处理字符；"" 
//		//图片配置：http://www.cdwb.com.cn/html/ 正则表达式："IMG src=\"(.*?)res(.*?)attpic_brief.jpg\"" 路径："http:\"?(.*?)(\"|>|\\s+)" 辅助："../../../"
//		//主题链接：http://www.cdwb.com.cn/html/[0-9]{4}-[0-9]{2}/[0-9]{2}/node_[0-9]{2,4}.htm ；内容http://www.cdwb.com.cn/html/[0-9]{4}-[0-9]{2}/[0-9]{2}/content_[0-9]{6,8}.htm
//		//s1 = http://www.cdwb.com.cn/html/ s2 - s3 / s4 /node_282.htm
//		TaskThreadPool cdwb = new TaskThreadPool("D","cg",new String[]{"class","bt1"},new String[]{"class","M_m_cont"},new String[]{"style","display:none"},new String[]{"成都晚报","新闻热线：962111  \\n 官方微博：http://weibo.com/cdwbwb \\n 订报热线：028-86741226 广告热线：028-86746906 \\n 报社地址：成都红星路二段159号(邮编:610017);成都电子版版权所有：成都晚报社 广告经营许可证号：5101034000060"},new String[]{"class","info"},"","utf-8",
//				"http://www.cdwb.com.cn/html/","IMG src=\"(.*?)res(.*?)attpic_brief.jpg\"","http:\"?(.*?)(\"|>|\\s+)","../../../",
//				"http://www.cdwb.com.cn/html/[0-9]{4}-[0-9]{2}/[0-9]{2}/node_[0-9]{2,4}.htm","http://www.cdwb.com.cn/html/[0-9]{4}-[0-9]{2}/[0-9]{2}/content_[0-9]{6,8}.htm","http://www.cdwb.com.cn/html/","-","/","/node_282.htm");
//		cg.execute(cdwb);
//		
//		//成都日报：新闻标题:(有小问题)     内容:  id= ozoom   日期： 暂不确定 分类：width=136 来源：成都日报   
//		
//		//解放军报：新闻标题："style","line-height:140%;" 内容 id= ozoom 日期：height="25" 来源：解放军报  分类：class=info 待处理字符""
//		//图片配置：http://www.chinamil.com.cn/jfjbmap/ 正则表达式："IMG src=\"(.*?)res(.*?)attpic_brief.jpg\"" 路径表达式："http:\"?(.*?)(\"|>|\\s+)" 辅助："../../../"
//		//主题链接：http://www.chinamil.com.cn/jfjbmap/content/[0-9]{4}-[0-9]{2}/[0-9]{2}/node_[0-9]{1,3}.htm,内容：http://www.chinamil.com.cn/jfjbmap/content/[0-9]{4}-[0-9]{1,2}/[0-9]{2}/content_[0-9]{4,6}.htm
//		//s1 = http://www.chinamil.com.cn/jfjbmap/content/ s2 = - s3 =/ s4 = /node_2.htm
//		TaskThreadPool jfjb = new TaskThreadPool("E","cg",new String[]{"style","line-height:140%;"},new String[]{"id","ozoom"},new String[]{"height","25"},new String[]{"解放军报","凡本网注明“来源：中国军网”或“解放军报 ** 电/讯”的文字、图片和音视频作品，版权均属中国军网所有，任何媒体、网站或个人未经本网书面授权不得转载、链接、转贴或以其他方式使用；已经本网书面授权的，在使用时必须注明“来源：中国军网”。"},new String[]{"class","info"},"","utf-8",
//				"http://www.chinamil.com.cn/jfjbmap/","IMG src=\"(.*?)res(.*?)attpic_brief.jpg\"","http:\"?(.*?)(\"|>|\\s+)","../../../",
//				"http://www.chinamil.com.cn/jfjbmap/content/[0-9]{4}-[0-9]{2}/[0-9]{2}/node_[0-9]{1,3}.htm","http://www.chinamil.com.cn/jfjbmap/content/[0-9]{4}-[0-9]{1,2}/[0-9]{2}/content_[0-9]{4,6}.htm","http://www.chinamil.com.cn/jfjbmap/content/","-","/","/node_2.htm");
//		cg.execute(jfjb);
//		
//		//新民晚报  标题：title 内容：id =ozoom  日期：span 来源：新民晚报 分类:"class","leftrq" 待处理："新民晚报数字报-"
//		//图片配置：http://xmwb.xinmin.cn/ 正则表达式："img src=\"(.*?)resfile(.*?)_b.jpg\"" 路径表达式："http:\"?(.*?)(\"|>|\\s+)" 辅助："../../../"
//		//主题链接：http://xmwb.xinmin.cn/html/[0-9]{4}-[0-9]{2}/[0-9]{2}/node_[0-9]{1,3}.htm 内容：http://xmwb.xinmin.cn/html/[0-9]{4}-[0-9]{2}/[0-9]{2}/content_[0-9]{1,2}_[0-9]{2}.htm
//		//s1= http://xmwb.xinmin.cn/html/ s2 - s3 -/ s4 /node_1.htm
//		TaskThreadPool xmwb = new TaskThreadPool("F","cg",new String[]{"title",""},new String[]{"id","ozoom"},new String[]{"span",""},new String[]{"新民晚报","以《新民晚报》强大的资源为依托，融新闻资讯、时尚娱乐、视频直播、名人访谈、数字报纸、交互社区、电子商务为一体，具有图文、视听、互动特色的独家资讯，致力于做到“上海资讯，快速全面”的服务宗旨。"},new String[]{"class","leftrq"},"新民晚报数字报-","utf-8",
//				"http://xmwb.xinmin.cn/","img src=\"(.*?)resfile(.*?)_b.jpg\"","http:\"?(.*?)(\"|>|\\s+)","../../../",
//				"http://xmwb.xinmin.cn/html/[0-9]{4}-[0-9]{2}/[0-9]{2}/node_[0-9]{1,3}.htm","http://xmwb.xinmin.cn/html/[0-9]{4}-[0-9]{2}/[0-9]{2}/content_[0-9]{1,2}_[0-9]{2}.htm","http://xmwb.xinmin.cn/html/","-","/","/node_1.htm");
//		cg.execute(xmwb);
//		
//		//新闻晨报(特殊编码"gb2312") 标题：title 内容：class = content 日期：h5 来源：新闻晨报 分类：h5 待处理  :""
//		//图片配置：http://newspaper.jfdaily.com/xwcb/resfiles/2014-11/06/l_33941_A0520141106S_3.jpg 正则表达式："img src=\"(.*?)xwcb\resfiles(.*?).jpg\"" 路径："http:\"?(.*?)(\"|>|\\s+)" 辅助："../../../"
//		//主题链接：http://newspaper.jfdaily.com/xwcb/html/[0-9]{4}-[0-9]{2}/[0-9]{2}/node_[0-9]{1,3}.htm 内容链接：http://newspaper.jfdaily.com/xwcb/html/[0-9]{4}-[0-9]{2}/[0-9]{2}/content_[0-9]{4,6}.htm
//		//s1 =http://newspaper.jfdaily.com/xwcb/html/ s2 - s3 / s4 /node_2.htm
////		TaskThreadPool xwcb = new TaskThreadPool("xwcb","cg",new String[]{"title",""},new String[]{"class","content"},new String[]{"h5",""},new String[]{"新闻晨报","主办单位：解放日报报业集团，国内统一刊号：CN31-0070"},new String[]{"h5",""},"","gb2312",
////				"http://newspaper.jfdaily.com/","img src=\"(.*?)xwcb\resfiles(.*?).jpg\"","http:\"?(.*?)(\"|>|\\s+)","../../../",
////				"http://newspaper.jfdaily.com/xwcb/html/[0-9]{4}-[0-9]{2}/[0-9]{2}/node_[0-9]{1,3}.htm","http://newspaper.jfdaily.com/xwcb/html/[0-9]{4}-[0-9]{2}/[0-9]{2}/content_[0-9]{4,6}.htm","http://newspaper.jfdaily.com/xwcb/html/","-","/","/node_2.htm");
////		cg.execute(xwcb);
//		
//		//广州日报 标题：h3 内容："class","article" 日期："class","infor" 来源：广州日报 分类："class","title" 待处理：""
//		//图片配置：http://gzdaily.dayoo.com/ 正则表达式："IMG src=\"(.*?)res(.*?)attpic_brief.jpg\"" 路径："http:\"?(.*?)(\"|>|\\s+)" 辅助："../../../"
//		//主题链接：http://gzdaily.dayoo.com/html/[0-9]{4}-[0-9]{2}/[0-9]{2}/node_[0-9]{1,3}.htm 内容链接：http://gzdaily.dayoo.com/html/[0-9]{4}-[0-9]{2}/[0-9]{2}/content_[0-9]{6,8}.htm
//		//s1 = http://gzdaily.dayoo.com/html/ s2 - s3 / s4 /node_1.htm
//		TaskThreadPool gzrb = new TaskThreadPool("G","cg",new String[]{"h3",""},new String[]{"class","article"},new String[]{"class","infor"},new String[]{"广州日报","广州日报报业集团,报料热线:(020)81919191,版权所有 不得转载1999-2011©广州市交互式信息网络有限公司（大洋网）,经营许可证编号:粤B2-20040381信息网络传播视听节目许可证：1906152"},new String[]{"class","title"},"","utf-8",
//				"http://gzdaily.dayoo.com/","IMG src=\"(.*?)res(.*?)attpic_brief.jpg\"","http:\"?(.*?)(\"|>|\\s+)","../../../",
//				"http://gzdaily.dayoo.com/html/[0-9]{4}-[0-9]{2}/[0-9]{2}/node_[0-9]{1,3}.htm","http://gzdaily.dayoo.com/html/[0-9]{4}-[0-9]{2}/[0-9]{2}/content_[0-9]{6,8}.htm","http://gzdaily.dayoo.com/html/","-","/","/node_1.htm");
//		cg.execute(gzrb);
//		
//		//羊城晚报 标题：title 内容：id=ozoom 日期：class time 来源 羊城晚报 分类：div  待处理："_金羊网"
//		//图片配置：http://www.ycwb.com/ePaper/ycwb/ 正则表达式："IMG src=\"(.*?)res(.*?)attpic_brief.jpg\"" 路径："http:\"?(.*?)(\"|>|\\s+)" 辅助："../../../"
//		//主题链接：http://www.ycwb.com/ePaper/ycwb/html/[0-9]{4}-[0-9]{2}/[0-9]{2}/node_[0-9]{3,4}.htm 内容http://www.ycwb.com/ePaper/ycwb/html/[0-9]{4}-[0-9]{2}/[0-9]{2}/content_[0-9]{5,7}.htm?div=-1
//		//s1 =http://www.ycwb.com/ePaper/ycwb/html/ s2 - s3 / s4 /node_2081.htm
//		TaskThreadPool ycwb = new TaskThreadPool("H","cg",new String[]{"title",""},new String[]{"id","ozoom"},new String[]{"class","time"},new String[]{"羊城晚报","粤B2-20040141 新出网证(粤)字022号 信息网络传播视听节目许可证:1910522 版权所有 [羊城晚报报业集团] 广东羊城晚报数字媒体有限公司"},new String[]{"div",""},"_金羊网","utf-8",
//				"http://www.ycwb.com/ePaper/ycwb/","IMG src=\"(.*?)res(.*?)attpic_brief.jpg\"","http:\"?(.*?)(\"|>|\\s+)","../../../",
//				"http://www.ycwb.com/ePaper/ycwb/html/[0-9]{4}-[0-9]{2}/[0-9]{2}/node_[0-9]{3,4}.htm","http://www.ycwb.com/ePaper/ycwb/html/[0-9]{4}-[0-9]{2}/[0-9]{2}/content_[0-9]{5,7}.htm?div=-1","http://www.ycwb.com/ePaper/ycwb/html/","-","/","/node_2081.htm");
//		cg.execute(ycwb);
//	
//		//南方都市报 标题：title 内容"class","content BSHARE_POP" 日期：id="pubtime_baidu" 来源：南方都市报  分类："class","info" 待处理："_南方都市报数字报TT"
//		//图片链接：http://epaper.nandu.com/epaper/A/res/2014-11/06/AA19/res03_attpic_brief.jpg 正则表达式："IMG src=\"(.*?)res(.*?)attpic_brief.jpg\""路径："http:\"?(.*?)(\"|>|\\s+)" 辅助："../../../"
//		//主题链接：http://epaper.nandu.com/epaper/A/html/[0-9]{4}-[0-9]{2}/[0-9]{2}/node_[0-9]{3,5}.htm 内容http://epaper.nandu.com/epaper/A/html/[0-9]{4}-[0-9]{2}/[0-9]{2}/content_[0-9]{6,8}.htm?div=-1
//		//s1 http://epaper.nandu.com/epaper/A/html/ s2 - s3 / s4 /node_2731.htm
//		
//		TaskThreadPool nandu = new TaskThreadPool("I","cg",new String[]{"title",""},new String[]{"class","content BSHARE_POP"},new String[]{"id","pubtime_baidu"},new String[]{"南方都市报","ICP证粤B2-20040112 | 广播电视节目制作经营许可证 | 互联网新闻许可证 | 信息网络传播视听节目许可证 | 网络文化经营许可证 深圳总机：0755-88351896 88351986  深圳广告热线：0755-36860181  广州广告热线：020-87366649"},new String[]{"class","info"},"_南方都市报数字报TT","utf-8",
//				"http://epaper.nandu.com/epaper/A/","IMG src=\"(.*?)res(.*?)attpic_brief.jpg\"","http:\"?(.*?)(\"|>|\\s+)","../../../",
//				"http://epaper.nandu.com/epaper/A/html/[0-9]{4}-[0-9]{2}/[0-9]{2}/node_[0-9]{3,5}.htm","http://epaper.nandu.com/epaper/A/html/[0-9]{4}-[0-9]{2}/[0-9]{2}/content_[0-9]{6,8}.htm?div=-1","http://epaper.nandu.com/epaper/A/html/","-","/","/node_2731.htm");
//		cg.execute(nandu);
//		
//		//京华时报 :标题：h1 内容："" "" 日期：class time 来源：京华时报 分类（具体分类暂时无法搞定）："class","nav_c" 待处理：""
//		//图片链接：http://epaper.jinghua.cn/ !!images/2014-11/07/017/p1_b.jpg 正则表达式："img src=\"(.*?)images(.*?)_b.jpg\"" 路径："http:\"?(.*?)(\"|>|\\s+)" 辅助："../../../"
//		//主题链接：http://epaper.jinghua.cn/html/[0-9]{4}-[0-9]{2}/[0-9]{2}/node_[0-9]{2,4}.htm 内容链接：http://epaper.jinghua.cn/html/[0-9]{4}-[0-9]{2}/[0-9]{2}/content_[0-9]{5,7}.htm
//		//s1 = http://epaper.jinghua.cn/html/ s2 - s3 / s4 /node_100.htm
//		TaskThreadPool jinghua = new TaskThreadPool("J","cg",new String[]{"h1",""},new String[]{"",""},new String[]{"class","time"},new String[]{"京华时报","电信与信息服务业务经营许可证070686号 | 电信业务审批[2007]字第433号 | 视听节目许可证0108269号 | 广播电视节目制作经营许可证京字693号 | 国新办网备字[2006]1号,短信息类服务接入代码使用证书 京号【2009】12001-B011 | 京公网安备110105000301号"},new String[]{"class","nav_c"},"","utf-8",
//				"http://epaper.jinghua.cn/","img src=\"(.*?)images(.*?)_b.jpg\"","http:\"?(.*?)(\"|>|\\s+)","../../../",
//				"http://epaper.jinghua.cn/html/[0-9]{4}-[0-9]{2}/[0-9]{2}/node_[0-9]{2,4}.htm","http://epaper.jinghua.cn/html/[0-9]{4}-[0-9]{2}/[0-9]{2}/content_[0-9]{5,7}.htm","http://epaper.jinghua.cn/html/","-","/","/node_100.htm");
//		cg.execute(jinghua);
		
		//现代快报 主题：id=mp369460 内容：
		
		cg.shutdown();
		
		
		
		
		
		
		
		
	
	}
//	@SuppressWarnings("unused")
//	private synchronized int getQueueSize(@SuppressWarnings("rawtypes") Queue queue){
//		
//		return queue.size();
//	}
	
	public static void main(String[] args){
		
//		TaskThreadPool testPool = new TaskThreadPool();
//		Thread t = new Thread(testPool);
//		t.start();
//		testPool.run();
		
		ThreadPoolHandleNews test = new ThreadPoolHandleNews();
		test.createThreadPool();
	}
	
	
}

class TaskThreadPool implements Runnable{

	/**
	 * 很多需要初始化的东西，
	 * 好好想想
	 * 1.数据库 名称 表名
	 * 2.新闻：新闻标题标签Title[] 内容标签Content[] 日期标签date[]  新闻来源 newSource[] 新闻分类 categroy[] 新闻待处理字符串 bufString
	 * 3.图片：图片配置url ，image正则表达式：IMURL_REG 路径表达式：IMSRC_REG 链接辅助字符串 ：imageBuf
	 * 4.新闻主题链接：ThemeLink ,内容链接：contentLink 以及配置字符串
	 * 
	 * */
//	public int flag ;           //第几个线程
	
	public String DBName ;      //数据库名称
	public String DBTable ;     //数据库表名
	
	public String title[];      // 新闻标题
	public String content[];    //新闻内容
	public String date[] ;      //新闻日期
	public String newSource[] ; //新闻来源    //可固定
	public String categroy[] ;  //新闻分类
	public String bufString ;   //新闻待处理字符串
	public String ENCODE ;
	
	public String imageUrl ;        //图片配置url
	public String imurl_reg ;      //image正则表达式
	public String imscr_reg ;      //图片路径
	public String imageBuf ;       //图片链接辅助字符串
	
	public String themeLink ;       //主题链接正则表达式
	public String contentLink;     //内容链接正则表达式
	public String newurl1 ;        //链接填充内容 1,2,3,4
	public String newurl2 ;
	public String newurl3 ;
	public String newurl4 ;
	
	
//	public TaskThreadPool(){
//		
//	}
	
	//感觉这个构造函数好累啊
	
	public TaskThreadPool(String DBName ,String DBTable ,
			String title[],String content[],String date[],String newSource[] ,String categroy[] ,String bufString,String encode,
			String imageurl ,String imurl_reg ,String imsrc_reg,String imageBuf,
			String themeLink ,String contentLink,String newurl1,String newurl2,String newurl3 ,String newurl4){
		
//		this.flag = flag ;
		
		this.DBName = DBName ;
		this.DBTable = DBTable ;
		
		this.title = title ;
		this.content = content ;
		this.date = date ;
		this.newSource = newSource ;
		this.categroy = categroy ;
		this.bufString = bufString ;
		this.ENCODE = encode;
		
		this.imageUrl = imageurl ;
		this.imurl_reg = imurl_reg ;
		this.imscr_reg = imsrc_reg ;
		this.imageBuf = imageBuf ;
		
		this.themeLink = themeLink ;
		this.contentLink = contentLink ;
		this.newurl1 = newurl1 ;
		this.newurl2 = newurl2 ;
		this.newurl3 = newurl3 ;
		this.newurl4 = newurl4 ;
		
		
		
		
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		GetLink forOneDay = new GetLink(themeLink,contentLink,newurl1,newurl2,newurl3,newurl4);
		forOneDay.resultForOneDay(2014, 11, 30, title, content, date, newSource, categroy, bufString, ENCODE, DBName, DBTable,imageUrl,imurl_reg,imscr_reg,imageBuf);
//		forOneDay = null;
//		System.out.println(Thread.currentThread()+"xixi");
	}
	
	
}
