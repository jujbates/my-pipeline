package com.myorg;

import java.util.Arrays;
import java.util.List;

import software.amazon.awscdk.*;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.stepfunctions.StateMachine;
import software.amazon.awscdk.services.stepfunctions.Succeed;
import software.amazon.awscdk.services.stepfunctions.tasks.LambdaInvoke;
import software.constructs.Construct;

import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.FunctionProps;
import software.amazon.awscdk.services.lambda.Runtime;
import software.amazon.awscdk.services.s3.assets.AssetOptions;

import static java.util.Collections.singletonList;
import static software.amazon.awscdk.BundlingOutput.ARCHIVED;


public class MyPipelineStepStack extends Stack {
    public MyPipelineStepStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public MyPipelineStepStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);


        List<String> helloWorldFunctionPackagingInstructions = Arrays.asList(
                "/bin/sh",
                "-c",

                "cd HelloWorldFunction " +
                "&& mvn clean install " +
                "&& cp /asset-input/HelloWorldFunction/target/HelloWorld-1.0.jar /asset-output/"
        );

        BundlingOptions.Builder builderOptions = BundlingOptions.builder()
                .command(helloWorldFunctionPackagingInstructions)
                .image(Runtime.JAVA_11.getBundlingImage())
                .volumes(singletonList(
                        // Mount local .m2 repo to avoid download all the dependencies again inside the container
                        DockerVolume.builder()
                                .hostPath(System.getProperty("user.home") + "/.m2/")
                                .containerPath("/root/.m2/")
                                .build()
                ))
                .user("root")
                .outputType(ARCHIVED);



        Function helloWorldFunction = new Function(this, "helloWorldFunction", FunctionProps.builder()
                .runtime(Runtime.JAVA_11)
                .code(Code.fromAsset("lambdas/HWTest/", AssetOptions.builder()
                        .bundling(builderOptions
                                .command(helloWorldFunctionPackagingInstructions)
                                .build())
                        .build()))
                .handler("helloworld.App")
                .build());


        StateMachine stateMachine = StateMachine.Builder.create(this, "MyStateMachine")
                .definition(LambdaInvoke.Builder.create(this, "MyLambdaTask")
                        .lambdaFunction(helloWorldFunction)
                        .build()
                        .next(new Succeed(this, "GreetedWorld")))
                .build();
    }
}
