package com.uestc.spider.www;

import java.util.Queue;

/*
 * �������ַ��� 
 * */
public interface FindLinks {

	public Queue<String> findThemeLinks(String themeLink ,String themeLinkReg) ; //��ȡ��������
	
	public Queue<String> findContentLinks(Queue<String> themeLink ,String ContentLinkReg) ;  //��ȡ��������
	
	public String findNewsTitle(String html) ; //��ȡ���ű���
	
	public String findNewsOriginalTite(String html) ; //��ȡ����ԭʼ����
	
	public String findNewsContent(String html) ;    //��ȡ��������
	
	public String findNewsImages(String html);     //��ȡ����ͼƬ
	 
	public String findNewsTime(String html) ;        //��ȡ���ŷ���ʱ��
	
	public String findNewsSource() ;           //������Դ
	
	public String findNewsOriginalSource() ;     //���ž�����Դ
	 
	public String findNewsCategroy(String html) ;  //���Ű�������
	
	public String findNewsOriginalCategroy(String html); //���ž����������
	
	public String findNewsComment(String url) ;         //��ȡ��������
	
	public void handle();                              //�����б�ǩ�������ݿ�
	
	
	
}
