# Keyspace
CREATE KEYSPACE aq00pld_lab WITH REPLICATION = {'class': 'SimpleStrategy', 'replication_factor': 1 };

#
use aq00pld_lab;
CREATE TABLE TBL_PATRIMONY (ssn TEXT, py_balance double, primary key (ssn));