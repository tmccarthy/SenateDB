package au.id.tmm.senatedb.data.rawdatastore.entityconstruction.distributionofpreferences

import au.id.tmm.senatedb.data.TestData
import au.id.tmm.senatedb.model.SenateElection
import au.id.tmm.senatedb.model.State._

import scala.io.Source

trait UsesDopData {
  val resourcesPackage = "/au/id/tmm/senatedb/data/rawdatastore/entityconstruction/distributionofpreferences"

  val actCsvResource = getClass.getResource(s"$resourcesPackage/SenateStateDOPDownload-20499-ACT.csv")
  val tasCsvResource = getClass.getResource(s"$resourcesPackage/SenateStateDOPDownload-20499-TAS.csv")
  val ntCsvResource = getClass.getResource(s"$resourcesPackage/SenateStateDOPDownload-20499-NT.csv")

  val testElection = SenateElection.`2016`

  lazy val parsedActCountData = parseDistributionOfPreferencesCsv(testElection, ACT, TestData.allActCandidates,
    Source.fromURL(actCsvResource)).get

  lazy val parsedTasCountData = parseDistributionOfPreferencesCsv(testElection, TAS, TestData.allTasCandidates,
    Source.fromURL(tasCsvResource)).get

  lazy val parsedNtCountData = parseDistributionOfPreferencesCsv(testElection, NT, TestData.allNtCandidates,
    Source.fromURL(ntCsvResource)).get

}
