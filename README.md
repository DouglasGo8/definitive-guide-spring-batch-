# Github
<https://github.com/Apress/def-guide-spring-batch />

# Command Line
# Win
mvn spring-boot:run -D"spring-boot.run.arguments"=name=Douglas

# fixedLengthTKEJ
mvn spring-boot:run -D"spring-boot.run.arguments"=patrimonyFile=file:./src/main/resources/input/patrimony.dat 
java -jar .\target\definitive-guide-bank-fixedLengthTKEJ-1.0-SNAPSHOT.jar patrimonyFile=file:./src/main/resources/input/patrimony.dat 

# Chapter 04 -  helloWorldJob
mvn spring-boot:run -D"spring-boot.run.arguments"=fileName=file.csv 

# Chapter 06 - transactionProcessingJob parameters
mvn spring-boot:run -D"spring-boot.run.arguments"=transactionFile=file:./src/main/resources/input/transactionFile.csv,summaryFile=file:./src/main/resources/output/summaryFile3.csv 

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
mvn spring-boot:run -D"spring-boot.run.arguments"=customerFile=file:./src/main/resources/input/customer.csv,script=file:./src/main/resources/input/lowerCase.js





# Maven archetype
mvn archetype:generate -DgroupId=com.apress.springbatch -DartifactId=definitive-guide-chXXX-

# clean tables without jobIdIncrementer

truncate table batch_job_execution cascade;
truncate table batch_step_execution cascade;
truncate table batch_job_instance cascade;
