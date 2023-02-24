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

package models

import models.CaseStatus.CaseStatus

import java.time.{Clock, Instant}

case class ValuationApplication(
                           holder: EORIDetails,
                           contact: Contact,
                           agent: Option[AgentDetails] = None,
                           offline: Boolean,
                           goodName: String,
                           goodDescription: String,
                           confidentialInformation: Option[String],
                           otherInformation: Option[String],
                           reissuedBTIReference: Option[String],
                           relatedBTIReference: Option[String] = None,
                           relatedBTIReferences: List[String]  = Nil,
                           knownLegalProceedings: Option[String],
                           envisagedCommodityCode: Option[String],
                           sampleToBeProvided: Boolean,
                           sampleToBeReturned: Boolean,
                           applicationPdf: Option[Attachment]
                         )


case class ValuationCase(
                 reference: String,
                 status: CaseStatus,
                 createdDate: Instant,
                 daysElapsed: Long,
                 caseBoardsFileNumber: Option[String],
                 assignee: Option[Operator],
                 queueId: Option[String],
                 application: ValuationApplication,
                 decision: Option[Decision],
                 attachments: Seq[Attachment],
                 keywords: Set[String]             = Set.empty,
                 sample: Sample                    = Sample(),
                 dateOfExtract: Option[Instant]    = None,
                 migratedDaysElapsed: Option[Long] = None,
                 referredDaysElapsed: Long
               ){
  private def hasRuling: Boolean =
    decision.flatMap(_.effectiveEndDate).isDefined

  def hasExpiredRuling(implicit clock: Clock = Clock.systemUTC()): Boolean =
    hasRuling && decision.flatMap(_.effectiveEndDate).exists(_.isBefore(Instant.now(clock)))
}
