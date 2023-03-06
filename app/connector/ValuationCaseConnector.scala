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

package connector

import config.AppConfig
import connector.ValuationCaseConnector.AssignCaseRequest
import avar2.models.{CaseWorker, ValuationCase}
import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ValuationCaseConnector @Inject() (config: AppConfig,
                             client: HttpClient)(implicit ec: ExecutionContext) {



  val serviceUrl = config.advanceValuationRulingsUrl + "/advance-valuation-rulings"

  val openCasesUrl = serviceUrl + "/valuation"

  val assignCaseUrl = openCasesUrl + "/assign"


   def allOpenCases()(implicit hc: HeaderCarrier): Future[List[ValuationCase]] = {
     client.GET[List[ValuationCase]](openCasesUrl)
   }

  def caseByReference(reference: String)(implicit hc: HeaderCarrier): Future[Option[ValuationCase]] =
    client.GET[Option[ValuationCase]](s"$openCasesUrl/$reference")

  def assignCase(reference: String, operator: CaseWorker)(implicit hc: HeaderCarrier): Future[Long] = {
    client.POST[AssignCaseRequest,Long](assignCaseUrl, AssignCaseRequest(reference, operator))
  }

}

object ValuationCaseConnector{

  case class AssignCaseRequest(reference: String, operator: CaseWorker)

  object AssignCaseRequest {
    implicit val fmt: OFormat[AssignCaseRequest] = Json.format[AssignCaseRequest]
  }

}
