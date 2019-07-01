# 1st stage, build the app
FROM maven:3.5.4-jdk-9 as build

WORKDIR /helidon

ENV MAVEN_OPTS -Dhttp.proxyHost= -Dhttp.proxyPort= -Dhttps.proxyHost= -Dhttps.proxyPort= -Dhttp.nonProxyHosts= -Dmaven.repo.local=/usr/share/maven/ref/repository
ADD pom.xml .
ADD build-resource/libs/* /helidon/build-resource/libs/

RUN ["mvn", "install:install-file", "-Dfile=/helidon/build-resource/libs/ojdbc8.jar", "-DgroupId=com.oracle.jdbc", "-DartifactId=ojdbc8", "-Dversion=18.3.0.0", "-Dpackaging=jar"]
RUN ["mvn", "install:install-file", "-Dfile=/helidon/build-resource/libs/oraclepki.jar", "-DgroupId=com.oracle.jdbc", "-DartifactId=oraclepki", "-Dversion=18.3.0.0", "-Dpackaging=jar"]
RUN ["mvn", "install:install-file", "-Dfile=/helidon/build-resource/libs/osdt_core.jar", "-DgroupId=com.oracle.jdbc", "-DartifactId=osdt_core", "-Dversion=18.3.0.0", "-Dpackaging=jar"]
RUN ["mvn", "install:install-file", "-Dfile=/helidon/build-resource/libs/osdt_cert.jar", "-DgroupId=com.oracle.jdbc", "-DartifactId=osdt_cert", "-Dversion=18.3.0.0", "-Dpackaging=jar"]

ADD src src
RUN mvn package -DskipTests

# 2nd stage, build the runtime image
FROM openjdk:9-jre-slim
WORKDIR /helidon

# Copy the binary built in the 1st stage
COPY --from=build /helidon/target/user-svc.jar ./
COPY --from=build /helidon/target/libs ./libs
RUN mkdir wallet
COPY /build-resource/wallet/* ./wallet/

EXPOSE 8080

CMD ["sh", "-c", "java -jar -Ddatasource.username=$DB_USER -Ddatasource.password=$DB_PASSWORD -Ddatasource.url=$DB_URL -Doracle.net.wallet_location=/helidon/wallet -Doracle.net.authentication_services=\"(TCPS)\" -Doracle.net.tns_admin=/helidon/wallet -Djavax.net.ssl.trustStore=/helidon/wallet/cwallet.sso -Djavax.net.ssl.trustStoreType=SSO -Djavax.net.ssl.keyStore=/helidon/wallet/cwallet.sso -Djavax.net.ssl.keyStoreType=SSO -Doracle.net.ssl_server_dn_match=true -Doracle.net.ssl_version=1.2 user-svc.jar"]

