
d:
set JAVA_HOME=D:\Autres\Developpement\java\jdk1.8.0_20
set JAVA_OPTS=%JAVA_OPTS% -Djavax.net.ssl.trustStore="D:\Autres\Developpement\java\jdk1.8.0_20\jre\lib\security\cacerts" -Djavax.net.ssl.trustStorePassword="changeit"
set PATH=%JAVA_HOME%\bin;%PATH%
set PATH=D:\Autres\Developpement\typesafe-activator-1.2.10-minimal;%PATH%
cd D:\Autres\Developpement\Eclipse\luna-git-workspace\hometime\web
activator
