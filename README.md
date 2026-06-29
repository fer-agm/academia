Integrantes:
  - Fernanda Gajardo Moreno.
  - Sofía Vega Morales.

El proyecto presenta una academia online, Noafer, con una arquitectura de múltiples microservicios y una api-gateway como única puerta de entrada. Con mysql como base de datos compartida para todos los ms, y con jwt para autenticar. Todo esto se levanta con docker compose.



Cada microservicio tiene controller, que controla peticiones, service, que maneja la lógica del negocio, repository, que da el accesoa los datos, y model y DTO, que dictan la forma en que se deben poner los datos.

Además dde los microservicios, tenemos el api-gateway, que es la entrada para todos estos. Valida el jwt, rutea por prefijo de ruta y agrega documentación swagger a todos los ms.

Con mysql tenemos la base de datos compartida. 

Los usuarios se crean únicamente en user-service (tabla usuarios); auth-service valida las credenciales leyendo esa misma tabla y entrega el token, así que no hay registro aparte ni una tabla de credenciales duplicada, por lo que api/auth/login y api/usuarios/crear est+an abiertos siempre, mientras que todos los demás necesitan autenticación. Se hace hincapié a que sólo se registran en la api de usuarios porque para simplificar el proceso de autenticado, se podía registrar usuarios nuevos sólo con run y clave en la api de auth, pero por motivos evidentes eso no funciona, así que esa tabla quedó obsoleta y la autenticación se conecta directa y únicamente a la tabla usuarios.

La comunicación entre servicios es mediante webclient. Por ejemplo, el pago-service valida que el curso exista, y varios servicios validan sus referencias (curso, estudiante, evaluación) llamando a endpoints /existe de otros servicios y reenviando el token. Si se referencia un id que no existe, se rechaza con un 400 y un mensaje que explique qué pasó.


Servicio        -       puerto      -         responsabilidad

api-gateway     -       8080        - entrada única. autenticación jwt, ruteo y swagger agregado.                      
pago-service    -       8082        - pagos y transacciones.                                                
clases-service  -       8083        - cursos, clases y categorías.                                                   
evaluaciones-service    8084        - evaluaciones, preguntas, alternativas.                                      
inscripciones-service   8085        - inscripciones y cupos.                                                     
user-service    -       8086        - usuarios y roles.                                                        
auth-service    -       8087        - login y token jwt (valida contra la tabla usuarios, sin registro propio).                                                                                                          
certificado-service     8088        - certificados.                                                              
calificaciones-service  8089        - calificaciones y promedios.
mysql           -       3307 3306   - base de datos.



rutas (vía 8080)
prefijo                                         -       destino
/api/auth                                       -       auth-service público
/api/usuarios/**      /api/roles/**             -       user-service 
/api/pagos/**         /api/transacciones        -       pago-service

/api/cursos/**        /api/categorias/**  /api/clases/**          -       clases-service 
/api/evaluaciones/**  /api/preguntas/**   /api/alternativas/**    -       evaluaciones-service 

/api/inscripciones    /api/cupos/**             -       inscripciones-service
/api/calificaciones   /api/promedios/**         -       calificaciones-service
/api/certificados                               -       certificado-service 
/v3/api-docs/{servicio}                         -       documentación openAPI de cada servicio.



swagger / openAPI
swagger hace un menú desplegable para elegir el servicio y expone su propio v3/api-docs, y el gateway lo reescribe a v3/api-docs/{servicio}



autenticación
usando post  /api/auth/login con {'run':'xxxxxxxx-x','clave':'clave'} te devuelve un token jwt que dura 8 horas.
usando el authorization: Bearer token, el gateway valida el token y puedes ver los otros microservicios.

rutas públicas (sin token): post /api/auth/login y post /api/usuarios/crear (registro de un usuario).
todo el resto de /api/** requiere el header authorization: Bearer token. si el token falta, es inválido o expiró, el gateway responde 401 con un mensaje claro (ej. "Token no válido").



ejecutar con docker
    docker compose up --build
    docker compose ps
    docker compose logs -f auth-service
    docker compose down

se abre http://localhost:8080/swagger-ui.html



se puede hacer pruebas unitarias con mockito
para ejecutar se tiene que hacer
    cd pago-service
    ./mvnw test


despliegue remoto (aws academy / ec2)
plataforma: aws ec2, dentro del laboratorio de aws academy.
instancia ec2 con amazon linux 2023, tipo t3.large.
security grpup con puertos abiertos: 22(ssh) y 8080 (hhtp).
Elastic ip asociada a al instancia.


proceso de despliegue (dentro de la instancia):
    sudo dnf install -y docker git
    sudo systemctl enable --now docker
    git clone https://github.com/fer-agm/academia.git
    cd academia
    sudo docker compose up --build -d

acceso (IP elástica fija): http://3.220.67.111:8080/swagger-ui.html desde aws de Fernanda.


Java 21 - spring boot - spring cloud gateway - spring data jpa - mysql - springdoc.openapi (swagger) - jwt - junit - mockito - docker/docker compose - aws academy


link repositorio: 
github.com/fer-agm/academia


