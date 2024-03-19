### Build & Run Besu
./gradlew build -x test -x javadoc -x checkSpdxHeader -x spotlessJavaCheck \

./gradlew installDist \


build/install/besu/bin/besu \
    --network=sepolia \
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
    --p2p-port=30308 \

### Build Teku

./gradlew installDist \

## Run Testnet Sepolia

build/install/teku/bin/teku \
    --network=sepolia                            \
    --ee-endpoint=http://localhost:8558          \
    --metrics-enabled=true                       \
    --rest-api-enabled=true                      \
    --checkpoint-sync-url=https://beaconstate-sepolia.chainsafe.io \


    
