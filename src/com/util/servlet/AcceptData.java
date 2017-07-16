package com.util.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.RequestContext;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;



/**
 * Servlet implementation class AcceptData
 */
@WebServlet("/AcceptData")
public class AcceptData extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private String fileType;
	private String fileName;
	private int totalBlocks;
	private int currentBlockNum;
	
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AcceptData() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    @Override
   	protected
       void service(HttpServletRequest request, HttpServletResponse response) throws ServletException
       {
    		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
    		DiskFileItemFactory factory = new DiskFileItemFactory();
    		 factory.setSizeThreshold(52428800);
    		 ServletFileUpload upload = new ServletFileUpload((FileItemFactory) factory);
    		 upload.setSizeMax(52428800);
    		 
    		 try {
				List<?> items = upload.parseRequest(request);
				Iterator<?> iter = items.iterator();
				while(iter.hasNext())
				{
					FileItem item = (FileItem) iter.next();
					if(item.isFormField())
					{
						String name = item.getFieldName();
						switch(name)
						{
						case "totalBlocks":
							totalBlocks = Integer.valueOf(item.getString());
							break;
						case "currentBlockNum":
							currentBlockNum = Integer.valueOf(item.getString());
							break;
						case "fileType":
							fileType = item.getString();
							break;
						case "fileName":
							fileName = URLDecoder.decode(item.getString(),"UTF-8");
							break;
						}

//						System.out.println(name);
					}else{
						String name = item.getFieldName();
						InputStream is = item.getInputStream();
						int index = fileName.indexOf(".");
						String prefix = fileName.substring(0,index);
						String suffix = fileName.substring(index);
						String path = "";
						if( totalBlocks == 1)
							path = "D:\\"+prefix+suffix;
						else if( currentBlockNum == 0)
							path = "D:\\"+prefix;
						else
							path = "D:\\"+prefix+currentBlockNum;
						File file = new File(path);
						file.createNewFile();
						OutputStream os = new FileOutputStream(file);
						byte[] buffer = new byte[1024];
						int size = -1;
						while((size = is.read(buffer))!= -1)
							os.write(buffer, 0, size);
						os.flush();
						os.close();
						is.close();
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FileUploadException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		 
    		 if( totalBlocks > 1 && currentBlockNum+1 == totalBlocks )
    			 combineFile();
    		 
       }
    
    private void combineFile()
    {
    	System.out.println("start to combine file blocks");
    	RandomAccessFile randomFile = null;
    	int index = fileName.indexOf(".");
		String prefix = fileName.substring(0,index);
		String suffix = fileName.substring(index);
		
		int count = 0;
		
    	try {
			randomFile = new RandomAccessFile("D:\\"+prefix, "rw");
			count++;
			 long fileLength = randomFile.length();     
	         randomFile.seek(fileLength); 
	         while( count < totalBlocks)
	 		{
	        	 File file = new File("D:\\"+prefix+count);
	        	 InputStream is = new FileInputStream( file );
	        	 byte[] buffer = new byte[5120];
	        	 int size = -1;
	        	 while( (size = is.read(buffer))!= -1)
	        		 randomFile.write(buffer, 0, size);
	        	 is.close();
	        	 count++;
	        	 file.delete();
	 		}
	        randomFile.close();
	        File file = new File("D:\\"+prefix);
	        file.renameTo( new File("D:\\"+prefix+suffix));
	        
	         
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	System.out.println("combine file blocks end");
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
