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

import avar2.controllers.actions.AuthenticatedCaseWorkerAction
import com.google.inject.Inject
import config.AppConfig
import avar2.models.viewmodels.CasesTabViewModel
import play.api.i18n.I18nSupport
import play.api.mvc._
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import avar2.views.html.open_cases_view
import avar2.services.ValuationCaseService

import scala.concurrent.ExecutionContext


class AllOpenCasesController @Inject() (
                                         verify: AuthenticatedCaseWorkerAction,
                                         valuationCaseService: ValuationCaseService,
                                         mcc: MessagesControllerComponents,
                                         val openCasesView: open_cases_view,
                                         implicit val appConfig: AppConfig,
                                         implicit val ec: ExecutionContext
) extends FrontendController(mcc)
    with I18nSupport {

  def displayAllOpenCases(activeSubNav: avar2.models.viewmodels.AvarSubNavigationTab = avar2.models.viewmodels.AVaRTab): Action[AnyContent] =
   // (verify.authenticated andThen verify.mustHave(Permission.VIEW_QUEUE_CASES))
      verify.async { implicit request =>
      for {
        cases <- valuationCaseService.allOpenvaluationCases()

        openCases = CasesTabViewModel.create(cases)

      } yield Ok(openCasesView(openCases, activeSubNav))
    }
}
