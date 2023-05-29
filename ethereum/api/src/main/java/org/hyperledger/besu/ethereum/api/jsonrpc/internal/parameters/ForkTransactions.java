package org.hyperledger.besu.ethereum.api.jsonrpc.internal.parameters;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;


public class ForkTransactions {
    private final List<String> transactions;

    @JsonCreator
    public ForkTransactions(@JsonProperty("transactions") final List<String> transactions) {
        this.transactions = transactions;
    }

    public List<String> getTransactions() {
        return transactions;
    }
}
