package au.id.tmm.senatedb.reporting.reports

import au.id.tmm.senatedb.computations.BallotWithFacts
import au.id.tmm.senatedb.model.parsing.Ballot.AtlPreferences
import au.id.tmm.senatedb.model.parsing.Preference
import au.id.tmm.senatedb.reporting.{ReportGenerator, TallyReportGenerator}

object OneAtlVoteReportGenerator extends ReportGenerator with TallyReportGenerator {
  override private[reporting] def shouldCount(ballotWithFacts: BallotWithFacts): Boolean = {
    val ballot = ballotWithFacts.ballot

    ballot.btlPreferences.isEmpty && hasOnly1Atl(ballot.atlPreferences)
  }

  private def hasOnly1Atl(atlPreferences: AtlPreferences) =
    atlPreferences.size == 1 && atlPreferences.head._2 == Preference.Numbered(1)
}