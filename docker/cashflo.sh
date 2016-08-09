#!/bin/sh

#option="${1}"
case $1 in
  build)
    docker build -t cashflo_postgres postgres
    ;;

  start)
    CF_PW='kram'
    docker run --name cashflo_postgres -e POSTGRES_PASSWORD=postgres -p 5432:5432 -d cashflo_postgres 
    sleep 5
    docker exec -u postgres -it cashflo_postgres /bin/sh -c "/usr/lib/postgresql/9.4/bin/psql --command \"CREATE USER cashflo_postgres WITH PASSWORD '${CF_PW}';\""
    ;;

  stop)
    #docker-compose -p css stop
    docker stop cashflo_postgres
    ;;

  clean)
    docker rm cashflo_postgres
    ;;

  rebuild)
    #docker-compose -p css down --rmi all --remove-orphans
    #docker-compose -p css build
    echo "No"
    ;;
  *)
    echo "start or stop or rebuild"
    ;;
esac
