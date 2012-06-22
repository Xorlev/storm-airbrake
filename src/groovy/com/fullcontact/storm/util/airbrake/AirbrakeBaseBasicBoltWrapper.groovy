package com.fullcontact.storm.util.airbrake

import backtype.storm.coordination.BatchOutputCollector
import backtype.storm.coordination.IBatchBolt
import backtype.storm.task.TopologyContext
import backtype.storm.topology.OutputFieldsDeclarer
import backtype.storm.tuple.Tuple
import backtype.storm.topology.IBasicBolt
import backtype.storm.topology.BasicOutputCollector
import backtype.storm.topology.base.BaseComponent

/**
 * 2012-06-14
 * @author Michael Rose <michael@fullcontact.com>
 */
class AirbrakeBaseBasicBoltWrapper extends BaseComponent implements IBasicBolt {
    Map _stormConf
    TopologyContext _context
    IBasicBolt _bolt

    AirbrakeBaseBasicBoltWrapper(IBasicBolt _bolt) {
        this._bolt = _bolt
    }


    @Override
    void prepare(Map stormConf, TopologyContext context) {
        _stormConf = stormConf
        _context = context
        _bolt.prepare(stormConf, context)
    }

    @Override
    void execute(Tuple input, BasicOutputCollector collector) {
        try {
            _bolt.execute(input, collector)
        } catch(Exception ex) {
            AirbrakeUtil.logException(_stormConf['airbrake.apiKey'], _stormConf['airbrake.environment'], _context, ex)
//            throw ex // Rethrow for normal storm handling
        }
    }

    @Override
    void cleanup() {
        _bolt.cleanup()
    }

    @Override
    void declareOutputFields(OutputFieldsDeclarer declarer) {
        _bolt.declareOutputFields(declarer)
    }

    @Override
    Map<String, Object> getComponentConfiguration() {
        _bolt.getComponentConfiguration()
    }
}
