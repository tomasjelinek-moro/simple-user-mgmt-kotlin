# simple-user-mgmt-kotlin

Just an example project to play with the kotlin

## Prerequisites

Have a postgres DB running and listening on port ```5432```, have a db called ```some_db``` in it owned by a user with name ```some_user``` and password ```S3cret```.
You can run such db by:

1. from the root of the project do: ```cd deployment```
2. run the container with the postgres: ```podman compose up```

## Setup for development

In your IDE, once the potgres is running and is available, run the ```main``` function located in the ```Main.kt``` file.

## Usage

By default, there are two users created:

- First user: username: ```un1``` password: ```p1```
- Second user: username: ```un2``` password: ```p2```

The API is documented using swagger available at http://localhost:8080/swagger-ui/