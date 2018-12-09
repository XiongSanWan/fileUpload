package com.xy.web.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;

public class UploadServlet1 extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		request.setCharacterEncoding("UTF-8");
		//判断表单是否支持文件上传。即enctype="multipart/form-data"
		boolean multipartContent = ServletFileUpload.isMultipartContent(request);
		if(!multipartContent){
			throw new RuntimeException("表单不支持上传文件，请添加enctype=\"multipart/form-data+\"");
		}
		//创建一个工厂类
		DiskFileItemFactory factory = new DiskFileItemFactory();
		
		//上传文件过大的时候会在磁盘产生一个临时文件，这个方法设置了临时文件的位置
		factory.setRepository(new File("F:/"));
		
		//创建核心类对象
		ServletFileUpload sfu = new ServletFileUpload(factory);
		//解决乱码问题
		//sfu.setHeaderEncoding("UTF-8");
		try {
			//控制文件上传的大小（一个文件）
			sfu.setFileSizeMax(1024*1024*3);
			
			//控制文件上传的大小（多个）
			sfu.setSizeMax(1024*1024*6);
			List<FileItem> fileItems = sfu.parseRequest(request);
			for (FileItem fileItem : fileItems) {
				//处理普通表单数据的方法
				if(fileItem.isFormField()){
					processFormField(fileItem);
				}else{
					processUploadField(fileItem);
				}
			}
		} catch (FileUploadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void processUploadField(FileItem fileItem) {
		// TODO Auto-generated method stub
		String filename = fileItem.getName();//文件项中的名字a.txt
		//有时候文件的路径会是绝对路径如：D:/文档/a.txt,这个方法就解决了这个问题，去掉了前面的路径
		filename = FilenameUtils.getName(filename);
		//效果同上，File.separator指的是分隔符“/”
		/*filename.substring(filename.lastIndexOf(File.separator)+1);*/
		
		
		try {
			//获得文件的输入流
			InputStream is = fileItem.getInputStream();
			//创建文件存盘的路径,使文件上传到WEB-INF目录下是为了安全性考虑，在WEB-INF目录下的文件浏览器访问不到。
			String directoryRealPath = this.getServletContext().getRealPath("/WEB-INF/upload");
			File storeDirectory = new File(directoryRealPath);
			if(!storeDirectory.exists()){
				storeDirectory.mkdirs();
			}
			//生成一个随机数，避免文件名重复
			UUID randomUUID = UUID.randomUUID();
			filename = randomUUID+"_"+filename;
			//生成一个日期的父类文件夹,按日期打散
			//String parentDir = getParentDir(storeDirectory);
			
			//按目录打散
			String parentDir = getParentDir(storeDirectory,filename);
			//在storeDirectory下创建完整的目录
			File file = new File(storeDirectory, parentDir+File.separator+filename);
			//通过文件输出流将图片保存到磁盘
			FileOutputStream fos = new FileOutputStream(file);
			int len=0;
			byte[] b = new byte[1024];
			while((len=is.read()) != -1){
				fos.write(b,0,len);
			}
			fos.close();
			is.close();
			
			//上传大文件的时候产生的临时文件在上传完后要及时删除
			fileItem.delete();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//按目录打散
	private String getParentDir(File storeDirectory, String filename) {
		// TODO Auto-generated method stub
		int hashCode = filename.hashCode();
		//将hashcode转换为十六进制的字符
		String code = Integer.toHexString(hashCode);
		
		String parentDir = code.charAt(0)+File.separator+code.charAt(1);
		File f = new File(storeDirectory, parentDir);
		if(!f.exists()){
			f.mkdirs();
		}
		return parentDir;
	}

	
	//按日期打散
	private String getParentDir(File storeDirectory) {
		// TODO Auto-generated method stub
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String format = sdf.format(new Date());
		
		File parentDir = new File(storeDirectory, format);
		if(!parentDir.exists()){
			parentDir.mkdirs();
		}
		return format;
	}

	private void processFormField(FileItem fileItem) {
		// TODO Auto-generated method stub
		String fieldName = fileItem.getFieldName();
		String fieldValue = null;
		try {
			fieldValue = fileItem.getString("UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(fieldName+"  "+fieldValue);
	}

}
