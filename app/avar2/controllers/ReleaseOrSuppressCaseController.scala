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
import avar2.forms.CaseStatusRadioInputFormProvider
import avar2.models.{CaseStatusRadioInput, ValuationCase}
import avar2.services.ValuationCaseService
import avar2.views.html.release_or_suppress
import config.AppConfig
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.controller.WithUnsafeDefaultFormBinding
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ReleaseOrSuppressCaseController @Inject()(
  verify: AuthenticatedCaseWorkerAction,
  valuationCaseService: ValuationCaseService,
  val release_or_suppress: release_or_suppress,
  mcc: MessagesControllerComponents,
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
//      verify.mustHaveOneOf(Seq(Permission.SUPPRESS_CASE, Permission.RELEASE_CASE))).async
    verify.async { implicit request: AuthenticatedCaseWorkerRequest[AnyContent] =>

      valuationCaseService.valuationCase(reference).flatMap {
        case Some(vc: ValuationCase) => validateAndRenderView((_: ValuationCase) => Future.successful(release_or_suppress(vc, form)))(request, vc)
        case None => throw new Exception("failed to load case view")
      }
    }

  def onSubmit(reference: String): Action[AnyContent] =
//    (verify.authenticated andThen
//      verify.casePermissions(reference) andThen
//      verify.mustHaveOneOf(Seq(Permission.SUPPRESS_CASE, Permission.RELEASE_CASE)))

  verify.async { implicit request: AuthenticatedCaseWorkerRequest[AnyContent] =>

    form
      .bindFromRequest()
      .fold(
        hasErrors => {
          valuationCaseService.valuationCase(reference).flatMap {
            case Some(vc: ValuationCase) => Future.successful(Ok(release_or_suppress(vc, hasErrors)))
            case None => throw new Exception("failed to load case view")
          }
        },
        {
          case CaseStatusRadioInput.Release =>
            Future.successful(Redirect(routes.ReleaseCaseController.releaseCase(reference)))
          case CaseStatusRadioInput.Suppress =>
            Future.successful(Redirect(routes.ReleaseCaseController.releaseCase(reference))) //TODO: Revert to and implement SuppressCaseController
//            Future.successful(Redirect(routes.SuppressCaseController.getSuppressCaseReason(reference)))
        }
      )
  }
}
