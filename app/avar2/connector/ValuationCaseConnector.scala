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

package avar2.connector

import avar2.models.{Attachment, CaseWorker, RejectReason, ValuationCase}
import config.AppConfig
import avar2.connector.ValuationCaseConnector.{AssignCaseRequest, AssignNewCaseRequest, RejectCaseRequest}
import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ValuationCaseConnector @Inject() (config: AppConfig,
                             client: HttpClient)(implicit ec: ExecutionContext) {


  val serviceUrl = config.advanceValuationRulingsUrl + "/advance-valuation-rulings"

  val openCasesUrl = serviceUrl + "/valuation"

  def casesByAssignee(assignee: String) = s"$serviceUrl/valuations/$assignee"

  val assignCaseUrl = openCasesUrl + "/assign"

  val assignNewCaseUrl = openCasesUrl + "/assign-new"


  val rejectCaseUrl = openCasesUrl + "/reject"

  val unAssignCaseUrl = openCasesUrl + "/unassign"


  val newCasesUrl = serviceUrl + "/valuation/new"


   def allOpenCases()(implicit hc: HeaderCarrier): Future[List[ValuationCase]] = {
     client.GET[List[ValuationCase]](openCasesUrl)
   }

  def allNewCases()(implicit hc: HeaderCarrier): Future[List[ValuationCase]] = {
    client.GET[List[ValuationCase]](newCasesUrl)
  }

  def caseByReference(reference: String)(implicit hc: HeaderCarrier): Future[Option[ValuationCase]] =
    client.GET[Option[ValuationCase]](s"$openCasesUrl/$reference")

  def assignCase(reference: String, operator: CaseWorker)(implicit hc: HeaderCarrier): Future[Long] = {
    client.POST[AssignCaseRequest, Long](assignCaseUrl, AssignCaseRequest(reference, operator))
  }

  def unAssignCase(reference: String, operator: CaseWorker)(implicit hc: HeaderCarrier): Future[Long] = {
    client.POST[AssignCaseRequest, Long](unAssignCaseUrl, AssignCaseRequest(reference, operator))
  }

  def findCasesByAssignee(assignee: String)(implicit hc: HeaderCarrier): Future[List[ValuationCase]] = {
      client.GET[List[ValuationCase]](casesByAssignee(assignee))
    }

  def rejectCase(reference: String, reason: RejectReason.Value, attachment: Attachment, note: String)(implicit hc: HeaderCarrier): Future[Long] = {
    client.POST[RejectCaseRequest, Long](rejectCaseUrl, RejectCaseRequest(reference, reason, attachment, note))
  }

  def assignNewCase(reference: String, operator: CaseWorker)(implicit hc: HeaderCarrier): Future[Long] =
    client.POST[AssignNewCaseRequest, Long](assignNewCaseUrl, AssignNewCaseRequest(reference, operator))

}

object ValuationCaseConnector{

  case class AssignNewCaseRequest(reference: String, caseWorker: CaseWorker)

  object AssignNewCaseRequest {
    implicit val fmt: OFormat[AssignNewCaseRequest] = Json.format[AssignNewCaseRequest]
  }

  case class AssignCaseRequest(reference: String, caseWorker: CaseWorker)

  object AssignCaseRequest {
    implicit val fmt: OFormat[AssignCaseRequest] = Json.format[AssignCaseRequest]
  }

  case class RejectCaseRequest(reference: String, reason: RejectReason.Value, attachment: Attachment, note: String)

  object RejectCaseRequest{
    implicit val fmt: OFormat[RejectCaseRequest] = Json.format[RejectCaseRequest]
  }

}
