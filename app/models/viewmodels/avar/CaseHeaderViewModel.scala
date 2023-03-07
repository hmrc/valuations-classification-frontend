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

import avar2.models.{Contact, ValuationCase}
import models.Case

case class CaseHeaderViewModel(
  businessName: Option[String],
  goodsName: String,
  referenceNumber: String,
  caseSource: Option[String],
  contact: Contact,
  caseStatus: CaseStatusViewModel,
  isMigrated: Boolean
)

object CaseHeaderViewModel {
  def fromCase(c: ValuationCase): CaseHeaderViewModel =
    CaseHeaderViewModel(
      Option(c.application.holder.businessName),
      c.application.goodName,
      c.reference,
      None,
      c.application.contact,
      CaseStatusViewModel.fromCase(c),
      c.dateOfExtract.isDefined
    )

  def fromOldCase(c: Case): CaseHeaderViewModel = {
    CaseHeaderViewModel(
      businessName = c.application.businessName,
      goodsName = c.application.goodsName,
      referenceNumber = c.reference,caseSource = None,
      contact = avar2.models.Contact(c.application.contact.name, c.application.contact.email, c.application.contact.phone),
      caseStatus = CaseStatusViewModel(None, None, None),
      isMigrated = false
      )
  }
}
