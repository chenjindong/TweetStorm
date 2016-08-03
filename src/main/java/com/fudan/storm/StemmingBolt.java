package com.fudan.storm;

import java.util.List;
import java.util.Map;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StemmingBolt implements IRichBolt
{
	private static final Logger LOG = LoggerFactory.getLogger(StemmingBolt.class);
	private List<String> stopWords;
	private OutputCollector collector;
	
	@Override
	public void prepare(Map stormConf, TopologyContext context,
			OutputCollector collector) {
		this.collector = collector;
		stopWords = StopWords.getWords();
	}

	@Override
	public void execute(Tuple input) {
		String tweetId = input.getString(0);
		String text = input.getString(1);
		for (String word : stopWords)
        {
            text = text.replaceAll("\\b" + word + "\\b", "");
        }
		collector.emit(new Values(tweetId,text));
	//	LOG.info(tweetId + " " + text);
	}
	
	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("tweetId","tweet"));
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
