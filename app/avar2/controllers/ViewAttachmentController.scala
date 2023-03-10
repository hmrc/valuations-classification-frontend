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
import avar2.models.response.FileMetadata
import avar2.services.FileStoreService
import cats.data.OptionT
import config.AppConfig
import play.api.i18n.I18nSupport
import play.api.mvc._
import uk.gov.hmrc.play.bootstrap.controller.WithUnsafeDefaultFormBinding
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import avar2.views.html.view_attachment_unavailable

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ViewAttachmentController @Inject()(
                                          verify: AuthenticatedCaseWorkerAction,
                                          fileService: FileStoreService,
                                          mcc: MessagesControllerComponents,
                                          val view_attachment_unavailable: view_attachment_unavailable,
                                          implicit val appConfig: AppConfig
)(implicit val executionContext: ExecutionContext)
    extends FrontendController(mcc)
    with I18nSupport
    with WithUnsafeDefaultFormBinding {

  def get(reference: String, id: String): Action[AnyContent] =
//    (verify.authenticated andThen verify.casePermissions(reference) andThen verify.mustHave(Permission.VIEW_CASES))
      verify.async { implicit request =>
        val outcome = for{
          meta <- OptionT(fileService.getFileMetadata(id))
          url <- OptionT(Future.successful(meta.url))
          content <- OptionT(fileService.downloadFile(url))
        } yield Ok
          .streamed(content, None, meta.mimeType)
          .withHeaders(
            "Content-Disposition" -> s"""filename=${meta.fileName.getOrElse(throw new IllegalArgumentException("filename was not specified"))}"""
          )

        outcome.getOrElse(throw new Exception("download failed"))
      }
}
