package com.fullcontact.storm.util.airbrake

import groovy.xml.MarkupBuilder
import backtype.storm.task.TopologyContext
import backtype.storm.generated.Bolt

/**
 * 2012-06-14
 * @author Michael Rose <michael@fullcontact.com>
 */
class AirbrakeDocumentCreator {
    String apiKey
    String environment
    private StringWriter writer
    private MarkupBuilder xml

    AirbrakeDocumentCreator(String apiKey, String environment) {
        this.apiKey = apiKey
        this.environment = environment
        this.writer = new StringWriter()
        this.xml = new MarkupBuilder(writer)
    }

    public String createXmlDocument(TopologyContext context, Exception ex) {
        StackTraceElement[] filteredStackTrace = filterStackTrace(ex?.stackTrace)

        xml.notice(version:"2.0") {
            'api-key'(apiKey)
            notifier {
                name("Airbrake Notifier for Storm")
                version("1.0.0-SNAPSHOT")
                url("https://github.com/Xorlev/storm-airbrake")
            }
            error {
                'class'(ex.getClass().canonicalName)
                message("${context.stormId} - ${ex.toString()}")
                backtrace {
                    line(method:"${ex.getClass().canonicalName} (${ex.toString()}) caused by: ${ex.cause?.toString()}", file:"<Exception> ${filteredStackTrace[0]?.fileName}", number:"-1")
                    filteredStackTrace?.each { StackTraceElement ste ->
                        if (ste) {
                            line(method:"${ste.className}.${ste.methodName}", file:"- ${ste.fileName}", number: ste.lineNumber)
                        }
                    }
                }
            }
            request {
                'url'("storm://${context.stormId}/bolt/${context.thisComponentId}")
                'component'(context.thisComponentId)
                'action'()
                'cgi-data' {
                    var(key:'stormId', context.stormId)
                    var(key:'taskId', context.thisTaskId.toString())
                    var(key:'workerPort', context.thisWorkerPort.toString())
                }
            }
            'server-environment' {
                'project-root'(context.codeDir)
                'environment-name'(environment)
            }
        }

        writer.toString()
    }

    StackTraceElement[] filterStackTrace(StackTraceElement[] stackTraceElements) {
        List<StackTraceElement> newStackTrace = []
        for (StackTraceElement ste : stackTraceElements) {
            if (!filter(ste)) {
                newStackTrace.add(ste)
            }
        }

        return newStackTrace.toArray(stackTraceElements)
    }

    boolean filter(StackTraceElement ste) {
        if (ste.className.startsWith("org.codehaus.groovy.")) return true
        if (ste.className.startsWith("java.lang.reflect.")) return true
        if (ste.className.startsWith("sun.reflect.")) return true
        if (ste.className.startsWith("com.fullcontact.storm.util.airbrake.AirbrakeBaseBasicBoltWrapper.")) return true

        return false
    }
}
