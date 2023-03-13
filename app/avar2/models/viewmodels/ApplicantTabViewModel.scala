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

package avar2.models.viewmodels

import avar2.models.{AgentDetails, CaseStatus, Contact, EORIDetails, Paged, ValuationCase}

case class ApplicationTabViewModel(headingMessageKey: String, applicationTabs: List[ApplicationsTab])

case class ApplicantTabViewModel(
  caseReference: String,
  eoriDetails: EORIDetails,
  contact: Contact,
  countryName: String,
  caseBoardsFileNumber: Option[String],
  agentDetails: Option[AgentDetails]
)

case class ApplicationsTab(
                            tabMessageKey: String,
                            elementId: String,
                            searchResult: Paged[ValuationCase]
                          )

object ApplicantTabViewModel {
  def fromValuationCase(vc: ValuationCase): ApplicantTabViewModel = {
    ApplicantTabViewModel(
      caseReference = vc.reference,
      eoriDetails = vc.application.holder,
      contact = vc.application.contact,
      countryName = vc.application.holder.country,
      caseBoardsFileNumber = vc.caseBoardsFileNumber,
      agentDetails = vc.application.agent
    )
  }

  def avar(
            searchResult: Paged[ValuationCase]                  = Paged.empty,

          ) =
    ApplicationsTab(
      "applicationTab.avar",
      "avar_tab",
      searchResult
    )

  def assignedToMeCases(cases: Seq[ValuationCase]): ApplicationTabViewModel = {

    val assignedCases =
      cases.filter(aCase => aCase.status == CaseStatus.REFERRED)

    val liabilities = assignedCases.filter(_.isLiabilityOrder)

    val isCorrespondence = assignedCases.filter(_.isCorrespondence)

    val isMiscellaneous = assignedCases.filter(_.isMisc)

    ApplicationTabViewModel(
      "applicationTab.assignedToMe",
      List(
        avar(Paged(assignedCases))
      )
    )
  }

  def referredByMe(cases: Seq[ValuationCase]): ApplicationTabViewModel = {

    val referredCases =
      cases.filter(aCase => aCase.status == CaseStatus.REFERRED || aCase.status == CaseStatus.SUSPENDED)

    ApplicationTabViewModel(
      "applicationTab.referredByMe",
      List(
        avar(Paged(referredCases))
      )
    )
  }

  def completedByMe(cases: Seq[ValuationCase]): ApplicationTabViewModel = {

    val completeByMe = cases.filter(aCase => aCase.status == CaseStatus.COMPLETED)

    ApplicationTabViewModel(
      "applicationTab.completedByMe",
      List(
        avar(Paged(completeByMe)))
    )
  }

}
