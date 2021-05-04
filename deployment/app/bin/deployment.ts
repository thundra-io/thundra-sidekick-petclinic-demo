#!/usr/bin/env node
import 'source-map-support/register';
import * as cdk from '@aws-cdk/core';
import {DeploymentStack} from '../lib/deployment-stack';

const app = new cdk.App();
new DeploymentStack(app, 'PetClinicAppDeploymentStack', {
    description: 'All resource is creating for Pet Clinic App',
    stage: process.env.STAGE || 'lab',
    env: {
        account: process.env.CDK_DEFAULT_ACCOUNT,
        region: process.env.CDK_DEFAULT_REGION
    }
});
