# Github

<https://github.com/Apress/def-guide-spring-batch />

# Cassandra Docker

<https://gokhanatil.com/2018/02/build-a-cassandra-cluster-on-docker.html />

# Command Line

# Win

mvn spring-boot:run -D"spring-boot.run.arguments"=name=Douglas

# Maven archetype

mvn archetype:generate -DgroupId=com.apress.springbatch -DartifactId=definitive-guide-chXXX-

# fixedLengthTKEJ

mvn spring-boot:run -D"spring-boot.run.arguments"=patrimonyFile=file:./src/main/resources/input/patrimony.dat
java -jar .\target\definitive-guide-bank-fixedLengthTKEJ-1.0-SNAPSHOT.jar patrimonyFile=file:
./src/main/resources/input/patrimony.dat

# Chapter 04 - helloWorldJob

mvn spring-boot:run -D"spring-boot.run.arguments"=fileName=file.csv

# Chapter 06 - transactionProcessingJob parameters

mvn spring-boot:run -D"spring-boot.run.arguments"=transactionFile=file:
./src/main/resources/input/transactionFile.csv,summaryFile=file:./src/main/resources/output/summaryFile3.csv

# Chapter 06 - scheduleQuartz

mvn spring-boot:run

# Chapter 07 - fixedWithFiles parameters

mvn spring-boot:run -D"spring-boot.run.arguments"=customerFile=file:./src/main/resources/input/customerFixedWidth.dat

# Chapter 07 - delimitedFiles parameters

mvn spring-boot:run -D"spring-boot.run.arguments"=customerFile=file:./src/main/resources/input/customer.csv

# Chapter 07 - delimitedLineTokenize parameters

mvn spring-boot:run -D"spring-boot.run.arguments"=customerFile=file:./src/main/resources/input/customer.csv

# Chapter 07 - multipleFormat parameters

mvn spring-boot:run -D"spring-boot.run.arguments"=customerFile=file:./src/main/resources/input/customerMultiFormat.csv

# Chapter 07 - multiLine parameters

mvn spring-boot:run -D"spring-boot.run.arguments"=customerFile=file:./src/main/resources/input/customerMultiFormat.csv

# Chapter 07 - multiFile parameters

mvn spring-boot:run -D"spring-boot.run.arguments"=customerFile=file:./src/main/resources/input/customerMultiFormat*

# Chapter 07 - xmlFile parameters

mvn spring-boot:run -D"spring-boot.run.arguments"=customerFile=file:./src/main/resources/input/customer.xml

# Chapter 07 - jsonFile parameters

mvn spring-boot:run -D"spring-boot.run.arguments"=customerFile=file:./src/main/resources/input/customer.json

# Chapter 07 - jdbcCursor parameters

mvn spring-boot:run -D"spring-boot.run.arguments"=city=Gary

# Chapter 07 - jdbcPagingProcessing parameters

mvn spring-boot:run -D"spring-boot.run.arguments"=city=Gary

# Chapter 07 - hibernateCursor

mvn spring-boot:run -D"spring-boot.run.arguments"=city=Chicago

# Chapter 07 - hibernatePagedProcessing parameters

mvn spring-boot:run -D"spring-boot.run.arguments"=city=Chicago

# Chapter 07 - jpaJob parameters

mvn spring-boot:run -D"spring-boot.run.arguments"=city=Chicago

# Chapter 07 - storedProcedureJob parameters

mvn spring-boot:run -D"spring-boot.run.arguments"=city=Chicago

# Chapter 07 - mongoJob parameters

mvn spring-boot:run -D"spring-boot.run.arguments"=id_str=54640519019642881

# Chapter 07 - springDataRepository parameters

mvn spring-boot:run -D"spring-boot.run.arguments"=city=Chicago

# Chapter 07 - existingService

mvn spring-boot:run

# Chapter 07 - customInput

mvn spring-boot:run

# Chapter 08 - classifierCompositeItemProcessor

mvn spring-boot:run -D"spring-boot.run.arguments"=customerFile=file:./src/main/resources/input/customer.csv,script=file:
./src/main/resources/input/lowerCase.js

# Chapter 08 - compositeItemProcessor

mvn spring-boot:run -D"spring-boot.run.arguments"=customerFile=file:./src/main/resources/input/customer.csv,script=file:
./src/main/resources/input/lowerCase.js

# Chapter 08 - customItemProcessor

mvn spring-boot:run -D"spring-boot.run.arguments"=customerFile=file:./src/main/resources/input/customer.csv

# Chapter 08 - ItemProcessorAdapter

mvn spring-boot:run -D"spring-boot.run.arguments"=customerFile=file:./src/main/resources/input/customer.csv

# Chapter 08 - scriptItemProcessor

mvn spring-boot:run -D"spring-boot.run.arguments"=customerFile=file:./src/main/resources/input/customer.csv,script=file:
./src/main/resources/input/upperCase.js

# Chapter 08 - validationProcessor

mvn spring-boot:run -D"spring-boot.run.arguments"=customerFile=file:./src/main/resources/input/customer.csv

# Chapter 09 - formattedTextLine

mvn spring-boot:run -D"spring-boot.run.arguments"=customerFile=file:
./src/main/resources/input/customer.csv,outputFile=file:./src/main/resources/output/formattedCustomer.txt

# Chapter 09 - delimitedFile

mvn spring-boot:run -D"spring-boot.run.arguments"=customerFile=file:
./src/main/resources/input/customer.csv,outputFile=file:./src/main/resources/output/delimitedCustomers.txt

# Chapter 09 - staxItemWriter

mvn spring-boot:run -D"spring-boot.run.arguments"=customerFile=file:
./src/main/resources/input/customer.csv,outputFile=file:./src/main/resources/output/customer.xml

# Chapter 09 - jdbcBatchItemWriter

mvn spring-boot:run -D"spring-boot.run.arguments"=customerFile=file:./src/main/resources/input/customer.csv

# Chapter 09 - hibernateItemWriter

mvn spring-boot:run -D"spring-boot.run.arguments"=customerFile=file:./src/main/resources/input/customer.csv

# Chapter 09 - hibernateBatchItemWriter

mvn spring-boot:run -D"spring-boot.run.arguments"=customerFile=file:./src/main/resources/input/customer.csv

# Chapter 09 - jpaBatchItemWriter

mvn spring-boot:run -D"spring-boot.run.arguments"=customerFile=file:./src/main/resources/input/customer.csv

# Chapter 09 - mongoDbItemWriter

mvn spring-boot:run -D"spring-boot.run.arguments"=customerFile=file:./src/main/resources/input/customer.csv

# Chapter 09 - repositoryImport

mvn spring-boot:run -D"spring-boot.run.arguments"=customerFile=file:./src/main/resources/input/customer.csv

# Chapter 09 - itemWriterAdapter

mvn spring-boot:run -D"spring-boot.run.arguments"=customerFile=file:./src/main/resources/input/customer.csv

# Chapter 09 - propertyExtract

mvn spring-boot:run -D"spring-boot.run.arguments"=customerFile=file:./src/main/resources/input/customer.csv

# Chapter 09 - jmsItemWriter

mvn spring-boot:run -D"spring-boot.run.arguments"=customerFile=file:
./src/main/resources/input/customer.csv,outputFile=file:./src/main/resources/output/customer.xml

# Chapter 09 - multiItemResource

mvn spring-boot:run

# Chapter 09 - multiItemResourceHeaderFooter

mvn spring-boot:run

# Chapter 09 - compositeItemWriter

mvn spring-boot:run -D"spring-boot.run.arguments"=customerFile=file:
./src/main/resources/input/customer.csv,outputFile=file:./src/main/resources/output/customer.xml

# Chapter 09 - classifierCompositeItemWriter

mvn spring-boot:run -D"spring-boot.run.arguments"=customerFile=file:
./src/main/resources/input/customer.csv,outputFile=file:./src/main/resources/output/customer.xml

# Chapter 10 - sampleApp

mvn spring-boot:run -D"spring-boot.run.arguments"=customerUpdateFile=file:
./src/main/resources/input/customer_update_shuffled.csv
mvn spring-boot:run -D"spring-boot.run.arguments"=customerUpdateFile=file:
./src/main/resources/input/customer_update_shuffled_2.csv,transactionFile=file:
./src/main/resources/input/transactions.xml,outputDirectory=file:./src/main/resources/output/file

# Chapter 11 - multiThreadedJob

mvn spring-boot:run -D"spring-boot.run.arguments"=inputFlatFile=file:./src/main/resources/input/bigtransactions.csv

# Chapter 11 - parallelStepsJob

mvn spring-boot:run -D"spring-boot.run.arguments"=inputFlatFile=file:
./src/main/resources/input/bigtransactions.csv,inputXmlFile=file:./src/main/resources/input/bigtransactions.xml

# Chapter 11 - partitionerMaster

mvn spring-boot:run -D"spring-boot.run.arguments"=inputFiles=file:./src/main/resources/data/csv/transactions*.csv

# Twenty Millions of

```shell
mvn clean compile \
 -Dspring-boot.run.arguments=uuidsFile=file:./src/main/resources/input/uuids.dat \
 spring-boot:run
```

# scheduleQuartz - openshift

mvn spring-boot:run

# Cassandra Docker command Line

docker run --name cas1 -p 9042:9042 -e CASSANDRA_CLUSTER_NAME=MyCluster -e
CASSANDRA_ENDPOINT_SNITCH=GossipingPropertyFileSnitch -e CASSANDRA_DC=datacenter1 -d cassandra

# RabbitMQ Docker command Line

docker run -d --hostname my-rabbit --name rabbitmq -e RABBITMQ_DEFAULT_USER=user -e RABBITMQ_DEFAULT_PASS=welcome1
rabbitmq:3-management

# IBM MQ Light On MacOs

minishift oc-env
eval $(minishift oc-env)

docker run --name mqlight --rm --env LICENSE=accept --env MQ_QMGR_NAME=QM1 -p 1414:1414 -p 9443:9443 ibmcom/mq

# OKD Batch

oc new-build --binary --name=spring-batch-camel -l app=spring-batch-camel
oc start-build spring-batch-camel --from-dir=. --follow
oc new-app spring-batch-camel -l app=spring-batch-camel

https://www.journaldev.com/11713/spring-amqp-rabbitmq-example