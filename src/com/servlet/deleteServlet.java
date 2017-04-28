package com.servlet;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class deleteServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public deleteServlet() {
		super();
	}

	public void destroy() {
		super.destroy();
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html");
		// 获取删除的文件名
		String filename = request.getParameter("filename");
		// 防止文件名乱码
		filename = new String(filename.getBytes("iso8859-1"), "UTF-8");
		// 上传的文件都是保存在服务器web-inf/upload下
		String webrootPath = this.getServletContext().getRealPath(
				"WEB-INF/upload");

		String realname = filename.substring(filename.lastIndexOf("_") + 1);

		// 获取文件所在文件夹
		String path = findPathByFilename(webrootPath, realname);

		// 获取要下载的文件
		File downloadFile = new File(path + File.separator + filename);

		if (!downloadFile.exists()) {
			request.setAttribute("message", "该文件已经不存在");
			request.getRequestDispatcher("message.jsp").forward(request,
					response);
			return;
		}

		boolean hasdelete = downloadFile.delete();
		if (hasdelete) {
            response.sendRedirect("listFile");
		} else {
			request.setAttribute("message", "删除文件失败");
			request.getRequestDispatcher("message.jsp").forward(request,
					response);
			return;
		}

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
