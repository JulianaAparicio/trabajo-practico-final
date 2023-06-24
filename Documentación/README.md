# DOCUMENTACION

Este proyecto parte de la base de la tarea propuesta en el trabajo práctico parcial E-commerce (ver apartado Entrega Parcial).

Para esta etapa vamos a trabajar en dos funcionalidades nuevas:

● Los diferentes proveedores de facturas podrán dar de alta facturas.

● Los usuarios podrán buscar sus facturas.


###### Arquitectura planteada:

![img.png](assets%2Fimg.png)

## Installation

Use the package manager [pip](https://pip.pypa.io/en/stable/) to install foobar.

```bash
pip install foobar
```

## Usage

```python
import foobar

# returns 'words'
foobar.pluralize('word')

# returns 'geese'
foobar.pluralize('goose')

# returns 'phenomenon'
foobar.singularize('phenomena')
```

## Entrega Parcial

En esta entrega partimos de un microservicio llamado ms-bills que nos permite la gestión de las facturas.

El acceso a la aplicación se realiza a través de un Gateway que contiene como filtro de seguridad inicial la autenticación del usuario.

Se utilizó Keycloak para la gestión de la autenticación y autorización a la aplicación.

###### Arquitectura planteada:

![img_1.png](assets%2Fimg_1.png)