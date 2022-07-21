package com.myorg;

import java.util.Arrays;
import java.util.List;

import software.amazon.awscdk.Duration;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.stepfunctions.StateMachine;
import software.amazon.awscdk.services.stepfunctions.Succeed;
import software.amazon.awscdk.services.stepfunctions.tasks.LambdaInvoke;
import software.constructs.Construct;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;

import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.FunctionProps;
import software.amazon.awscdk.services.lambda.Runtime;
import software.amazon.awscdk.services.s3.assets.AssetOptions;


public class MyPipelineStepStack extends Stack {
    public MyPipelineStepStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public MyPipelineStepStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

//
//        Function helloWorldFunction = Function.Builder.create(this, "MyLambdaFunction")
//
//                .runtime(Runtime.NODEJS_14_X)
//                .handler("index.handler")
//                .code(Code.fromInline(
//                        "exports.handler = async function(event, context) {\n" +
//                                "    context.succeed('hello world!');\n" +
//                                "};"))
//                .timeout(Duration.seconds(25))
//                .build();



        Function helloWorldFunction = new Function(this, "helloWorldFunction", FunctionProps.builder()
                .runtime(Runtime.JAVA_11)
                .code(Code.fromAsset("lambdas/HWTest/", AssetOptions.builder()
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
