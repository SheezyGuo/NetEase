package com.uestc.spider.www;

import java.io.File;
import java.net.UnknownHostException;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;

public class CRUT {

	static private Mongo mg  = null;
	static private DB db ;
	static private DBCollection users;
	
	public CRUT(){
		
		try {
			
            mg = new Mongo();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (MongoException e) {
            e.printStackTrace();
        }
		//��ȡcdsb DB�����Ĭ��û�д�����mongodb���Զ�����
		db = mg.getDB("cdsb");
		//��ȡusers DBCollection�����Ĭ��û�д�����mongodb���Զ�����
		users = db.getCollection("cg");
//		System.out.println("�ұ�ִ����");
	}
	//ɾ�����ݿ�
	public void destory() {
        if (mg != null)
            mg.close();
        mg = null;
        db = null;
        users = null;
        System.gc();
    }
	
	/*
	 * �������,��������,����s�ĳ��ȱ���Ϊż��
	 * ����Ϊ��������Ŀ���������ݣ��������ƣ�����ʱ��
	 * k-v:(name ,time) (content , office)
	 */
	public void add(String newName,String newTime ,String newContent,String newOffice){
		DBObject user = new BasicDBObject();
		user.put(newName, newTime);
		user.put(newContent, newOffice);
		users.insert(user);
//		System.out.println("111");
//		System.out.println(users.find(new BasicDBObject(newName, newTime)).toArray());
		
	}
	
	//���ͼƬ�Լ�PDF�ļ�
	public void add(File file){
		
		
	}
	//��ѯ���Բ�������Ŀ���������ݣ����ŷ���ʱ�䣬�������Ƶ�
	public void query(String key,String value){
	
		System.out.println(users.find(new BasicDBObject(key, value)).toArray());
		
	}
	
	//�鿴���ݿ�����������
    private void queryAll() {
    	DBCollection users = db.getCollection("users");
		System.out.println("��ѯusers���������ݣ�");
		//db�α�
		DBCursor cur = users.find();
		while (cur.hasNext()) {
			 System.out.println(cur.next());
			 
		}
		
    }
  //ɾ������
  	 public void remove(String key ,String value) {
  		    users.remove(new BasicDBObject(key, new BasicDBObject("$gte", value))).getN();
  		
  	 }
  	 
    public static void main(String args[]){
    	
    	CRUT test = new CRUT();
		for(String name:mg.getDatabaseNames())
			System.out.println(name);
		String url1 = "http://e.chengdu.cn/html/2014-09/10/content_487767.htm";
		CDSB test1 = new CDSB(url1);	
//		test.queryAll();
//    	test.destory();
//    	test.add("xixi", "2014.9.10", "��ʦ�ڿ���", "uestc");
//    	test.remove("xi","2014.9.10");
//    	test.add("xi","2014.9.10","jiaoshijiekuailfe","uestc");
    	test.query("2014",test1.handleTitle(test1.text));
    	test.query(test1.handleTitle(test1.text),"2014");
//    	test.query("xixi","2014.9.10");
    }
}
