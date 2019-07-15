package com.piotics.config;

import com.mongodb.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

import java.util.Arrays;

@Configuration
public class SpringMongoConfig extends AbstractMongoConfiguration {

	@Value("${mongo.server.name}")
	String mongoServerName;

	@Value("${mongo.database.name}")
	String mongoDatabaseName;

	@Value("${mongo.maxConnection}")
	Integer maxConnection;

	@Value("${mongo.username}")
	String userName;

	@Value("${mongo.password}")
	String password;

	@Autowired
	private ApplicationContext appContext;

	@Override
	protected String getDatabaseName() {
		return "ServiceBrick";
	}
	
	
	
	public Mongo mongo() throws Exception {
		MongoClientOptions.Builder builder = new MongoClientOptions.Builder();
		MongoClientOptions options = builder.connectionsPerHost(100).build();

		MongoClient client = new MongoClient(mongoServerName, options);
		client.setWriteConcern(WriteConcern.SAFE);
		return client;
	}

	@Bean
	public MongoDbFactory mongoDbFactory() {

		// Set credentials
		MongoCredential credential = MongoCredential.createCredential(userName, mongoDatabaseName,
				password.toCharArray());
		ServerAddress serverAddress = new ServerAddress(mongoServerName, 27017);

		// Mongo Client
		MongoClient mongoClient = new MongoClient(serverAddress, Arrays.asList(credential));

		// Mongo DB Factory
		SimpleMongoDbFactory simpleMongoDbFactory = new SimpleMongoDbFactory(mongoClient, mongoDatabaseName);

		return simpleMongoDbFactory;
	}

	public @Bean MongoTemplate mongoTemplate() throws Exception {
//		MongoTemplate mongoTemplate = new MongoTemplate(mongoDbFactory());
////	        mongoTemplate.setWriteResultChecking(WriteResultChecking.EXCEPTION);
//		return mongoTemplate;
		final MongoDbFactory factory = mongoDbFactory();

		final MongoMappingContext mongoMappingContext = new MongoMappingContext();
		mongoMappingContext.setApplicationContext(appContext);

		// Learned from web, prevents Spring from including the _class attribute
		final MappingMongoConverter converter = new MappingMongoConverter(factory, mongoMappingContext);
		converter.setTypeMapper(new DefaultMongoTypeMapper(null));

		return new MongoTemplate(factory, converter);
	}

	@Override
	public MongoClient mongoClient() {
		// TODO Auto-generated method stub
		return null;
	}
	
	

//	public Mongo mongo() throws Exception {
//		MongoClientOptions.Builder builder = new MongoClientOptions.Builder();
//		MongoClientOptions options = builder.connectionsPerHost(100).build();
//
//		MongoClient client = new MongoClient("localhost", options);
//		client.setWriteConcern(WriteConcern.SAFE);
//		return client;
//	}
//
//	@Bean
//	public MongoDbFactory mongoDbFactory() {
//
//		// Set credentials
//		MongoCredential credential = MongoCredential.createCredential("dbUser", "serviceBrick",
//				"mpeeDESR".toCharArray());
//		ServerAddress serverAddress = new ServerAddress("localhost", 27017);
//
//		// Mongo Client
//		MongoClient mongoClient = new MongoClient(serverAddress, Arrays.asList(credential));
//
//		// Mongo DB Factory
//		SimpleMongoDbFactory simpleMongoDbFactory = new SimpleMongoDbFactory(mongoClient, "serviceBrick");
//
//		return simpleMongoDbFactory;
//	}
//
//	public @Bean MongoTemplate mongoTemplate() throws Exception {
//		MongoTemplate mongoTemplate = new MongoTemplate(mongoDbFactory());
////	        mongoTemplate.setWriteResultChecking(WriteResultChecking.EXCEPTION);
//		return mongoTemplate;
//	}
//
//	@Override
//	public MongoClient mongoClient() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	protected String getDatabaseName() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//	
	
}