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

package avar2.models.viewmodels

import models.{Case, Event, Paged, SampleReturn, SampleStatus}

case class SampleTabViewModel(
  caseReference: String,
  sampleToBeProvided: Boolean,
  sampleToBeReturned: Boolean,
  sampleRequestedBy: Option[String],
  sampleReturnStatus: String,
  sampleStatus: String,
  sampleActivity: Paged[Event]
)

object SampleTabViewModel {
  def fromCase(cse: Case, activity: Paged[Event]): SampleTabViewModel = {
    val avarApplication = cse.application.asAVAR

    SampleTabViewModel(
      caseReference      = cse.reference,
      sampleToBeProvided = avarApplication.sampleToBeProvided,
      sampleToBeReturned = avarApplication.sampleToBeReturned,
      sampleRequestedBy  = cse.sample.requestedBy.flatMap(_.name),
      sampleReturnStatus = SampleReturn.format(cse.sample.returnStatus),
      sampleStatus       = SampleStatus.format(cse.sample.status),
      sampleActivity     = activity
    )
  }
}