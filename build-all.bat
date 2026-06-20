rem $env:DOCKER_BUILDKIT=0
rem docker rm -f $(docker ps -aq)
FOR /f %%i IN ('docker ps -aq') DO docker rm -f %%i
FOR /f %%i IN ('docker images -aq') DO docker rmi -f %%i
cd api-gateway
call .\mvnw clean package -DskipTests

cd ../auth_service
call .\mvnw clean package -DskipTests

cd ../calificaciones_service
call .\mvnw clean package -DskipTests

cd ../certificado_service
call  .\mvnw clean package -DskipTests

cd ../clases-service
call .\mvnw clean package -DskipTests

cd ../evaluaciones-service
call .\mvnw clean package -DskipTests

cd ../inscripciones-service
call .\mvnw clean package -DskipTests

cd ../pago-service
call .\mvnw clean package -DskipTests

cd ../user-service
call .\mvnw clean package -DskipTests