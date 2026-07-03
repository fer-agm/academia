Integrantes:
  - Fernanda Gajardo Moreno.
  - Sofía Vega Morales.

El proyecto presenta una academia online, Noafer, con una arquitectura de múltiples microservicios y una api-gateway como única puerta de entrada. Con mysql como base de datos compartida para todos los ms, y con jwt para autenticar. Todo esto se levanta con docker compose.



Cada microservicio tiene controller, que controla peticiones, service, que maneja la lógica del negocio, repository, que da el accesoa los datos, y model y DTO, que dictan la forma en que se deben poner los datos.

Además dde los microservicios, tenemos el api-gateway, que es la entrada para todos estos. Valida el jwt, rutea por prefijo de ruta y agrega documentación swagger a todos los ms.

Con mysql tenemos la base de datos compartida. 

los usuarios se crean únicamente en user-service (tabla usuarios) y auth-service valida las credenciales leyendo esa misma tabla; no hay registro aparte.

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
notificaciones-service  8090        - notificaciones de certificado.

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
/api/notificaciones                             -       notificaciones-service                                   

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

se abre http://localhost:8080/swagger-ui.html (LOCALMENTE)






despliegue remoto (aws academy / ec2)
desplegado en aws ec2 (amazon linux 2023) con ip elástica fija.
acceso: http://3.220.67.111:8080/swagger-ui.html


Java 21 - spring boot - spring cloud gateway - spring data jpa - mysql - springdoc.openapi (swagger) - jwt - junit - mockito - docker/docker compose - aws academy - ec2 - elastic ip


link repositorio: 
github.com/fer-agm/academia


