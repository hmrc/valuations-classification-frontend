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

package avar2.services

import avar2.connector.ValuationCaseConnector
import avar2.models.{Attachment, CaseWorker, Paged, RejectReason, ValuationCase}
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ArsValuationCaseService @Inject() (connector: ValuationCaseConnector)(implicit ec: ExecutionContext)
    extends ValuationCaseService {
  override def allOpenvaluationCases()(implicit hc: HeaderCarrier): Future[Paged[ValuationCase]] =
    connector.allOpenCases().map(Paged(_))

  override def valuationCase(reference: String)(implicit hc: HeaderCarrier): Future[Option[ValuationCase]] =
    connector.caseByReference(reference)

  override def findCasesByAssignee(operator: CaseWorker)(implicit hc: HeaderCarrier): Future[Paged[ValuationCase]] =
    connector.findCasesByAssignee(operator.id).map(Paged(_))

  override def assignCase(reference: String, operator: CaseWorker)(implicit hc: HeaderCarrier): Future[Long] =
    connector.assignCase(reference, operator)


  override def rejectCase(reference: String, reason: RejectReason.Value,
                          attachment: Attachment, note: String, caseWorker: CaseWorker)(implicit hc: HeaderCarrier): Future[Long] =
    connector.rejectCase(reference, reason, attachment, note)

  override def unAssignCase(reference: String, operator: CaseWorker)(implicit hc: HeaderCarrier): Future[Long] = {
    connector.unAssignCase(reference, operator)
  }

  override def allNewValuationCases()(implicit hc: HeaderCarrier): Future[Paged[ValuationCase]] = connector.allNewCases().map(Paged(_))

  override def assignNewCase(reference: String, operator: CaseWorker)(implicit hc: HeaderCarrier): Future[Long] = connector.assignNewCase(reference, operator)
}
