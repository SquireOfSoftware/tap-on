from django.http import HttpResponse
import json


def send_success(message):
    return HttpResponse(json.dumps({
        "message": message
    }), status=200)


def send_error(message, error_code=500):
    return HttpResponse(json.dumps({
        "message": message
    }), status=error_code)
