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

import akka.stream.Materializer
import avar2.models.CaseWorker
import avar2.services.ValuationCaseService
import com.google.inject.Inject
import config.AppConfig
import controllers.RequestActions
import models.{NoPagination, Permission}
import models.request.AuthenticatedRequest
import avar2.models.viewmodels._
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import service.{CasesService, EventsService}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import avar2.views.html.my_cases_view

import scala.concurrent.ExecutionContext

class MyCasesController @Inject() (
                                    verify: RequestActions,
                                    casesService: ValuationCaseService,
                                    eventsService: EventsService,
                                    mcc: MessagesControllerComponents,
                                    val myCasesView: my_cases_view
                                  )(
                                    implicit val appConfig: AppConfig,
                                    mat: Materializer
                                  ) extends FrontendController(mcc)
  with I18nSupport
  with Logging {

  implicit val ec: ExecutionContext = mat.executionContext

  def displayMyCases(activeSubNav: AvarSubNavigationTab = AssignedToMeTab): Action[AnyContent] =
    (verify.authenticated andThen verify.mustHave(Permission.VIEW_MY_CASES)).async {
      implicit request: AuthenticatedRequest[AnyContent] =>
        for {
          cases <- casesService.findCasesByAssignee(CaseWorker.operatorToCaseWorker(request.operator))
          caseReferences = cases.results.map(_.reference).toSet
          myCaseStatuses = activeSubNav match {
            case AssignedToMeTab  => ApplicantTabViewModel.assignedToMeCases(cases.results)
            case ReferredByMeTab  => ApplicantTabViewModel.referredByMe(cases.results)
            case CompletedByMeTab => ApplicantTabViewModel.completedByMe(cases.results)
          }
        } yield Ok(myCasesView(myCaseStatuses, activeSubNav))
    }
}
