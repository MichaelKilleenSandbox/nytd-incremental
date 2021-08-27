package gov.hhs.acf.cb.nytd.service.impl;

import gov.hhs.acf.cb.nytd.models.helper.DataTable;
import org.apache.log4j.Logger;

import java.io.*;

public class SPSSWriterThread implements Runnable
{
	protected final Logger log = Logger.getLogger(getClass());
	private volatile boolean startOfRecs;
	private volatile boolean endOfRecs;
	private volatile PipedOutputStream out;
	private volatile PipedInputStream in;
	private volatile DataTable dataTable;
	private volatile File file;
	private volatile File finalFile;
	private Thread prevThread;
	public SPSSWriterThread(DataTable dataTable, PipedOutputStream out,boolean startOfRecs, boolean endOfRecs)
	{
		this.startOfRecs = startOfRecs;
		this.endOfRecs = endOfRecs;
		this.out = out;
		this.dataTable = dataTable;
	}
	public SPSSWriterThread(PipedInputStream in,boolean startOfRecs,boolean endOfRecs, File file, File finalFile,Thread prevThread)
	{
		this.startOfRecs = startOfRecs;
		this.endOfRecs = endOfRecs;
		this.in = in;
		this.file = file;
		this.finalFile = finalFile;
		this.prevThread = prevThread;
	
	}
	
	@Override
	public void run()
	{
		
		try {
			if(!startOfRecs)
			{
				if (prevThread != null)
				{
					prevThread.join();
				}
			}
			else
			{
				writeToFile(in, file);
			}
			
		} catch (Exception e) {
		
			writeToFile(in, file);
				 
		}
		
		if(endOfRecs)
		 {
			// Renaming the temp file
			file.renameTo(finalFile);
			
		 }
	}
	
	private void  writeToFile(InputStream is, File file)
	{
		try
		{
			
			InputStreamReader isr = new InputStreamReader(is);
			OutputStream out = new FileOutputStream(file,true);
			
			Reader rdr = new InputStreamReader(is,"ISO-8859-1");
			Writer wtr = new OutputStreamWriter(out, "ISO-8859-1");
			
			/*byte buf[] = new byte[1024];
			int len;
			
			while ((len = is.read(buf)) > 0)
			{
				out.write(buf, 0, len);
			
			}*/
			
			int c;
			while ((c = rdr.read())!= -1 )
			{
				wtr.write(c);
			}

		//	out.close();
		//	isr.close();
			
			rdr.close();
			wtr.close();

			out.close();
			isr.close();
		}
		catch (IOException e)
		{
			log.error("Error Writing/Reading Streams.", e);
		}
	}


}
