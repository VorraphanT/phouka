package com.selfdualbrain.simulator_engine

import com.selfdualbrain.abstract_consensus.Ether
import com.selfdualbrain.blockchain_structure._
import com.selfdualbrain.des.Event

sealed abstract class EventPayload(val filteringTag: Int)

sealed abstract class NodeEventPayload(filteringTag: Int) extends EventPayload(filteringTag)
object NodeEventPayload {
  case class BrickDelivered(brick: Brick) extends NodeEventPayload(EventTag.BRICK_DELIVERED)
  case object WakeUpForCreatingNewBrick extends NodeEventPayload(EventTag.WAKE_UP)
}

sealed abstract class OutputEventPayload(filteringTag: Int) extends EventPayload(filteringTag)
object OutputEventPayload {
  case class BrickProposed(forkChoiceWinner: Block, brickJustCreated: Brick) extends OutputEventPayload(EventTag.BRICK_PROPOSED)
  case class AcceptedIncomingBrickWithoutBuffering(brick: Brick) extends OutputEventPayload(EventTag.DIRECT_ACCEPT)
  case class AddedIncomingBrickToMsgBuffer(bufferedBrick: Brick, missingDependencies: Iterable[Brick], bufTransition: MsgBufferTransition)  extends OutputEventPayload(EventTag.ADDED_ENTRY_TO_BUF)
  case class AcceptedIncomingBrickAfterBuffering(bufferedBrick: Brick, bufTransition: MsgBufferTransition) extends OutputEventPayload(EventTag.REMOVED_ENTRY_FROM_BUF)
  case class PreFinality(bGameAnchor: Block, partialSummit: ACC.Summit) extends OutputEventPayload(EventTag.PRE_FINALITY)
  case class BlockFinalized(bGameAnchor: Block, finalizedBlock: NormalBlock, summit: ACC.Summit) extends OutputEventPayload(EventTag.FINALITY)
  case class EquivocationDetected(evilValidator: ValidatorId, brick1: Brick, brick2: Brick) extends OutputEventPayload(EventTag.EQUIVOCATION)
  case class EquivocationCatastrophe(validators: Iterable[ValidatorId], absoluteFttExceededBy: Ether, relativeFttExceededBy: Double) extends OutputEventPayload(EventTag.CATASTROPHE)
}

case class MsgBufferTransition(snapshotBefore: MsgBufferSnapshot, snapshotAfter: MsgBufferSnapshot)

object EventTag {
  val BRICK_DELIVERED = 1
  val WAKE_UP = 2
  val BRICK_PROPOSED = 3
  val DIRECT_ACCEPT = 4
  val ADDED_ENTRY_TO_BUF = 5
  val REMOVED_ENTRY_FROM_BUF = 6
  val PRE_FINALITY = 7
  val FINALITY = 8
  val EQUIVOCATION = 9
  val CATASTROPHE = 10

  val collection = Map(
    BRICK_DELIVERED -> "brick delivery",
    WAKE_UP -> "wake up",
    BRICK_PROPOSED -> "propose",
    DIRECT_ACCEPT -> "accept (direct)",
    ADDED_ENTRY_TO_BUF -> "buffering",
    REMOVED_ENTRY_FROM_BUF -> "accept (from buf)",
    PRE_FINALITY -> "pre-finality",
    FINALITY -> "block finalized",
    EQUIVOCATION -> "equivocation",
    CATASTROPHE -> "catastrophe"
  )

  def of(event: Event[ValidatorId]): Int = {
    val p: EventPayload = event match {
      case Event.External(id, timepoint, destination, payload) => throw new RuntimeException("not supported")
      case Event.MessagePassing(id, timepoint, source, destination, payload) => payload.asInstanceOf[NodeEventPayload]
      case Event.Semantic(id, timepoint, source, payload) => payload.asInstanceOf[OutputEventPayload]
    }
    return p.filteringTag
  }

  def asString(event: Event[ValidatorId]): String = collection(EventTag.of(event))

  def tag2description(eventTag: Int): String = collection(eventTag)
}



