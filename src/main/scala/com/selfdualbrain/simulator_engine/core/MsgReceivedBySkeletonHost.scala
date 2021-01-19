package com.selfdualbrain.simulator_engine.core

import com.selfdualbrain.blockchain_structure.{BlockchainNode, Brick}
import com.selfdualbrain.time.SimTimepoint

case class MsgReceivedBySkeletonHost(sender: BlockchainNode, brick: Brick, arrival: SimTimepoint)
