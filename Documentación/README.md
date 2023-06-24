# DOCUMENTACION

Este proyecto parte de la base de la tarea propuesta en el trabajo práctico parcial E-commerce (ver apartado Entrega Parcial).

Para esta etapa vamos a trabajar en dos funcionalidades nuevas:

● Los diferentes proveedores de facturas podrán dar de alta facturas.

● Los usuarios podrán buscar sus facturas.


###### Arquitectura planteada:

![img.png](assets%2Fimg.png)

A continuación, vemos una descripción de cada uno de los elementos de este proyecto a fin de entender la configuración y el desarrollo de cada parte del mismo.

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

### 2. Configuración del archivo Application.yml

El archivo application.yml se realizó de la siguiente manera:

```python
dh:
  keycloak:
    serverUrl: http://localhost:8080/
    realm: master
    username: admin
    password: admin
    clientId: admin-cli
```

Donde establecemos la misma configuración que en la Entrega Parcial (solo que acá sin el uso del comando).

### 3. Archivo de configuración de Keycloak

Se creó una clase de configuración llamada "KeycloakConfiguration" que se encargará de leer los atributos que establecimos en el archivo application.yml para luego configurar el cliente REST de Keycloak mediante el builder "KeycloakBuilder". 

Agregamos además un Bean de tipo Keycloak para poder inyectarlo. 

### 4. Model y Service

Se creó una entidad llamada "Cliente" con los datos que se van a leer.















## Microservicio ms-bills






## Gateway



## Entrega Parcial

En esta entrega partimos de un microservicio llamado ms-bills que nos permite la gestión de las facturas.

El acceso a la aplicación se realiza a través de un Gateway que contiene como filtro de seguridad inicial la autenticación del usuario.

Se utilizó Keycloak para la gestión de la autenticación y autorización a la aplicación.

###### Arquitectura planteada:

![img_1.png](assets%2Fimg_1.png)