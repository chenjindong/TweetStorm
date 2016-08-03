package com.fudan.storm;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TopWordBolt implements IRichBolt{

	private static final Logger LOG = LoggerFactory.getLogger(TopWordBolt.class);
	private static final Integer TOP = 10;
	private static String FORMAT = "yyyy-MM-dd HH:mm:ss.SSS"; 
	private OutputCollector collector;
	private Map<String,Long> hMap = new HashMap<String, Long>();
	private Long[] a = new Long[TOP];
	private String[] b = new String[TOP];
	
	//hdfs部分
	private Configuration configuration;
	private FileSystem fs = null;
	private FSDataOutputStream os = null;

	
	private Long lastTimeStamp;
	private String lastTime;
	private SimpleDateFormat sdf;
	
	@Override
	public void prepare(Map stormConf, TopologyContext context,
			OutputCollector collector) {
		this.collector = collector;
		lastTimeStamp = (new Date()).getTime();
		sdf = new SimpleDateFormat(FORMAT);
		lastTime = sdf.format(new Date());
		
		configuration = new Configuration();
		configuration.set("fs.defaultFS","hdfs://master:9000");  
        configuration.set("fs.hdfs.impl",org.apache.hadoop.hdfs.DistributedFileSystem.class.getName());  
        configuration.set("fs.file.impl",org.apache.hadoop.fs.LocalFileSystem.class.getName());
        try {
			fs = FileSystem.get(configuration);
		} catch (IOException e) {
			e.printStackTrace();
		} 
		try {
			os = fs.create(new Path("hdfs://master:9000/user/cjd/output/tweets.txt"));
		} catch (IllegalArgumentException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	public void countTop(){
		int cnt = 0;
		for(Map.Entry<String, Long> entry : hMap.entrySet()){
			String word = entry.getKey();
			Long value = entry.getValue();
			if(cnt < TOP){
				a[cnt] = value;
				b[cnt] = word;
				cnt++;
			}
			else{
				int minFlag = 0;
				Long minVal = Long.MAX_VALUE;
				for(int i = 0; i < TOP; i++)
					if(a[i] < minVal){
						minFlag = i;
						minVal = a[i];
					}
				if(value > a[minFlag]){
					a[minFlag] = value;
					b[minFlag] = word;				
				}
			}
		}
	}

	@Override
	public void execute(Tuple input) {
		String word = input.getString(0);
		Long nowTimeStamp = (new Date()).getTime();
		if(nowTimeStamp-lastTimeStamp >= 10000){
		
			lastTime += "\n";
			byte[] buff0 = lastTime.getBytes();
			try {
				os.write(buff0,0,buff0.length);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
			lastTimeStamp = nowTimeStamp;
			lastTime = sdf.format(new Date());
			countTop();
			hMap.clear();
			
			try {
				String str = "";
				for(int i = 0; i < TOP; i++)
				{
					str = String.valueOf(b[i]) + " " + a[i] + "\n";
					byte[] buff = str.getBytes();
					os.write(buff,0,buff.length);   //写入str到hdfs
				}
				str = "\n\n";
				byte[] buff = str.getBytes();
				os.write(buff,0,buff.length);  
			
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		else
		{
			Long val = 1L;
			if(hMap.keySet().contains(word))
				val = val + hMap.get(word);
			hMap.put(word, val);
		}
		
	}
	
	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		
	}

	@Override
	public void cleanup() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Map<String, Object> getComponentConfiguration() {
		// TODO Auto-generated method stub
		return null;
	}

}
