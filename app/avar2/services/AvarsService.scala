package avar2.services

import avar2.models.StoredAttachment
import cats.data.OptionT
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton()
class AvarsService @Inject() (valuationCaseService: ValuationCaseService, fileStoreService: FileStoreService)(implicit ec: ExecutionContext) {

  def valuationCaseFileMetaData(reference: String)(implicit hc: HeaderCarrier): Future[Seq[StoredAttachment]] = {
    val outcome = for{
      vc <- OptionT(valuationCaseService.valuationCase(reference))
      sa <- OptionT.liftF(fileStoreService.getAttachments(vc))
    } yield sa

    outcome.getOrElse(throw new Exception("failed to retrieve stored attachments from the file store"))
  }

}
