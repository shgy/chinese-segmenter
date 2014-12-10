package org.ictweb.example;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nlpir.ictclas.ICTToken;
import org.nlpir.ictclas.NLPIR_ICTCLAS;

public class SegServlet extends HttpServlet{
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		this.doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		req.setCharacterEncoding("utf-8");
		resp.setContentType("text/html;charset=UTF-8");
		
		String text = req.getParameter("text");
		
		StringBuffer buf = new StringBuffer("分词结果：");
		NLPIR_ICTCLAS ict = new NLPIR_ICTCLAS(new StringReader(text));
		ICTToken tok = null;
		while((tok=ict.next())!=null){
			buf.append(tok+"\t");
		}
		
		PrintWriter pw = resp.getWriter();
		pw.append(buf);
		pw.flush();
	}
	
}
