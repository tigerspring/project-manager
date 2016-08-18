JAVA_OPTS="-server -Xms1024M -Xmx1024M -Xss512k -XX:PermSize=256M -XX:MaxPermSize=512M"
git pull
../install.sh
mvn clean package -U
rm -rf ./lib
mvn dependency:copy-dependencies -DoutputDirectory="./lib"
./shutdown.sh
nohup java -classpath ./lib/*:./target/classes com.vcg.UcMain &
