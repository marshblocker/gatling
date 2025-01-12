import io.gatling.javaapi.core.*
import io.gatling.javaapi.core.CoreDsl.*
import io.gatling.javaapi.grpc.*
import io.gatling.javaapi.grpc.GrpcDsl.*

import io.grpc.*

import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit.MILLISECONDS

class GrpcMethodsSampleKotlin {

  private class RequestMessage(val message: String?)
  private class ResponseMessage(val _message: String?) {
    fun getMessage(): String? = _message
  }
  private object ExampleServiceGrpc {
    fun getExampleMethod(): MethodDescriptor<RequestMessage, ResponseMessage> = TODO()
  }

  private val grpcProtocol = grpc.forAddress("host", 50051)
  private val message = RequestMessage("hello")
  private val message1 = RequestMessage("hello")
  private val message2 = RequestMessage("hello")

  init {
    //#unaryInstantiation
    // with a static value
    grpc("request name").unary(ExampleServiceGrpc.getExampleMethod())
    // with a Gatling EL string
    grpc("#{requestName}").unary(ExampleServiceGrpc.getExampleMethod())
    // with a function
    grpc { session -> session.getString("requestName") }.unary(ExampleServiceGrpc.getExampleMethod())
    //#unaryInstantiation

    //#unaryLifecycle
    val scn = scenario("scenario name").exec(
      // Sends a request and awaits a response, similarly to regular HTTP requests
      grpc("request name")
        .unary(ExampleServiceGrpc.getExampleMethod())
        .send(RequestMessage ("hello"))
    )
    //#unaryLifecycle
  }

  init {
    //#serverStreamInstantiation
    // with a static value
    grpc("request name").serverStream(ExampleServiceGrpc.getExampleMethod())
    // with a Gatling EL string
    grpc("#{requestName}").serverStream(ExampleServiceGrpc.getExampleMethod())
    // with a function
    grpc { session -> session.getString("requestName") }.serverStream(ExampleServiceGrpc.getExampleMethod())
    //#serverStreamInstantiation

    //#serverStreamLifecycle
    val stream = grpc("request name").serverStream(ExampleServiceGrpc.getExampleMethod())

    val scn = scenario("scenario name").exec(
      stream.send(message),
      stream.awaitStreamEnd()
    )
    //#serverStreamLifecycle

    //#serverStreamNames
    val stream1 = grpc("request name")
      // specify streamName initially
      .serverStream(ExampleServiceGrpc.getExampleMethod(), "first-stream")
    val stream2 = grpc("request name")
      .serverStream(ExampleServiceGrpc.getExampleMethod())
      // or use the streamName method
      .streamName("second-stream")

    exec(
      stream1.send(message),
      stream2.send(message)
    )
    // both streams are concurrently open at this point
    //#serverStreamNames
  }

  init {
    //#clientStreamInstantiation
    // with a static value
    grpc("request name").clientStream(ExampleServiceGrpc.getExampleMethod())
    // with a Gatling EL string
    grpc("#{requestName}").clientStream(ExampleServiceGrpc.getExampleMethod())
    // with a function
    grpc { session -> session.getString("requestName") }.clientStream(ExampleServiceGrpc.getExampleMethod())
    //#clientStreamInstantiation

    //#clientStreamLifecycle
    val stream = grpc("request name").clientStream(ExampleServiceGrpc.getExampleMethod())

    val scn = scenario("scenario name").exec(
      stream.start(),
      stream.send(message1),
      stream.send(message2),
      stream.halfClose(),
      stream.awaitStreamEnd()
    )
    //#clientStreamLifecycle

    //#clientStreamNames
    val stream1 = grpc("request name")
      // specify streamName initially
      .clientStream(ExampleServiceGrpc.getExampleMethod(), "first-stream")
    val stream2 = grpc("request name")
      .clientStream(ExampleServiceGrpc.getExampleMethod())
      // or use the streamName method
      .streamName("second-stream")

    exec(
      stream1.start(),
      stream2.start()
    )
    // both streams are concurrently open at this point
    //#clientStreamNames
  }

  init {
    //#bidiStreamInstantiation
    // with a static value
    grpc("request name").bidiStream(ExampleServiceGrpc.getExampleMethod())
    // with a Gatling EL string
    grpc("#{requestName}").bidiStream(ExampleServiceGrpc.getExampleMethod())
    // with a function
    grpc { session -> session.getString("requestName") }.bidiStream(ExampleServiceGrpc.getExampleMethod())
    //#bidiStreamInstantiation

    //#bidiStreamLifecycle
    val stream = grpc("request name").bidiStream(ExampleServiceGrpc.getExampleMethod())

    val scn = scenario("scenario name").exec(
      stream.start(),
      stream.send(message1),
      stream.send(message2),
      stream.halfClose(),
      stream.awaitStreamEnd()
    )
    //#bidiStreamLifecycle

    //#bidiStreamNames
    val stream1 = grpc("request name")
      // specify streamName initially
      .bidiStream(ExampleServiceGrpc.getExampleMethod(), "first-stream")
    val stream2 = grpc("request name")
      .bidiStream(ExampleServiceGrpc.getExampleMethod())
      // or use the streamName method
      .streamName("second-stream")

    exec(
      stream1.start(),
      stream2.start()
    )
    // both streams are concurrently open at this point
    //#bidiStreamNames
  }

  init {
    //#unarySend
    // with a static payload
    grpc("name").unary(ExampleServiceGrpc.getExampleMethod())
      .send(RequestMessage("hello"))
    // with a function payload
    grpc("name").unary(ExampleServiceGrpc.getExampleMethod())
      .send { session -> RequestMessage(session.getString("message")) }
    //#unarySend
  }

  init {
    //#clientStreamSend
    val stream = grpc("name").clientStream(ExampleServiceGrpc.getExampleMethod())

    exec(
      stream.send(RequestMessage("first message")),
      stream.send(RequestMessage("second message")),
      stream.send { session -> RequestMessage(session.getString("third-message")) }
    )
    //#clientStreamSend
  }

  init {
    //#unaryAsciiHeaders
    // Extracting a map of headers allows you to reuse these in several requests
    val sentHeaders = hashMapOf(
      "header-1" to "first value",
      "header-2" to "second value"
    )

    grpc("name")
      .unary(ExampleServiceGrpc.getExampleMethod())
      .send(message)
      // Adds several headers at once
      .asciiHeaders(sentHeaders)
      // Adds another header
      .asciiHeader("header", "value")
    //#unaryAsciiHeaders
  }

  init {
    //#unaryBinaryHeaders
    // Extracting a map of headers allows you to reuse these in several requests
    val utf8 = StandardCharsets.UTF_8
    val sentHeaders = hashMapOf(
      "header-1-bin" to "first value".toByteArray(utf8),
      "header-2-bin" to "second value".toByteArray(utf8)
    )

    grpc("name")
      .unary(ExampleServiceGrpc.getExampleMethod())
      .send(message)
      // Adds several headers at once
      .binaryHeaders(sentHeaders)
      // Adds another header
      .binaryHeader("header-bin", "value".toByteArray(utf8))
    //#unaryBinaryHeaders
  }

  init {
    //#unaryCustomHeaders
    // Define custom marshallers (implementations not shown here)
    val intToAsciiMarshaller: Metadata.AsciiMarshaller<Int> = TODO()
    val doubleToBinaryMarshaller: Metadata.BinaryMarshaller<Double> = TODO()

    grpc("name")
      .unary(ExampleServiceGrpc.getExampleMethod())
      .send(message)
      // Add headers one at a time (the type of the value must match the type
      // expected by the Key's serializer, e.g. Integer for the first one here)
      .header(Metadata.Key.of("header", intToAsciiMarshaller), 123)
      .header(Metadata.Key.of("header-bin", doubleToBinaryMarshaller), 4.56)
    //#unaryCustomHeaders
  }

  init {
    //#clientStreamAsciiHeaders
    val stream = grpc("name")
      .clientStream(ExampleServiceGrpc.getExampleMethod())
      .asciiHeader("header", "value")


    exec(
      stream.start(), // Header is sent only once, on stream start
      stream.send(message1),
      stream.send(message2)
    )
    //#clientStreamAsciiHeaders
  }

  init {
    //#unaryCallOptions
    grpc("name")
      .unary(ExampleServiceGrpc.getExampleMethod())
      .send(message)
      .callOptions(CallOptions.DEFAULT.withDeadlineAfter(500, MILLISECONDS))
    //#unaryCallOptions

    //#unaryChecks
    grpc("name")
      .unary(ExampleServiceGrpc.getExampleMethod())
      .send(message)
      .check(
        statusCode().shouldBe(Status.Code.OK),
        response(ResponseMessage::getMessage).shouldBe("hello")
      )
    //#unaryChecks

    //#bidiMessageResponseTimePolicy
    grpc("name").bidiStream(ExampleServiceGrpc.getExampleMethod())
      // Default: from the start of the entire stream
      .messageResponseTimePolicy(MessageResponseTimePolicy.FromStreamStart)
      // From the time when the last request message was sent
      .messageResponseTimePolicy(MessageResponseTimePolicy.FromLastMessageSent)
      // From the time the previous response message was received
      .messageResponseTimePolicy(MessageResponseTimePolicy.FromLastMessageReceived)
    //#bidiMessageResponseTimePolicy
  }

  init {
    //#clientStreamStart
    val stream = grpc("name").clientStream(ExampleServiceGrpc.getExampleMethod())

    exec(stream.start())
    //#clientStreamStart
  }

  init {
    //#clientStreamHalfClose
    val stream = grpc("name").clientStream(ExampleServiceGrpc.getExampleMethod())

    exec(stream.halfClose())
    //#clientStreamHalfClose
  }

  init {
    //#bidiStreamWaitEnd
    val stream = grpc("name").bidiStream(ExampleServiceGrpc.getExampleMethod())

    exec(stream.awaitStreamEnd())
    //#bidiStreamWaitEnd
  }

  init {
    //#bidiStreamCancel
    val stream = grpc("name").bidiStream(ExampleServiceGrpc.getExampleMethod())

    exec(stream.cancel())
    //#bidiStreamCancel
  }
}
