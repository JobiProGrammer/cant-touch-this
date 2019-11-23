import json

from django.contrib.auth import get_user_model
from django.http import JsonResponse, HttpResponseBadRequest
from django.shortcuts import get_object_or_404
# Create your views here.
from django.utils.decorators import method_decorator
from django.views import View
from django.views.decorators.csrf import csrf_exempt

from main.models import File, Project


class FileView(View):
    @csrf_exempt
    def get(self, request):
        if "project" in request.GET:
            p = get_object_or_404(Project, name=request.GET["project"])
            project_files = File.objects.filter(project=p)
            if "dict" in request.GET:
                re_dict = {"project": p.name}
                for i in project_files:
                    re_dict[i.path] = i.get_num_user()
                return JsonResponse(re_dict, safe=False)

            re_list = []
            for i in project_files.all():
                re_list.append(i.path)
            return JsonResponse({"files": re_list, "project": p.name}, safe=False)
        else:
            return HttpResponseBadRequest(json.dumps({"status": "ERROR: BAD REQUEST, Missing parameters"}))

    # @csrf_exempt
    # def post(self, request):
    # useless because we only want to save files with changes
    #    if "path" in request.POST and "project" in request.POST:
    #        p, _ = Project.objects.get_or_create(name=request.POST["project"])
    #        f = File(path=request.POST["path"], project=p)
    #        f.save()
    #        JsonResponse(f.get_changes_dict(), safe=False)
    #    else:
    #        return HttpResponseBadRequest(json.dumps({"status": "ERROR: BAD REQUEST, Missing parameters"}))


class ChangeView(View):

    @method_decorator(csrf_exempt)
    def dispatch(self, *args, **kwargs):
        return super(ChangeView, self).dispatch(*args, **kwargs)

    def get(self, request):
        if "project" in request.GET:
            p = get_object_or_404(Project, name=request.GET["project"])
            if "path" in request.GET:
                f = get_object_or_404(File, path=request.GET["path"], project=p)
                return JsonResponse(f.get_changes_dict(), safe=False)
            re = []
            for i in File.objects.filter(project=p).all():
                t = i.get_changes_dict()
                t["num_user"] = i.get_num_user()
                re.append(t)
            return JsonResponse(re, safe=False)



        else:
            return HttpResponseBadRequest(json.dumps({"status": "ERROR: BAD REQUEST, Missing parameters"}))

    def post(self, request):
        # will also create file and project
        if "path" in request.POST and "project" in request.POST and "change" in request.POST and "email" in request.POST:
            p, _ = Project.objects.get_or_create(name=request.POST["project"])

            f, _ = File.objects.get_or_create(path=request.POST["path"], project=p)
            # f = get_object_or_404(File, path=request.POST["path"], project=request.POST["project"])

            u, _ = get_user_model().objects.get_or_create(email=request.POST["email"], username=request.POST["email"])
            change_list = json.loads(request.POST["change"])
            print(request.POST["change"])
            print(change_list)

            re = f.create_change(change_list, u)
            return JsonResponse(re, safe=False)
        else:
            return HttpResponseBadRequest(json.dumps({"status": "ERROR: BAD REQUEST, Missing parameters"}))


class ProjectView(View):
    @csrf_exempt
    def get(self, request):
        return JsonResponse([i.name for i in Project.objects.all()], safe=False)

    @csrf_exempt
    def post(self, request):
        if "project" in request.POST:
            p = Project(name=request.POST["project"])
            p.save()
            return JsonResponse([i.name for i in Project.objects.all()], safe=False)
        else:
            return HttpResponseBadRequest(json.dumps({"status": "ERROR: BAD REQUEST, Missing parameters"}))
