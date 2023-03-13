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
import avar2.forms.ReleaseCaseForm
import avar2.models.ValuationCase
import avar2.services.ValuationCaseService
import config.AppConfig
import play.api.data.Form
import play.api.mvc._
import uk.gov.hmrc.play.bootstrap.controller.WithUnsafeDefaultFormBinding
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import avar2.views.html.release_case

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ReleaseCaseController @Inject()(
                                       verify: AuthenticatedCaseWorkerAction,
                                       valuationCaseService: ValuationCaseService,
                                       mcc: MessagesControllerComponents,
                                       val releaseCaseView: release_case,
                                       implicit val appConfig: AppConfig
)(implicit val executionContext: ExecutionContext)
    extends FrontendController(mcc)
    with RenderCaseAction
    with WithUnsafeDefaultFormBinding {

  private lazy val releaseCaseForm: Form[String]   = ReleaseCaseForm.form
  override protected val config: AppConfig         = appConfig
  override protected val caseService: ValuationCaseService = valuationCaseService

  def releaseCase(reference: String): Action[AnyContent] =
//    (verify.authenticated andThen verify.casePermissions(reference) andThen
//      verify.mustHave(Permission.RELEASE_CASE))
      verify.async {implicit request =>
    valuationCaseService.valuationCase(reference).flatMap {
      case Some(vc: ValuationCase) => getCaseAndRenderView(vc => Future.successful(releaseCaseView(vc, releaseCaseForm)))(request, vc)
      case None => throw new Exception("failed to load case view")
    }}

  def releaseCaseToQueue(reference: String): Action[AnyContent] =
  //    (verify.authenticated andThen verify.casePermissions(reference) andThen
  //      verify.mustHave(Permission.RELEASE_CASE))
    verify.async {implicit request =>
      for {
        _ <- valuationCaseService.unAssignCase(reference, request.caseWorker)
      } yield Redirect(avar2.controllers.routes.AllOpenCasesController.displayAllOpenCases())
    }

//  def confirmReleaseCase(reference: String): Action[AnyContent] =
////    (verify.authenticated
////      andThen verify.casePermissions(reference)
////      andThen verify.mustHave(Permission.VIEW_CASES))
//      verify.async { implicit request =>
//      def queueNotFound(implicit request: AuthenticatedCaseWorkerRequest[_]) =
//        successful(resource_not_found(s"Case Queue"))
//
//      renderView(
//        c => c.status == CaseStatus.OPEN,
//        c =>
//          c.queueId
//            .map(id =>
//              queueService.getOneById(id) flatMap {
//                case Some(queue) => successful(confirmation_case_creation(c, queue.name))
//                case None        => queueNotFound
//              }
//            )
//            .getOrElse(queueNotFound)
//      )
//    }
}
