set JAVA_OPTS="-server -Xms1024M -Xmx1024M -Xss512k -XX:PermSize=256M -XX:MaxPermSize=512M"
cmd /c mvn clean package -U
rmdir /s /q .\lib
cmd /c mvn dependency:copy-dependencies -DoutputDirectory="./lib"
java -classpath ./lib/*;./target/classes com.vcg.Main