package com.util.servlet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class AcceptData
 */
@WebServlet("/AcceptSmallData")
public class AcceptSmallData extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private String fileType;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AcceptSmallData() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    @Override
	protected
    void service(HttpServletRequest request, HttpServletResponse response) throws ServletException
    {
    	if ( request.getContentLength()>0 )
    	{
    		InputStream is = null;
    		OutputStream os = null;
    		try{
    			is = request.getInputStream();
    			fileType = request.getHeader("fileTYpe");
    			String suffix = fileType;
    			suffix = suffix.substring(suffix.indexOf("/")+1);
    			File file = new File("C:\\Users\\I341385\\Downloads\\nobackup\\test"+"."+suffix);
    			file.createNewFile();
//    			System.out.println("file create");
    			os = new FileOutputStream(file);
    			byte temp[] = new byte[1024];  
    			int size = -1;  
    			while ((size = is.read(temp)) != -1) { // 每次读取1KB，直至读完  
    				os.write(temp, 0, size);  
    			}  
    			os.flush();   
    			os.close();
    			is.close();
    			System.out.println("upload successly");
    		}catch(Exception e){
    			System.out.println(e.toString());
    			
    		}
    	}
    	try {
			doPost(request, response);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("<p>Your file has been successfully received.</p><p>Type: "+fileType+"}</p>");
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
