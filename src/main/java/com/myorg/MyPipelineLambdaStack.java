package com.myorg;

import software.amazon.awscdk.services.lambda.Code;
import software.constructs.Construct;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;

import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.Runtime;
import software.amazon.awscdk.services.lambda.InlineCode;

public class MyPipelineLambdaStack extends Stack {
    public MyPipelineLambdaStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public MyPipelineLambdaStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);


        Function.Builder.create(this, "LambdaFunction")
                .runtime(Runtime.NODEJS_14_X)
                .handler("index.handler")
                .code(Code.fromInline(
                        "exports.handler = async (event, context) {\n" +
                                "    context.succeed('hello world');\n" +
                                "};"))
                .build();

    }

}