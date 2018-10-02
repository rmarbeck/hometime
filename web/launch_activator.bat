
c:
set JAVA_HOME=C:\Developpement\Java\jdk1.8.0_25
#set JAVA_OPTS=-Djavax.net.ssl.trustStore=C:\Developpement\git\hometime\web\conf\keystore -Djavax.net.ssl.trustStorePassword=rafoufou %JAVA_OPTS%
#set JAVA_OPTS=-Djavax.net.debug=ssl -Djavax.net.ssl.trustStore=C:\Developpement\git\hometime\web\conf\keystore -Djavax.net.ssl.trustStorePassword=rafoufou %JAVA_OPTS%
set PATH=%JAVA_HOME%\bin;%PATH%
set PATH=C:\Developpement\typesafe-activator-1.2.10-minimal;%PATH%
set SYSTEM_PAY_TEST_KEY=5240800751587805
cd C:\Developpement\git\hometime\web
activator
