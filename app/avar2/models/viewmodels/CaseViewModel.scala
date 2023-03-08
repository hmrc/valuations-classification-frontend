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

import avar2.models.{CaseStatus, CaseWorker, ValuationCase}


case class CaseViewModel(
  caseHeader: CaseHeaderViewModel,
  hasPermissions: Boolean,
  showChangeCaseStatus: Boolean,
  showTakeOffReferral: Boolean,
  showReopen: Boolean,
  showAppealTab: Boolean,
  caseStatus: CaseStatus.Value
) {

  def showRulingAndKeywordsTabs: Boolean =
    Set(
      CaseStatus.OPEN,
      CaseStatus.REFERRED,
      CaseStatus.REJECTED,
      CaseStatus.SUSPENDED,
      CaseStatus.COMPLETED
    ).contains(caseStatus)

  def showActionThisCase: Boolean = isNewCase && hasPermissions

  def isNewCase: Boolean = caseStatus == CaseStatus.NEW

  def showAdvancedSearchButton: Boolean =
    Set(
      CaseStatus.OPEN,
      CaseStatus.REFERRED,
      CaseStatus.SUSPENDED
    ).contains(caseStatus)
}

object CaseViewModel {
  def fromValuationCase(c: ValuationCase): CaseViewModel = {
    val headerVM: CaseHeaderViewModel = CaseHeaderViewModel(
      Option(c.application.holder.businessName), c.application.goodName,c.reference,None, c.application.contact,
      CaseStatusViewModel(None,None,None),false)
    CaseViewModel(headerVM,false,false,false,false,false,c.status)
  }

  def fromCase(c: ValuationCase, operator: CaseWorker): CaseViewModel = {

//    def releaseOrSuppressPermissions: Boolean =
//      operator.permissions.contains(Permission.RELEASE_CASE) || operator.permissions.contains(Permission.SUPPRESS_CASE)
//
//    def completeCasePermission: Boolean =
//      operator.permissions.contains(Permission.COMPLETE_CASE)
//
//    def reopenCasePermission: Boolean =
//      operator.permissions.contains(Permission.REOPEN_CASE)
//
//    def appealCasePermission: Boolean =
//      operator.permissions.contains(Permission.APPEAL_CASE)

//    def changeCaseStatus: Boolean =
//      c.status == CaseStatus.OPEN && completeCasePermission
//
//    def takeOffReferral: Boolean =
//      c.status == CaseStatus.REFERRED && reopenCasePermission
//
//    def showReopenButton: Boolean = c.status == CaseStatus.SUSPENDED && reopenCasePermission
//
//    def showAppeal: Boolean =
//      (c.status == CaseStatus.COMPLETED || c.status == CaseStatus.CANCELLED) && appealCasePermission

    CaseViewModel(
      CaseHeaderViewModel.fromCase(c),
      hasPermissions       = false,
      showChangeCaseStatus = false,
      showTakeOffReferral  = false,
      showReopen           = false,
      showAppealTab        = false,
      caseStatus           = c.status
    )
  }
}
