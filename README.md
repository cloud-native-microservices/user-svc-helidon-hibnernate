# Cloud Native Microservices - User Service

## Framework

This service utilizes Helidon MP with Hibernate to persist users to a `user` table in an Oracle ATP instance.

## Setup

This service was created using the Helidon MP Maven archetype like so:

```bash
mvn archetype:generate -DinteractiveMode=false \
    -DarchetypeGroupId=io.helidon.archetypes \
    -DarchetypeArtifactId=helidon-quickstart-mp \
    -DarchetypeVersion=1.1.1 \
    -DgroupId=codes.recursive \
    -DartifactId=user-svc \
    -Dpackage=codes.recursive.cnms.user
```

The DDL to create the database table looks like so:

Create the schema/user:

```sql
CREATE USER usersvc IDENTIFIED BY "STRONGPASSWORD";
GRANT create session TO usersvc;
GRANT create table TO usersvc;
GRANT create view TO usersvc;
GRANT create any trigger TO usersvc;
GRANT create any procedure TO usersvc;
GRANT create sequence TO usersvc;
GRANT create synonym TO usersvc;
GRANT CONNECT TO usersvc;
GRANT RESOURCE TO usersvc;
GRANT UNLIMITED TABLESPACE TO usersvc;
```

Create the necessary table(s):

```sql
CREATE TABLE jpa_users(
    "ID" VARCHAR2(32 BYTE) DEFAULT ON NULL SYS_GUID(), 
	"FIRST_NAME" VARCHAR2(50 BYTE) COLLATE "USING_NLS_COMP" NOT NULL ENABLE, 
	"LAST_NAME" VARCHAR2(50 BYTE) COLLATE "USING_NLS_COMP" NOT NULL ENABLE, 
	"USERNAME" VARCHAR2(50 BYTE) COLLATE "USING_NLS_COMP" NOT NULL ENABLE, 
	"CREATED_ON" TIMESTAMP (6) DEFAULT ON NULL CURRENT_TIMESTAMP, 
	 CONSTRAINT "JPA_USER_PK" PRIMARY KEY ("ID")
);
```

## Dependencies

This project requires the following JARs from Oracle.

* ojdbc8.jar
* oraclepki.jar
* osdt_cert.jar
* osdt_core.jar

These can be downloaded [here](https://www.oracle.com/technetwork/database/application-development/jdbc/downloads/index.html). Once downloaded, place them in `build-resource/libs/`.

Install these to your local Maven via commands similar to these (modify the path and/or version numbers as appropriate):

```bash
mvn install:install-file -Dfile=/path/to/ojdbc8.jar -DgroupId=com.oracle.jdbc -DartifactId=ojdbc8 -Dversion=18.3.0.0 -Dpackaging=jar
mvn install:install-file -Dfile=/path/to/oraclepki.jar -DgroupId=com.oracle.jdbc -DartifactId=oraclepki -Dversion=18.3.0.0 -Dpackaging=jar
mvn install:install-file -Dfile=/path/to/osdt_core.jar -DgroupId=com.oracle.jdbc -DartifactId=osdt_core -Dversion=18.3.0.0 -Dpackaging=jar
mvn install:install-file -Dfile=/path/to/osdt_cert.jar -DgroupId=com.oracle.jdbc -DartifactId=osdt_cert -Dversion=18.3.0.0 -Dpackaging=jar
```
## Building

```bash
mvn package
```

## Running

Create a debug configuration in IntelliJ that runs `mvn package` and then runs the generated JAR. Pass the following properties as 'VM Options':

```bash
-Doracle.net.wallet_location=/path/to/wallet
-Doracle.net.authentication_services="(TCPS)"
-Doracle.net.tns_admin=/wallet-demodb
-Djavax.net.ssl.trustStore=/path/to/wallet/cwallet.sso
-Djavax.net.ssl.trustStoreType=SSO
-Djavax.net.ssl.keyStore=/path/to/wallet/cwallet.sso
-Djavax.net.ssl.keyStoreType=SSO
-Doracle.net.ssl_server_dn_match=true
-Doracle.net.ssl_version="1.2"
-Ddatasource.username=[Username]
-Ddatasource.password=[Strong Password]
-Ddatasource.url=jdbc:oracle:thin:@demodb_LOW?TNS_ADMIN=/path/to/wallet
```

**Note:** If you're not using IntelliJ, just make sure that you pass all properties:
 
 ```bash
java 
    -Doracle.net.wallet_location=/path/to/wallet \
    -Doracle.net.authentication_services="(TCPS)" \
    -Doracle.net.tns_admin=/wallet-demodb \
    -Djavax.net.ssl.trustStore=/path/to/wallet/cwallet.sso \
    -Djavax.net.ssl.trustStoreType=SSO \
    -Djavax.net.ssl.keyStore=/path/to/wallet/cwallet.sso \
    -Djavax.net.ssl.keyStoreType=SSO \
    -Doracle.net.ssl_server_dn_match=true \
    -Doracle.net.ssl_version="1.2" \
    -Ddatasource.username=[username] \
    -Ddatasource.password=[password] \
    -Ddatasource.url=jdbc:oracle:thin:@demodb_LOW?TNS_ADMIN=/path/to/wallet \
-jar target/user-svc.jar
```

## Test Endpoints

Get User Service Endpoint (returns 200 OK):

```
curl -iX GET http://localhost:8080/user                                                                                                                                                    
HTTP/1.1 200 OK
Content-Type: application/json
Date: Thu, 20 Jun 2019 10:35:06 -0400
transfer-encoding: chunked
connection: keep-alive
{"OK":true}                                                          
```

Save a new user (ID is returned in `Location` header):

```bash
curl -iX POST -H "Content-Type: application/json" -d '{"firstName": "Todd", "lastName": "Sharp", "username": "recursivecodes"}' http://localhost:8080/user/save                            
HTTP/1.1 201 Created
Date: Thu, 20 Jun 2019 10:45:38 -0400
Location: http://[0:0:0:0:0:0:0:1]:8080/user/8BC3669097C9EC53E0532110000A6E11
transfer-encoding: chunked
connection: keep-alive
```

Save a new user with invalid data (will return 422 and validation errors):

```bash
curl -iX POST -H "Content-Type: application/json" -d '{"firstName": "A Really Long First Name That Will Be Longer Than 50 Chars", "lastName": null, "username": null}' http://localhost:8080/user/save                            
HTTP/1.1 422 Unprocessable Entity
Content-Type: application/json
Date: Mon, 1 Jul 2019 11:21:57 -0400
transfer-encoding: chunked
connection: keep-alive

{"validationErrors":[{"field":"username","message":"may not be null","currentValue":null},{"field":"lastName","message":"may not be null","currentValue":null},{"field":"firstName","message":"size must be between 0 and 50","currentValue":"A Really Long First Name That Will Be Longer Than 50 Chars"}]}%                                    
```

Get the new user

```bash
curl -iX GET http://localhost:8080/user/8BC3669097C9EC53E0532110000A6E11                                                                                                                   
HTTP/1.1 200 OK
Content-Type: application/json
Date: Thu, 20 Jun 2019 10:46:17 -0400
transfer-encoding: chunked
connection: keep-alive

{"id":"8BC3669097C9EC53E0532110000A6E11","firstName":"Todd","lastName":"Sharp","username":"recursivecodes","createdOn":"2019-06-20T14:45:38.509Z"}
```

List all users:

```bash
curl -iX GET http://localhost:8080/user/list                                                                                                                                               
HTTP/1.1 200 OK
Content-Type: application/json
Date: Thu, 20 Jun 2019 10:46:51 -0400
transfer-encoding: chunked
connection: keep-alive

[{"id":"8BC3669097C9EC53E0532110000A6E11","firstName":"Todd","lastName":"Sharp","username":"recursivecodes","createdOn":"2019-06-20T14:45:38.509Z"}]
```

Delete a user:

```bash
curl -iX DELETE http://localhost:8080/user/8BC3669097C9EC53E0532110000A6E11                                                                                                                
HTTP/1.1 204 No Content
Date: Thu, 20 Jun 2019 10:47:21 -0400
connection: keep-alive
```

Confirm delete (same GET by ID will return 404):

```bash
curl -iX GET http://localhost:8080/user/8BC3669097C9EC53E0532110000A6E11                                                                                                                   
HTTP/1.1 404 Not Found
Date: Thu, 20 Jun 2019 10:47:43 -0400
transfer-encoding: chunked
connection: keep-alive
```

## View Health and Metrics

```bash
curl -s -X GET http://localhost:8080/health                                                                                                                                                
{"outcome":"UP","checks":[{"name":"deadlock","state":"UP"},{"name":"diskSpace","state":"UP","data":{"free":"254.50 GB","freeBytes":273264726016,"percentFree":"54.73%","total":"465.02 GB","totalBytes":499313172480}},{"name":"heapMemory","state":"UP","data":{"free":"254.45 MB","freeBytes":266813240,"max":"4.00 GB","maxBytes":4294967296,"percentFree":"98.69%","total":"308.00 MB","totalBytes":322961408}}]}
```

Prometheus Format

```bash
curl -s -X GET http://localhost:8080/metrics                                                                                                                                               
# TYPE base:classloader_current_loaded_class_count counter
# HELP base:classloader_current_loaded_class_count Displays the number of classes that are currently loaded in the Java virtual machine.
base:classloader_current_loaded_class_count 13218
# TYPE base:classloader_total_loaded_class_count counter
[Truncated]
```

Prometheus JSON Format

```bash
curl -H 'Accept: application/json' -X GET http://localhost:8080/metrics                                                                                                                    
{"base":{"classloader.currentLoadedClass.count":13229,"classloader.totalLoadedClass.count":13229,"classloader.totalUnloadedClass.count":0,"cpu.availableProcessors":4,"cpu.systemLoadAverage":3.65185546875,"gc.G1 Old Generation.count":0,"gc.G1 Old Generation.time":0,"gc.G1 Young Generation.count":9,"gc.G1 Young Generation.time":118,"jvm.uptime":556886,"memory.committedHeap":322961408,"memory.maxHeap":4294967296,"memory.usedHeap":58893312,"thread.count":59,"thread.daemon.count":45,"thread.max.count":59},"vendor":{"grpc.requests.count":0,"grpc.requests.meter":{"count":0,"meanRate":0.0,"oneMinRate":0.0,"fiveMinRate":0.0,"fifteenMinRate":0.0},"requests.count":8,"requests.meter":{"count":8,"meanRate":0.014449382373834188,"oneMinRate":0.022447789926396358,"fiveMinRate":0.009851690967428134,"fifteenMinRate":0.005533794777883567}}}
```

## Dockerfile

The generated `Dockerfile` requires some changes. See the `Dockerfile` for reference, particularly the need to install the `ojdbc` dependencies to the local Maven repo so they are included in the build since these are unavailable via public Maven repos. 

## Building the Docker Image

```
docker build -t user-svc .
```

## Running with Docker

```
docker run --rm --env DB_URL=jdbc:oracle:thin:@demodb_LOW\?TNS_ADMIN=/helidon/wallet  --env DB_PASSWORD=[Strong Password] --env DB_USER=[USER] -p 8080:8080 user-svc:latest
```

Test the endpoints as [described above](#test-endpoints)

## Deploying to Kubernetes

```
kubectl cluster-info                         # Verify which cluster
kubectl get pods                             # Verify connectivity to cluster
kubectl create -f app.yaml               # Deploy application
kubectl get service user-svc-helidon  # Verify deployed service
```