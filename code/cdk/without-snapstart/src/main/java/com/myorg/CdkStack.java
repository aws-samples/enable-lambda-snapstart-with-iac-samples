package com.myorg;

import java.util.Map;
import software.amazon.awscdk.Duration;
import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.apigateway.LambdaRestApi;
import software.amazon.awscdk.services.dynamodb.Attribute;
import software.amazon.awscdk.services.dynamodb.AttributeType;
import software.amazon.awscdk.services.dynamodb.BillingMode;
import software.amazon.awscdk.services.dynamodb.Table;
import software.amazon.awscdk.services.lambda.Architecture;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.Runtime;
import software.amazon.awscdk.services.s3.Bucket;
import software.amazon.awscdk.services.s3.BucketEncryption;
import software.constructs.Construct;

public class CdkStack extends Stack {
    public CdkStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public CdkStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        // DDB table
        Table table = Table.Builder.create(this, "TransactionsTable")
                .partitionKey(Attribute.builder()
                        .name("transactionId")
                        .type(AttributeType.STRING)
                        .build())
                .billingMode(BillingMode.PAY_PER_REQUEST)
                .build();

        // S3 bucket
        Bucket bucket = Bucket.Builder.create(this, "ValidationFilesS3Bucket")
                .encryption(BucketEncryption.S3_MANAGED)
                .versioned(true)
                .autoDeleteObjects(true)
                .removalPolicy(RemovalPolicy.DESTROY)
                .build();


        // Lambda function
        final Function unicornStockBrokerFunction = Function.Builder.create(this, "UnicornStockBrokerFunction")
                .runtime(Runtime.JAVA_11)
                .code(Code.fromAsset("../../UnicornStockLambda/target/UnicornStockBroker-1.0-aws.jar"))
                .handler("org.springframework.cloud.function.adapter.aws.FunctionInvoker::handleRequest")
                .memorySize(2048)
                .timeout(Duration.seconds(200))
                .architecture(Architecture.X86_64)
                .environment(Map.of(
                        "TABLE_NAME", table.getTableName(),
                        "BUCKET_NAME", bucket.getBucketName()
                ))
                .build();

        // Permissions
        table.grantReadWriteData(unicornStockBrokerFunction);
        bucket.grantReadWrite(unicornStockBrokerFunction);

        // API Gateway
        LambdaRestApi.Builder.create(this, "UnicornStockBrokerApi")
                .handler(unicornStockBrokerFunction)
                .build();

    }
}
