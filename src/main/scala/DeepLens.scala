package shapelens

import scalaz._, Scalaz._
import shapeless._, labelled._
import _root_.monocle.Lens

trait DeepLens[S, Ctx <: HList] {
  type A
  val value: Lens[S, A]
}

object DeepLens {

  type Aux[S, Ctx <: HList, A2] = DeepLens[S, Ctx] { type A = A2 }

  def apply[S, Ctx <: HList](implicit ln: DeepLens[S, Ctx]): Lens[S, ln.A] =
    ln.value

  def apply[S, Ctx <: HList, A2](ln: Lens[S, A2]): Aux[S, Ctx, A2] =
    new DeepLens[S, Ctx] { type A = A2; val value = ln }

  implicit def base[S]: Aux[S, HNil, S] =
    apply(Lens.id)

  implicit def inductive[S, H, T <: HList, A, B](implicit
      hln: MkFieldLens.Aux[S, H, A],
      tln: Aux[A, T, B]): Aux[S, H :: T, B] =
    apply(Lens(hln().get)(a => s => hln().set(s)(a)) composeLens tln.value)
}

