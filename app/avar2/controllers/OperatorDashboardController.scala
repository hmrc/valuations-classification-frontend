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

package avar2.controllers

import avar2.controllers.actions.{AuthenticatedCaseWorkerAction, AuthenticatedCaseWorkerRequest}
import avar2.services.CasesService
import avar2.views.html.operator_dashboard_classification
import config.AppConfig
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.controller.WithUnsafeDefaultFormBinding
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.Inject
import scala.concurrent.ExecutionContext


class OperatorDashboardController @Inject()(
                                             verify: AuthenticatedCaseWorkerAction,
                                             casesService: CasesService,
                                             mcc: MessagesControllerComponents,
                                             operator_dashboard_classification: operator_dashboard_classification,
                                             implicit val appConfig: AppConfig,
                                             implicit val ec: ExecutionContext
) extends FrontendController(mcc)
    with I18nSupport
    with WithUnsafeDefaultFormBinding {

  def onPageLoad: Action[AnyContent] = //(verify.authenticated andThen verify.mustHave(Permission.VIEW_MY_CASES))
    verify.async {
    implicit request: AuthenticatedCaseWorkerRequest[AnyContent] =>
      for {
        casesByAssignee <- casesService.getCasesByAssignee(request.operator, NoPagination())
        casesByQueue    <- casesService.countCasesByQueue
        totalCasesAssignedToMe = casesByAssignee.results.count(c => c.status == CaseStatus.OPEN)
        referredCasesAssignedToMe = casesByAssignee.results.count(c =>
          c.status == CaseStatus.REFERRED || c.status == CaseStatus.SUSPENDED
        )
        completedCasesAssignedToMe = casesByAssignee.results.count(c => c.status == CaseStatus.COMPLETED)
      } yield Ok(
        operator_dashboard_classification(
          casesByQueue,
          totalCasesAssignedToMe,
          referredCasesAssignedToMe,
          completedCasesAssignedToMe
        )
      )
  }

}
