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
import avar2.forms.{RejectCaseForm, UploadAttachmentForm}
import avar2.models.request.FileStoreInitiateRequest
import avar2.models.{Attachment, CaseRejection, RejectReason, ValuationCase}
import avar2.services.{FileStoreService, ValuationCaseService}
import avar2.views.html.{confirm_rejected, reject_case_email, reject_case_reason}
import cats.data.OptionT
import config.AppConfig
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc._
import uk.gov.hmrc.play.bootstrap.controller.WithUnsafeDefaultFormBinding
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import java.util.UUID
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class RejectCaseController @Inject()(
                                      verify: AuthenticatedCaseWorkerAction,
                                      valuationCaseService: ValuationCaseService,
                                      fileService: FileStoreService,
                                      mcc: MessagesControllerComponents,
                                      val reject_case_reason: reject_case_reason,
                                      val reject_case_email: reject_case_email,
                                      val confirm_rejected: confirm_rejected,
                                      implicit val appConfig: AppConfig
)(implicit ec: ExecutionContext)
    extends FrontendController(mcc)
    with I18nSupport
    with UpscanErrorHandling
    with WithUnsafeDefaultFormBinding {

  private val RejectionCacheKey = "rejection"
  private def cacheKey(reference: String) = s"reject_case-$reference"

  def getRejectCaseReason(reference: String): Action[AnyContent] =
//    (verify.authenticated
//      andThen verify.casePermissions(reference)
//      andThen verify.mustHave(Permission.REJECT_CASE))
    verify.async{ implicit request =>
      val outcome = for{
        vc <- OptionT(valuationCaseService.valuationCase(reference))
      } yield Ok(reject_case_reason(vc, RejectCaseForm.form))

      outcome.getOrElse(throw new Exception("failed to load rejection form"))
    }

  def postRejectCaseReason(reference: String): Action[AnyContent] =
//    (verify.authenticated andThen
//      verify.casePermissions(reference) andThen
//      verify.mustHave(Permission.REJECT_CASE)).async
    verify.async { implicit request =>
      valuationCaseService.valuationCase(reference).flatMap {
        case Some(vc: ValuationCase) => {
          RejectCaseForm.form
            .bindFromRequest()
            .fold(
              formWithErrors => Future.successful(BadRequest(reject_case_reason(vc, formWithErrors))),
              _ => Future.successful(Redirect(routes.RejectCaseController.getRejectCaseEmail(reference)))
            )
        }
        case None => throw new Exception("failed to load case view")
      }
    }

  def renderRejectCaseEmail(
    reference: String,
    fileId: Option[String]   = None,
    uploadForm: Form[String] = UploadAttachmentForm.form
  )(implicit request: AuthenticatedCaseWorkerRequest[_]): Future[Result] = {
    val uploadFileId = fileId.getOrElse(UUID.randomUUID().toString)

    val fileUploadSuccessRedirect =
      appConfig.host + avar2.controllers.routes.RejectCaseController
        .rejectCase(reference, uploadFileId)
        .path

    val fileUploadErrorRedirect =
      appConfig.host + avar2.controllers.routes.RejectCaseController
        .getRejectCaseEmail(reference, Some(uploadFileId))
        .path

    val result = for{
      vc <- OptionT(valuationCaseService.valuationCase(reference))
      ir <- OptionT.liftF(fileService.initiate(
          FileStoreInitiateRequest(
            id = Some(uploadFileId),
            successRedirect = Some(fileUploadSuccessRedirect),
            errorRedirect = Some(fileUploadErrorRedirect),
            maxFileSize = appConfig.fileUploadMaxSize
          )))
    } yield Ok(reject_case_email(vc, uploadForm, ir))

    result.getOrElse(throw new Exception("failed to load form"))
  }

  def getRejectCaseEmail(reference: String, fileId: Option[String] = None): Action[AnyContent] =
//    (verify.authenticated andThen verify.casePermissions(reference) andThen verify.mustHave(Permission.REJECT_CASE))
       verify.async { implicit request =>
         renderRejectCaseEmail(reference)
       }

  def rejectCase(reference: String, fileId: String): Action[AnyContent] = verify.async{ implicit request =>
     val caseRejection: CaseRejection = CaseRejection(RejectReason.OTHER, "invalid data")

              for {
                _ <- valuationCaseService
                      .rejectCase(
                        reference,
                        caseRejection.reason,
                        Attachment(id = fileId, caseWorker = Option(request.caseWorker)),
                        caseRejection.note,
                        request.caseWorker
                      )
              } yield Redirect(routes.RejectCaseController.confirmRejectCase(reference))
  }
//    (verify.authenticated andThen
//      verify.casePermissions(reference) andThen
//      verify.mustHave(Permission.REJECT_CASE) andThen
//      verify.requireCaseData(reference, cacheKey(reference)))
//    verify.async { implicit request =>
//      request.userAnswers
//        .get[CaseRejection](RejectionCacheKey)
//        .map { caseRejection =>
//          for {
//            _ <- casesService
//                  .rejectCase(
//                    request.`case`,
//                    caseRejection.reason,
//                    Attachment(id = fileId, operator = Some(request.operator)),
//                    caseRejection.note,
//                    request.operator
//                  )
//
//            _ <- dataCacheConnector.remove(request.userAnswers.cacheMap)
//
//          } yield Redirect(routes.RejectCaseController.confirmRejectCase(reference))
//        }
//        .getOrElse {
//          successful(Redirect(routes.SecurityController.unauthorized()))
//        }
//    }

  def confirmRejectCase(reference: String): Action[AnyContent] = verify.async{ implicit request =>
    val outcome = for{
      vc <- OptionT(valuationCaseService.valuationCase(reference))
    } yield Ok(confirm_rejected(vc))

    outcome.getOrElse(throw new Exception("failed to confirm case"))
  }
//    (verify.authenticated
//      andThen verify.casePermissions(reference)
//      andThen verify.mustHave(Permission.VIEW_CASES))(implicit request => Ok(confirm_rejected(request.`case`)))
}
