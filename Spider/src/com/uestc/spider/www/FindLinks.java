package com.uestc.spider.www;

import java.util.Queue;

/*
 * �������ַ��� 
 * */
public interface FindLinks {

	public Queue<String> findThemeLinks(String themeLink) ; //��ȡ��������
	
	public Queue<String> findContentLinks(String themeLink) ;  //��ȡ��������
	
	public String findNewTitle(String html) ; //��ȡ���ű���
	
	public String findNewOriginalTite(String html) ; //��ȡ����ԭʼ����
	
	public String findNewContent(String html) ;    //��ȡ��������
	
	public String findNewImages(String html);     //��ȡ����ͼƬ
	 
	public String findNewTime(String html) ;        //��ȡ���ŷ���ʱ��
	
	public String findNewSource() ;           //������Դ
	
	public String findNewOriginalSource() ;     //���ž�����Դ
	 
	public String findNewCategroy(String html) ;  //���Ű�������
	
	public String findNewOriginalCategroy(String html); //���ž����������
	
	public String findNewComment(String url) ;         //��ȡ��������
	
	public void handle();                              //�����б�ǩ�������ݿ�
	
	
	
}
