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
import avar2.forms.CaseStatusRadioInputFormProvider
import avar2.models.{CaseStatusRadioInput, ValuationCase}
import avar2.services.ValuationCaseService
import avar2.views.html.change_case_status
import config.AppConfig
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.controller.WithUnsafeDefaultFormBinding
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.Future.successful

class ChangeCaseStatusController @Inject()(
                                            verify: AuthenticatedCaseWorkerAction,
                                            valuationCaseService: ValuationCaseService,
                                            mcc: MessagesControllerComponents,
                                            val change_case_status: change_case_status,
                                            implicit val appConfig: AppConfig
)(implicit val executionContext: ExecutionContext)
    extends FrontendController(mcc)
    with RenderCaseAction
    with WithUnsafeDefaultFormBinding {

  override protected val config: AppConfig         = appConfig
  override protected val caseService: ValuationCaseService = valuationCaseService

  val form = new CaseStatusRadioInputFormProvider()()

  def onPageLoad(reference: String): Action[AnyContent] =
//    (verify.authenticated andThen
//      verify.casePermissions(reference) andThen
//      verify.mustHave(Permission.EDIT_RULING)).async
  verify.async{ implicit request =>
    valuationCaseService.valuationCase(reference).flatMap {
      case Some(vc: ValuationCase) => validateAndRenderView(vc => successful(change_case_status(vc, form)))(request, vc)
      case None => throw new Exception("failed to load case view")
    }
  }

  def onSubmit(reference: String): Action[AnyContent] =
//    (verify.authenticated andThen
//      verify.casePermissions(reference) andThen
//      verify.mustHave(Permission.EDIT_RULING)).async
    verify.async { implicit request =>

      valuationCaseService.valuationCase(reference).flatMap {
        case Some(vc: ValuationCase) => {
          form
            .bindFromRequest()
            .fold(
              hasErrors => Future.successful(Ok(change_case_status(vc, hasErrors))),
              {
                //            case CaseStatusRadioInput.Complete => Redirect(routes.CompleteCaseController.completeCase(reference))
                //            case CaseStatusRadioInput.Refer    => Redirect(routes.ReferCaseController.getReferCaseReason(reference))
                case CaseStatusRadioInput.Reject   => Future.successful(Redirect(routes.RejectCaseController.getRejectCaseReason(reference)))
                //            case CaseStatusRadioInput.Suspend  => Redirect(routes.SuspendCaseController.getSuspendCaseReason(reference))
//                case CaseStatusRadioInput.MoveBackToQueue => Redirect(routes.ReassignCaseController.reassignCase(reference, request.uri))
              }
            )
        }
        case None => throw new Exception("failed to load case view")
      }
    }
}
