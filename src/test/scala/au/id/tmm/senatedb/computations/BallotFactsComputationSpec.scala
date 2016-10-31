package au.id.tmm.senatedb.computations

import au.id.tmm.senatedb.fixtures._
import au.id.tmm.senatedb.model.computation.{FirstPreference, NormalisedBallot}
import au.id.tmm.senatedb.model.parsing.RegisteredParty
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class BallotFactsComputationSpec extends ImprovedFlatSpec with TestsBallotFacts {
  private val ballotMaker = BallotMaker(Candidates.ACT)

  private val testBallot = Ballots.ACT.formalAtl

  "ballot facts computation" should "correctly match the original ballot to its facts" in {
    val ballotWithFacts = factsFor(testBallot)

    assert(ballotWithFacts.ballot eq testBallot)
  }

  it should "compute the normalised ballot" in {
    val ballotWithFacts = factsFor(testBallot)

    val expectedNormalisedAtl =
      ballotMaker.candidateOrder("A0", "A1", "B0", "B1", "C0", "C1", "D0", "D1", "E0", "E1", "F0", "F1")

    val expectedNormalisedBallot = NormalisedBallot(
      atlGroupOrder = ballotMaker.groupOrder("A", "B", "C", "D", "E", "F"),
      atlCandidateOrder = expectedNormalisedAtl,
      atlFormalPreferenceCount = 6,
      btlCandidateOrder = Vector.empty,
      btlFormalPreferenceCount = 0,
      canonicalOrder = expectedNormalisedAtl
    )

    assert(ballotWithFacts.normalisedBallot === expectedNormalisedBallot)
  }

  it should "compute whether the ballot is a donkey vote" in {
    val ballotWithFacts = factsFor(testBallot)

    assert(ballotWithFacts.isDonkeyVote)
  }

  it should "compute the first preferenced party" in {
    val ballotWithFacts = factsFor(testBallot)

    assert(ballotWithFacts.firstPreference ===
      FirstPreference(ballotMaker.group("A"), RegisteredParty("Liberal Democratic Party")))
  }
}
