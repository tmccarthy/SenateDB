package au.id.tmm.senatedb.api.services

import akka.testkit.TestProbe
import au.id.tmm.senatedb.api.persistence.daos.{DivisionDao, ElectionDao}
import au.id.tmm.senatedb.core.model.SenateElection
import au.id.tmm.utilities.testing.ImprovedFlatSpec

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

class DivisionServiceSpec extends ImprovedFlatSpec with MocksActor {
  private val mockDbPopulationActor = TestProbe()
  private val divisionDao = mock[DivisionDao]

  private val testElection = SenateElection.`2016`
  private val testElectionId = ElectionDao.idOf(testElection).get

  private val sut = new DivisionService(divisionDao, mockDbPopulationActor.ref)

  private def await[A](future: Future[A]) = Await.result(future, Duration.Inf)
}