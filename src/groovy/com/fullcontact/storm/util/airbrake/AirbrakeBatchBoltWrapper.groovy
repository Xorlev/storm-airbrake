package com.fullcontact.storm.util.airbrake

import backtype.storm.coordination.IBatchBolt
import backtype.storm.task.TopologyContext
import backtype.storm.coordination.BatchOutputCollector
import backtype.storm.tuple.Tuple
import backtype.storm.topology.OutputFieldsDeclarer
import backtype.storm.topology.base.BaseComponent

/**
 * 2012-06-14
 * @author Michael Rose <michael@fullcontact.com>
 */
class AirbrakeBatchBoltWrapper extends BaseComponent implements IBatchBolt {
    IBatchBolt _bolt
    TopologyContext _context
    Map _stormConf
    AirbrakeBatchBoltWrapper(IBatchBolt _bolt) {
        this._bolt = _bolt
        this._stormConf = _stormConf
    }

    @Override
    void prepare(Map conf, TopologyContext context, BatchOutputCollector collector, Object id) {
        _context = context

        _bolt.prepare(conf, context, collector, id)
    }

    @Override
    void execute(Tuple tuple) {
        try {
            _bolt.execute(tuple)
        } catch(Exception ex) {
            AirbrakeUtil.logException(_stormConf['airbrake.apiKey'] as String, _stormConf['airbrake.environment'] as String, _context, ex)
            //throw ex // Rethrow for normal storm handling
        }
    }

    @Override
    void finishBatch() {
        _bolt.finishBatch()
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
