package com.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class downloadServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public downloadServlet() {
		super();
	}

	public void destroy() {
		super.destroy();
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html");
		response.setCharacterEncoding("UTF-8");
		// 获取下载的文件名
		String filename = request.getParameter("filename");
		// 防止文件名乱码
		filename = new String(filename.getBytes("iso8859-1"), "UTF-8");
		// 上传的文件都是保存在服务器web-inf/upload下
		String webrootPath = this.getServletContext().getRealPath(
				"WEB-INF/upload");

		String realname = filename.substring(filename.lastIndexOf("_") + 1);
		
		//获取文件所在文件夹
		String path = findPathByFilename(webrootPath, realname);
		
		//获取要下载的文件
		File downloadFile = new File(path+File.separator+filename);
		
		if(!downloadFile.exists()){
			request.setAttribute("message", "该文件已经被删除");
			request.getRequestDispatcher("message.jsp").forward(request, response);
			return ;
		}
		
		//控制浏览器下载文件
		response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode(realname, "UTF-8"));
		
		//获取文件输入流
		FileInputStream fis = new FileInputStream(path+File.separator+filename);
		
		//创建文件输出流
		OutputStream out = response.getOutputStream();		
		
		
		//FileChannel ochanel = ((FileOutputStream)out).getChannel();发生异常暂时放弃异步io下载
		/**
		//设置缓冲区
		ByteBuffer buf = ByteBuffer.allocate(1024);
		
		//读通道
		FileChannel fchanel = fis.getChannel();
		
		//写通道
		FileChannel ochanel = ((FileOutputStream)out).getChannel();
		//异步io下载文件
		while(true){
		    buf.clear();
		    int len = fchanel.read(buf);
		    if(len<0){
		    	break;
		    }
		    buf.flip();
		    ochanel.write(buf);
		}**/
		
		byte[] buf = new byte[1024];
		
		int len = 0;
		while((len = fis.read(buf))!=-1){
			out.write(buf, 0, len);
		}
		
		fis.close();
		out.close();
		
	}

	private String findPathByFilename(String webrootPath, String realname) {

		int hashcode = realname.hashCode();
		int dir1 = hashcode & 0xf;
		int dir2 = (hashcode & 0xf0) >> 4;
		String dir = webrootPath + "\\" + dir1 + "\\" + dir2;
		File file = new File(dir);
		if (!file.exists()) {
			file.mkdirs();
		}
		return dir;
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		this.doGet(request, response);
	}

	public void init() throws ServletException {

	}

}
