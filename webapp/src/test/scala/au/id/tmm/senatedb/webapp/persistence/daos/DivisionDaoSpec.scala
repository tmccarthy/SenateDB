package au.id.tmm.senatedb.webapp.persistence.daos

import au.id.tmm.senatedb.core.fixtures.Divisions
import au.id.tmm.utilities.testing.ImprovedFlatSpec
import org.scalamock.scalatest.MockFactory

class DivisionDaoSpec extends ImprovedFlatSpec with MockFactory {

  "the division dao" should "generate a unique id for a division" in {
    val dbStructureCache = mock[DbStructureCache]

    val divisionDao = new ConcreteDivisionDao(new HardCodedElectionDao, dbStructureCache)

    val actualId = divisionDao.idOf(Divisions.ACT.CANBERRA)
    val expectedId = 420229601

    assert(expectedId === actualId)
  }

}
