package com.fudan.storm;

import org.apache.storm.Config;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.generated.AuthorizationException;
import org.apache.storm.generated.InvalidTopologyException;
import org.apache.storm.topology.TopologyBuilder;


public class TwitterTopology {
	
	public static void main(String[] args) throws AlreadyAliveException, InvalidTopologyException, AuthorizationException{
		
		TopologyBuilder builder = new TopologyBuilder();
		
		builder.setSpout("readTweetSpout", new ReadTweetSpout());
		builder.setBolt("textFilterBolt", new TextFilterBolt()).shuffleGrouping("readTweetSpout");
		builder.setBolt("stemmingBolt", new StemmingBolt()).shuffleGrouping("textFilterBolt");
		builder.setBolt("splitTweetBolt", new SplitTweetBolt()).shuffleGrouping("stemmingBolt");
		builder.setBolt("topWordBolt", new TopWordBolt()).shuffleGrouping("splitTweetBolt");
		
		Config conf = new Config();
		
		StormSubmitter.submitTopology("twitter", conf, builder.createTopology());
	
	}
	
}
