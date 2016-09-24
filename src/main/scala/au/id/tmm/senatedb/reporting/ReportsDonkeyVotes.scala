package au.id.tmm.senatedb.reporting

import scala.concurrent.Future

trait ReportsDonkeyVotes { this: ReportingUtilities with ReportsFormalBallots =>

  import api._

  private val baseQuery = {
    val joinedRows = for {
      (ballots, ballotFacts) <- dal.ballots join dal.ballotFacts on (_.ballotId === _.ballotId)
    } yield (ballots, ballotFacts)

    joinedRows
      .filter {
        case (ballot, ballotFacts) => ballotFacts.isDonkeyVote
      }
      .map {
        case (ballot, _) => ballot
      }
  }

  def constructDonkeyVoteReport: Future[Report] = {
    for {
      perStateTable <- constructPerStateTable
      perElectorateTable <- constructPerElectorateTable
    } yield Report("Donkey votes", perStateTable, perElectorateTable)
  }

  private val perStateQuery = baseQuery
    .groupBy(_.state)
    .map {
      case (state, rowsPerState) => (state, rowsPerState.length)
    }

  private val donkeyVoteTallyByStateFuture = {
    val byStateName = persistence.runQuery(perStateQuery)

    fillInStates(byStateName)
  }

  private def constructPerStateTable: Future[ReportTable] = {
    val sql = sqlOf(perStateQuery)

    val tableFuture = perStateTableFrom(
      talliesColTitle = "Donkey votes",
      talliesPerStateFuture = donkeyVoteTallyByStateFuture,
      denominatorType = DenominatorType.ByStateTotal
    )

    tableFuture.map(_.copy(description = sql))
  }

  private val perDivisionQuery = baseQuery
    .groupBy(row => row.electorate)
    .map {
      case (key, rowsPerElectorate) => (key, rowsPerElectorate.map(_.ballotId).size)
    }

  private val donkeyVoteTallyByDivisionFuture = {
    val byDivisionName = persistence.runQuery(perDivisionQuery)

    fillInDivisions(byDivisionName)
  }

  private def constructPerElectorateTable: Future[ReportTable] = {
    val sql = sqlOf(perDivisionQuery)

    val tableFuture = perDivisionTableFrom(
      talliesColTitle = "Donkey votes",
      talliesPerDivisionFuture = donkeyVoteTallyByDivisionFuture,
      denominatorType = DenominatorType.ByDivisionTotal
    )

    tableFuture.map(_.copy(description = sql))
  }
}