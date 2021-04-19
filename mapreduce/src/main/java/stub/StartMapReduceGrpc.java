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
public final class StartMapReduceGrpc {

  private StartMapReduceGrpc() {}

  public static final String SERVICE_NAME = "StartMapReduce";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<stub.Master.Config,
      stub.Master.APIResponse> getRunMapredMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "runMapred",
      requestType = stub.Master.Config.class,
      responseType = stub.Master.APIResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<stub.Master.Config,
      stub.Master.APIResponse> getRunMapredMethod() {
    io.grpc.MethodDescriptor<stub.Master.Config, stub.Master.APIResponse> getRunMapredMethod;
    if ((getRunMapredMethod = StartMapReduceGrpc.getRunMapredMethod) == null) {
      synchronized (StartMapReduceGrpc.class) {
        if ((getRunMapredMethod = StartMapReduceGrpc.getRunMapredMethod) == null) {
          StartMapReduceGrpc.getRunMapredMethod = getRunMapredMethod = 
              io.grpc.MethodDescriptor.<stub.Master.Config, stub.Master.APIResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "StartMapReduce", "runMapred"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  stub.Master.Config.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  stub.Master.APIResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new StartMapReduceMethodDescriptorSupplier("runMapred"))
                  .build();
          }
        }
     }
     return getRunMapredMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static StartMapReduceStub newStub(io.grpc.Channel channel) {
    return new StartMapReduceStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static StartMapReduceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new StartMapReduceBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static StartMapReduceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new StartMapReduceFutureStub(channel);
  }

  /**
   */
  public static abstract class StartMapReduceImplBase implements io.grpc.BindableService {

    /**
     */
    public void runMapred(stub.Master.Config request,
        io.grpc.stub.StreamObserver<stub.Master.APIResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getRunMapredMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getRunMapredMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                stub.Master.Config,
                stub.Master.APIResponse>(
                  this, METHODID_RUN_MAPRED)))
          .build();
    }
  }

  /**
   */
  public static final class StartMapReduceStub extends io.grpc.stub.AbstractStub<StartMapReduceStub> {
    private StartMapReduceStub(io.grpc.Channel channel) {
      super(channel);
    }

    private StartMapReduceStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected StartMapReduceStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new StartMapReduceStub(channel, callOptions);
    }

    /**
     */
    public void runMapred(stub.Master.Config request,
        io.grpc.stub.StreamObserver<stub.Master.APIResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getRunMapredMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class StartMapReduceBlockingStub extends io.grpc.stub.AbstractStub<StartMapReduceBlockingStub> {
    private StartMapReduceBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private StartMapReduceBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected StartMapReduceBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new StartMapReduceBlockingStub(channel, callOptions);
    }

    /**
     */
    public stub.Master.APIResponse runMapred(stub.Master.Config request) {
      return blockingUnaryCall(
          getChannel(), getRunMapredMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class StartMapReduceFutureStub extends io.grpc.stub.AbstractStub<StartMapReduceFutureStub> {
    private StartMapReduceFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private StartMapReduceFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected StartMapReduceFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new StartMapReduceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<stub.Master.APIResponse> runMapred(
        stub.Master.Config request) {
      return futureUnaryCall(
          getChannel().newCall(getRunMapredMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_RUN_MAPRED = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final StartMapReduceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(StartMapReduceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_RUN_MAPRED:
          serviceImpl.runMapred((stub.Master.Config) request,
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

  private static abstract class StartMapReduceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    StartMapReduceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return stub.Master.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("StartMapReduce");
    }
  }

  private static final class StartMapReduceFileDescriptorSupplier
      extends StartMapReduceBaseDescriptorSupplier {
    StartMapReduceFileDescriptorSupplier() {}
  }

  private static final class StartMapReduceMethodDescriptorSupplier
      extends StartMapReduceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    StartMapReduceMethodDescriptorSupplier(String methodName) {
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
      synchronized (StartMapReduceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new StartMapReduceFileDescriptorSupplier())
              .addMethod(getRunMapredMethod())
              .build();
        }
      }
    }
    return result;
  }
}
