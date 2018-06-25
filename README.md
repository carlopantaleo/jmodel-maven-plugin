# JModel Maven Plugin

[![Build Status](https://travis-ci.org/carlopantaleo/jmodel-maven-plugin.svg?branch=master)](https://travis-ci.org/carlopantaleo/jmodel-maven-plugin)
[![codecov](https://codecov.io/gh/carlopantaleo/jmodel-maven-plugin/branch/master/graph/badge.svg)](https://codecov.io/gh/carlopantaleo/jmodel-maven-plugin)

A Maven plugin to autogenerate a bunch of boilerplate code starting from a data model described in an XML file.

It generates:
- Java entity classes
- TypeScript entity classes
- Hibernate mapping files

Please note: this plugin is in early development: it has not been extensively tested and most features are missing. 
**DO NOT USE** in production until version 1.0.0 will be released.

Anyways, any contribution is well welcomed.


## How to use

1. Include the `jmodel-maven-plugin` in the plugins section of your POM. Configure the path of your `jmodel.xml` and
`jmodel-configuration.xml` files and the project directory. Set the goals you need.

   ```xml
   <plugins>
       <plugin>
           <groupId>com.github.carlopantaleo</groupId>
           <artifactId>jmodel-maven-plugin</artifactId>
           <version>0.1.1</version>
           <configuration>
               <configurationFileName>${project.basedir}/src/main/resources/jmodel-configuration.xml</configurationFileName>
               <jmodelFileName>${project.basedir}/src/main/resources/jmodel.xml</jmodelFileName>
               <projectDir>${project.basedir}</projectDir>
           </configuration>
           <executions>
               <execution>
                   <phase>generate-sources</phase>
                   <goals>
                       <goal>generate-java-model</goal>
                       <goal>generate-hbm-files</goal>
                       <goal>generate-typescript-model</goal>
                   </goals>
               </execution>
           </executions>
       </plugin>
   </plugins>
   ``` 
2. Run `mvn compile`.
  
You can have a look of a working sample project at [https://github.com/carlopantaleo/jmodel-maven-plugin/tree/master/jmodel-sample-project](https://github.com/carlopantaleo/jmodel-maven-plugin/tree/master/jmodel-sample-project)