package com.fullcontact.storm.util.airbrake

import backtype.storm.task.TopologyContext
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import backtype.storm.topology.base.BaseBatchBolt
import backtype.storm.topology.base.BaseBasicBolt
import backtype.storm.coordination.IBatchBolt
import backtype.storm.topology.IBasicBolt
import backtype.storm.topology.IRichBolt

/**
 * 2012-06-14
 * @author Michael Rose <michael@fullcontact.com>
 */
class AirbrakeUtil {
    private static ExecutorService loggingPool = Executors.newFixedThreadPool(2)

    public static void logException(String apiKey, String environment, TopologyContext context, Exception ex) {
        if (!apiKey.isEmpty() && !environment?.isEmpty()) {
            loggingPool.submit(new AsyncAirbrakeNotifier(apiKey, environment, context, ex))
        }
    }

    public static IBasicBolt wrapBolt(BaseBasicBolt bolt) {
        new AirbrakeBaseBasicBoltWrapper(bolt)
    }

    public static IBatchBolt wrapBolt(BaseBatchBolt bolt) {
        new AirbrakeBatchBoltWrapper(bolt)
    }

    public static IRichBolt wrapBolt(IRichBolt bolt) {
        new AirbrakeRichBoltWrapper(bolt)
    }

}