package au.id.tmm.senatedb.tallies
import au.id.tmm.senatedb.computations.BallotWithFacts
import au.id.tmm.senatedb.model.parsing.Ballot.AtlPreferences
import au.id.tmm.senatedb.model.parsing.Preference

object CountOneAtl extends PredicateTallier {
  private val oneAtlPreferences: Set[Preference] = Set(Preference.Numbered(1), Preference.Tick, Preference.Cross)

  override def shouldCount(ballotWithFacts: BallotWithFacts): Boolean = {
    val ballot = ballotWithFacts.ballot

    ballot.btlPreferences.isEmpty && hasOnly1Atl(ballot.atlPreferences)
  }

  private def hasOnly1Atl(atlPreferences: AtlPreferences) =
    atlPreferences.size == 1 && oneAtlPreferences.contains(atlPreferences.head._2)
}
