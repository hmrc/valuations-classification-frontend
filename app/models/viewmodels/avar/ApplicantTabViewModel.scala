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

import avar2.models.{AgentDetails, Contact, EORIDetails, ValuationCase}
import models.{ApplicationType, CaseStatus, Event, Paged}

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
                            applicationType: ApplicationType,
                            elementId: String,
                            searchResult: Paged[ValuationCase],
                            referralEvent: Option[Map[String, Event]]  = None,
                            completedEvent: Option[Map[String, Event]] = None
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
            referralEvent: Option[Map[String, Event]]  = None,
            completedEvent: Option[Map[String, Event]] = None
          ) =
    ApplicationsTab(
      "applicationTab.avar",
      ApplicationType.AVAR,
      "avar_tab",
      searchResult,
      referralEvent,
      completedEvent
    )

  def liability(
                 searchResult: Paged[ValuationCase]                  = Paged.empty,
                 referralEvent: Option[Map[String, Event]]  = None,
                 completedEvent: Option[Map[String, Event]] = None
               ) =
    ApplicationsTab(
      "applicationTab.liability",
      ApplicationType.LIABILITY,
      "liability_tab",
      searchResult,
      referralEvent,
      completedEvent
    )

  def correspondence(
                      searchResult: Paged[ValuationCase]                  = Paged.empty,
                      referralEvent: Option[Map[String, Event]]  = None,
                      completedEvent: Option[Map[String, Event]] = None
                    ) =
    ApplicationsTab(
      "applicationTab.correspondence",
      ApplicationType.CORRESPONDENCE,
      "correspondence_tab",
      searchResult,
      referralEvent,
      completedEvent
    )

  def miscellaneous(
                     searchResult: Paged[ValuationCase]                  = Paged.empty,
                     referralEvent: Option[Map[String, Event]]  = None,
                     completedEvent: Option[Map[String, Event]] = None
                   ) =
    ApplicationsTab(
      "applicationTab.miscellaneous",
      ApplicationType.MISCELLANEOUS,
      "miscellaneous_tab",
      searchResult,
      referralEvent,
      completedEvent
    )

  def assignedToMeCases(cases: Seq[ValuationCase]): ApplicationTabViewModel = {

    val assignedCases =
      cases.filter(aCase => aCase.status == CaseStatus.OPEN)

    val liabilities = assignedCases.filter(_.isLiabilityOrder)

    val isCorrespondence = assignedCases.filter(_.isCorrespondence)

    val isMiscellaneous = assignedCases.filter(_.isMisc)

    ApplicationTabViewModel(
      "applicationTab.assignedToMe",
      List(
        avar(Paged(assignedCases)),
        liability(Paged(liabilities)),
        correspondence(Paged(isCorrespondence)),
        miscellaneous(Paged(isMiscellaneous))
      )
    )
  }

  def referredByMe(cases: Seq[ValuationCase]): ApplicationTabViewModel = {

    val referredCases =
      cases.filter(aCase => aCase.status == CaseStatus.REFERRED || aCase.status == CaseStatus.SUSPENDED)

    val liabilities = referredCases.filter(_.isLiabilityOrder)

    val isCorrespondence = referredCases.filter(_.isCorrespondence)

    val isMiscellaneous = referredCases.filter(_.isMisc)

    ApplicationTabViewModel(
      "applicationTab.referredByMe",
      List(
        avar(Paged(referredCases)),
        liability(Paged(liabilities)),
        correspondence(Paged(isCorrespondence)),
        miscellaneous(Paged(isMiscellaneous))
      )
    )
  }

  def completedByMe(cases: Seq[ValuationCase]): ApplicationTabViewModel = {

    val completeByMe = cases.filter(aCase => aCase.status == CaseStatus.COMPLETED)

    val liabilities = completeByMe.filter(_.isLiabilityOrder)

    val isCorrespondence = completeByMe.filter(_.isCorrespondence)

    val isMiscellaneous = completeByMe.filter(_.isMisc)

    ApplicationTabViewModel(
      "applicationTab.completedByMe",
      List(
        avar(Paged(completeByMe), None),
        liability(Paged(liabilities), None),
        correspondence(Paged(isCorrespondence), None),
        miscellaneous(Paged(isMiscellaneous), None)
      )
    )
  }

}
