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

package controllers

import avar2.services.ValuationCaseService
import cats.data.OptionT
import config.AppConfig
import models.{Operator2, Role}
import models.forms.TakeOwnerShipForm
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.controller.WithUnsafeDefaultFormBinding
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.assign_case

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AssignCaseController2 @Inject() (
                                        verify: RequestActions,
                                        valuationCaseService: ValuationCaseService,
                                        mcc: MessagesControllerComponents,
                                        val assignCase: assign_case)(implicit ec: ExecutionContext, config: AppConfig) extends FrontendController(mcc)
  with WithUnsafeDefaultFormBinding with I18nSupport {

  private lazy val form: Form[Boolean] = TakeOwnerShipForm.form

  def show(reference: String): Action[AnyContent] =
    verify.authenticated
      .async { implicit request =>
        val result = for{
          vc <- OptionT(valuationCaseService.valuationCase(reference))
        } yield Ok(assignCase(vc, form))

        result.getOrElse(Ok("unknown valuation case"))
      }

  def assignOrViewCase(reference: String) =  verify.authenticated.async(parse.form(form)) { implicit  request =>
       if(request.body){
         for{
           _ <- valuationCaseService.assignCase(reference, Operator2(id="joe",role=Role.CLASSIFICATION_OFFICER))
         } yield Redirect(controllers.v2.routes.AvarController2.show(reference))
       }else{
         Future.successful(Redirect(controllers.v2.routes.AvarController2.show(reference)))
       }
    }
}
