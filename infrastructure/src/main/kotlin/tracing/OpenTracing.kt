package com.github.vitormbgoncalves.starwarsmovies.infrastructure.tracing

import com.zopa.ktor.opentracing.OpenTracingServer
import com.zopa.ktor.opentracing.ThreadContextElementScopeManager
import io.jaegertracing.Configuration
import io.jaegertracing.internal.samplers.ConstSampler
import io.ktor.application.Application
import io.ktor.application.install
import io.opentracing.contrib.mongo.common.TracingCommandListener
import io.opentracing.contrib.redis.common.TracingConfiguration
import io.opentracing.util.GlobalTracer

/**
 * OpenTracing config
 *
 * @author Vitor Goncalves
 * @since 13.07.2021, ter, 11:44
 */

object OpenTracing {

  @OptIn(ExperimentalStdlibApi::class)
  fun Application.installOpenTracing() {

    GlobalTracer.registerIfAbsent(tracer)

    install(OpenTracingServer) {
      addTag("threadName") { Thread.currentThread().name }
    }
  }

  fun listener() = TracingCommandListener.Builder(tracer).build()

  private val tracer = Configuration("star-wars-movies-tracing")
    .withSampler(
      Configuration.SamplerConfiguration.fromEnv()
        .withType(ConstSampler.TYPE)
        .withParam(1)
    )
    .withReporter(
      Configuration.ReporterConfiguration.fromEnv()
        .withLogSpans(true)
        .withSender(
          Configuration.SenderConfiguration()
            .withAgentHost("jaeger")
            .withAgentPort(6831)
        )
    ).tracerBuilder
    .withScopeManager(ThreadContextElementScopeManager())
    .build()

  val tracingConfiguration: TracingConfiguration = TracingConfiguration.Builder(tracer).build()
}
