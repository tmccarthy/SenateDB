package au.id.tmm.senatedb.computations.ballotnormalisation

import au.id.tmm.senatedb.data.database.model.{AtlPreferencesRow, BtlPreferencesRow, CandidatesRow}
import au.id.tmm.senatedb.model.{CandidatePosition, NormalisedBallot, Preference, Preferenceable}


class BallotNormaliser private (candidates: Set[CandidatesRow]) {

  private lazy val positionsPerGroup: Map[String, Vector[CandidatePosition]] =
    candidates
      .toStream
      .map(_.position)
      .sorted
      .toVector
      .groupBy(_.group)

  def normalise(atlPreferences: Set[AtlPreferencesRow], btlPreferences: Set[BtlPreferencesRow]): Option[NormalisedBallot] = {
    normaliseBtl(btlPreferences) orElse normaliseAtl(atlPreferences)
  }

  def normaliseAtl(atlPreferences: Set[AtlPreferencesRow]): Option[NormalisedBallot] = {
    val groupsInPreferenceOrder = generalNormalise(atlPreferences, 1)
      .map(rows => rows.map(_.group))

    groupsInPreferenceOrder
      .map(distributeToCandidatePositions)
      .map(NormalisedBallot)
  }

  private def distributeToCandidatePositions(groupsInPreferenceOrder: Vector[String]): Vector[CandidatePosition] =
    groupsInPreferenceOrder.flatMap(positionsPerGroup)

  def normaliseBtl(btlPreferences: Set[BtlPreferencesRow]): Option[NormalisedBallot] = {
    val positionsInPreferenceOrder = generalNormalise(btlPreferences, 6)

    positionsInPreferenceOrder
      .map(rows => rows.map(_.position))
      .map(NormalisedBallot)
  }

  private def generalNormalise[A <: Preferenceable](rows: Set[A], minNumPreferences: Int): Option[Vector[A]] = {
    val rowsInPreferenceOrder = orderAccordingToPreferences(rows)

    val formalPreferences = truncateAtCountError(rowsInPreferenceOrder)

    if (formalPreferences.size < minNumPreferences) {
      None
    } else {
      Some(formalPreferences)
    }
  }

  private def orderAccordingToPreferences[A <: Preferenceable](rows: Set[A]): Vector[Set[A]] = {
    val returnedVector: scala.collection.mutable.Buffer[Set[A]] = Vector.fill(rows.size)(Set.empty[A]).toBuffer

    @inline def isWithinValidPreferencesRange(prefAsNumber: Int) = prefAsNumber <= rows.size
    @inline def indexForPreference(prefAsNumber: Int) = prefAsNumber - 1

    for (row <- rows) {
      val preference = toNumber(row.parsedPreference)

      if (preference.isDefined && isWithinValidPreferencesRange(preference.get)) {
        val index = indexForPreference(preference.get)
        returnedVector.update(index, returnedVector(index) + row)
      }
    }

    returnedVector.toVector
  }

  private def toNumber(preference: Preference): Option[Int] = {
    preference match {
      case Preference.Numbered(number) => Some(number)
      case Preference.Tick | Preference.Cross => Some(1)
      case Preference.Missing => None
    }
  }

  private def truncateAtCountError[A <: Preferenceable](rowsInPreferenceOrder: Vector[Set[A]]): Vector[A] = {
    // As long as we have only one row with each preference, we haven't encountered a count error
    rowsInPreferenceOrder
      .toStream
      .takeWhile(_.size == 1)
      .map(_.head)
      .toVector
  }
}

object BallotNormaliser {
  def forCandidates(candidates: Set[CandidatesRow]): BallotNormaliser = new BallotNormaliser(candidates)
}