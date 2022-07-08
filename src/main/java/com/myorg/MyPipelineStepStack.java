package com.myorg;

import software.amazon.awscdk.Duration;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.stepfunctions.StateMachine;
import software.amazon.awscdk.services.stepfunctions.Succeed;
import software.amazon.awscdk.services.stepfunctions.tasks.LambdaInvoke;
import software.constructs.Construct;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;

import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.Runtime;

public class MyPipelineStepStack extends Stack {
    public MyPipelineStepStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public MyPipelineStepStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        Function helloFunction = Function.Builder.create(this, "MyLambdaFunction")

                .runtime(Runtime.NODEJS_14_X)
                .handler("index.handler")
                .code(Code.fromInline(
                        "exports.handler = async function(event, context) {\n" +
                                "    context.succeed('hello world');\n" +
                                "};"))
                .timeout(Duration.seconds(25))
                .build();


        StateMachine stateMachine = StateMachine.Builder.create(this, "MyStateMachine")
                .definition(LambdaInvoke.Builder.create(this, "MyLambdaTask")
                        .lambdaFunction(helloFunction)
                        .build()
                        .next(new Succeed(this, "GreetedWorld")))
                .build();
    }
}
