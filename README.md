*Hello,*

*You can get info different configurations about wiremock  within following the bdd scenarios  as long as i create*


**Project Structure:**

      src/main                                                                    
      |--- java
           |--- helpers
                *LogHelpers.java
                *MockHelpers.java
                *ParserHelpers.java
                *PropertyHelpers.java
           
           |--- testData
                *User.java
           |--- TestRunner
                *TestRunner.java
           |--- resources
	        *log4j.properties
								
        src/test
        |--- java
            |--- stepDefinitions
                 |--- hook
                 *BeforeAfterTest.java
                 *Steps.java
           
        |--- resources
            |--- features
            |--- mock-bases
            |--- wiremock-standalone
            |--- resources
              * application.properties
              
							
							
**Technologies:**		

- Language: Java 8
- Builder: Maven 3.6.3
- Step Definition Tools:  RestAssured 3.0.2
- Assertion Tools: TestNG  1.2.5
- BDD Tool: Cucumber 1.2.5
- Helpers: Rest Assured 4.3.1, 
- Logger: log4j 1.2.17
- Mocking Tools : Wiremock 2.27.2  , Wiremock Standalone 2.27.2 , Wiremock Body Transformer 1.1.6

			
								
				
								
								

     
     
    
     
     
     
     
     

          
          

   




