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
import play.api.libs.json.{Json, OFormat}
import formats.JsonFormatInstances._
import java.time.{Clock, Instant}

case class AgentDetails2(
                         eoriDetails: EORIDetails,
                         letterOfAuthorisation: Option[Attachment2]
                       )
object AgentDetails2{
  implicit val fmt = Json.format[AgentDetails2]
}


case class ValuationApplication(
                           holder: EORIDetails,
                           contact: Contact,
                           goodName: String,
                           goodDescription: String,
                           agent: Option[AgentDetails2] = None,
                           confidentialInformation: Option[String] = None,
                           otherInformation: Option[String] = None,
                           knownLegalProceedings: Option[String] = None,
                           envisagedCommodityCode: Option[String] = None,
                           applicationPdf: Option[Attachment2] = None
                         )
object ValuationApplication{
  implicit val fmt = Json.format[ValuationApplication]
}


case class ValuationCase(
                 reference: String,
                 status: CaseStatus,
                 createdDate: Instant,
                 daysElapsed: Long,
                 application: ValuationApplication,
                 referredDaysElapsed: Long,
                 caseBoardsFileNumber: Option[String] = None,
                 assignee: Option[Operator2] = None,
                 decision: Option[Decision2] = None,
                 attachments: Seq[Attachment2] = Seq.empty,
                 keywords: Set[String]             = Set.empty,
                 dateOfExtract: Option[Instant]    = None,
                 migratedDaysElapsed: Option[Long] = None
               ){
  private def hasRuling: Boolean =
    decision.flatMap(_.effectiveEndDate).isDefined

  def hasExpiredRuling(implicit clock: Clock = Clock.systemUTC()): Boolean =
    hasRuling && decision.flatMap(_.effectiveEndDate).exists(_.isBefore(Instant.now(clock)))
}

object ValuationCase{
  implicit val fmt: OFormat[ValuationCase] = Json.format[ValuationCase]
}
