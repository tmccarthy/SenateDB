package au.id.tmm.senatedb.api.persistence.entities

final case class TotalFormalBallotsTally(
    absoluteCount: Long,

    ordinalNationally: Int,
    ordinalInState: Option[Int],
    ordinalInDivision: Option[Int]
)
