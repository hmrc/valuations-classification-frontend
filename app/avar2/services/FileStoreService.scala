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

package avar2.services

import akka.stream.scaladsl.Source
import akka.util.ByteString
import avar2.connector.FileStoreConnector
import avar2.models.request.FileStoreInitiateRequest
import avar2.models.{Attachment, FileStoreAttachment, FileUpload, StoredAttachment, ValuationCase}
import avar2.models.response.{FileMetadata, FileStoreInitiateResponse}
import play.api.Logging
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class FileStoreService @Inject()(connector: FileStoreConnector)(implicit ec: ExecutionContext) extends Logging {

  def getFileMetadata(id: String)(implicit hc: HeaderCarrier): Future[Option[FileMetadata]] = connector.get(id)

  def getAttachments(c: ValuationCase)(implicit hc: HeaderCarrier): Future[Seq[StoredAttachment]] =
    getAttachments(Seq(c))
      .map(group => group.getOrElse(c, Seq.empty))
      .map(_.sortBy(_.timestamp))

  def getAttachments(cases: Seq[ValuationCase])(implicit hc: HeaderCarrier): Future[Map[ValuationCase, Seq[StoredAttachment]]] = {
    val caseByFileId: Map[String, ValuationCase] =
      cases.foldLeft(Map[String, ValuationCase]())((existing, c) => existing ++ c.attachments.map(_.id -> c))
    val attachmentsById: Map[String, Attachment] = cases.flatMap(_.attachments).map(a => a.id -> a).toMap

    def groupingByCase: Seq[FileMetadata] => Map[ValuationCase, Seq[StoredAttachment]] = { files =>
      val group: Map[ValuationCase, Seq[StoredAttachment]] = files.map { file =>
        caseByFileId.get(file.id) -> attachmentsById.get(file.id).map(StoredAttachment(_, file))
      } filter {
        // Select only attachments where the File is linked to a Case & Attachment
        case (c: Option[ValuationCase], att: Option[StoredAttachment]) => c.isDefined && att.isDefined
      } map {
        case (c: Option[ValuationCase], att: Option[StoredAttachment]) => (c.get, att.get)
      } groupBy (_._1) map {
        case (c: ValuationCase, seq: Seq[(ValuationCase, StoredAttachment)]) => (c, seq.map(_._2))
      }

      // Log an error for any attachments which arent in the response
      val idsFound: Set[String] = group.flatMap(_._2).map(_.id).toSet
      attachmentsById.keys.filterNot(idsFound.contains).foreach { id =>
        logger.error(s"Published file [$id] was not found in the Filestore")
      }

      // The Map currently only contains Cases which have >=1 attachments.
      // Add in the cases with 0 attachments
      val missing = cases.filterNot(group.contains)
      group ++ missing.map(_ -> Seq.empty)
    }

    connector.get(cases.flatMap(_.attachments)) map groupingByCase
  }



  def initiate(request: FileStoreInitiateRequest)(implicit hc: HeaderCarrier): Future[FileStoreInitiateResponse] =
    connector.initiate(request)

  def upload(fileUpload: FileUpload)(implicit hc: HeaderCarrier): Future[FileStoreAttachment] =
    connector.upload(fileUpload).map(toFileAttachment(fileUpload.content.path.toFile.length))

  def downloadFile(url: String)(implicit hc: HeaderCarrier): Future[Option[Source[ByteString, _]]] =
    connector.downloadFile(url)

  def removeAttachment(fileId: String)(implicit hc: HeaderCarrier): Future[Unit] = ???
    // connector.delete(fileId)

  private def toFileAttachment(size: Long): FileMetadata => FileStoreAttachment = { r =>
    FileStoreAttachment(r.id, r.fileName.getOrElse(""), r.mimeType.getOrElse(""), size)
  }

}
