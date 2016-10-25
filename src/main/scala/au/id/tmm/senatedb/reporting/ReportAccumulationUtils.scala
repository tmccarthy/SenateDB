package au.id.tmm.senatedb.reporting

// TODO these should go into the utility class
object ReportAccumulationUtils {

  private def mergeTallies[K, V](left: Map[K, Long],
                                 right: Map[K, Long],
                                 op: (Long, Long) => V,
                                 extractLeft: (Map[K, Long], K) => Long = (map: Map[K, Long], key: K) => map.getOrElse(key, 0l),
                                 extractRight: (Map[K, Long], K) => Long = (map: Map[K, Long], key: K) => map.getOrElse(key, 0l)
                                ): Map[K, V] = {
    val keys = left.keySet ++ right.keySet

    keys.toStream
      .map(key => {
        val leftVal = extractLeft(left, key)
        val rightVal = extractRight(right, key)

        key -> op(leftVal, rightVal)
      })
      .toMap
  }

  def sumTallies[K](left: Map[K, Long], right: Map[K, Long]): Map[K, Long] = mergeTallies[K, Long](left, right, _ + _)

  def sumTieredTallies[A, B](left: Map[A, Map[B, Long]], right: Map[A, Map[B, Long]]): Map[A, Map[B, Long]] = {
    val keys = left.keySet ++ right.keySet

    keys.toStream
      .map(key => {
        val leftMap = left.getOrElse(key, Map.empty)
        val rightMap = right.getOrElse(key, Map.empty)

        key -> sumTallies(leftMap, rightMap)
      })
      .toMap
  }

  def divideTally[K](tally: Map[K, Long], denominator: Long): Map[K, Double] = {
    val denominatorAsDouble = denominator.toDouble

    tally.mapValues(_ / denominatorAsDouble)
  }

  def divideTally[K](tally: Map[K, Long], denominator: Map[K, Long]): Map[K, Double] =
    mergeTallies[K, Double](tally, denominator, _.toDouble / _.toDouble, extractRight = _(_))

  def emptyCountMap[K]: Map[K, Long] = Map.empty.withDefaultValue(0l)

  def combineFloatTallies[K](left: Map[K, Double], right: Map[K, Double]): Map[K, Double] = {
    val keys = left.keySet ++ right.keySet

    keys.toStream
      .map(key => {
        key -> (left.getOrElse[Double](key, 0) + right.getOrElse[Double](key, 0))
      })
      .toMap
  }

  def emptyFloatCountMap[K]: Map[K, Double] = Map.empty.withDefaultValue(0l)

  def increment[K](map: scala.collection.mutable.Map[K, Long], key: K): Unit = {
    map.put(key, map(key) + 1)
  }
}
