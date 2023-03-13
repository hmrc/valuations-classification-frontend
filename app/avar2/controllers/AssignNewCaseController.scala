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
import avar2.forms.TakeOwnerShipForm
import avar2.models.viewmodels.CaseHeaderViewModel
import avar2.services.ValuationCaseService
import avar2.views.html.assign_case
import cats.data.OptionT
import config.AppConfig
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.controller.WithUnsafeDefaultFormBinding
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AssignNewCaseController @Inject()(
                                      verify: AuthenticatedCaseWorkerAction,
                                      valuationCaseService: ValuationCaseService,
                                      mcc: MessagesControllerComponents,
                                      val assignCase: assign_case)(implicit ec: ExecutionContext, config: AppConfig) extends FrontendController(mcc)
  with WithUnsafeDefaultFormBinding with I18nSupport {

  private lazy val form: Form[Boolean] = TakeOwnerShipForm.form

  def show(reference: String): Action[AnyContent] =
    verify.async { implicit request =>
        val result = for{
          vc <- OptionT(valuationCaseService.valuationCase(reference))
        } yield Ok(assignCase(CaseHeaderViewModel.fromCase(vc), vc, form))

        result.getOrElse(Ok("unknown valuation case"))
      }

  def assignOrViewCase(reference: String): Action[Boolean] =  verify.async(parse.form(form)) { implicit request =>
       if(request.body){
         for{
           _ <- valuationCaseService.assignNewCase(reference, request.caseWorker)
         } yield Redirect(avar2.controllers.routes.AvarController.show(reference))
       }else{
         Future.successful(Redirect(avar2.controllers.routes.AvarController.show(reference)))
       }
    }
}
