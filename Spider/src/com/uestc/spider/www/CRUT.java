package com.uestc.spider.www;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;

public class CRUT {

	private Mongo mg  = null;
	private DB db ;
	private DBCollection users;
	private GridFS gd;
	private String DBName ;  // = "TODAY"; //数据库名称 华西都市报 成都商报等
	private String DBTable  ; //= "cg"; //数据库表名
	
	public CRUT(String dbname,String dbtable){
		
		this.DBName = dbname ;
		this.DBTable = dbtable;
		try {
			
            mg = new Mongo();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (MongoException e) {
            e.printStackTrace();
        }
		//获取cdsb DB；如果默认没有创建，mongodb会自动创建
		db = mg.getDB(DBName);
		//获取users DBCollection；如果默认没有创建，mongodb会自动创建
		users = db.getCollection(DBTable);
//connect reset!!! 需要处理一下	 根本不需要啊不需要啊
//		gd = new GridFS(db);
//		System.out.println("我被执行啦");
	}
	//删除数据库
	public void destory() {
        if (mg != null)
            mg.close();
        mg = null;
        db = null;
        users = null;
//        System.gc();
    }
	
	/*
	 * 添加数据,新闻内容,其中s的长度必须为偶数
	 * 内容为：新闻题目，新闻内容，报社名称，发布时间
	 * k-v:(name ,time) (content , office)
	 */
	public void add(String title,String originalTitle,String titleContent,
			String time ,String content,
			String newSource,String originalSource,
			String category,String originalCategroy,
			String url ,String image){
		DBObject user = new BasicDBObject();
		//三个标题：标题，内容标题，原始标题
		user.put("Title", title);
		user.put("OriginalTitle", originalTitle);
		user.put("TitleContent", titleContent);
		
		//发布时间
		user.put("Time", time);
		//新闻内容
		user.put("Content",content);
		//两个新闻来源 ：新闻来源，新闻原始来源
		user.put("NewSource",newSource);
		user.put("OriginalSource", originalSource);
		//两个新闻分类 ：类别 新闻原始类别
		user.put("Category", category);
		user.put("OriginalCategroy", originalCategroy);
		//新闻网址
		user.put("Url", url);
		//新闻图片
		user.put("image",image);

		users.insert(user);

		
	}
	//重载函数 新闻评论栏 
	public void add(String url,String commentNumber,String commentUrl,String[] comment){
		
		int lenth = 0 ;
		DBObject user = new BasicDBObject();
		//先判断是否存在该条评论
		if(!query("Url",url)){ //如果不存在
			//三个标题：标题，内容标题，原始标题
			user.put("Url", url);
			user.put("CommentUrl",commentUrl);
			user.put("CommentNumber", commentNumber);
			lenth = comment.length;
			for(int i = 0 ; i < lenth ; i++ ){
				user.put("Comment-"+i, comment[i]);
			}
		}else{
			
		}

		users.insert(user);

	}
	
	//查询可以查新闻题目，新闻内容，新闻发布时间，报社名称等
	public boolean query(String key,String value){
		
		boolean buf = false ;
		if(users.find(new BasicDBObject(key, value)).toArray() != null){
			buf = true ;
		}
		return buf;
		
	}
	
	//查看数据库中所有数据
    @SuppressWarnings("unused")
	private void queryAll() {
    	DBCollection users = db.getCollection("users");
		System.out.println("查询users的所有数据：");
		//db游标
		DBCursor cur = users.find();
		while (cur.hasNext()) {
			 System.out.println(cur.next());
			 
		}
		
    }
  //删除数据
  	 public void remove(String key ,String value) {
  		    users.remove(new BasicDBObject(key, new BasicDBObject("$gte", value))).getN();
  		
  	 }
  	 
    public static void main(String args[]){
    	
    	CRUT test = new CRUT("TODAY" ,"CG");
//		for(String name:mg.getDatabaseNames())
//			System.out.println(name);
//		String url1 = "http://e.chengdu.cn/html/2014-09/10/content_487767.htm";
//		CDSB test1 = new CDSB(url1);	
//		test.queryAll();
//    	test.destory();
//    	test.add("xixi", "2014.9.10", "教师节快乐", "uestc");
//    	test.remove("xi","2014.9.10");
//    	test.add("xi","2014.9.10","jiaoshijiekuailfe","uestc");
//		System.out.println(test1.handleTitle(test1.text)+"      gfhfhfg");
//		test.add(test1.handleTitle(test1.text),test1.handleTime(test1.text),test1.handleContent(test1.text),test1.handleOfficeName(test1.text),test1.handlePage(test1.text),url1);
    	test.query("Url","http://e.chengdu.cn/html/2014-10/16/content_493017.htm");
//    	test.query(test1.handleTitle(test1.text),"2014");
//    	test.query("xixi","2014.9.10");
    }
}
