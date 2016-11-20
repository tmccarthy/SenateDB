package au.id.tmm.senatedb.webapp.controllers

import javax.inject._

import play.api.mvc._

@Singleton
class HomeController @Inject() extends Controller {

  def index = Action {
    Ok(views.html.index("Hello world!"))
  }

}
