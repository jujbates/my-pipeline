package com.myorg;
import java.util.Arrays;
import software.constructs.Construct;
import software.amazon.awscdk.Environment;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.StageProps;
import software.amazon.awscdk.pipelines.CodePipeline;
import software.amazon.awscdk.pipelines.CodePipelineSource;
import software.amazon.awscdk.pipelines.ShellStep;
public class MyPipelineStack extends Stack {
    public MyPipelineStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public MyPipelineStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);


        CodePipeline pipeline = CodePipeline.Builder.create(this, "pipeline")
                .pipelineName("MyPipeline")
                .synth(ShellStep.Builder.create("Synth")
                        .input(CodePipelineSource.gitHub("jujbates/my-pipeline", "main"))
                        .commands(Arrays.asList("npm install -g aws-cdk", "cdk synth"))
                        .build())
                .build();

        pipeline.addStage(new MyPipelineAppStage(this, "Test", StageProps.builder()
                .env(Environment.builder()
                        .account("413048945805")
                        .region("us-west-2")
                        .build())
                .build()));
    }
}
