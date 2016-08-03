package com.fudan.storm;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.htrace.fasterxml.jackson.core.util.BufferRecycler;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.URI;
import java.util.List;
import java.util.ArrayList;

public class StopWords implements Serializable
{
    public static final long serialVersionUID = 42L;
    private List<String> stopWords;
    private static StopWords _singleton;
    private static final String FILENAME="hdfs://master:9000/user/cjd/input/stop-words.txt";
    private BufferedReader reader;
    private StopWords()
    {
        this.stopWords = new ArrayList<String>();
        try
        {
        	Configuration configuration = new Configuration();
        	configuration.set("fs.hdfs.impl", "org.apache.hadoop.hdfs.DistributedFileSystem");
    		FileSystem hdfs1 = FileSystem.get(URI.create(FILENAME),configuration);
    		InputStream in = null;
    		in = hdfs1.open(new Path(FILENAME));
    		reader = new BufferedReader(new InputStreamReader(in));	      	
        	String line = null;
            while ((line = reader.readLine()) != null)
                this.stopWords.add(line);
        }
        catch (IOException ex)
        {
            Logger.getLogger(this.getClass())
                  .error("IO error while initializing", ex);
        }
        finally
        {
            try {
                if (reader != null) reader.close();
            } catch (IOException ex) {}
        }
        
    }

    private static StopWords get()
    {
        if (_singleton == null)
            _singleton = new StopWords();
        return _singleton;
    }

    public static List<String> getWords()
    {
        return get().stopWords;
    }
    
//    public static void main(String[] args){
//    	List<String> words = StopWords.getWords();
//    	for(String word: words){
//    		System.out.println(word);
//    	}
//    }
}
