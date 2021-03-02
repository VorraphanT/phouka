package com.selfdualbrain.stats

import com.selfdualbrain.blockchain_structure.BlockchainNodeRef
import com.selfdualbrain.textout.AbstractTextOutput
import com.selfdualbrain.time.TimeDelta

class StatsPrinter(out: AbstractTextOutput) {

  def print(stats: BlockchainSimulationStats): Unit = {
    out.section("****** Simulation summary ******") {
      out.print(s"...............total time [sec]: ${stats.totalTime} (${stats.totalTime.asHumanReadable.toStringCutToSeconds})")
      out.print(s"...........number of validators: ${stats.numberOfValidators}")
      out.print(s".........................weight: average ${stats.averageWeight} total ${stats.totalWeight}")
      out.print(f"average computing power [sprck]: ${stats.averageComputingPower / 1000000}%.5f")
      out.print(s"...............ack level in use: ${stats.ackLevel}")
      out.print(s"......fault tolerance threshold: absolute ${stats.absoluteFTT} relative ${stats.relativeFTT}")
      out.print(s"...............blockchain nodes: total ${stats.numberOfBlockchainNodes} still alive [todo]")
      out.print(s"...............number of events: ${stats.numberOfEvents}")
      out.print(s"...............published bricks: ${stats.numberOfBlocksPublished + stats.numberOfBallotsPublished} (${stats.numberOfBlocksPublished} blocks, ${stats.numberOfBallotsPublished} ballots)")
      out.print(f"........fraction of ballots [%%]: ${stats.fractionOfBallots * 100}%.2f")
      out.print(s".....number of finalized blocks: ${stats.numberOfVisiblyFinalizedBlocks} visibly, ${stats.numberOfCompletelyFinalizedBlocks} completely")
      out.print(s"number of observed equivocators: ${stats.numberOfObservedEquivocators}")
    }

    out.newLine()
    out.section("****** Published blocks geometry ******") {
      out.print(f"........average block size [MB]: ${stats.averageBlockBinarySize / 1000000}%.5f")
      out.print(f".....average block payload [MB]: ${stats.averageBlockPayloadSize / 1000000}%.5f")
      out.print(f"......transactions in one block: ${stats.averageNumberOfTransactionsInOneBlock}%.1f")
      out.print(s".......average block cost [gas]: ${stats.averageBlockExecutionCost.toLong}")
      out.print(s".....average trans size [bytes]: ${stats.averageTransactionSize.toInt}")
      out.print(s".......average trans cost [gas]: ${stats.averageTransactionCost.toLong}")
    }

    out.newLine()
    out.section("****** Blockchain performance ******") {
      out.print(f"..................latency [sec]: ${stats.cumulativeLatency}%.2f")
      val bph = stats.totalThroughputBlocksPerSecond * 3600
      val tps = stats.totalThroughputTransactionsPerSecond
      val gps = stats.totalThroughputGasPerSecond.toLong
      out.print(f".....................throughput: [blocks/h] $bph%.2f [trans/sec] $tps%.2f [gas/sec] $gps ")
      val orphanRateAsPercent: Double = stats.orphanRate * 100
      out.print(f"................orphan rate [%%]: $orphanRateAsPercent%.2f")
      out.print(f"..........protocol overhead [%%]: ${stats.protocolOverhead * 100}%.2f")
    }

    out.newLine()
    out.section("****** Per-node possible bottlenecks (worst averages among nodes) ******") {
      out.print(f"........consumption delay [sec]: ${stats.topConsumptionDelay}%.2f")
      out.print(f"computing power utilization [%%]: ${stats.topComputingPowerUtilization * 100}%.2f")
      out.print(f".network delay for blocks [sec]: ${stats.topNetworkDelayForBlocks}%.3f")
      out.print(f"network delay for ballots [sec]: ${stats.topNetworkDelayForBallots}%.3f")
    }

      out.newLine()
    out.section("****** Per-node stats ******") {
      for (node <- 0 until stats.numberOfBlockchainNodes) {
        out.section(s"=============== node $node ===============") {
          printNodeStats(stats.perNodeStats(BlockchainNodeRef(node)))
        }
      }
    }

  }

  private def printNodeStats(stats: BlockchainPerNodeStats): Unit = {
    out.section("*** state ***") {
      out.print(s"..........................status: ${stats.status}")
      out.print(s"...........................j-dag: size ${stats.jdagSize} depth ${stats.jdagDepth}")
      out.print(s"........bricks in message buffer: ${stats.numberOfBricksInTheBuffer}")
      out.print(s"................LFB chain length: ${stats.lengthOfLfbChain}")
      out.print(s"............last brick published: ${stats.lastBrickPublished}")
      out.print(s"............last finalized block: ${stats.lastFinalizedBlock}")
      out.print(s".........last fork-choice winner: ${stats.lastForkChoiceWinner}")
      out.print(s"..................current b-game: anchored at block ${stats.lastFinalizedBlock.id} generation ${stats.lastFinalizedBlock.generation}")
      val bGameStatusDescription: String = stats.currentBGameStatus match {
        case None => "no summit"
        case Some((level, block)) => s"summit level $level for block $block"
      }
      out.print(s"...................b-game status: $bGameStatusDescription")
      out.print(s"..............known equivocators: total ${stats.numberOfObservedEquivocators} weight ${stats.weightOfObservedEquivocators} ids ${stats.knownEquivocators.mkString(",")}")
      out.print(s"......equivocation catastrophe ?: [${if (stats.isAfterObservingEquivocationCatastrophe) "x" else " "}]")
    }

    out.section("*** local performance stats ***") {
      out.print(f"...download bandwidth [MBit/sec]: ${stats.configuredDownloadBandwidth / 1000000}%.2f")
      out.print(f"....max length of download queue: [items] ${stats.downloadQueueMaxLengthAsItems} [MB] ${stats.downloadQueueMaxLengthAsBytes.toDouble / 1000000}")
      out.print(f"...........data transmitted [GB]: download ${stats.dataDownloaded.toDouble / 1000000000}%.2f upload ${stats.dataUploaded.toDouble / 1000000000}%.2f")
      out.print(s"................published bricks: ${stats.ownBricksPublished} (${stats.ownBlocksPublished} blocks, ${stats.ownBallotsPublished} ballots)")
      out.print(s".................received bricks: ${stats.allBricksReceived} (${stats.allBlocksReceived} blocks, ${stats.allBallotsReceived} ballots)")
      val accepted = stats.allBlocksAccepted + stats.allBallotsAccepted
      val acceptedBlocks = stats.allBlocksAccepted
      val acceptedBallots = stats.allBallotsAccepted
      out.print(s".................accepted bricks: $accepted ($acceptedBlocks blocks, $acceptedBallots ballots)")
      out.print(s".............own blocks finality: uncertain ${stats.ownBlocksUncertain} finalized ${stats.ownBlocksFinalized} orphaned ${stats.ownBlocksOrphaned}")
      out.print(s"............... finalization lag: ${stats.finalizationLag}")
      out.print(f"..finalization participation [%%]: ${stats.finalizationParticipation * 100}%.3f")
      out.print(f"own blocks average latency [sec]: ${stats.ownBlocksAverageLatency}%.2f")
      out.print(f"...........own blocks throughput: [blocks/h] ${stats.ownBlocksThroughputBlocksPerSecond * 3600}%.4f [trans/sec] ${stats.ownBlocksThroughputTransactionsPerSecond}%.4f [gas/sec] ${stats.ownBlocksThroughputGasPerSecond}%.4f")
      out.print(f"......own blocks orphan rate [%%]: ${stats.ownBlocksOrphanRate * 100}%.3f")
      out.print(f"....average buffering time [sec]: over bricks that left the buffer ${stats.averageBufferingTimeOverBricksThatWereBuffered}%.3f over all accepted bricks ${stats.averageBufferingTimeOverAllBricksAccepted}%.3f")
      out.print(f"....average buffering chance [%%]: ${stats.averageBufferingChanceForIncomingBricks * 100}%.3f")
      out.print(f".....average network delay [sec]: blocks ${stats.averageNetworkDelayForBlocks}%.4f ballots ${stats.averageNetworkDelayForBallots}%.4f")
      out.print(f".average consumption delay [sec]: ${stats.averageConsumptionDelay}%.8f")
      out.print(f".................computing power: nominal [gas/sec] ${stats.configuredComputingPower} utilization [%%] ${stats.averageComputingPowerUtilization * 100}%.5f")
      out.print(f".....total processing time [sec]: ${TimeDelta.toString(stats.totalComputingTimeUsed)}")
    }

    out.section("*** global performance stats ***") {
      out.print(f"......................throughput: [blocks/h] ${stats.blockchainThroughputBlocksPerSecond * 3600}%.2f [trans/sec] ${stats.blockchainThroughputTransactionsPerSecond}%.2f [gas/sec] ${stats.blockchainThroughputGasPerSecond}%.2f")
      out.print(f"...................latency [sec]: ${stats.blockchainLatency}%.2f")
      out.print(f"..................runahead [sec]: ${TimeDelta.toString(stats.blockchainRunahead)}")
      out.print(f".................orphan rate [%%]: ${stats.blockchainOrphanRate * 100}%.3f")
      out.print(f"...........protocol overhead [%%]: ${stats.dataProtocolOverhead * 100}%.2f")
    }

  }

}
