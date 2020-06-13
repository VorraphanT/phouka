package com.selfdualbrain.blockchain_structure

import com.selfdualbrain.abstract_consensus.{PanoramaBuilderComponent, ReferenceFinalityDetectorComponent}

//concrete instance of abstract casper consensus that we use for the blockchain
//here:
// - we pick abstract consensus variant to be used in the blockchain model
// - we fill-in the extension points of abstract consensus implementation (like assigning concrete values to type params)
object ACC extends ReferenceFinalityDetectorComponent[VertexId,ValidatorId,NormalBlock, Brick] with PanoramaBuilderComponent[VertexId,ValidatorId,NormalBlock, Brick] {

  object CmApi extends ConsensusMessageApi {
    override def id(m: Brick): VertexId = m.id

    override def creator(m: Brick): ValidatorId = m.creator

    override def prevInSwimlane(m: Brick): Option[Brick] = m.prevInSwimlane

    override def justifications(m: Brick): Iterable[Brick] = m.justifications

    override def daglevel(m: Brick): VertexId = m.daglevel
  }

  override val cmApi: ConsensusMessageApi = CmApi
}
