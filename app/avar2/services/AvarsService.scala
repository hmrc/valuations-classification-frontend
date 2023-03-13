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

import avar2.models.{StoredAttachment, ValuationCase}
import cats.data.OptionT
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton()
class AvarsService @Inject() (valuationCaseService: ValuationCaseService, fileStoreService: FileStoreService)(implicit ec: ExecutionContext) {

  def valuationCaseFileMetaData(reference: String)(implicit hc: HeaderCarrier): Future[(ValuationCase,Seq[StoredAttachment])] = {
    val outcome = for{
      vc <- OptionT(valuationCaseService.valuationCase(reference))
      sa <- OptionT.liftF(fileStoreService.getAttachments(vc))
    } yield (vc,sa)

    outcome.getOrElse(throw new Exception("failed to retrieve stored attachments from the file store"))
  }

}
