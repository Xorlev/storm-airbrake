package com.fullcontact.storm.util.airbrake

import backtype.storm.task.TopologyContext
import org.apache.http.client.HttpClient
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.client.methods.HttpPost
import org.apache.http.HttpResponse
import org.apache.http.HttpEntity
import org.apache.http.entity.StringEntity
import org.apache.http.entity.ContentType

/**
 * 2012-06-14
 * @author Michael Rose <michael@fullcontact.com>
 */
class AsyncAirbrakeNotifier implements Runnable {
    String apiKey
    String environment
    TopologyContext topologyContext
    Exception exception

    AsyncAirbrakeNotifier(String apiKey, String environment, TopologyContext topologyContext, Exception exception) {
        this.apiKey = apiKey
        this.environment = environment
        this.topologyContext = topologyContext
        this.exception = exception
    }

    @Override
    void run() {
        AirbrakeDocumentCreator creator = new AirbrakeDocumentCreator(apiKey, environment)

        submitToAirbrake(creator.createXmlDocument(topologyContext, exception))
    }

    void submitToAirbrake(String xmlDocument) {
        StringEntity stringEntity = new StringEntity(xmlDocument, ContentType.APPLICATION_XML)

        HttpClient httpClient = new DefaultHttpClient()
        HttpPost post = new HttpPost("http://airbrake.io/notifier_api/v2/notices")
        post.setEntity(stringEntity)

        HttpResponse response = httpClient.execute(post)
    }
}
