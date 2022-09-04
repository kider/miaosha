java -Dfile.encoding=UTF-8 -server -Xms4g -Xmx4g -XX:+UseConcMarkSweepGC -XX:NewRatio=3 -XX:+UnlockExperimentalVMOptions -XX:+DisableExplicitGC -XX:+CMSParallelRemarkEnabled -XX:+UseCMSInitiatingOccupancyOnly -XX:CMSInitiatingOccupancyFraction=75 -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/var/log/gc/miaosha-web.hprof -Xloggc:/var/log/gc/miaosha-web.log -verbose:gc -jar ./target/miaosha-web.jar