package com.selfdualbrain.simulator_engine

import com.selfdualbrain.abstract_consensus.Ether
import com.selfdualbrain.blockchain_structure.{ACC, AbstractNormalBlock, Block, Brick, ValidatorId}

/**
  * Encapsulates core logic of blockchain consensus: fork choice calculation, finality and equivocations detection.
  *
  * Implementation note: Isolating this as separate component helps with reusing the consensus core in various implementations of validators.
  * Typically, while core consensus mechanics is the same, validator implementations differ in bricks proposing strategy, which leads to quite
  * different blockchains.
  */
trait Finalizer {
  def addToLocalJdag(brick: Brick, isLocallyCreated: Boolean): Unit
  def calculateCurrentForkChoiceWinner(): Block
}

object Finalizer {
  trait Listener {
    def preFinality(bGameAnchor: Block, partialSummit: ACC.Summit)
    def blockFinalized(bGameAnchor: Block, finalizedBlock: AbstractNormalBlock, summit: ACC.Summit)
    def equivocationDetected(evilValidator: ValidatorId, brick1: Brick, brick2: Brick): Unit
    def equivocationCatastrophe(validators: Iterable[ValidatorId], absoluteFttExceededBy: Ether, relativeFttExceededBy: Double): Unit
  }
}
