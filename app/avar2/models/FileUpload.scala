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

package avar2.models

import play.api.libs.Files.TemporaryFile
import play.api.libs.json.Json
import play.api.mvc.MultipartFormData.FilePart

case class FileUpload(
  content: TemporaryFile,
  fileName: String,
  contentType: String
)

object FileUpload {

  def fromFilePart(filePart: FilePart[TemporaryFile]): FileUpload =
    FileUpload(
      filePart.ref,
      filePart.filename,
      filePart.contentType.getOrElse(throw new IllegalArgumentException("Missing file type"))
    )
}