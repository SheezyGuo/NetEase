package com.uestc.spider.www;
import java.io.File;  
import java.io.FileOutputStream;  
import java.io.InputStream;  
import java.net.URL;  
import java.net.URLConnection;  
import java.util.ArrayList;  
import java.util.List;  
import java.util.Vector;
import java.util.regex.Matcher;  
import java.util.regex.Pattern;  
  
/*** 
 * javaץȡ����ͼƬ 
 * @author uestc 
 * 
 */  
public class GetImage {  
  
    // ��ַ  
    public String URL ; 
    public String urll = "http://e.chengdu.cn/";  //������
    public String fileName ;
    // ����  
    private static final String ECODING = "UTF-8";  
    // ��ȡimg��ǩ����  
//    private static final String IMGURL_REG = "<img src=(.*?)[^>]*?>";
    private static final String IMGURL_REG = "img src=\"(.*?)res(.*?)attpic_brief.jpg\"";
    // ��ȡsrc·��������  
    private static final String IMGSRC_REG = "http:\"?(.*?)(\"|>|\\s+)";   //���޸�~~
//    private static final String IMGSRC_REG = "http://e.chengdu.cn/res/(.*?)_attpic_brief.jpg";
  
      
//    public static void main(String[] args) throws Exception { 
//    	String url = "http://e.chengdu.cn/html/2014-08/22/content_485002.htm";
//        GetImage cm = new GetImage();  
//        //���html�ı�����  
//        String HTML = cm.getHTML(url);  
//        //��ȡͼƬ��ǩ  
//        List<String> imgUrl = cm.getImageUrl(HTML);  
//        //��ȡͼƬsrc��ַ  
//        List<String> imgSrc = cm.getImageSrc(imgUrl);  
//        //����ͼƬ  
//        cm.Download(imgSrc);  
//    }  
      
//    public GetImage(String url){
//    	this.URL = url;
//    }  
    /*** 
     * ��ȡHTML���� 
     *  
     * @param url 
     * @return 
     * @throws Exception 
     */  
    private String getHTML(String url) throws Exception {  
        URL uri = new URL(url);  
        URLConnection connection = uri.openConnection();  
        InputStream in = connection.getInputStream();  
        byte[] buf = new byte[1024];  
        int length = 0;  
        StringBuffer sb = new StringBuffer();  
        while ((length = in.read(buf, 0, buf.length)) > 0) {  
            sb.append(new String(buf, ECODING));  
        }  
        in.close();  
        return sb.toString();  
    }  
  
    /*** 
     * ��ȡImageUrl��ַ 
     *  
     * @param HTML 
     * @return 
     */  
    private List<String> getImageUrl(String HTML) {  
        Matcher matcher = Pattern.compile(IMGURL_REG).matcher(HTML);  
        List<String> listImgUrl = new ArrayList<String>();  
        while (matcher.find()) {  
            listImgUrl.add(matcher.group());
//            System.out.println(matcher.group());
        }  
        return listImgUrl;  
    }  
  
    /*** 
     * ��ȡImageSrc��ַ 
     *  
     * @param listImageUrl 
     * @return 
     */  
    private List<String> getImageSrc(List<String> listImageUrl) {  
        List<String> listImgSrc = new ArrayList<String>();  
        for (String image : listImageUrl) {
        	
        	if(image.contains("../../../"))
        		image = image.replace("../../../",urll );
//        	System.out.println(image+"222");
            Matcher matcher = Pattern.compile(IMGSRC_REG).matcher(image);  
            while (matcher.find()) { 
//            	System.out.println(matcher.group());
                listImgSrc.add(matcher.group().substring(0, matcher.group().length() - 1));  
            }  
        }  
        return listImgSrc;  
    }  
  
    /*** 
     * ����ͼƬ 
     *  
     * @param listImgSrc 
     */  
    private Vector<String> Download(List<String> listImgSrc) {
    	Vector<String> name = new Vector<String>();
        try {  
            for (int i = 1 ; i < listImgSrc.size();i++ ) { 
//            	System.out.println(url+"tttt");
            	String url = listImgSrc.get(i);
                String imageName = url.substring(url.lastIndexOf("."), url.length());  
                URL uri = new URL(url);  
                InputStream in = uri.openStream();  
                FileOutputStream fo = new FileOutputStream(new File(".\\image",fileName+"00"+i+""+imageName)); 
                name.add(fileName+"00"+i+""+imageName);
                byte[] buf = new byte[1024];  
                int length = 0;  
//                System.out.println("��ʼ����:" + url);  
                while ((length = in.read(buf, 0, buf.length)) != -1) {  
                    fo.write(buf, 0, length);  
                }  
                in.close();  
                fo.close();  
//                System.out.println(imageName + "�������");  
            }  
        } catch (Exception e) {  
//            System.out.println("����ʧ��");  
        } 
        
        return name;
    }  
  
   /* ����
    * 
    * */ 
    public Vector<String> getImage(String html){
//    	GetImage image = new GetImage();
        //��ȡͼƬ��ǩ  
        List<String> imgUrl = getImageUrl(html);  
        //��ȡͼƬsrc��ַ  
        List<String> imgSrc = getImageSrc(imgUrl);  
        //����ͼƬ  
       return Download(imgSrc);
        
    }
}  