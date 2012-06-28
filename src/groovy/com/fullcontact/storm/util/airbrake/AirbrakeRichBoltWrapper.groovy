package com.fullcontact.storm.util.airbrake

import backtype.storm.topology.IRichBolt
import backtype.storm.task.TopologyContext
import backtype.storm.task.OutputCollector
import backtype.storm.tuple.Tuple
import backtype.storm.topology.OutputFieldsDeclarer

class  AirbrakeRichBoltWrapper implements IRichBolt {
    final IRichBolt _bolt

    Map _stormConf
    TopologyContext _context

    AirbrakeRichBoltWrapper(IRichBolt bolt) {
        this._bolt = bolt
    }

    @Override
    void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        this._stormConf = stormConf
        this._context = context
        this._bolt.prepare(stormConf, context, collector)
    }

    @Override
    void execute(Tuple input) {
        try {
            _bolt.execute(input)
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

