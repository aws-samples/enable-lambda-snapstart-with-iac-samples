# How to Enable Lambda Snapstart with AWS Cloudformation, AWS SAM, AWS CDK and Terraform

This Sample Code repository demonstrates how to enable Lambda SnapStart with AWS Cloudformation, AWS SAM, AWS CDK and Terraform.

For each IaC tooling, you will find 2 templates: one without SnapStart and one with SnapStart.
Each template is configured with the same architecture: an API Gateway, a Lambda Function and a DynamoDB table, and all the examples leverage the same Lambda Function code, which you can find under `code/UnicornStockLambda/`.  
This allows you to compare and contrast the differences between different implementations.

## How to use this repository

All the code in this repository can be deployed to your own AWS Account. Please note that you may incur cost when deploying the resources.

### Build the sample application

**Before** deploying the Sample, ensure that the application successfully builds
```bash
cd code/UnicornStockLambda
mvn clean package
```

### Deploy the sample
#### AWS Cloudformation (without SnapStart)
```bash
BUCKET_NAME=<<YOUR BUCKET NAME>>
FUNCTION_KEY=<<SAMPLE KEY>>
aws s3 cp code/UnicornStockLambda/target/UnicornStockBroker-1.0-aws.jar s3://$BUCKET_NAME/$FUNCTION_KEY
cd code/cloudformation
aws cloudformation deploy --template-file ./template.yaml --stack-name UnicornBrokerWithoutSnapStart --capabilities CAPABILITY_NAMED_IAM --parameter-overrides FunctionSourceCodeBucketName=$BUCKET_NAME FunctionSourceCodeKey=$FUNCTION_KEY
```
#### AWS Cloudformation (With SnapStart)
```bash
BUCKET_NAME=<<YOUR BUCKET NAME>>
FUNCTION_KEY=<<SAMPLE KEY>>
aws s3 cp code/UnicornStockLambda/target/UnicornStockBroker-1.0-aws.jar s3://$BUCKET_NAME/$FUNCTION_KEY
cd code/cloudformation
aws cloudformation deploy --template-file ./template-with-snapstart.yaml --stack-name UnicornBrokerWithSnapStart --capabilities CAPABILITY_NAMED_IAM --parameter-overrides FunctionSourceCodeBucketName=$BUCKET_NAME FunctionSourceCodeKey=$FUNCTION_KEY
```

#### AWS SAM (without SnapStart)
```bash
cd code/sam
sam build  -t template.yaml 
sam deploy --guided -t template.yaml
```
#### AWS SAM (with SnapStart)
```bash
cd code/sam
sam build -t template-with-snapstart.yaml 
sam deploy -t template-with-snapstart.yaml --guided
```

#### Terraform (without SnapStart)
```bash
cd code/terraform/without-snapstart/
terraform apply
```

#### Terraform (with SnapStart)
```bash
cd code/terraform/with-snapstart/
terraform apply
```

#### CDK (without SnapStart)
```bash
cd code/cdk/without-snapstart/
mvn clean package
cdk deploy
```

#### CDK (with SnapStart)
```bash
cd code/cdk/with-snapstart/
mvn clean package
cdk deploy
```

### Test the application
After deploying the application, you can invoke it with the following commands:
```shell
export API_GW_URL="YOUR API GATEWAY URL HERE"
curl -XPOST "$API_GW_URL/transactions" --data '{"stockId":"UNICORN_STOCK", "quantity":"2"}' --header 'Content-Type: application/json'
```

## Security

See [CONTRIBUTING](CONTRIBUTING.md#security-issue-notifications) for more information.

## License

This library is licensed under the MIT-0 License. See the LICENSE file.
