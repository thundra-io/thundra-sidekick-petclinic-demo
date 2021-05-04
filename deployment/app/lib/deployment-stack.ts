import * as cdk from '@aws-cdk/core';
import * as ec2 from '@aws-cdk/aws-ec2';
import * as secretsManager from '@aws-cdk/aws-secretsmanager';
import * as rds from '@aws-cdk/aws-rds';
import * as sqs from '@aws-cdk/aws-sqs';
import * as ecs from "@aws-cdk/aws-ecs";
import * as ecsPatterns from "@aws-cdk/aws-ecs-patterns";
import * as iam from "@aws-cdk/aws-iam";

interface DeploymentStackProps extends cdk.StackProps {
    stage: string
}

export class DeploymentStack extends cdk.Stack {

    constructor(scope: cdk.Construct, id: string, props: DeploymentStackProps) {
        super(scope, id, props);

        const thundraApiKey = this.node.tryGetContext('thundraApiKey');

        const vpc = ec2.Vpc.fromLookup(this, "DefaultVpc", {
            isDefault: true
        });

        const secret = new secretsManager.Secret(this, 'PetClinicAppRdsCredentials', {
            secretName: 'pet-clinic-app-rds-credentials',
            generateSecretString: {excludePunctuation: true}
        });

        const rdsSecurityGroup = new ec2.SecurityGroup(this, "SGPetClinicAppRds", {
            securityGroupName: 'pet-clinic-app-rds-sg',
            vpc: vpc
        });
        rdsSecurityGroup.addIngressRule(ec2.Peer.ipv4(vpc.vpcCidrBlock), ec2.Port.tcp(3306));

        const credentials = rds.Credentials.fromPassword("petclinic", secret.secretValue);

        const databaseInstance = new rds.DatabaseInstance(this, "PetClinicAppRds", {
                engine: rds.DatabaseInstanceEngine.mysql({
                    version: rds.MysqlEngineVersion.VER_8_0_21
                }),
                instanceType: ec2.InstanceType.of(ec2.InstanceClass.BURSTABLE2, ec2.InstanceSize.MICRO),
                vpc: vpc,
                multiAz: false,
                backupRetention: cdk.Duration.days(0),
                databaseName: 'petclinic',
                allocatedStorage: 20,
                deleteAutomatedBackups: true,
                deletionProtection: false,
                securityGroups: [rdsSecurityGroup],
                port: 3306,
                credentials: credentials,
                vpcSubnets: {
                    subnetType: ec2.SubnetType.PUBLIC
                }
            }
        );

        const queue = new sqs.Queue(this, "PetClinicAppSqs", {queueName: 'visit-queue'});

        const cluster = new ecs.Cluster(this, 'PetClinicAppCluster', {
            clusterName: 'pet-clinic-app-cluster', vpc: vpc
        });

        const petClinicApp = new ecsPatterns.ApplicationLoadBalancedFargateService(this, 'PetClinicAppFargateService', {
            serviceName: 'pet-clinic-app-fargate-service',
            cluster: cluster,
            cpu: 256,
            desiredCount: 1,
            memoryLimitMiB: 512,
            publicLoadBalancer: true,
            assignPublicIp: true,
            taskImageOptions: {
                image: ecs.ContainerImage.fromAsset('../../', {file: 'petclinic-app/Dockerfile'}),
                containerPort: 8080,
                enableLogging: true,
                environment: {
                    MYSQL_USER: credentials.username,
                    MYSQL_URL: 'jdbc:mysql://' + databaseInstance.dbInstanceEndpointAddress + ':' + databaseInstance.dbInstanceEndpointPort + '/petclinic',
                    QUEUE_URL: queue.queueUrl,
                    thundra_apiKey: thundraApiKey,
                    thundra_agent_trace_integrations_spring_web_path_depth: '2',
                    thundra_agent_application_name: 'petclinic-app',
                    thundra_agent_application_version: '1.0.0',
                    thundra_agent_application_stage: 'aws',
                },
                secrets: {
                    MYSQL_PASS: ecs.Secret.fromSecretsManager(secret)
                }
            }
        });
        petClinicApp.targetGroup.configureHealthCheck({
            path: '/actuator/health',
            interval: cdk.Duration.minutes(2),
            port: '8080'
        });
        queue.grantSendMessages(petClinicApp.taskDefinition.taskRole);

        const petClinicNotificationApp = new ecsPatterns.ApplicationLoadBalancedFargateService(this, 'PetClinicNotificationAppFargateService', {
            serviceName: 'pet-clinic-notification-app-fargate-service',
            cluster: cluster,
            cpu: 256,
            desiredCount: 1,
            memoryLimitMiB: 512,
            publicLoadBalancer: false,
            assignPublicIp: true,
            taskImageOptions: {
                image: ecs.ContainerImage.fromAsset('../../', {file: 'petclinic-notification-app/Dockerfile'}),
                containerPort: 8081,
                enableLogging: true,
                environment: {
                    MYSQL_USER: credentials.username,
                    MYSQL_URL: 'jdbc:mysql://' + databaseInstance.dbInstanceEndpointAddress + ':' + databaseInstance.dbInstanceEndpointPort + '/petclinic',
                    QUEUE_URL: queue.queueUrl,
                    thundra_apiKey: thundraApiKey,
                    thundra_agent_application_name: 'petclinic-notification-app',
                    thundra_agent_application_version: '1.0.1',
                    thundra_agent_application_stage: 'aws',
                },
                secrets: {
                    MYSQL_PASS: ecs.Secret.fromSecretsManager(secret)
                }
            }
        });
        petClinicNotificationApp.targetGroup.configureHealthCheck({
            path: '/actuator/health',
            interval: cdk.Duration.minutes(2),
            port: '8081'
        });
        queue.grantConsumeMessages(petClinicNotificationApp.taskDefinition.taskRole);
        petClinicNotificationApp.taskDefinition.addToTaskRolePolicy(new iam.PolicyStatement({
            effect: iam.Effect.ALLOW,
            actions: ['sns:Publish'],
            resources: ['*']
        }));
    }
}
