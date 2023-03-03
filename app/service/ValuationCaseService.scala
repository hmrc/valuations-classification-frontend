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
import models.{CaseStatus, Contact, EORIDetails, Operator, Operator2, Paged, ValuationApplication, ValuationCase}
import uk.gov.hmrc.http.HeaderCarrier

import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

trait ValuationCaseService {
  def assignCase(reference: String, operator: Operator2)(implicit hc: HeaderCarrier): Future[Long]

  def allOpenvaluationCases()(implicit hc: HeaderCarrier): Future[Paged[ValuationCase]]

  def valuationCase(reference: String)(implicit hc: HeaderCarrier): Future[Option[ValuationCase]]

}

class ArsValuationCaseService @Inject() (connector: ValuationCaseConnector)(implicit ec: ExecutionContext) extends ValuationCaseService{
  override def allOpenvaluationCases()(implicit hc: HeaderCarrier): Future[Paged[ValuationCase]] = connector.allOpenCases().map(Paged(_))

  override def valuationCase(reference: String)(implicit hc: HeaderCarrier): Future[Option[ValuationCase]] = connector.caseByReference(reference)

  override def assignCase(reference: String, operator: Operator2)(implicit hc: HeaderCarrier): Future[Long] = connector.assignCase(reference, operator)
}

