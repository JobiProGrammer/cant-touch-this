import json

from django.conf import settings
from django.http import JsonResponse, HttpResponseBadRequest
from django.shortcuts import get_object_or_404
# Create your views here.
from django.views import View

from main.models import File, Project


class FileView(View):
    def get(self, request):
        if "project" in request.GET:
            p = get_object_or_404(Project, name=request.GET["project"])
            project_files = File.objects.filter(project=p)
            re_list = []
            for i in project_files.all():
                re_list.append(i.path)
            return JsonResponse({"files": re_list, "project": p.name}, safe=False)
        else:
            return HttpResponseBadRequest(json.dumps({"status": "ERROR: BAD REQUEST, Missing parameters"}))

    def post(self, request):
        if "path" in request.POST and "project" in request.POST:
            p = Project.objects.get_or_create(name=request.POST["project"])
            f = File(path=request.POST["path"], project=p)
            f.save()
            JsonResponse(f.get_changes_dict(), safe=False)
        else:
            return HttpResponseBadRequest(json.dumps({"status": "ERROR: BAD REQUEST, Missing parameters"}))


class ChangeView(View):
    def get(self, request):
        if "path" in request.GET and "project" in request.GET:
            p = get_object_or_404(Project, name=request.GET["project"])
            f = get_object_or_404(File, path=request.GET["path"], project=p)
            return JsonResponse(f.get_changes_dict(), safe=False)

        else:
            return HttpResponseBadRequest(json.dumps({"status": "ERROR: BAD REQUEST, Missing parameters"}))

    def post(self, request):
        # will also create file and project
        if "path" in request.POST and "project" in request.POST and "change" in request.POST and "email" in request.POST:
            p = Project.objects.get_or_create(name=request.POST["project"])
            f = File.objects.get_or_create(path=request.POST["path"], project=p)
            # f = get_object_or_404(File, path=request.POST["path"], project=request.POST["project"])

            u = settings.AUTH_USER_MODEL.objects.get_or_create(email=request.POST["email"])
            change_list = json.loads(request.POST["change"])
            f.create_change(change_list, u)
            return JsonResponse(f.get_changes_dict(), safe=False)
        else:
            return HttpResponseBadRequest(json.dumps({"status": "ERROR: BAD REQUEST, Missing parameters"}))


class ProjectView(View):
    def get(self, request):
        return JsonResponse([i.name for i in Project.objects.all()], safe=False)

    def post(self, request):
        if "project" in request.POST:
            p = Project(name=request.POST["project"])
            p.save()
            return JsonResponse([i.name for i in Project.objects.all()], safe=False)
        else:
            return HttpResponseBadRequest(json.dumps({"status": "ERROR: BAD REQUEST, Missing parameters"}))
