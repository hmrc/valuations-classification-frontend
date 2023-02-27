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
                           goodName: String,
                           goodDescription: String,
                           agent: Option[AgentDetails] = None,
                           confidentialInformation: Option[String] = None,
                           otherInformation: Option[String] = None,
                           knownLegalProceedings: Option[String] = None,
                           envisagedCommodityCode: Option[String] = None,
                           applicationPdf: Option[Attachment] = None
                         )


case class ValuationCase(
                 reference: String,
                 status: CaseStatus,
                 createdDate: Instant,
                 daysElapsed: Long,
                 caseBoardsFileNumber: Option[String],
                 assignee: Option[Operator],
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
