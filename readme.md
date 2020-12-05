Very simple maven plugin to
create java constant class from maven properties.

You can add a constant just by property definition:

    <properties>
        <constant.my.actifact>${project.artifactId}-${project.version}</constant.my.actifact>
    </properties>

define the MY_ARTIFACT constant with the given value.

The constants NAME, FULL_NAME, VERSION, BUILD are defined by default with the values 
${project.artifactId}, ${project.artifactId}, ${project.artifactId}, ${session.request.startTime}.

You can redefine there's using properties:

    <properties>
        <constant.version>${project.artifactId}-${project.version}</constant.version>
    </properties>
    
The project.artifactId is converted to class name by capitalizing all words and removing dots.
The project.groupId is used for package name.
The ${maven.build.timestamp.format} is used, if defined, for the BUILD constant.

Usage:
            <plugin>
				<groupId>org.sekaijin</groupId>
  				<artifactId>constants-maven-plugin</artifactId>
  				<version>1.0.0-SNAPSHOT</version>
       			<executions>
          			<execution>
            			<id>constants</id>
            			<goals>
              				<goal>generate</goal>
            			</goals>
          			</execution>
        		</executions>
      		</plugin>

The result is :

		package com.sap.conn;

		public class SapJco3
		{
			public static final String FULL_NAME ="SAP :: JCO :: 3";
			public static final String NAME ="sapjco3";
			public static final String BUILD ="2020-12-05 17:12:20 CET";
			public static final String VERSION ="sapjco3-3.0.0";
		} //class

      
