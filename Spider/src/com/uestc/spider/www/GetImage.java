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
	public static int imageNum = 1; //ͼƬ������1��ʼ
    public String URL ; 
    public String photourl ; // = "http://e.chengdu.cn/";  //������
    public String fileName ;
    public String imageBuf;      //"../../../"
    // ����  
    private static  String ECODING = "UTF-8";  
    // ��ȡimg��ǩ����  
//    private static final String IMGURL_REG = "<img src=(.*?)[^>]*?>";
    private static  String IMGURL_REG ; //= "img src=\"(.*?)res(.*?)attpic_brief.jpg\"";
    // ��ȡsrc·��������  
    private static  String IMGSRC_REG ; // = "http:\"?(.*?)(\"|>|\\s+)";   //���޸�~~
//    private static final String IMGSRC_REG = "http://e.chengdu.cn/res/(.*?)_attpic_brief.jpg"; 
      
    @SuppressWarnings("static-access")
	public GetImage(String url ,String imgurl ,String imgsrc,String imageBuf){
    	this.photourl = url;
    	this.IMGURL_REG = imgurl;
    	this.IMGSRC_REG = imgsrc;
    	this.imageBuf = imageBuf;
    }  
    /*** 
     * ��ȡHTML���� 
     *  
     * @param url 
     * @return 
     * @throws Exception 
     */  
    @SuppressWarnings("unused")
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
//            System.out.println(matcher.group()+"dddddddd");
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
        	// ��ȡ����url
        	if(image.contains(imageBuf))
        		image = image.replace(imageBuf,photourl );
//        	image = image.replace("\"", "");
//        	image = image.replaceAll("\"", "");
//        	System.out.println(image+"222");
            Matcher matcher = Pattern.compile(IMGSRC_REG).matcher(image);  
            while (matcher.find()) { 
//            	System.out.println(matcher.group());
//            	if(listImgSrc.contains(matcher.group().substring(0, matcher.group().length() - 1)))
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
    	File f = new File("image");
    	if(!f.exists()){
    		f.mkdir();
    	}
        try {
//        	System.out.println("kaishi");
            for (int i = 0 ; i < listImgSrc.size();i++ ) { 
//            	System.out.println("qqq");
//            	System.out.println(url+"tttt");
            	String url = listImgSrc.get(i);
//            	System.out.println(url+"tttt");
                String imageName = url.substring(url.lastIndexOf("."), url.length());  
                URL uri = new URL(url);  
                InputStream in = uri.openStream();
                FileOutputStream fo;
//                System.out.println("tttttt");
                if(imageNum < 9){
//                	System.out.println("sssssssss");
                	fo = new FileOutputStream(new File(".\\image",fileName+"000"+imageNum+"000"+(i+1)+imageName)); 
                	name.add(fileName+"000"+imageNum+"000"+(i+1)+""+imageName);
                	
//                	System.out.println("11111");)
                }else if(imageNum < 99){
                	
                	fo = new FileOutputStream(new File(".\\image",fileName+"00"+imageNum+"000"+(i+1)+imageName));
                	name.add(fileName+"00"+imageNum+"000"+(i+1)+""+imageName);
//                	System.out.println("222222");
                }else if(imageNum < 999){
                	
                	fo = new FileOutputStream(new File(".\\image",fileName+"0"+imageNum+"000"+(i+1)+imageName));
                	name.add(fileName+"0"+imageNum+"000"+(i+1)+""+imageName);
//                	System.out.println("33333333");
                }else{
                	fo = new FileOutputStream(new File(".\\image",fileName+imageNum+"000"+(i+1)+imageName));
                	name.add(fileName+imageNum+"000"+(i+1)+""+imageName);
//                	System.out.println("4444444");
                }
                
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
            System.out.println("����ʧ��"); 
//            e.printStackTrace();
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
        imageNum ++;
        //����ͼƬ  
        Vector<String> result = Download(imgSrc);
        //gc
        imgUrl.clear();imgUrl = null;
        imgSrc.clear();imgSrc = null ;
       return result;
        
    }
    
    public static void main(String args[]) throws Exception{
//    	GetImage test =  new GetImage();
//    	test.getImage(test.getHTML("http://e.chengdu.cn/html/2014-09/04/content_486943.htm"));
    	
    }
}  