LB_HOME=/Users/default/java_libs/liquibase-4.7.0
$LB_HOME/liquibase --driver=org.postgresql.Driver \
--classpath=$LB_HOME/lib \
--changeLogFile=databaseChangeLog.sql \
--url="jdbc:postgresql://localhost:5432/masterjava" \
--username=edu \
--password=password \
update