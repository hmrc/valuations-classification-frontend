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

package models.viewmodels.avar

import avar2.models.ValuationCase

case class GoodsTabViewModel(
  caseReference: String,
  goodsName: String,
  goodsDescription: String,
  confidentialInformation: Option[String] = None,
  hasAttachments: Boolean = false,
  hasAttachmentsFromApplicant: Boolean = false,
  sendingSamples: Boolean = false,
  suggestedCommodityCode: Option[String] = None,
  knownLegalProceedings: Option[String] = None,
  reissuedBTIReference: Option[String] = None,
  relatedBTIReferences: List[String] = List.empty,
  otherInformation: Option[String] = None
)

object GoodsTabViewModel {
  def fromValuationCase(cse: ValuationCase): GoodsTabViewModel = {
    val avarApplication = cse.application
    GoodsTabViewModel(
      cse.reference,
      avarApplication.goodName,
      avarApplication.goodDescription,
      avarApplication.confidentialInformation,
      cse.attachments.nonEmpty,
      cse.attachments.exists(_.caseWorker.isEmpty),
      false,
      avarApplication.envisagedCommodityCode,
      avarApplication.knownLegalProceedings
    )
  }
}
