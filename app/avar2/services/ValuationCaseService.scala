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

import avar2.models.{Attachment, CaseWorker, Paged, RejectReason, ValuationCase}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.Future


trait ValuationCaseService {
  def rejectCase(reference: String, reason: RejectReason.Value, attachment: Attachment, note: String, caseWorker: CaseWorker): Future[Unit]

  def assignCase(reference: String, operator: CaseWorker)(implicit hc: HeaderCarrier): Future[Long]

  def allOpenvaluationCases()(implicit hc: HeaderCarrier): Future[Paged[ValuationCase]]

  def valuationCase(reference: String)(implicit hc: HeaderCarrier): Future[Option[ValuationCase]]

  def findCasesByAssignee(operator: CaseWorker)(implicit hc: HeaderCarrier): Future[Paged[ValuationCase]]

}
