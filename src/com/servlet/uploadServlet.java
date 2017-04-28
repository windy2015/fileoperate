package com.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.ProgressListener;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

public class uploadServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public uploadServlet() {
		super();
	}
	
	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {       
		response.setContentType("text/html");
		response.setCharacterEncoding("UTF-8");		
		
		//获取文件的真实保存路径
		String savepath = this.getServletContext().getRealPath("/web-inf/upload");
		
		//上传文件的临时保存目录
		String temppath = this.getServletContext().getRealPath("/WEB-INF/temp");
		
		File file = new File(temppath);
		if(!file.exists() && !file.isDirectory()){
			System.out.println("文件不存在");
			file.mkdir();
		}
		
		String message = "";
		DiskFileItemFactory diskFileitemFactory = new DiskFileItemFactory();
		diskFileitemFactory.setRepository(file);
		//设置缓冲区大小，当上传的文件超过缓冲区大小时，就会生成一个临时文件写在临时目录中
		diskFileitemFactory.setSizeThreshold(1024*100);
		//创建一个文件解析器
		ServletFileUpload fileUpload = new ServletFileUpload(diskFileitemFactory);
		//解决文件上传乱码
		fileUpload.setHeaderEncoding("UTF-8");
		
		//监听文件上传进度
		fileUpload.setProgressListener(new ProgressListener(){

			@Override
			public void update(long arg0, long arg1, int arg2) {
				System.out.println("文件大小为："+arg0+",当前已上传"+arg1);
			}
			
		});
		
		if(!fileUpload.isMultipartContent(request)){
			return;
		}
		//设置单个文件上传的大小为1MB
		fileUpload.setFileSizeMax(1024*1024);
		//设置总文件大小为10MB
		fileUpload.setSizeMax(1024*1024*10);
		
		try {
			List<FileItem> items = fileUpload.parseRequest(request);
			
			for(FileItem item : items){
				if(item.isFormField()){
					//普通字段处理
					String fieldName = item.getFieldName();
					String value = item.getString("utf-8");
					String value1 = new String(fieldName.getBytes("iso8859-1"),"UTF-8");
                    System.out.println(fieldName+"  "+value);
                    System.out.println(fieldName+"  "+value1);
					
				}else{
					String fileName = item.getName();					
					if(fileName==null || "".equals(fileName.trim())){
						continue;
					}
					//获取文件名 a.txt
					String fname = fileName.substring(fileName.lastIndexOf(File.separator)+1);	
					
					//获取扩展名
					String extName = fileName.substring(fileName.lastIndexOf(".")+1);
					
					//过滤文件名不符合的情况
					if("zip".equals(extName) || "tar".equals(extName) || "zip".equals(extName)){
						request.setAttribute("message", "上传文件类型不支持");
						request.getRequestDispatcher("message.jsp").forward(request, response);
						return;
					}
					
					String finalfileName = makeFileName(fname);
					
					String finalfilePath = makeFilePath(savepath,fname);
					
					System.out.println("文件保存路径为："+finalfilePath);
					//获取文件上传输入流
					InputStream in = item.getInputStream();
					
					//创建文件输出流
					FileOutputStream fos = new FileOutputStream(finalfilePath+File.separator+finalfileName);
					
					/**
					byte[] buf = new byte[2*1024];
					
					int length = 0;
					
					while((length= in.read(buf))>0){
						fos.write(buf, 0, length);
					}**/
					//异步io来读取
					//获取读通道
					FileChannel readChanel = ((FileInputStream)in).getChannel();
					
					//获取写通道
					FileChannel writeChanel = fos.getChannel();
					
					ByteBuffer buffer = ByteBuffer.allocate(1024);
					while(true){
						buffer.clear();
						int len = readChanel.read(buffer);
						if(len<0){
							//读取完毕
							break;							
						}
						buffer.flip();
						writeChanel.write(buffer);
					}
					//关闭流
					in.close();
					fos.close();
					item.delete();
					message = "文件上传成功";
					
				}
			}
			
		} catch (FileUploadException e) {			
			e.printStackTrace();
			message = "文件上传失败";
		}
		request.setAttribute("message", message);		
		request.getRequestDispatcher("message.jsp").forward(request, response);		
	}
	
	//生成上传文件的文件名，uuid+"_"+fname
	private String makeFileName(String fname) {
		
		return UUID.randomUUID().toString()+"_"+fname;
	}
	
	//防止一个目录太多文件，使用hash算法打散存储
	private String makeFilePath(String filepath ,String filename){
		int hashcode = filename.hashCode();
		int dir1 = hashcode & 0xf;
		int dir2 = (hashcode&0xf0)>>4;
		String dir = filepath+"\\"+dir1+"\\"+dir2;
		File file = new File(dir);
		if(!file.exists()){
			file.mkdirs();
		}
		return dir;
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
	      this.doGet(request, response);
	}

	
	public void init() throws ServletException {
		// Put your code here
	}

}
