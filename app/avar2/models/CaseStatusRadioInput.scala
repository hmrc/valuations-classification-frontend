/*
 * Copyright 2023 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package avar2.models

import utils.{Enumerable, WithName}

sealed abstract class CaseStatusRadioInput(name: String)
    extends WithName(name) {
}

object CaseStatusRadioInput extends Enumerable.Implicits {

  case object Complete extends CaseStatusRadioInput("complete")
  case object Refer extends CaseStatusRadioInput("refer")
  case object Reject extends CaseStatusRadioInput("reject")
  case object Suspend extends CaseStatusRadioInput("suspend")
  case object MoveBackToQueue extends CaseStatusRadioInput("move_back_to_queue")
  case object Release extends CaseStatusRadioInput("release")
  case object Suppress extends CaseStatusRadioInput("suppress")

  private val changeCaseStatusValues: Seq[CaseStatusRadioInput]  = Seq(Complete, Refer, Reject, Suspend, MoveBackToQueue)
  private val releaseOrSuppressValues: Seq[CaseStatusRadioInput] = Seq(Release, Suppress)

  def changeCaseStatusOptionsFor(): Seq[InputRadio] =
    changeCaseStatusValues.map { value =>
      InputRadio("change_case_status", value.toString)
    }

  val releaseOrSuppressOptions: Seq[InputRadio] = releaseOrSuppressValues.map { value =>
    InputRadio("change_case_status", value.toString)
  }

  implicit val enumerable: Enumerable[CaseStatusRadioInput] =
    Enumerable((changeCaseStatusValues ++ releaseOrSuppressValues).map(v => v.toString -> v): _*)
}
