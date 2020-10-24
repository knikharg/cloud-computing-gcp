package stub;

import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ClientCalls.asyncClientStreamingCall;
import static io.grpc.stub.ClientCalls.asyncServerStreamingCall;
import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.15.0)",
    comments = "Source: Master.proto")
public final class ShutDownGrpc {

  private ShutDownGrpc() {}

  public static final String SERVICE_NAME = "ShutDown";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<stub.Master.Config,
      stub.Master.APIResponse> getShutDownMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "shutDown",
      requestType = stub.Master.Config.class,
      responseType = stub.Master.APIResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<stub.Master.Config,
      stub.Master.APIResponse> getShutDownMethod() {
    io.grpc.MethodDescriptor<stub.Master.Config, stub.Master.APIResponse> getShutDownMethod;
    if ((getShutDownMethod = ShutDownGrpc.getShutDownMethod) == null) {
      synchronized (ShutDownGrpc.class) {
        if ((getShutDownMethod = ShutDownGrpc.getShutDownMethod) == null) {
          ShutDownGrpc.getShutDownMethod = getShutDownMethod = 
              io.grpc.MethodDescriptor.<stub.Master.Config, stub.Master.APIResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "ShutDown", "shutDown"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  stub.Master.Config.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  stub.Master.APIResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new ShutDownMethodDescriptorSupplier("shutDown"))
                  .build();
          }
        }
     }
     return getShutDownMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static ShutDownStub newStub(io.grpc.Channel channel) {
    return new ShutDownStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static ShutDownBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new ShutDownBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static ShutDownFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new ShutDownFutureStub(channel);
  }

  /**
   */
  public static abstract class ShutDownImplBase implements io.grpc.BindableService {

    /**
     */
    public void shutDown(stub.Master.Config request,
        io.grpc.stub.StreamObserver<stub.Master.APIResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getShutDownMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getShutDownMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                stub.Master.Config,
                stub.Master.APIResponse>(
                  this, METHODID_SHUT_DOWN)))
          .build();
    }
  }

  /**
   */
  public static final class ShutDownStub extends io.grpc.stub.AbstractStub<ShutDownStub> {
    private ShutDownStub(io.grpc.Channel channel) {
      super(channel);
    }

    private ShutDownStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ShutDownStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new ShutDownStub(channel, callOptions);
    }

    /**
     */
    public void shutDown(stub.Master.Config request,
        io.grpc.stub.StreamObserver<stub.Master.APIResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getShutDownMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class ShutDownBlockingStub extends io.grpc.stub.AbstractStub<ShutDownBlockingStub> {
    private ShutDownBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private ShutDownBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ShutDownBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new ShutDownBlockingStub(channel, callOptions);
    }

    /**
     */
    public stub.Master.APIResponse shutDown(stub.Master.Config request) {
      return blockingUnaryCall(
          getChannel(), getShutDownMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class ShutDownFutureStub extends io.grpc.stub.AbstractStub<ShutDownFutureStub> {
    private ShutDownFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private ShutDownFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ShutDownFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new ShutDownFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<stub.Master.APIResponse> shutDown(
        stub.Master.Config request) {
      return futureUnaryCall(
          getChannel().newCall(getShutDownMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_SHUT_DOWN = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final ShutDownImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(ShutDownImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_SHUT_DOWN:
          serviceImpl.shutDown((stub.Master.Config) request,
              (io.grpc.stub.StreamObserver<stub.Master.APIResponse>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class ShutDownBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    ShutDownBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return stub.Master.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("ShutDown");
    }
  }

  private static final class ShutDownFileDescriptorSupplier
      extends ShutDownBaseDescriptorSupplier {
    ShutDownFileDescriptorSupplier() {}
  }

  private static final class ShutDownMethodDescriptorSupplier
      extends ShutDownBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    ShutDownMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (ShutDownGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new ShutDownFileDescriptorSupplier())
              .addMethod(getShutDownMethod())
              .build();
        }
      }
    }
    return result;
  }
}
