package com.uestc.spider.www;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;

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

	static private Mongo mg  = null;
	static private DB db ;
	static private DBCollection users;
	static private GridFS gd;
	
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
		
		gd = new GridFS(db);
//		System.out.println("�ұ�ִ����");
	}
	//ɾ�����ݿ�
	public void destory() {
        if (mg != null)
            mg.close();
        mg = null;
        db = null;
        users = null;
//        System.gc();
    }
	
	/*
	 * �������,��������,����s�ĳ��ȱ���Ϊż��
	 * ����Ϊ��������Ŀ���������ݣ��������ƣ�����ʱ��
	 * k-v:(name ,time) (content , office)
	 */
	public void add(String title,String originalTitle,String titleContent,
			String time ,String content,
			String newSource,String originalSource,
			String category,String originalCategroy,
			String url ,String image){
		DBObject user = new BasicDBObject();
		//�������⣺���⣬���ݱ��⣬ԭʼ����
		user.put("Title", title);
		user.put("OriginalTitle", originalTitle);
		user.put("TitleContent", titleContent);
		
		//����ʱ��
		user.put("Time", time);
		//��������
		user.put("Content",content);
		//����������Դ ��������Դ������ԭʼ��Դ
		user.put("NewSource",newSource);
		user.put("OriginalSource", originalSource);
		//�������ŷ��� ����� ����ԭʼ���
		user.put("Category", category);
		user.put("OriginalCategroy", originalCategroy);
		//������ַ
		user.put("Url", url);
		//����ͼƬ
		user.put("image",image);

		users.insert(user);

		
	}
	
	//���ͼƬ�Լ�PDF�ļ� ȡ��ǰĿ¼�µ�image�ļ�������
	public void addFile(InputStream in,Object id){
		File filePath = new File(".\\image");
		String fileName[] = filePath.list();
		GridFSInputFile mongoFile = gd.createFile();
		for(int i = 0 ; i< fileName.length;i++){
			mongoFile.put("image"+i, fileName[i]);
			mongoFile.save();
		}
		
	}
	//��ȡ�ļ���д��������(��ǰ�ļ����µ�file�ļ�����)
	public void readFile(String filename) throws IOException{
		
		GridFSDBFile fileOut = gd.findOne(filename);
//		System.out.println(fileOut);
		fileOut.writeTo(".\\file"+filename);
	}
	
	//ɾ��ͼƬ
	public void deleteFile(String filename){
		
		gd.remove(gd.findOne(filename));
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
		System.out.println(test1.handleTitle(test1.text)+"      gfhfhfg");
//		test.add(test1.handleTitle(test1.text),test1.handleTime(test1.text),test1.handleContent(test1.text),test1.handleOfficeName(test1.text),test1.handlePage(test1.text),url1);
    	test.query("Title",test1.handleTitle(test1.text));
//    	test.query(test1.handleTitle(test1.text),"2014");
//    	test.query("xixi","2014.9.10");
    }
}
