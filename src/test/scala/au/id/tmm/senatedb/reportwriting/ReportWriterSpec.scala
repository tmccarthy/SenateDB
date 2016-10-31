package au.id.tmm.senatedb.reportwriting

import java.nio.file.Files

import au.id.tmm.senatedb.fixtures.Divisions
import au.id.tmm.senatedb.model.parsing.Division
import au.id.tmm.senatedb.tallies.{SimpleTally, Tally}
import au.id.tmm.utilities.geo.australia.State
import au.id.tmm.utilities.geo.australia.State._
import au.id.tmm.utilities.testing.{ImprovedFlatSpec, NeedsCleanDirectory}

class ReportWriterSpec extends ImprovedFlatSpec with NeedsCleanDirectory {

  "the report writer" should "write the report markdown to a file" in {
    Given("a report with two tables")

    val table1 = {
      val primaryCountTally = Tally(
        NSW -> 4492197d,
        VIC -> 3500237d,
        QLD -> 2723166d,
        WA -> 1366182d,
        SA -> 1061165d,
        TAS -> 339159d,
        ACT -> 254767d,
        NT -> 102027d
      )

      val totalCount = SimpleTally(13838900d)

      val columns = Vector(
        TallyTable.StateNameColumn,
        TallyTable.PrimaryCountColumn("Formal ballots in state"),
        TallyTable.DenominatorCountColumn("Total formal ballots"),
        TallyTable.FractionColumn("% of total")
      )

      TallyTable[State](primaryCountTally, _ => totalCount.count, totalCount.count, totalCount.count, columns)
        .withTitle("Total formal ballots")
    }

    val table2 = {
      val primaryCountTally = Tally[Division](
        Divisions.ACT.CANBERRA -> 5d,
        Divisions.NT.LINGIARI -> 2d
      )

      val denominatorTally = Tally[Division](
        Divisions.ACT.CANBERRA -> 10d,
        Divisions.NT.LINGIARI -> 8d
      )

      val columns = Vector(
        TallyTable.StateNameColumn,
        TallyTable.DivisionNameColumn,
        TallyTable.PrimaryCountColumn("Monkey votes"),
        TallyTable.FractionColumn("% of total")
      )

      TallyTable[Division](primaryCountTally, denominatorTally(_), 13, 26, columns)
        .withTitle("Monkey votes by division")
    }

    val report = Report("Test report!", Vector(table1, table2))

    When("the report is written")
    ReportWriter.writeReport(cleanDirectory, report)

    Then("the markdown is correct")
    val expectedLocation = cleanDirectory resolve "Test_report.md"

    val actualContent = new String(Files.readAllBytes(expectedLocation), "UTF-8")

    val expectedContent = """# Test report!
                            |
                            |### Total formal ballots
                            |
                            ||State|Formal ballots in state|Total formal ballots|% of total|
                            ||---|---|---|---|
                            ||NSW|4,492,197|13,838,900|32.46%|
                            ||VIC|3,500,237|13,838,900|25.29%|
                            ||QLD|2,723,166|13,838,900|19.68%|
                            ||WA|1,366,182|13,838,900|9.87%|
                            ||SA|1,061,165|13,838,900|7.67%|
                            ||TAS|339,159|13,838,900|2.45%|
                            ||ACT|254,767|13,838,900|1.84%|
                            ||NT|102,027|13,838,900|0.74%|
                            ||**Total**|**13,838,900**|**13,838,900**|**100.00%**|
                            |
                            |### Monkey votes by division
                            |
                            ||State|Division|Monkey votes|% of total|
                            ||---|---|---|---|
                            ||ACT|Canberra|5|50.00%|
                            ||NT|Lingiari|2|25.00%|
                            ||**Total**|****|**13**|**50.00%**|
                            |""".stripMargin

    assert(expectedContent === actualContent)
  }

}
