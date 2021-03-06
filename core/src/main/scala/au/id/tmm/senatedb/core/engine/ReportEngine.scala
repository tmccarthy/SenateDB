package au.id.tmm.senatedb.core.engine

import au.id.tmm.senatedb.core.model.SenateElection
import au.id.tmm.senatedb.core.reporting.ReportBuilder
import au.id.tmm.senatedb.core.reportwriting.Report
import au.id.tmm.utilities.geo.australia.State

import scala.concurrent.Future

object ReportEngine {
  import scala.concurrent.ExecutionContext.Implicits.global

  def runFor(parsedDataStore: ParsedDataStore,
             tallyEngine: TallyEngine,
             election: SenateElection,
             states: Set[State],
             reportGenerators: Set[ReportBuilder]): Future[Set[Report]] = {
    val talliers = reportGenerators.flatMap(_.requiredTalliers)

    tallyEngine.runFor(parsedDataStore, election, states, talliers)
      .map(tallies => {
        reportGenerators.map(_.buildReportFrom(tallies))
      })
  }
}
