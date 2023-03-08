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

import avar2.controllers.AvarController.createViewModel
import avar2.models.{Attachment, StoredAttachment, ValuationCase}
import avar2.services.ValuationCaseService
import cats.data.OptionT
import config.AppConfig
import controllers.RequestActions
import avar2.models.viewmodels.{ApplicantTabViewModel, AttachmentsTabViewModel, AvarViewModel, CaseViewModel, GoodsTabViewModel}
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import avar2.views.html.avar_view
import models.forms.UploadAttachmentForm
import models.response.{FileMetadata, FileStoreInitiateResponse, ScanStatus, UpscanFormTemplate}
import play.api.data.Form

import java.nio.file.FileStore
import java.time.Instant
import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class AvarController @Inject()(
                                 verify: RequestActions,
                                 mcc: MessagesControllerComponents,
                                 valuationCaseService: ValuationCaseService,
                                 avarView: avar_view,
                                 implicit val appConfig: AppConfig
                               )(implicit ec: ExecutionContext)
  extends FrontendController(mcc)
  /*  with UpscanErrorHandling */
    with I18nSupport {

  val form: Form[String] = UploadAttachmentForm.form

  def show(reference: String): Action[AnyContent] = {
    verify.authenticated.async { implicit request =>
      val outcome = for{
        c <- OptionT(valuationCaseService.valuationCase(reference))
      } yield Ok(avarView(createViewModel(c), form))

      outcome.getOrElse(throw new Exception("failed to load case view"))
    }
  }
}

object AvarController{

  def createViewModel(c: ValuationCase): AvarViewModel = {
    val cvm = CaseViewModel.fromValuationCase(c)
    val appvm = ApplicantTabViewModel.fromValuationCase(c)
    val gvm = GoodsTabViewModel.fromValuationCase(c)
    val workerAttachments: Seq[StoredAttachment] = List(
        StoredAttachment.apply(Attachment(id = "attachment-id1", caseWorker = None, description = Option("attachment file number 1")),
                 FileMetadata(id="attachment-id1", fileName=Option("the filename"),
                    mimeType = Option("image/jpeg"), url = Option("the file url"),
                                                  scanStatus = Option(ScanStatus.READY))))
    val atm: AttachmentsTabViewModel = AttachmentsTabViewModel("case reference","case contact", Seq.empty, workerAttachments)
    val template: UpscanFormTemplate = UpscanFormTemplate("href goes here", Map.empty)
    val response: FileStoreInitiateResponse = FileStoreInitiateResponse("an-id","upscan-reference",template )
    AvarViewModel(cvm, appvm, gvm, atm,  response)
  }

}
