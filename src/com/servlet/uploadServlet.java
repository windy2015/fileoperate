package com.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

public class uploadServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor of the object.
	 */
	public uploadServlet() {
		super();
	}

	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {       
		response.setContentType("text/html");
		response.setCharacterEncoding("UTF-8");		
		
		//获取文件的保存路径
		String savepath = this.getServletContext().getRealPath("/web-inf/upload");
		
		File file = new File(savepath);
		if(!file.exists() && !file.isDirectory()){
			System.out.println("文件不存在");
			file.mkdir();
		}
		
		String message = "";
		DiskFileItemFactory diskFileitemFactory = new DiskFileItemFactory();
		
		ServletFileUpload fileUpload = new ServletFileUpload(diskFileitemFactory);
		
		fileUpload.setHeaderEncoding("UTF-8");
		
		if(!fileUpload.isMultipartContent(request)){
			return;
		}
		
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
					int index = fileName.lastIndexOf(File.separator);
					
					if(index>-1){
					
					  fileName = fileName.substring(index);
					}
					
					//获取文件上传输入流
					InputStream in = item.getInputStream();
					
					//创建文件输出流
					FileOutputStream fos = new FileOutputStream(savepath+File.separator+fileName);
					
					byte[] buf = new byte[2*1024];
					
					int length = 0;
					
					while((length= in.read())>0){
						fos.write(buf, 0, length);
					}
					
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
	
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
	      this.doGet(request, response);
	}

	
	public void init() throws ServletException {
		// Put your code here
	}

}
