package com.selfdualbrain.experiments

/**
  * The paper proves that once the finality of a block is achieved, this block will be always selected by the fork choice.
  *
  * Here we run a brute force checking that this theorem actually holds true.
  * If a violation is found, the program stops.
  * Violation means that either the theory is wrong or there is a bug in the implementation,
  * i.e. in practice this is a self-test of this implementation.  */
class FinalityTheoremChecker {
  //todo
}
