Purpose
-------

Provide limited abilities to add roles to servers via a small wrapper API

Notes
-----

Java API

http://www.jclouds.org/documentation/quickstart/chef/

example

https://github.com/jclouds/jclouds-examples/blob/master/chef-basics/src/main/java/org/jclouds/examples/chef/basics/MainApp.java

needed to add extra

- copy maven-ant-tasks.jar to lib
- drop in copy of google guava

Configuration File Things
-------------------------

* Chef URL
* Client name
* PEM location
* API Key file list
* Supported role
* TODO: Sent user name with request

Refactorings
------------

* Actually check for roles/node existence rather than rely on exceptions
* Use EL instead of JSP scripts
