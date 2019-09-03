

# Github
<https://github.com/Apress/def-guide-spring-batch />

# Command Line
# Win
mvn spring-boot:run -D"spring-boot.run.arguments"=name=Douglas

# Chapter 06 - transactionProcessingJob parameters
mvn spring-boot:run -D"spring-boot.run.arguments"=transactionFile=file:./src/main/resources/input/transactionFile.csv,summaryFile=file:./src/main/resources/output/summaryFile3.csv 

# Chapter 07 - fixedWithFiles parameters
mvn spring-boot:run -D"spring-boot.run.arguments"=customerFile=file:./src/main/resources/input/customerFixedWidth.dat

# Chapter 07 - delimitedFiles parameters
mvn spring-boot:run -D"spring-boot.run.arguments"=customerFile=file:./src/main/resources/input/customer.csv

# Maven archetype
mvn archetype:generate -DgroupId=com.apress.springbatch -DartifactId=definitive-guide-chXXX-

# curl (win10)



# clean tables without jobIdIncrementer

truncate table batch_job_execution cascade;
truncate table batch_step_execution cascade;
truncate table batch_job_instance cascade;