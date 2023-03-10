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
import avar2.controllers.actions.AuthenticatedCaseWorkerAction
import avar2.forms.UploadAttachmentForm
import avar2.models.response.{FileMetadata, FileStoreInitiateResponse, ScanStatus, UpscanFormTemplate}
import avar2.models.{Attachment, StoredAttachment, ValuationCase}
import avar2.services.ValuationCaseService
import cats.data.OptionT
import config.AppConfig
import avar2.models.viewmodels.{ApplicantTabViewModel, AttachmentsTabViewModel, AvarViewModel, CaseViewModel, GoodsTabViewModel}
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import avar2.views.html.avar_view
import play.api.data.Form
import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class AvarController @Inject()(
                                verify: AuthenticatedCaseWorkerAction,
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
    verify.async { implicit request =>
      val outcome = for{
        c <- OptionT(valuationCaseService.valuationCase(reference))
      } yield Ok(avarView(createViewModel(c), form))

      outcome.getOrElse(throw new Exception("failed to load case view"))
    }
  }
}

object AvarController{

  def createViewModel(c: ValuationCase): AvarViewModel = {
    def storedAttachmentOf(attachment: Attachment): StoredAttachment = {
      StoredAttachment.apply(attachment,
        FileMetadata(id = attachment.id, fileName = Option("the filename"),
          mimeType = Option("image/jpeg"), url = Option("the file url"),
          scanStatus = Option(ScanStatus.READY)))
    }
    val cvm = CaseViewModel.fromValuationCase(c)
    val appvm = ApplicantTabViewModel.fromValuationCase(c)
    val gvm = GoodsTabViewModel.fromValuationCase(c)
    val workerAttachments = c.attachments.map(storedAttachmentOf)
    val atm: AttachmentsTabViewModel = AttachmentsTabViewModel("case reference","case contact", Seq.empty, workerAttachments)
    val template: UpscanFormTemplate = UpscanFormTemplate("href goes here", Map.empty)
    val response: FileStoreInitiateResponse = FileStoreInitiateResponse("an-id","upscan-reference",template )
    AvarViewModel(cvm, appvm, gvm, atm,  response)
  }

}
