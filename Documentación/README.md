# DOCUMENTACION

Este proyecto parte de la base de la tarea propuesta en el trabajo práctico parcial E-commerce (ver apartado Entrega Parcial).

Para esta etapa vamos a trabajar en dos funcionalidades nuevas:

● Los diferentes proveedores de facturas podrán dar de alta facturas.

● Los usuarios podrán buscar sus facturas.


###### Arquitectura planteada:

![img.png](assets%2Fimg.png)

A continuación, vemos una descripción de cada uno de los elementos de este proyecto a fin de entender la configuración y el desarrollo de cada parte del mismo.

#### Aclaración: 
Todo el proyecto se realizó utilizando:

- Spring Boot 3.0.7
- Java versión 17.
- JDK usado fue corretto-17 de Amazon



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


Sin embargo, en este caso no se utilizó el panel de administración de Keycloak, en su lugar usaremos la API de Keycloak (Keycloak ADMIN REST API) para realizar la creación del reino principal, así como también, sus respectivos clientes, usuarios, grupos y roles.

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

Se creó una entidad llamada "Client" con los datos que se van a leer así como también una clase que representa al Service la cual contiene los siguientes métodos:

- createRealm (crea el reino, así como también, los roles a nivel del mismo)
- deleteRealm (borra un reino)
- createClient (crea un cliente)
- createGatewayClient (crea un cliente para el Gateway)
- createUser (crea un usuario)
- createRole (crea un rol)
- createGroup (crea un grupo)
- assignUserToGroup (asigna un usuario a un grupo determinado)

Dichos métodos son los que se utilizaran en la siguiente clase.

### 5. KeycloakApplication

Utilizando la inyección del Service realizamos el llamado a los métodos para crear:

1. El reino llamado "EcommerceAparicio"
2. 2 clientes (gateway-client y users-client)
3. 2 usuarios (juliana y pia)
4. 1 grupo llamado "PROVIDERS".
5. 1 rol llamado "USER".

Luego utilizando el método para asignar usuarios a un grupo los asociamos al mismo.

Una vez que se ejecute la aplicación se creará automáticamente todo en Keycloak.


### 6. Configuraciones adicionales

Dentro de la página de Keycloak, debemos dirigirnos a Client Scopes a profile y en el menú Mappers añadir el grupo:

![keycloak config.png](assets%2Fkeycloak%20config.png)

Además, debemos tener habilitadas esas 3 opciones que se muestran en la imagen.

Nos quedaría asi:

![keycloak config 2.png](assets%2Fkeycloak%20config%202.png)

Por otra parte, dentro de los Clients debemos habilitar en los 2 clientes que creamos los siguientes service account roles:

![service-account-roles.png](assets%2Fservice-account-roles.png)

De esta forma, tendrán acceso a la información de los usuarios.



## Microservicio ms-bills

Partiendo de nuestra base de la Entrega Parcial nos centraremos en las modificaciones que se hicieron.

### 1. JWT Converter

Como ahora necesitaremos que todos los usuarios pertenecientes al grupo "PROVIDERS" sean quienes pueden dar de alta las facturas, se añadió una modificación a esta clase para que se haga la extracción de los grupos del token.

Se creó un nuevo método para este fin:

```python
private static List<GrantedAuthority> extractGroup(String route, JsonNode jwt) {
Set<String> rolesWithPrefix = new HashSet<>();

    jwt.path(route)
            .elements()
            .forEachRemaining(r -> rolesWithPrefix.add(r.asText()));

    final List<GrantedAuthority> authorityList =
            AuthorityUtils.createAuthorityList(rolesWithPrefix.toArray(new String[0]));

    return authorityList;
}
```

Y se añadió junto con los demás de la misma forma que ya habíamos creado:

```python
resourcesRoles.addAll(extractGroup("groups", objectMapper.readTree(objectMapper.writeValueAsString(jwt)).get("claims")));
```


### 2. Bill Controller

Se agregaron 2 nuevos endpoints para:

1) Dar de alta facturas (con la condición de que solo los usuarios del grupo "PROVIDERS" puedan hacerlo).

2) Buscar facturas por ID de usuario.

Para ello se utilizó nuevamente la anotación @PreAuthorize para la restricción del primer caso:

```python
@PostMapping
@PreAuthorize("hasAnyAuthority('/PROVIDERS')")
public ResponseEntity<Bill> save(@RequestBody Bill bill){
return ResponseEntity.ok().body(service.save(bill));
}
```

En cuanto al segundo caso, la búsqueda se hace a través del ID del usuario que recibe por parámetro:

```python
@GetMapping("/findById")
public ResponseEntity<List<Bill>> getAll(@RequestParam String customerBill) {
return ResponseEntity.ok().body(service.findByCustomerId(customerBill));
}
```

Este endpoints nos traerá todas las facturas asociadas a ese usuario en particular.



## Microservicio ms-users

### 1. Dependencias utilizadas

Este microservicio se creó utilizando las siguientes dependencias:

- Spring Web
- Lombok
- Eureka Discovery Client
- Spring Boot Actuator
- OAuth2 Client
- OAuth2 Resource Server
- OpenFeign
- Keycloak Admin Client

### 2. Configuración del archivo application.properties

El archivo application.properties se realizó de la siguiente manera:

```python
spring.application.name=ms-users

server.port= 8085
server.servlet.context-path=/api/v1/

eureka.instance.hostname=localhost
eureka.instance.instance-id=${spring.application.name}:${spring.application.instance_id:${random.value}}
eureka.client.service-url.defaultZone= http://localhost:8761/eureka

dh.keycloak.serverUrl=http://localhost:8080
dh.keycloak.realm=EcommerceAparicio
dh.keycloak.clientId=users-client
dh.keycloak.clientSecret=client-secret

spring.security.oauth2.client.provider.keycloak.issuer-uri=http://localhost:8080/realms/EcommerceAparicio
spring.security.oauth2.client.registration.keycloak.authorization-grant-type=client_credentials
spring.security.oauth2.client.registration.keycloak.client-id=users-client
spring.security.oauth2.client.registration.keycloak.client-secret=client-secret
spring.security.oauth2.client.provider.keycloak.token-uri=http://localhost:8085/login/oauth2/code/keycloak
```

Como podemos observar se configuró para ser levantado en el puerto 8085 (puerto estático) mientras que la configuración del Eureka se mantuvo igual que para el microservicio ms-bills.

En cuanto a la seguridad, se implementó la configuración de Keycloak para poder conectarse correctamente.


### 3. Model

Dentro del model se crearon 2 clases: 

1) Bill (para traer las facturas utilizando Feign).

2) User (que mapea los datos del usuario de Keycloak).


### 4. Repository

Dentro de este paquete tenemos la interfaz de BillsRepository, así como también, una carpeta con la lógica de Feign dentro para la comunicación con el microservicio ms-bills.

Además, incluye la clase KeycloakUserRepository en donde se inyecta, por un lado, el Keycloak, el Feign de Bills que nos traerá la información de las facturas y el nombre del reino que obtenemos de nuestro archivo application.properties.


### 5. Service

El service contiene la lógica del método para buscar el usuario por ID e incluir todas las facturas asociadas al mismo, para ello se vale nuevamente del keycloak para obtener la información de los usuarios y Feign para la de facturas.


### 6. Controller

El controller posee un Endpoint que permite buscar un usuario por ID y sus facturas.


### 7. Configuration

Dentro de configuration tenemos, por un lado, la configuración para el uso de Feign y por otro la seguridad del microservicio.

También se añade la configuración de Keycloak para generar el cliente.


### 8. Clase principal

En MsUsersApplication se añadió la anotación @EnableFeignClients para poder hacer uso de Feign.



## API Gateway

### Configuración del archivo application.properties

Se partió de la base del Gateway creado para la Entrega Parcial.

En este caso se añadió la configuración del ruteo para el nuevo microservicio de ms-users:

```python
spring.cloud.gateway.routes[1].id=ms-users
spring.cloud.gateway.routes[1].uri=lb://ms-users
spring.cloud.gateway.routes[1].predicates=Path=/api/v1/**
```



## Entrega Parcial

En esta entrega partimos de un microservicio llamado ms-bills que nos permite la gestión de las facturas.

El acceso a la aplicación se realiza a través de un Gateway que contiene como filtro de seguridad inicial la autenticación del usuario.

Se utilizó Keycloak para la gestión de la autenticación y autorización a la aplicación.

###### Arquitectura planteada:

![img_1.png](assets%2Fimg_1.png)