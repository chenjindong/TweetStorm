package com.fudan.storm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Map;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichSpout;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReadTweetSpout implements IRichSpout {

	private static final Logger LOG = LoggerFactory.getLogger(ReadTweetSpout.class);
	private static final String FILENAME = "hdfs://master:9000/user/cjd/input/a.csv";
	private SpoutOutputCollector collector;
	private BufferedReader reader;
	Configuration configuration;

	@Override
	public void open(Map conf, TopologyContext context,
			SpoutOutputCollector collector) {
		this.collector = collector;
	    configuration = new Configuration();
		configuration.set("fs.hdfs.impl", "org.apache.hadoop.hdfs.DistributedFileSystem");
		try {
			FileSystem hdfs1 = FileSystem.get(URI.create(FILENAME),configuration);
			InputStream in = null;
			in = hdfs1.open(new Path(FILENAME));
			reader = new BufferedReader(new InputStreamReader(in));	
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void nextTuple() {
		String line = null;
		try {
			while((line = reader.readLine()) != null){
				String[] items = line.split(",");
	        	String text = "";
	        	for(int i = 1; i < items.length; i++)
	        		text += items[i];
	        	collector.emit(new Values(items[0],text));
	        	//LOG.info(items[0]+" "+text);	 
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("tweetId", "tweet"));
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

	@Override
	public void activate() {
		// TODO Auto-generated method stub

	}

	@Override
	public void deactivate() {
		// TODO Auto-generated method stub

	}

	@Override
	public void ack(Object msgId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void fail(Object msgId) {
		// TODO Auto-generated method stub

	}

	@Override
	public Map<String, Object> getComponentConfiguration() {
		// TODO Auto-generated method stub
		return null;
	}

}
