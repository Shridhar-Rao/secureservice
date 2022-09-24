
# Secure Service

Author: Shridhar Rao

Secure service is a simple(and possibly not very secure) authentication and authorization service.
The service allows users to be authenticated and authorizes different behavior

-> Open below link for open api docs:

http://localhost:8080/swagger-ui-secure-service.html

-> Open below url for h2 in memory database:

http://localhost:8080/h2-console

userid is "sa" and password is "password"

-> Postman collection is at below location in project:

/src/main/resources/postmancollection/SecureService.postman_collection.json


-> Additional libraries used:

1. springdoc-openapi-ui - For open api docs
2. spring-boot-starter-security - Only for BCrypt password encoder. And SecurityAutoConfiguration is excluded on the SecureServiceApplication class.
3. spring-boot-starter-data-jpa - For using Spring Data JPA to connect to and interact with H2 in memory database



## Author

- Shridhar Rao(rlrao1987@gmail.com)

