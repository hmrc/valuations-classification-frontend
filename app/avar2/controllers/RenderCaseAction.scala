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

import avar2.controllers.actions.AuthenticatedCaseWorkerRequest
import avar2.models.ValuationCase
import avar2.services.{CasesService, ValuationCaseService}
import config.AppConfig
import play.api.i18n.I18nSupport
import play.api.mvc._
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import scala.concurrent.Future.successful
import scala.concurrent.{ExecutionContext, Future}

trait RenderCaseAction extends I18nSupport { this: FrontendController =>

  protected implicit val config: AppConfig
  protected implicit val executionContext: ExecutionContext
  protected val caseService: ValuationCaseService

  protected def redirect: String => Call = routes.AvarController.show

  protected def isValidCase(vc: ValuationCase)(implicit request: AuthenticatedCaseWorkerRequest[_]): Boolean = true

  protected def getCaseAndRenderView(toHtml: ValuationCase => Future[HtmlFormat.Appendable])(
    implicit request: AuthenticatedCaseWorkerRequest[_], valuationCase: ValuationCase
  ): Future[Result] =
    if (isValidCase(valuationCase)(request)) {
      toHtml(valuationCase).map(Ok(_))
    } else {
      defaultRedirect()
    }

  protected def defaultRedirect(
    reference: Option[String] = None
  )(implicit request: AuthenticatedCaseWorkerRequest[_], valuationCase: ValuationCase): Future[Result] =
    successful(Redirect(redirect(reference.getOrElse(valuationCase.reference))))

//  protected def defaultRedirectAndEdit(
//    reference: Option[String] = None
//  )(implicit request: AuthenticatedValuationCaseRequest[_]): Future[Result] =
//    successful(Redirect(routes.RulingController.validateBeforeComplete(reference.getOrElse(request.valuationCase.reference))))

//  protected def validateAndRedirect(
//    toHtml: ValuationCase => Future[Call]
//  )(implicit request: AuthenticatedValuationCaseRequest[_]): Future[Result] =
//    if (isValidCase(request.`case`)(request)) {
//      toHtml(request.`case`).map(Redirect)
//    } else {
//      defaultRedirect()
//    }

  protected def validateAndRenderView(
    toHtml: ValuationCase => Future[HtmlFormat.Appendable]
  )(implicit request: AuthenticatedCaseWorkerRequest[_], valuationCase: ValuationCase): Future[Result] =
    if (isValidCase(valuationCase)(request)) {
      toHtml(valuationCase).map(Ok(_))
    } else {
      defaultRedirect()
    }

//  protected def renderView(valid: ValuationCase => Boolean, toHtml: ValuationCase => Future[HtmlFormat.Appendable])(
//    implicit request: AuthenticatedValuationCaseRequest[_]
//  ): Future[Result] =
//    if (valid(request.`case`)) {
//      toHtml(request.`case`).map(Ok(_))
//    } else {
//      defaultRedirect()
//    }

//  protected def getCaseAndRespond(caseReference: String, toResult: ValuationCase => Future[Result])(
//    implicit request: AuthenticatedValuationCaseRequest[_]
//  ): Future[Result] =
//    request.`case` match {
//      case c: ValuationCase if isValidCase(c)(request) => toResult(c)
//      case _                                  => defaultRedirect(Some(caseReference))
//    }

//  protected def validateAndRespond(
//    toResult: ValuationCase => Future[Result]
//  )(implicit request: AuthenticatedValuationCaseRequest[_]): Future[Result] =
//    if (isValidCase(request.`case`)(request)) {
//      toResult(request.`case`)
//    } else {
//      defaultRedirectAndEdit()
//    }
}
