./mvnw clean package

# To test locally
./mvnw spring-boot:run

# To build a docker image
docker build --tag=dev.local/spring-xmltojson:0.0.1 .