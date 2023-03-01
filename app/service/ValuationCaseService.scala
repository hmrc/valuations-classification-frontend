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

package service

import connector.ValuationCaseConnector
import models.{CaseStatus, Contact, EORIDetails, Paged, ValuationApplication, ValuationCase}
import uk.gov.hmrc.http.HeaderCarrier

import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

trait ValuationCaseService {
  def allOpenvaluationCases()(implicit hc: HeaderCarrier): Future[Paged[ValuationCase]]

  def valuationCase(reference: String)(implicit hc: HeaderCarrier): Future[ValuationCase]

}

class ArsValuationCaseService @Inject() (connector: ValuationCaseConnector)(implicit ec: ExecutionContext) extends ValuationCaseService{
  override def allOpenvaluationCases()(implicit hc: HeaderCarrier): Future[Paged[ValuationCase]] = connector.allOpenCases().map(Paged(_))

  override def valuationCase(reference: String)(implicit hc: HeaderCarrier): Future[ValuationCase] = {
    val app: ValuationApplication = ValuationApplication(EORIDetails("eori-number", "AMEX", "Cleaver House", "High St", "Brighton", "KL12 8ND", "UK"),
      Contact("John Jones", "jones@orange.com"),
      goodName = "mobile phone case",
      goodDescription = "pricey")

    val vc = ValuationCase(
      reference = "case-reference",
      status = CaseStatus.OPEN,
      createdDate = Instant.now().minus(5, ChronoUnit.DAYS),
      daysElapsed = 5,
      caseBoardsFileNumber = None,
      assignee = None,
      application = app,
      decision = None,
      attachments = Seq.empty,
      referredDaysElapsed = 0)
    Future.successful(vc)
  }
}

