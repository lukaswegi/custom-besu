### Build Besu
./gradlew build -x test -x javadoc -x checkSpdxHeader -x spotlessJavaCheck

./gradlew installDist

 build/install/besu/bin/besu \
    --rpc-http-enabled=true \
    --rpc-http-host=0.0.0.0 \
    --rpc-http-port=8554 \
    --rpc-http-cors-origins="*" \
    --rpc-ws-enabled=true \
    --rpc-ws-host=0.0.0.0 \
    --rpc-ws-port=30307 \
    --rpc-http-api=ETH,NET,WEB3,ADMIN \
    --engine-jwt-disabled=true \
    --engine-rpc-port=8558 \
    --p2p-port=30308
