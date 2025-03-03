/*
 * Copyright Hyperledger Besu Contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package org.hyperledger.besu.consensus.merge.blockcreation;

import org.hyperledger.besu.consensus.merge.PostMergeContext;
import org.hyperledger.besu.consensus.merge.TransitionUtils;
import org.hyperledger.besu.datatypes.Address;
import org.hyperledger.besu.datatypes.Hash;
import org.hyperledger.besu.datatypes.Wei;
import org.hyperledger.besu.ethereum.BlockProcessingResult;
import org.hyperledger.besu.ethereum.blockcreation.MiningCoordinator;
import org.hyperledger.besu.ethereum.blockcreation.PoWMiningCoordinator;
import org.hyperledger.besu.ethereum.chain.PoWObserver;
import org.hyperledger.besu.ethereum.core.Block;
import org.hyperledger.besu.ethereum.core.BlockHeader;
import org.hyperledger.besu.ethereum.core.Transaction;
import org.hyperledger.besu.ethereum.core.Withdrawal;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.apache.tuweni.bytes.Bytes;
import org.apache.tuweni.bytes.Bytes32;

/** The Transition coordinator. */
public class TransitionCoordinator extends TransitionUtils<MiningCoordinator>
    implements MergeMiningCoordinator {

  private final MiningCoordinator miningCoordinator;
  private final MergeMiningCoordinator mergeCoordinator;

  /**
   * Instantiates a new Transition coordinator.
   *
   * @param miningCoordinator the mining coordinator
   * @param mergeCoordinator the merge coordinator
   */
  public TransitionCoordinator(
      final MiningCoordinator miningCoordinator, final MiningCoordinator mergeCoordinator) {
    super(miningCoordinator, mergeCoordinator, PostMergeContext.get());
    this.miningCoordinator = miningCoordinator;
    this.mergeCoordinator = (MergeMiningCoordinator) mergeCoordinator;
  }

  /**
   * Gets merge coordinator.
   *
   * @return the merge coordinator
   */
  public MergeMiningCoordinator getMergeCoordinator() {
    return mergeCoordinator;
  }

  @Override
  public void start() {
    if (isMiningBeforeMerge()) {
      miningCoordinator.start();
    }
  }

  @Override
  public void stop() {
    miningCoordinator.stop();
  }

  @Override
  public void awaitStop() throws InterruptedException {
    miningCoordinator.awaitStop();
  }

  @Override
  public boolean enable() {
    return dispatchFunctionAccordingToMergeState(MiningCoordinator::enable);
  }

  @Override
  public boolean disable() {
    return dispatchFunctionAccordingToMergeState(MiningCoordinator::disable);
  }

  @Override
  public boolean isMining() {
    return dispatchFunctionAccordingToMergeState(MiningCoordinator::isMining);
  }

  @Override
  public Wei getMinTransactionGasPrice() {
    return dispatchFunctionAccordingToMergeState(MiningCoordinator::getMinTransactionGasPrice);
  }

  @Override
  public void setExtraData(final Bytes extraData) {
    miningCoordinator.setExtraData(extraData);
    mergeCoordinator.setExtraData(extraData);
  }

  @Override
  public Optional<Address> getCoinbase() {
    return dispatchFunctionAccordingToMergeState(MiningCoordinator::getCoinbase);
  }

  @Override
  public Optional<Block> createBlock(
      final BlockHeader parentHeader,
      final List<Transaction> transactions,
      final List<BlockHeader> ommers) {
    return dispatchFunctionAccordingToMergeState(
        (MiningCoordinator coordinator) ->
            miningCoordinator.createBlock(parentHeader, transactions, ommers));
  }

  @Override
  public Optional<Block> createBlock(final BlockHeader parentHeader, final long timestamp) {
    return dispatchFunctionAccordingToMergeState(
        (MiningCoordinator coordinator) -> coordinator.createBlock(parentHeader, timestamp));
  }

  @Override
  public void addEthHashObserver(final PoWObserver observer) {
    if (this.miningCoordinator instanceof PoWMiningCoordinator) {
      miningCoordinator.addEthHashObserver(observer);
    }
  }

  @Override
  public void changeTargetGasLimit(final Long targetGasLimit) {
    miningCoordinator.changeTargetGasLimit(targetGasLimit);
    mergeCoordinator.changeTargetGasLimit(targetGasLimit);
  }

  @Override
  public PayloadIdentifier preparePayload(
      final BlockHeader parentHeader,
      final Long timestamp,
      final Bytes32 prevRandao,
      final Address feeRecipient,
      final Optional<List<Withdrawal>> withdrawals) {
    return mergeCoordinator.preparePayload(
        parentHeader, timestamp, prevRandao, feeRecipient, withdrawals);
  }

  @Override
  public PayloadIdentifier preparePayload(final BlockHeader parentHeader, final Long timestamp, final Bytes32 prevRandao, final Address feeRecipient, final Optional<List<Withdrawal>> withdrawals, final List<Transaction> transactions) {
    return mergeCoordinator.preparePayload(
            parentHeader, timestamp, prevRandao, feeRecipient, withdrawals, transactions
    );
  }

  @Override
  public BlockProcessingResult rememberBlock(final Block block) {
    return mergeCoordinator.rememberBlock(block);
  }

  @Override
  public BlockProcessingResult validateBlock(final Block block) {
    return mergeCoordinator.validateBlock(block);
  }

  @Override
  public ForkchoiceResult updateForkChoice(
      final BlockHeader newHead, final Hash finalizedBlockHash, final Hash safeBlockHash) {
    return mergeCoordinator.updateForkChoice(newHead, finalizedBlockHash, safeBlockHash);
  }

  @Override
  public Optional<Hash> getLatestValidAncestor(final Hash blockHash) {
    return mergeCoordinator.getLatestValidAncestor(blockHash);
  }

  @Override
  public Optional<Hash> getLatestValidAncestor(final BlockHeader blockHeader) {
    return mergeCoordinator.getLatestValidAncestor(blockHeader);
  }

  @Override
  public boolean latestValidAncestorDescendsFromTerminal(final BlockHeader blockHeader) {
    // this is nonsensical pre-merge, but should be fine to delegate
    return mergeCoordinator.latestValidAncestorDescendsFromTerminal(blockHeader);
  }

  @Override
  public boolean isBackwardSyncing() {
    return mergeCoordinator.isBackwardSyncing();
  }

  @Override
  public CompletableFuture<Void> appendNewPayloadToSync(final Block newPayload) {
    return mergeCoordinator.appendNewPayloadToSync(newPayload);
  }

  @Override
  public Optional<BlockHeader> getOrSyncHeadByHash(final Hash headHash, final Hash finalizedHash) {
    return mergeCoordinator.getOrSyncHeadByHash(headHash, finalizedHash);
  }

  @Override
  public boolean isMiningBeforeMerge() {
    return mergeCoordinator.isMiningBeforeMerge();
  }

  @Override
  public boolean isDescendantOf(final BlockHeader ancestorBlock, final BlockHeader newBlock) {
    return mergeCoordinator.isDescendantOf(ancestorBlock, newBlock);
  }

  @Override
  public void addBadBlock(final Block block, final Optional<Throwable> maybeCause) {
    mergeCoordinator.addBadBlock(block, maybeCause);
  }

  @Override
  public boolean isBadBlock(final Hash blockHash) {
    return mergeCoordinator.isBadBlock(blockHash);
  }

  @Override
  public Optional<Hash> getLatestValidHashOfBadBlock(final Hash blockHash) {
    return mergeCoordinator.getLatestValidHashOfBadBlock(blockHash);
  }

  @Override
  public void finalizeProposalById(final PayloadIdentifier payloadId) {
    mergeCoordinator.finalizeProposalById(payloadId);
  }
}
