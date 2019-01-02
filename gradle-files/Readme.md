
### SOFTWARE AND VERSIONS
 * Version: 1.0
 * JDK 1.8
 * Gradle
 * Spring Boot 2.0.x.
 * Spring 5.x.
 * Spring WebFlux.
 * We are using Netty as Embedded Server.
 * For Persistence, we are using Couchbase Server and spring data.
 * For Logging, slf4 is used which is backed by logback.
 * Sleuth and zipkin for distributed tracing.
 

### BUILDING PROJECT
 ##### Build components and import to the eclipse 
			
	1. Move to the base folder i.e. ${PROJECT_CHECKOUT_FOLDER}/plt-ore.
	2. Run "gradle clean install to build all the project with unit test cases.
	3. Run "gradle clean install -x test" to build all the projects without unit test cases.
	4. Run "gradle eclipse " to generate the code for eclipse. 		
	5. Import all the project to the eclipse or STS.
	6. Run microservice-sample-implementation from build\libs folder: java -jar  plp-microservice-sample-implementation-0.0.1-SNAPSHOT
	

