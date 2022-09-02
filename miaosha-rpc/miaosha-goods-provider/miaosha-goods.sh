java -server -Xms256m -Xmx512m -XX:+UseConcMarkSweepGC -XX:NewRatio=3 -XX:+UnlockExperimentalVMOptions -XX:+DisableExplicitGC -XX:+CMSParallelRemarkEnabled -XX:+UseCMSInitiatingOccupancyOnly -XX:CMSInitiatingOccupancyFraction=75 -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/var/log/miaosha-goods.hprof -Xloggc:/var/log/miaosha-goods.log -verbose:gc -jar ./target/miaosha-goods-provider.jar