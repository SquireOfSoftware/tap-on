from . import response
HTTP = {
    "GET": "GET",
    "POST": "POST"
}


def login(request):
    if request.method == HTTP.GET:
        return


def check_in(request):
    if request.method == "GET":
        return response.send_success("Success")
    return response.send_error("Problem with method", 404)
