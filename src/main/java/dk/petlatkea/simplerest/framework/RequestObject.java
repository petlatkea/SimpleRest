package dk.petlatkea.simplerest.framework;

/**
 * A RequestObject is sent to a RequestHandler when a request is received by the server.
 * It contains the path variables and the JSON body of the request.
 *
 * So far, the only path variable supported is {id} and it must be an integer.
 */
public class RequestObject {
  // locked in to take only one path variable, and it must be the integer: {id}
  private Integer pathVariable_id;
  private String jsonBody;

  public RequestObject() {
  }

  public RequestObject(String jsonBody) {
    this.pathVariable_id = null;
    this.jsonBody = jsonBody;
  }

  public RequestObject(int id, String jsonBody) {
    this.pathVariable_id = id;
    this.jsonBody = jsonBody;
  }

  public Integer getPathVariable_id() {
    return pathVariable_id;
  }

  public boolean hasPathVariable_id() {
    return pathVariable_id != null;
  }

  public void setPathVariable_id(Integer pathVariable_id) {
    this.pathVariable_id = pathVariable_id;
  }

  public String getJsonBody() {
    return jsonBody;
  }

  public void setJsonBody(String jsonBody) {
    this.jsonBody = jsonBody;
  }
}
