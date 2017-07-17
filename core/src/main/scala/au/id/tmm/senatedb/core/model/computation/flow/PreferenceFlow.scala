package au.id.tmm.senatedb.core.model.computation.flow

import au.id.tmm.senatedb.core.tallies.Tally

final case class PreferenceFlow[A](tally: Int, children: Set[PreferenceTree[A]])
  extends PreferenceTreeLike[A] with Tally {

  override type SelfType = PreferenceFlow[A]

  override def +(that: PreferenceFlow[A]): PreferenceFlow[A] = {
    if (that.isEmpty) return this
    if (this.isEmpty) return that

    val newTally = this sumTallyWith that
    val newChildren = this sumChildrenWith that

    PreferenceFlow(newTally, newChildren)
  }

  override def /(denominator: Double): PreferenceFlow[A] = throw new UnsupportedOperationException()
}

object PreferenceFlow {
  private val emptyInstance: PreferenceFlow[Nothing] = PreferenceFlow[Nothing](0, Set.empty)

  def empty[A]: PreferenceFlow[A] = emptyInstance.asInstanceOf[PreferenceFlow[A]]
}