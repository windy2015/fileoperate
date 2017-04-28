package com.servlet;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class listFileServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	public listFileServlet() {
		super();
	}

	public void destroy() {
		super.destroy(); 		
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html");
		response.setCharacterEncoding("utf-8");
		Map<String,String> fileMap = new HashMap<String,String>();
		String filepath = this.getServletContext().getRealPath("WEB-INF/upload");
		File f = new File(filepath);
		listFiles(f,fileMap);
		request.setAttribute("fileMap", fileMap);
		request.getRequestDispatcher("/listfile.jsp").forward(request, response);
		
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
	    this.doGet(request, response);
	}
	
	public void init() throws ServletException {
		
	}
	
	/***
	 * 遍历指定目录下所有文件
	 * @param f
	 */
	private void listFiles(File f,Map<String,String> fileMap){
		if(!f.isFile()){
			File[] files = f.listFiles();
			for(File file : files){
				listFiles(file , fileMap);
			}
		}else{
			String realname = f.getName().substring(f.getName().lastIndexOf("_")+1);
			fileMap.put(f.getName(), realname);
		}
	}

}
