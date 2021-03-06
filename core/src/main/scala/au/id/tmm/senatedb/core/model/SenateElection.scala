package au.id.tmm.senatedb.core.model

import java.time.LocalDate
import java.time.Month._

import au.id.tmm.utilities.geo.australia.State

sealed trait SenateElection extends Ordered[SenateElection] {
  def date: LocalDate
  def states: Set[State] = State.ALL_STATES
  def aecID: Int
  def name: String = s"${date.getYear} election"

  override def compare(that: SenateElection): Int = this.date compareTo that.date

  override def toString: String = name
}

object SenateElection {

  case object `2016` extends SenateElection {
    override def date: LocalDate = LocalDate.of(2016, JULY, 2)

    override def aecID: Int = 20499
  }

  case object `2014 WA` extends SenateElection {
    override def date: LocalDate = LocalDate.of(2014, APRIL, 5)

    override def states: Set[State] = Set(State.WA)

    override def aecID: Int = 17875

    override def name: String = "2014 WA Senate election"
  }

  case object `2013` extends SenateElection {
    override def date: LocalDate = LocalDate.of(2013, SEPTEMBER, 7)

    override def aecID: Int = 17496
  }

}