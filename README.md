

# Github
<https://github.com/Apress/def-guide-spring-batch />

# Command Line
# Win
mvn spring-boot:run -D"spring-boot.run.arguments"=name=Douglas

mvn spring-boot:run -D"spring-boot.run.arguments"=fileName=foo.csv,name=Douglas

mvn archetype:generate -DgroupId=com.apress.springbatch -DartifactId=definitive-guide-chXXX-

# Clean Db Postgres

truncate table batch_job_execution cascade;
truncate table batch_job_instance cascade;