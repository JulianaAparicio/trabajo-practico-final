# DOCUMENTACION

Este proyecto parte de la base de la tarea propuesta en el trabajo práctico parcial E-commerce (ver apartado Entrega Parcial).

Para esta etapa vamos a trabajar en dos funcionalidades nuevas:

● Los diferentes proveedores de facturas podrán dar de alta facturas.

● Los usuarios podrán buscar sus facturas.


###### Arquitectura planteada:

![img.png](assets%2Fimg.png)

A continuación, vemos una descripción de cada uno de los elementos de este proyecto a fin de entender la configuración y el desarrollo de cada parte del mismo.

Aclaración: todo el proyecto se realizó utilizando Java en versión 17, el jdk usado fue corretto-17 de Amazon, con lo cual es necesario tenerlo configurado dentro de la estructura del proyecto al momento de hacer la ejecución.

## Keycloak

### 1. Dependencias utilizadas

Al igual que en la Entrega Parcial utilizamos Keycloak como IAM. Para ello debemos colocar en nuestro archivo POM la dependencia necesaria:

```python
		<dependency>
			<groupId>org.keycloak</groupId>
			<artifactId>keycloak-spring-boot-starter</artifactId>
			<version>21.0.1</version>
		</dependency>
```


Sin embargo, en este caso no se utilizó el panel de administración de Keycloak, en su lugar usaremos la API de Keycloak (Keycloak ADMIN REST API) para realizar la creación del reino principal, así como también, sus respectivos clientes, usuarios y roles.

Para ello se necesita tener la siguiente dependencia en nuestro archivo POM:

```python
		<dependency>
			<groupId>org.keycloak</groupId>
			<artifactId>keycloak-admin-client</artifactId>
			<version>21.0.1</version>
			<scope>compile</scope>
		</dependency>
```

### 2. Configuración del archivo application.yml

El archivo application.yml se realizó de la siguiente manera:

```python
dh:
  keycloak:
    serverUrl: http://localhost:8080/
    realm: master
    username: admin
    password: admin
    clientId: admin-cli
    clientSecret: CQ5HsXlLFftDzWyrsHYONKIfBJFr6v6X
    
server:
  port: 8084
```

Donde establecemos la misma configuración que en la Entrega Parcial (solo que acá sin el uso del comando).


### 3. Archivo de configuración de Keycloak

Se creó una clase de configuración llamada "KeycloakConfiguration" que se encargará de leer los atributos que establecimos en el archivo application.yml para luego configurar el cliente REST de Keycloak mediante el builder "KeycloakBuilder". 

Agregamos además un Bean de tipo Keycloak para poder inyectarlo. 

### 4. Model y Service

Se creó una entidad llamada "Cliente" con los datos que se van a leer así como también una clase que representa al Service la cual contiene los siguientes métodos:

- createRealm
- deleteRealm
- createClient
- createGatewayClient
- createUser
- createRole
- createGroup
- assignUserToGroup

Dichos métodos son los que se utilizaran en la siguiente clase.

### 5. KeycloakApplication

Utilizando la inyección del Service realizamos el llamado a los métodos para crear:

1. El reino llamado "EcommerceAparicio"
2. 2 clientes (Gateway y Users)
3. 1 Rol denominado "USER".
4. 2 usuarios.
5. 1 grupo llamado "PROVIDERS".

Posteriormente asignamos ambos usuarios al grupo creado.














## Microservicio ms-bills

Se agregó dentro del Controller un nuevo endpoint para dar de alta las facturas teniendo en cuenta que solo aquellos usuarios que pertenecen al grupo "PROVIDERS" tienen acceso al mismo.

Para ello se utilizó nuevamente la anotación @PreAuthorize:

```python
@PostMapping
@PreAuthorize("hasAnyAuthority('/PROVIDERS')")
public ResponseEntity<Bill> save(@RequestBody Bill bill){
return ResponseEntity.ok().body(service.save(bill));
}
```




## Microservicio ms-users

### 1. Características

Se utilizó Spring Boot en la versión 3.0.8, Maven y Java 17. El jdk del proyecto 

### 2. Dependencias utilizadas

Este microservicio se creó utilizando las siguientes dependencias:

- Spring Web
- Lombok
- Eureka Discovery Client
- Spring Boot Actuator
- OAuth2 Client
- OAuth2 Resource Server
- OpenFeign
- Keycloak Admin Client

### 3. Configuración del archivo application.properties

El archivo application.properties se realizó de la siguiente manera:

```python
spring.application.name=ms-users
eureka.instance.hostname=localhost
server.port= 8085
eureka.instance.instance-id=${spring.application.name}:${spring.application.instance_id:${random.value}}
eureka.client.service-url.defaultZone= http://localhost:8761/eureka

dh.keycloak.serverUrl=http://localhost:8080/
dh.keycloak.realm=EcommerceAparicio
dh.keycloak.clientId=users-client
dh.keycloak.clientSecret=

spring.security.oauth2.resourceserver.jwt.issuer-uri=http://localhost:8080/realms/EcommerceAparicio
spring.security.oauth2.client.registration.keycloak.authorization-grant-type=client_credentials
spring.security.oauth2.client.registration.keycloak.client-id=users-client
spring.security.oauth2.client.registration.keycloak.client-secret=
spring.security.oauth2.client.provider.keycloak.token-uri=http://localhost:8085/realms/DH/protocol/openid-connect/token
```

### 4. Model, Repository

Dentro del model se crearon 2 clases: Bill y User.





## API Gateway

Se partió de la base del Gateway creado para la Entrega Parcial.


## Entrega Parcial

En esta entrega partimos de un microservicio llamado ms-bills que nos permite la gestión de las facturas.

El acceso a la aplicación se realiza a través de un Gateway que contiene como filtro de seguridad inicial la autenticación del usuario.

Se utilizó Keycloak para la gestión de la autenticación y autorización a la aplicación.

###### Arquitectura planteada:

![img_1.png](assets%2Fimg_1.png)