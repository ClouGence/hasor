Introduction
============

jTDS is Free Software. jTDS is released under the terms of the GNU Lesser
General Public License. A copy of the LGPL is provided in the LICENSE file. The
LGPL is sufficiently flexible to allow the use of jTDS in both open source and
commercial projects.

This document has been superseded by the HTML documentation that can be found
in the html directory. However, since it still contains pertinent information
it has been left in place. If you are a first time user please read this
document and the HTML FAQ before proceeding. It's also recommended that you
also read at least part of the HTML documentation.


License
=======

jTDS is released under the terms of the LGPL. A copy of the LGPL is provided
in the LICENSE file.


Thanks
======

jTDS is based on software written by the FreeTDS project that can be found at
http://www.freetds.org/. Much kudos goes to the developers of that software.

Lots of thanks go to SourceForge.net, who in a big part made possible the very
existence of jTDS.


Status
======

Production, Stable.

Stable for concurrent usage (Connections are multithread-safe, Statements are
completely independent). Full support is provided for forward-only and
scrollable/updateable ResultSets, PreparedStatements, and CallableStatements.

A DataSource, a ConnectionPoolDataSource and an experimental XADataSource
implementation are also provided. All of these are implemented by class
net.sourceforge.jtds.jdbcx.JtdsDataSource.

Meta data information is 99.99% complete and accurate (both ResultSetMetaData
and DatabaseMetaData). ParameterMetaData support is partial, some methods
return the same value (which is acceptable, according to the JDBC spec).

jTDS is used in a number of commercial applications. It has been tested with and
is actually recommended as the driver to use for MS SQL Server by pretty much
all open source AND commercial database management tools:

 o iSQL-Viewer (http://isql.sourceforge.net)
 o SQL Workbench/J (http://www.sql-workbench.net)
 o SQuirreL SQL Client (http://squirrel-sql.sourceforge.net)
 o Db-Visualizer (http://www.minq.se/products/dbvis/index.html)
 o SQL Developer (http://sqldeveloper.solyp.com, really nice tool).
 o Artiso Visual Case (http://www.visualcase.com)

There are quite a few database management tools that come bundled together with
jTDS:

 o DataDino (http://www.datadino.com/)
 o DBInspect (http://www.dbinspect.com/)
 o Aqua Data Studio (http://www.aquafold.com/)
 o DB Viewer (http://victorpendleton.net/products/dbviewer.html)

For more information about jTDS check out the project's homepage
(http://jtds.sourceforge.net/).


URL Format
==========

Please see the FAQ page for a more detailed explanation of the URL format and
the supported URL properties.


To Do
=====

1. Locator-based Blob/Clob implementation.
2. Minor features, such as failover support.


Contacts
========

jTDS homepage:            http://jtds.sourceforge.net/
SourceForge project info: http://sourceforge.net/projects/jtds/


Unit Tests
==========

These are the steps you should follow to run the JUnit tests provided with jTDS
(they are included in the source package, along with some reverse-engineering
tools):

1. Duplicate conf/connection.properties.tmpl as conf/connection.properties.

2. Modify conf/connection.properties to point to your server/database, and put
in your username and password. Most of the tests use only temporary tables, so
almost any user should do (there are a few tests that need to create permanent
tables but they also delete them so you should not end up with garbage in your
database).

3. Set the JAVA_HOME system property to point to your Java installation
location.

4. In a command prompt, type:

   build test

This will run a series of JUnit tests on your database. All tests should pass
normally. If any of them fails, please let us know about it (along with the
particular system configuration you were using).
