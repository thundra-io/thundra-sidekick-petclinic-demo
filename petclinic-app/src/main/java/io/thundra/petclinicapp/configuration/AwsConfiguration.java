package io.thundra.petclinicapp.configuration;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Configuration
public class AwsConfiguration {

	private final ThundraConfig config;

	public AwsConfiguration(ThundraConfig config) {
		this.config = config;
	}

	@Bean
	public AWSCredentialsProvider awsCredentialsProvider() {
		if (StringUtils.hasText(config.getProfile())) {
			return new ProfileCredentialsProvider(config.getProfile());
		}
		else {
			return DefaultAWSCredentialsProviderChain.getInstance();
		}
	}

	@Bean
	public AmazonSQSAsync sqsClient(AWSCredentialsProvider awsCredentialsProvider) {
		return AmazonSQSAsyncClientBuilder.standard().withCredentials(awsCredentialsProvider)
				.withRegion(config.getRegion()).build();
	}

}
